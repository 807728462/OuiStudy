package mo.singou.vehicle.ai.communicate.usb;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;
import android.text.TextUtils;

import com.maikel.logger.LogTools;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mo.singou.vehicle.ai.communicate.parser.ParserManagerFactory;
import mo.singou.vehicle.ai.communicate.tools.ThreadPoolTools;
import mo.singou.vehicle.ai.communicate.usb.receiver.UsbHostReceiver;
import mo.singou.vehicle.ai.communicate.usb.utils.UsbPermissionUtils;


public class USBHostManager extends BaseUSBManager implements OnUsbDeviceStateListener<UsbDevice> {
    private static final String TAG = "USBHostManager";
    //private WeakReference<Context> mWeakContext;

    private PendingIntent mPermissionIntent = null;
    private boolean mToggle = true;
    private UsbDeviceConnection mUsbDeviceConnection;
    private UsbEndpoint mUsbEndpointOut;
    private UsbEndpoint mUsbEndpointIn;
    private UsbInterface mUsbInterface;
//    private boolean isCanReceiveMsg = false;
    /**
     * 设备连接的数量
     */
    //private volatile int mDeviceCounts = 0;

    private static USBHostManager usbHostManager;
    private UsbHostReceiver mUsbReceiver;

    private USBHostManager(Context context) {
        super(context);

    }

    static USBHostManager getInstance(Context context) {
        if (usbHostManager == null) {
            synchronized (USBHostManager.class) {
                if (usbHostManager == null) {
                    usbHostManager = new USBHostManager(context);
                }
            }
        }
        return usbHostManager;
    }

    @Override
    public void init() {
        if (mPermissionIntent == null) {
            LogTools.p(TAG, "init--->");
            mUsbReceiver = new UsbHostReceiver(this);
            mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_HOST_PERMISSION), 0);
            final IntentFilter filter = new IntentFilter(ACTION_USB_HOST_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            mContext.registerReceiver(mUsbReceiver, filter);
            // start connection check
            //mDeviceCounts = 0;
            mAsyncHandler.postDelayed(mDeviceCheckRunnable, 2000);
            openDevice();
        }
    }

    private void openDevice() {

        LogTools.p(TAG,"openDevice check if has device mConnectState-->"+mConnectState);

        if (mConnectState == STATE_CONNECT_NONE || mConnectState == STATE_CONNECT_FAILURE) {
            HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
            if (deviceList != null) {
                for (UsbDevice usbDevice : deviceList.values()) {
                    int productId = usbDevice.getProductId();
                    int vendorId = usbDevice.getVendorId();
                    LogTools.p(TAG, "openDevice-->" + productId + ",vendorId--->" + vendorId);
                    if (productId!=424&&productId!=4354&&productId!=9520) {
                        mConnectState = STATE_CONNECT_CONNECTING;
                        grantPermission(mContext, usbDevice);
                        break;
                    }
                }
            }
        }
    }


    /**
     * 定期检查连接的设备，如果发生变化，调用OnAttach。
     */
    private final Runnable mDeviceCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if (destroyed) return;
            if (mConnectState == STATE_CONNECT_SUCCESS) {
                mAsyncHandler.removeCallbacks(this);
                return;
            }
            openDevice();
            mAsyncHandler.postDelayed(this, 3000);    // confirm every 3 seconds
        }
    };

    public final boolean hasPermission(final UsbDevice device) throws IllegalStateException {
        if (destroyed) throw new IllegalStateException("already destroyed");
        return mUsbManager.hasPermission(device);
    }

    private void grantPermission(Context context, UsbDevice device) {
        if (device != null) {
            LogTools.p(TAG, "find device check it has permission or not--->");
            if (!mUsbManager.hasPermission(device)) {
                try {
                    UsbPermissionUtils.grantAutomaticPermission(device, context);
                    Intent intent = new Intent();
                    intent.setAction(ACTION_USB_HOST_PERMISSION);
                    intent.putExtra(UsbManager.EXTRA_DEVICE, device);
                    mContext.sendBroadcast(intent);
                } catch (Exception e) {
                    UsbPermissionUtils.requestPermission(mUsbManager, device, mPermissionIntent);
                    LogTools.p(TAG, "授权--->");
                }
            } else {
                LogTools.p(TAG, "已经有权限--->");
                processConnect(device);
            }
        }
    }

    private final void processAttach(final UsbDevice device) {
        if (destroyed) return;
        if (mOnDeviceConnectListener != null) {
            mAsyncHandler.post(new Runnable() {
                @Override
                public void run() {
                    mOnDeviceConnectListener.onAttach(device);
                }
            });
        }
    }

    /**
     * return device list, return empty list if no device matched
     *
     * @return
     * @throws IllegalStateException
     */
    public List<UsbDevice> getDeviceList() throws IllegalStateException {
        if (destroyed) throw new IllegalStateException("already destroyed");
        final HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        final List<UsbDevice> result = new ArrayList<UsbDevice>();
        if (deviceList != null) {
            result.addAll(deviceList.values());
        }
        return result;
    }


    /**
     * open specific USBHostManager device
     *
     * @param device
     */
    private final void processConnect(final UsbDevice device) {
        if (destroyed) return;
        if (mConnectState != STATE_CONNECT_CONNECTING) {
            LogTools.p(TAG, "can't not step in initAccessory because mConnectState=" + mConnectState);
            return;
        }
        mAsyncHandler.post(() -> initAccessory(device));
    }

    /**
     * 发送命令 , 让手机进入Accessory模式
     *
     * @param device
     */
    private void initAccessory(UsbDevice device) {
        UsbDeviceConnection usbDeviceConnection = mUsbManager.openDevice(device);
        if (usbDeviceConnection == null) {
            LogTools.p(TAG, "initAccessory usbDeviceConnection is null");
            mConnectState = STATE_CONNECT_FAILURE;
            return;
        }
        int i = sendControlAccessory(usbDeviceConnection, UsbConstants.USB_DIR_IN | UsbConstants.USB_TYPE_VENDOR,
                51, 0, 0, "1.0");
        if (i > 0) {
            i = sendControlAccessory(usbDeviceConnection, UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_VENDOR, 52,
                    0, 0, "Google, Inc.\0");
            i = sendControlAccessory(usbDeviceConnection, UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_VENDOR, 52,
                    0, 1, "mo.singou.vehicle.ai\0");
            i = sendControlAccessory(usbDeviceConnection, UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_VENDOR, 52,
                    0, 2, "mo.singou.vehicle.ai\0");
            i = sendControlAccessory(usbDeviceConnection, UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_VENDOR, 52,
                    0, 3, "1.0\0");
            i = sendControlAccessory(usbDeviceConnection, UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_VENDOR, 52,
                    0, 4, "http://www.android.com\0");
            i = sendControlAccessory(usbDeviceConnection, UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_VENDOR, 52,
                    0, 5, "0123456789\0");
            i = sendControlAccessory(usbDeviceConnection, UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_VENDOR, 53,
                    0, 5, "");
            initDevice();
        }
    }

    private int sendControlAccessory(UsbDeviceConnection deviceConnection, int requestType, int request, int value, int index, String cmd) {
        byte[] buffer = null;
        int len = 0;
        if (!TextUtils.isEmpty(cmd)) {
            buffer = cmd.getBytes(StandardCharsets.UTF_8);
            len = buffer.length;
        }
        int result = deviceConnection.controlTransfer(requestType, request,
                value, index, buffer, len, 100);
        SystemClock.sleep(50);
        return result;
    }
    /**
     * 初始化设备(手机) , 当手机进入Accessory模式后 , 手机的PID会变为Google定义的2个常量值其中的一个 ,
     */
    private void initDevice() {
        if (mConnectState != STATE_CONNECT_CONNECTING) {
            LogTools.p(TAG, "initDevice can't step to connect because mConnectState = " + mConnectState);
            return;
        }
        ThreadPoolTools.execute(() -> {
            LogTools.p(TAG, "phone's pid change to be google has defined(0x2d00,0x2d01) -->");
            mToggle = true;
            while (mToggle) {
                SystemClock.sleep(3000);
                HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
                Collection<UsbDevice> values = deviceList.values();
                if (values.isEmpty()) {
                    mToggle = false;
                    mConnectState = STATE_CONNECT_FAILURE;
                    return;
                }
                //boolean hasDevice = false;
                for (UsbDevice usbDevice : values) {
                    int productId = usbDevice.getProductId();
                    LogTools.p(TAG, "initDevice productId=" + productId + ",usbDevice.getVendorId()=" + usbDevice.getVendorId());
                    if (productId == 0x2D00 || productId == 0x2D01) {
                        //hasDevice = true;
                        if (mConnectState == STATE_CONNECT_SUCCESS) {
                            LogTools.p(TAG, "device has connected");
                            return;
                        }
                        if (mUsbManager.hasPermission(usbDevice)) {
                            mUsbDeviceConnection = mUsbManager.openDevice(usbDevice);
                            if (mUsbDeviceConnection != null) {
                                mUsbInterface = usbDevice.getInterface(0);
                                int endpointCount = mUsbInterface.getEndpointCount();
                                for (int i = 0; i < endpointCount; i++) {
                                    UsbEndpoint usbEndpoint = mUsbInterface.getEndpoint(i);
                                    if (usbEndpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                                        if (usbEndpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                                            mUsbEndpointOut = usbEndpoint;
                                        } else if (usbEndpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                                            mUsbEndpointIn = usbEndpoint;
                                        }
                                    }
                                }
                                if (mUsbEndpointOut != null && mUsbEndpointIn != null) {
                                    LogTools.p(TAG, "connect success start receive thread --->");
                                    //isCanReceiveMsg = true;
                                    mConnectState = STATE_CONNECT_SUCCESS;
                                    mToggle = false;
                                    receiverMessage();
                                    if (mOnDeviceConnectListener != null) {
                                        mOnDeviceConnectListener.onConnect(usbDevice);
                                    }
                                    SystemClock.sleep(1000);
                                    sendOffLineMessage();
                                    break;
                                }
                            }
                        } else {
                            mUsbManager.requestPermission(usbDevice, PendingIntent.getBroadcast(mContext, 0, new Intent(""), 0));
                            LogTools.p(TAG, "initDevice has no permission requestPermission--initDevice--->");
                        }
                    }
//                    else {
//                        initAccessory(usbDevice);
//                    }
                }
//                if (!hasDevice){
//                    mConnectState = STATE_CONNECT_FAILURE;
//                    mAsyncHandler.removeCallbacks(mDeviceCheckRunnable);
//                    mAsyncHandler.postDelayed(mDeviceCheckRunnable,5000);
//                }
            }
        });
    }


    private void detach(UsbDevice device) {
        processDettach(device);
    }

    private final void processDettach(final UsbDevice device) {
        if (destroyed) return;
        if (mOnDeviceConnectListener != null) {
            mAsyncHandler.post(new Runnable() {
                @Override
                public void run() {
                    mOnDeviceConnectListener.onDettach(device);
                }
            });
        }
    }

    /**
     * 接受消息线程 , 此线程在设备(手机)初始化完成后 , 就一直循环接受消息
     */
    private void receiverMessage() {
        if (hasRun){
            LogTools.p(TAG,"receiverMessage has run");
            return;
        }
        new Thread(new MessageReceiveRunnable()).start();
    }

    /**
     * unregister BroadcastReceiver
     *
     * @throws IllegalStateException
     */
    @Override
    public synchronized void unInit() throws IllegalStateException {
        //mDeviceCounts = 0;
        //isCanReceiveMsg = false;
        mConnectState = STATE_CONNECT_NONE;
        if (!destroyed) {
            mAsyncHandler.removeCallbacks(mDeviceCheckRunnable);
        }
        if (mPermissionIntent != null) {
            mContext.unregisterReceiver(mUsbReceiver);
            mPermissionIntent = null;
        }
    }

    @Override
    protected void sendTo(String serial, String message) {
        ThreadPoolTools.execute(() -> {
            /**
             * 发送数据的地方 , 只接受byte数据类型的数据
             */
            if (mUsbDeviceConnection != null&& mConnectState == STATE_CONNECT_SUCCESS) {
                LogTools.p(TAG, "start to send:" + message);
                byte[] data = tcpDataPack.packData(message);
                int i = mUsbDeviceConnection.bulkTransfer(mUsbEndpointOut, data, data.length, 3000);
                if (i > 0) {//大于0表示发送成功
                    //removeTimeOutCallback(serial);
                    LogTools.p(TAG, "send success:" + message);
                } else {
                    onSendFailer(serial, "send failure:");
                    removeTimeOutCallback(serial);
                    LogTools.p(TAG, "send fail:" + message);
                    reset();
                    mAsyncHandler.removeCallbacks(mDeviceCheckRunnable);
                    mAsyncHandler.postDelayed(mDeviceCheckRunnable, 1000);
                }
            } else {
                LogTools.p(TAG, "send fail:" + message);
                onSendFailer(serial, "send failure");
                removeTimeOutCallback(serial);
            }
        });

    }

    @Override
    protected void sendTo(String serial, byte[] message) {

    }


    @Override
    public void destroy() {
        destroyed = true;
        reset();
        closeConnection();
        if (/**context != null &&**/mPermissionIntent != null) {
            mContext.unregisterReceiver(mUsbReceiver);
        }
        super.destroy();
    }

    private void reset() {
        mToggle = false;
        mConnectState = STATE_CONNECT_NONE;
    }

    private void closeConnection() {
        mAsyncHandler.removeCallbacks(mDeviceCheckRunnable);
        if (mUsbDeviceConnection != null) {
            mUsbDeviceConnection.releaseInterface(mUsbInterface);
            mUsbDeviceConnection.close();
            mUsbDeviceConnection = null;
        }
        mUsbEndpointIn = null;
        mUsbEndpointOut = null;
    }

    @Override
    public void onAttached(UsbDevice device) {
        if (!destroyed && device != null) {
            processAttach(device);

            LogTools.p(TAG, "onAttached:" + device.getProductId() + ",mConnectState:" + mConnectState);
            if (mConnectState == STATE_CONNECT_NONE
                    || mConnectState == STATE_CONNECT_FAILURE) {
                mConnectState = STATE_CONNECT_CONNECTING;
                grantPermission(mContext, device);
            }
        }
    }

    @Override
    public void onPermissionGranted(UsbDevice device) {
        synchronized (USBHostManager.class) {
            if (device != null) {
                processConnect(device);
            }
        }
    }

    @Override
    public void onPermissionCancel(UsbDevice device) {

    }

    @Override
    public void onDetached(UsbDevice device) {
        if (device != null) {

            LogTools.p(TAG, "onDetached ---vendorId-->" + device.getVendorId());
            int productId = device.getProductId();
            if (productId!=424&&productId!=4354&&productId!=9520) {
                //mDeviceCounts = 0;
                reset();
                clearMap();
            }
            detach(device);
        }
        mAsyncHandler.removeCallbacks(mDeviceCheckRunnable);
        mAsyncHandler.postDelayed(mDeviceCheckRunnable, 3000);
    }

    private class MessageReceiveRunnable implements Runnable {

        @Override
        public void run() {
            hasRun = true;
            SystemClock.sleep(1000);
            byte[] mBytes = new byte[1024*16];
            ParserManagerFactory parserFactory = ParserManagerFactory.getInstance();
            parserFactory.setOnDataListener(USBHostManager.this);
            parserFactory.startParser();
            while (mConnectState == STATE_CONNECT_SUCCESS && !destroyed) {
                /**
                 * 循环接受数据的地方 , 只接受byte数据类型的数据
                 */
                if (mUsbDeviceConnection != null && mUsbEndpointIn != null) {
                    try {
                        int i = mUsbDeviceConnection.bulkTransfer(mUsbEndpointIn, mBytes, mBytes.length, 100);
                        if (i > 0) {
                            try {
                                parserFactory.byteArrayParser(mBytes, i);
                            } catch (Exception e) {
                                LogTools.p(TAG, e, "parse error：" + e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        LogTools.e(TAG, e, "receive error：" + e.getMessage());
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            parserFactory.stopParser();
            closeConnection();
            hasRun = false;
            LogTools.p(TAG, "MessageReceiveRunnable end-->");
        }
    }

}

package mo.singou.vehicle.ai.communicate.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;


import com.maikel.logger.LogTools;
import com.maikel.logger.utils.IOUtils;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import mo.singou.vehicle.ai.communicate.parser.ParserManagerFactory;
import mo.singou.vehicle.ai.communicate.tools.ThreadPoolTools;
import mo.singou.vehicle.ai.communicate.usb.utils.UsbPermissionUtils;

public class USBAccessoryManager extends BaseUSBManager {
    private static final String TAG = "USBAccessoryManager";
    //private WeakReference<Context> mWeakContext;
    private PendingIntent mPermissionIntent = null;
    public static final String ACTION_USB_PERMISSION = "mo.singou.vehicle.ai.usb.accessory.USB_PERMISSION";
    //private int mDeviceCounts = 0;
    private ParcelFileDescriptor mParcelFileDescriptor;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    private static USBAccessoryManager usbAccessoryManager;

    private USBAccessoryManager(Context context) {
        super(context);
    }

    static USBAccessoryManager getInstance(Context context) {
        if (usbAccessoryManager == null) {
            synchronized (USBAccessoryManager.class) {
                if (usbAccessoryManager == null) {
                    usbAccessoryManager = new USBAccessoryManager(context);
                }
            }
        }
        return usbAccessoryManager;
    }

    @Override
    public void init() {
        //Context context = mWeakContext.get();
        if (mPermissionIntent == null) {
            //if (context != null) {
            LogTools.p(TAG, "init 初始化-->");
            mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
            final IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
            mContext.registerReceiver(mUsbReceiver, filter);
            //}
            // start connection check

            mConnectState = STATE_CONNECT_NONE;
            mAsyncHandler.postDelayed(mDeviceCheckRunnable, 1000);

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
            openAccessory();
            mAsyncHandler.postDelayed(this, 3000);    // confirm every 3 seconds
        }
    };

    private void openAccessory() {

        LogTools.p(TAG, "openAccessory check has  usbAccessory  mConnectState-->"+mConnectState);

        if (mConnectState == STATE_CONNECT_NONE || mConnectState == STATE_CONNECT_FAILURE) {
            UsbAccessory[] accessories = mUsbManager.getAccessoryList();
            UsbAccessory usbAccessory = (accessories == null ? null : accessories[0]);
            if (usbAccessory != null) {
                LogTools.p(TAG, "mDeviceCheckRunnable check has  usbAccessory-->");
                mConnectState = STATE_CONNECT_CONNECTING;
                grantPermission(mContext, usbAccessory);
            }
        }
    }

    private void grantPermission(Context context, UsbAccessory device) {

        processAttach(device);
        if (!mUsbManager.hasPermission(device)) {
            try {
                UsbPermissionUtils.grantAutomaticPermission(device, context);
                Intent intent = new Intent();
                intent.setAction(ACTION_USB_PERMISSION);
                intent.putExtra(UsbManager.EXTRA_DEVICE, device);
                context.sendBroadcast(intent);
            } catch (Exception e) {
                LogTools.p(TAG, "auto grantPermission error  start grant permission-->");
                UsbPermissionUtils.requestPermission(mUsbManager, device, mPermissionIntent);
            }
        } else {
            LogTools.p(TAG, "grantPermission has permission to connect");
            processConnect(device);
        }

    }

    private final void processAttach(final UsbAccessory device) {
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
     * 打开Accessory模式
     *
     * @param usbAccessory
     */
    private void openAccessory(UsbAccessory usbAccessory) {

        mParcelFileDescriptor = mUsbManager.openAccessory(usbAccessory);
        LogTools.p(TAG, "openAccessory mode");
        if (mParcelFileDescriptor != null) {
            FileDescriptor fileDescriptor = mParcelFileDescriptor.getFileDescriptor();
            mFileInputStream = new FileInputStream(fileDescriptor);
            mFileOutputStream = new FileOutputStream(fileDescriptor);
            //isCanReceiveMsg = true;
            mConnectState = STATE_CONNECT_SUCCESS;
            if (mOnDeviceConnectListener != null) {
                mOnDeviceConnectListener.onConnect(usbAccessory);
            }
            new Thread(new MessageReceveRunnable()).start();
            LogTools.p(TAG, "connect success start read usb data");
        } else {
            //mDeviceCounts = 0;
            mConnectState = STATE_CONNECT_FAILURE;
//            mAsyncHandler.removeCallbacks(mDeviceCheckRunnable);
//            mAsyncHandler.postDelayed(mDeviceCheckRunnable,2000);
        }
    }

    @Override
    public void unInit() {
        //mDeviceCounts = 0;
        //isCanReceiveMsg = false;
        mConnectState = STATE_CONNECT_NONE;
        if (!destroyed) {
            mAsyncHandler.removeCallbacks(mDeviceCheckRunnable);
        }
        if (mPermissionIntent != null) {
            //final Context context = mWeakContext.get();
            try {
                //  if (context != null) {
                mContext.unregisterReceiver(mUsbReceiver);
                // }
            } catch (final Exception e) {
            }
            mPermissionIntent = null;
        }
    }

    @Override
    protected void sendTo(String serial, byte[] message) {

    }

    @Override
    protected void sendTo(String serial, String message) {
        ThreadPoolTools.execute(() -> {
            if (mFileOutputStream != null&& mConnectState == STATE_CONNECT_SUCCESS) {
                byte[] data = tcpDataPack.packData(message);
                LogTools.p(TAG, "开始发送:" + message);
                try {
                    mFileOutputStream.write(data);
                    mFileOutputStream.flush();
                    //removeTimeOutCallback(serial);
                    LogTools.p(TAG, "发送成功:" + Arrays.toString(data));
                } catch (IOException e) {
                    LogTools.p(TAG, e, "发送失败:" + message);
                    onSendFailer(serial, e.getMessage());
                    removeTimeOutCallback(serial);
                    mConnectState = STATE_CONNECT_NONE;
                    mAsyncHandler.removeCallbacks(mDeviceCheckRunnable);
                    mAsyncHandler.postDelayed(mDeviceCheckRunnable, 1000);
                }
            } else {
                LogTools.p(TAG, "发送失败:" + message);
                onSendFailer(serial, "发送失败");
                removeTimeOutCallback(serial);
            }
        });
    }


    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (destroyed) return;
            final String action = intent.getAction();
            LogTools.p(TAG, "usb event action-->" + action);
            if (ACTION_USB_PERMISSION.equals(action)) {
                // when received the result of requesting USBHostManager permission
                synchronized (USBAccessoryManager.this) {
                    final UsbAccessory usbAccessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (usbAccessory != null) {
                        processConnect(usbAccessory);
                    }
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
                final UsbAccessory usbAccessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (!destroyed && usbAccessory != null) {
                    mConnectState = STATE_CONNECT_CONNECTING;
                    grantPermission(context, usbAccessory);
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                final UsbAccessory usbAccessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (usbAccessory != null) {
                    detach(usbAccessory);
                }
            }
        }
    };

    /**
     * open specific USBHostManager device
     *
     * @param device
     */
    private final void processConnect(final UsbAccessory device) {
        if (destroyed) return;
        mAsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                openAccessory(device);
            }
        });

    }

    private void detach(UsbAccessory device) {
        // mDeviceCounts = 0;
        mAsyncHandler.removeCallbacks(mDeviceCheckRunnable);

        mAsyncHandler.postDelayed(mDeviceCheckRunnable, 3000);

        //processDettach(device);
        mConnectState = STATE_CONNECT_NONE;
        //isCanReceiveMsg = false;
        if (mOnDeviceConnectListener != null) {
            mOnDeviceConnectListener.onDettach(device);
        }
    }

//    private final void processDettach(final UsbAccessory device) {
//        if (destroyed) return;
//        if (mOnDeviceConnectListener != null) {
//            mAsyncHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    mOnDeviceConnectListener.onDettach(device);
//                }
//            });
//        }
//    }

    @Override
    public void destroy() {
        destroyed = true;
        //isCanReceiveMsg = false;
        mConnectState = STATE_CONNECT_NONE;
        IOUtils.closeQuietly(mParcelFileDescriptor);
        IOUtils.closeQuietly(mFileInputStream);
        IOUtils.closeQuietly(mFileOutputStream);
        //Context context = mWeakContext.get();
        if (/**context != null &&**/mPermissionIntent != null) {
            mContext.unregisterReceiver(mUsbReceiver);
        }
        //mWeakContext.clear();
    }

    private class MessageReceveRunnable implements Runnable {

        @Override
        public void run() {
            int i = 0;
            byte[] mBytes = new byte[1024];
            ParserManagerFactory parserFactory = ParserManagerFactory.getInstance();
            parserFactory.setOnDataListener(USBAccessoryManager.this);
            parserFactory.startParser();
            while (!destroyed && mConnectState == STATE_CONNECT_SUCCESS) {
                try {
                    i = mFileInputStream.read(mBytes);
                    if (i > 0) {
                        try {
                            parserFactory.byteArrayParser(mBytes, i);
                        } catch (Exception e) {
                            LogTools.e(TAG, e, "解析出错：" + e.getMessage());
                        }
                    }
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

            }
            IOUtils.closeQuietly(mParcelFileDescriptor);
            IOUtils.closeQuietly(mFileInputStream);
            IOUtils.closeQuietly(mFileOutputStream);
            parserFactory.stopParser();
            //isCanReceiveMsg = false;
            mConnectState = STATE_CONNECT_NONE;
            LogTools.p(TAG, "MessageReceveRunnable end-->");
        }
    }
}

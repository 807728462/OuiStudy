package mo.singou.vehicle.ai.communicate.usb;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;

import com.maikel.logger.LogTools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mo.singou.vehicle.ai.base.bean.Email;
import mo.singou.vehicle.ai.base.utils.JsonUtils;
import mo.singou.vehicle.ai.base.utils.StringUtlis;
import mo.singou.vehicle.ai.communicate.pack.PacketerFactory;
import mo.singou.vehicle.ai.communicate.parser.IByteParserManager;

import static android.content.ContentValues.TAG;

public abstract class BaseUSBManager implements IUSB, IByteParserManager.OnDataListener{

    //protected boolean isCanReceiveMsg = false;
    protected Map<String, String> messageMap = new HashMap<>();
    protected Map<String,Runnable> timeOutMap = new HashMap<>();
    protected OnDeviceConnectListener mOnDeviceConnectListener;
    protected final Handler mAsyncHandler;
    protected Context mContext;
    protected UsbManager mUsbManager;
    protected volatile boolean destroyed = false;
    protected PacketerFactory tcpDataPack;

    protected static final int STATE_CONNECT_NONE = 0;
    protected static final int STATE_CONNECT_CONNECTING = 1;
    protected static final int STATE_CONNECT_FAILURE = 2;
    protected static final int STATE_CONNECT_SUCCESS = 3;

    protected volatile int mConnectState = STATE_CONNECT_NONE;
    protected boolean hasRun = false;

    protected BaseUSBManager(Context context){
        mAsyncHandler = new Handler(Looper.getMainLooper());
        mContext = context;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        tcpDataPack = PacketerFactory.getInstance();
    }

    @Override
    public void sendMessage(String serial, String message) {
        if (message==null||message.length()==0) return;
        if (mConnectState==STATE_CONNECT_SUCCESS){
            sendTo(serial,message);
        }else {
            storeMessage(serial, message);
        }
    }

    private void storeMessage(String serial, String message) {
        LogTools.p("BaseUSBManager","storeMessage");
        messageMap.put(serial, message);
        TimeOutRunnable timeOutRunnable = new TimeOutRunnable(serial);
        mAsyncHandler.postDelayed(timeOutRunnable, rpcMsgTimeOutTime);
        timeOutMap.put(serial, timeOutRunnable);
    }

    @Override
    public void sendMessage(String serial, byte[] message) {
        if (message == null||message.length==0) return;
        if (mConnectState==STATE_CONNECT_SUCCESS){
            sendTo(serial,message);
        }else {
            storeMessage(serial, new String(message));
        }
    }

    protected void sendTo(String serial, String message){

    }
    protected void sendTo(String serial, byte[] message){

    }

    protected void removeTimeOutCallback(String serial) {
        Runnable timeOutRunnable = timeOutMap.get(serial);
        mAsyncHandler.removeCallbacks(timeOutRunnable);
        timeOutMap.remove(serial);
    }
    /**
     * 重发机制
     */
    protected void onSendFailer(String serial, String error) {
        if (mOnDeviceConnectListener != null) {
            mOnDeviceConnectListener.onError(serial, error);
        }
    }
    @Override
    public void destroy() {
        clearMap();
    }

    protected void clearMap() {
        if (!timeOutMap.isEmpty()) {
            Iterator iter = timeOutMap.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                Runnable runnable = timeOutMap.get(key);
                mAsyncHandler.removeCallbacks(runnable);
                if (runnable!=null){
                    runnable.run();
                }
            }
        }
        messageMap.clear();
        timeOutMap.clear();
    }

    @Override
    public void setOnDeviceConnectListener(OnDeviceConnectListener listener) {
        mOnDeviceConnectListener = listener;
    }

   protected class TimeOutRunnable implements Runnable{
        private String serial;
        TimeOutRunnable(String serial){
            this.serial = serial;
        }
        @Override
        public void run() {
            messageMap.remove(serial);
            if (mOnDeviceConnectListener != null) {
                mOnDeviceConnectListener.onTimeOut(serial);
            }
            //timeOutMap.remove(serial);
        }
    }

    protected void sendOffLineMessage() {
        if (!messageMap.isEmpty()) {
            Iterator iter = messageMap.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String val = messageMap.get(key);
                sendMessage(key, val);
            }
            messageMap.clear();
        }
    }

    @Override
    public void onDataResponse(String response) {
        LogTools.p("BaseUsBManager","response:"+response);
        if (StringUtlis.isEmpty(response)) {
            LogTools.p(TAG, "接收内容为空！");
            return;
        }
        Email responseBase = JsonUtils.parse(response, Email.class);
        if (responseBase == null) {
            LogTools.p(TAG, "ProcessEmail 对象为空!---->" + response);
            if (mOnDeviceConnectListener!=null)mOnDeviceConnectListener.onMessage("",0,response);
            return;
        }
        String serial = responseBase.serial;
        timeOutMap.remove(serial);
        if (mOnDeviceConnectListener!=null)mOnDeviceConnectListener.onMessage(serial,responseBase.eventId,responseBase.data);
    }
}

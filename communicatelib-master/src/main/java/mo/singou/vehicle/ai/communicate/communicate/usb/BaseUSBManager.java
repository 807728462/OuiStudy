package mo.singou.vehicle.ai.communicate.communicate.usb;

import android.content.Context;

import com.maikel.logger.LogTools;

import java.util.HashMap;
import java.util.Map;

import mo.singou.vehicle.ai.base.BaseApplication;
import mo.singou.vehicle.ai.communicate.communicate.ICommunicateManager;
import mo.singou.vehicle.ai.communicate.usb.IUSB;
import mo.singou.vehicle.ai.communicate.usb.VehicleUsbManager;

public abstract class BaseUSBManager<T> implements ICommunicateManager ,IUSB.OnDeviceConnectListener<T> {
    private static final String TAG = "BaseUSBManager";
    protected OnRemoteMessageListener mListener;
    protected IUSB mUsb;
    protected Map<String,OnResponseListener> responseListenerMap = new HashMap<>();
    protected Context mContext;
    public BaseUSBManager(Context context,int type){
        mContext = context.getApplicationContext();
        mUsb = VehicleUsbManager.getInstance(type, mContext);
    }
    @Override
    public void init(String tag,String ack) {
        mUsb.init();
    }

    @Override
    public void start() {
        mUsb.setOnDeviceConnectListener(this);
        LogTools.p(TAG,"start--->setOnDeviceConnectListener");
    }

    @Override
    public void stop() {
        mUsb.unInit();
        mUsb.destroy();
    }

    @Override
    public void sendMessage(String serial, String message, OnResponseListener listener) {
        responseListenerMap.remove(serial);
        responseListenerMap.put(serial,listener);
        mUsb.sendMessage(serial,message);
    }

    @Override
    public void sendMessage(String message) {
        mUsb.sendMessage(String.valueOf(System.currentTimeMillis()),message);
    }

    @Override
    public void sendMessage(byte[] message) {
        mUsb.sendMessage(String.valueOf(System.currentTimeMillis()),message);
    }

    @Override
    public void setOnRemoteMessageListener(OnRemoteMessageListener listener) {
        mListener = listener;
    }


    @Override
    public void onMessage(String serial,int eventId,String message) {
        OnResponseListener listener = responseListenerMap.get(serial);
        if (listener!=null){
            listener.onSuccess(message);
            responseListenerMap.remove(serial);
        }else {
            if (mListener != null) mListener.onMessage(eventId,serial, message);
        }
    }

    @Override
    public void onTimeOut(String serial) {
        onError(serial,"连接超时或者发送超时");
    }

    @Override
    public void onError(String serial, String error) {
        OnResponseListener listener = responseListenerMap.get(serial);
        if (listener!=null){
            listener.onError(error);
            responseListenerMap.remove(serial);
        }
    }
}

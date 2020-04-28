package mo.singou.vehicle.ai.communicate.communicate;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface ICommunicateManager {


    int TYPE_TCP_SERVER = 0;
    int TYPE_TCP_CLIENT = 1;
    int TYPE_WEB_SOCKET = 2;
    int TYPE_USB_HOST = 3;
    int TYPE_USB_ACCESSORY = 4;
    @IntDef(value = {
            TYPE_TCP_SERVER,
            TYPE_TCP_CLIENT,
            TYPE_WEB_SOCKET,
            TYPE_USB_HOST,
            TYPE_USB_ACCESSORY
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface ConnectType {
    }

    void init(String tag,String ack);

    void start();

    void stop();

    void sendMessage(String serial, String message, OnResponseListener listener);

    void sendMessage(String message);

    void sendMessage(byte[] message);

    void setOnRemoteMessageListener(OnRemoteMessageListener listener);

    void setOnAddressListener(OnAddressListener listener);

    interface OnResponseListener{
        void onSuccess(String response);
        void onError(String error);
//        void onPhotoPath(String path);
//        void onTts(String ttsJson);
//        void onWakeStatus(String json);
    }

    interface OnRemoteMessageListener{
        void onMessage(int eventId,String serial, String message);
        void onConnected();
        void onDisconnected();
    }

    /**
     * 为了解决科大讯飞开发版离线NLP
     */
    interface OnAddressListener{
        void onAddress(String address);
    }
}

package mo.singou.vehicle.ai.communicate.socket.tcp.service;

import mo.singou.vehicle.ai.communicate.socket.tcp.OnConnectTimeOutListener;

public interface SocketManager {
    int CODE_CONNECTED = 1;
    int CODE_DISCONNECTED = 2;
    int CODE_SERVER_START = 0;
    int CODE_SERVER_ERROR = 3;
    void sendMessage(String msg);
    void sendMessage(String msg,String serial);
    void sendMessage(byte[]message);

    void setOnSocketListener(OnSocketListener listener);

    void setOnConnectTimeOutListener(OnConnectTimeOutListener listener);

    void startService(int port);

    void close();

    boolean isServiceRun();

    void setServiceRun(boolean run);

    boolean hasClient();

    interface OnSocketListener{
        void onClientMessage(String message);
        void onSendFailure(String serial,String errorMsg);
        void onStateMessage(String message,int code);
    }

}

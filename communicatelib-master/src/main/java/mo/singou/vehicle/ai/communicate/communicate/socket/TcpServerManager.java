package mo.singou.vehicle.ai.communicate.communicate.socket;


import android.content.Context;
import android.os.SystemClock;
import mo.singou.vehicle.ai.communicate.socket.tcp.OnConnectTimeOutListener;
import mo.singou.vehicle.ai.communicate.socket.tcp.service.SocketFactory;
import mo.singou.vehicle.ai.communicate.socket.tcp.service.SocketManager;
import mo.singou.vehicle.ai.communicate.socket.udp.UDPManager;


public class TcpServerManager extends BaseTcpManager implements SocketManager.OnSocketListener, OnConnectTimeOutListener {
    private static final String TAG = TcpServerManager.class.getSimpleName();

    private SocketManager socketManager;

    public TcpServerManager(Context context) {
        super(context);
    }

    @Override
    protected void handleConstruct() {
        mTcpType = UDPManager.TYPE_SERVER;
        socketManager = SocketFactory.getInstance();
        socketManager.setOnSocketListener(this);
        socketManager.setOnConnectTimeOutListener(this);
    }

    @Override
    protected void handleIp(String ip) {
        runTcpServer();
    }

    private void runTcpServer() {
        if (socketManager.isServiceRun()) {
            LogTools.p(TAG, "run tcp service ip===");
            socketManager.setServiceRun(false);
            socketManager.close();
            SystemClock.sleep(1000);
        }
        socketManager.startService(mPort);
    }


    @Override
    protected void handleStop() {
        socketManager.setServiceRun(false);
    }

//    @Override
//    public void sendMessage(String serial,String message, OnResponseListener listener) {
//        responseListenerMap.remove(serial);
//        responseListenerMap.put(serial,listener);
//        socketManager.sendMessage(message);
//    }

    @Override
    protected void sendTo(String serial, String message) {
        socketManager.sendMessage(message,serial);
    }

    @Override
    protected void sendTo(byte[] message) {
        socketManager.sendMessage(message);
    }

    @Override
    public void onClientMessage(String message) {
        LogTools.p(TAG, "接收到消息=" + message);
        if (StringUtlis.isEmpty(message)) {
            LogTools.p(TAG, "请求内容为空！");
            return;
        }
        Email responseBase = JsonUtils.parse(message, Email.class);
        if (responseBase == null) {
            LogTools.p(TAG, "ProcessEmail 对象为空!---->" + message);
            if (mListener != null) mListener.onMessage(0, null, message);
            return;
        }
        OnResponseListener listener = responseListenerMap.get(responseBase.serial);
        if (listener != null) {
            listener.onSuccess(responseBase.data);
            responseListenerMap.remove(responseBase.serial);
        } else {
            if (mListener != null)
                mListener.onMessage(responseBase.eventId, responseBase.serial, responseBase.data);
        }
    }

    @Override
    public void onSendFailure(String serial,String errorMsg) {
        OnResponseListener listener = responseListenerMap.get(serial);
        if (listener != null) {
            listener.onError(errorMsg);
            responseListenerMap.remove(serial);
        }
       startUdp(false);
    }

    @Override
    public void onStateMessage(String message, int code) {
        switch (code) {
            case SocketManager.CODE_CONNECTED:
                if (mListener != null) mListener.onConnected();
                mUdpManager.setRun(false);
                LogTools.p(TAG,"连接成功断开udp --->");
                break;
            case SocketManager.CODE_SERVER_START:
                mUdpManager.setRun(false);
                break;
            case SocketManager.CODE_DISCONNECTED:
                if (mListener != null) mListener.onDisconnected();
                break;
        }
    }

    @Override
    protected void startTcp(String ip) {
        LogTools.p(TAG,"ip-->"+ip);
        runTcpServer();
    }

    @Override
    protected void startUdp(boolean isDestroy) {
        if (mUdpManager.isRun()) {
            if (isDestroy) {
                socketManager.setServiceRun(false);
                mUdpManager.destroy();
                SystemClock.sleep(1000);
            } else {
                LogTools.p(TAG, "notifyWifiConnected：udp 已经开启 无需重复开启");
                return;
            }
        }
        mUdpManager.setOnIpAddressListener(this);
        mUdpManager.start(mPort, mAck);
    }


    @Override
    protected void handleOnWifiDisconnected() {
        socketManager.setServiceRun(false);
    }

    @Override
    public void onTimeOut() {
        LogTools.p(TAG, "not connect to client start udp");
        startUdp(true);
    }
}

package mo.singou.vehicle.ai.communicate.socket.tcp.service;

import com.maikel.logger.LogTools;

import mo.singou.vehicle.ai.communicate.socket.tcp.OnConnectTimeOutListener;

public class SocketFactory implements SocketManager {
    SocketManager socketManager;
    private static SocketFactory socketFactory;

    private SocketFactory() {
        socketManager = new SocketManagerImpl();
    }

    public static SocketFactory getInstance() {
        if (socketFactory == null) {
            synchronized (SocketFactory.class) {
                if (socketFactory == null) {
                    socketFactory = new SocketFactory();
                }
            }
        }
        return socketFactory;
    }

    @Override
    public void sendMessage(String msg) {
        LogTools.p("SocketFactory", "发送消息=" + msg);
        socketManager.sendMessage(msg);
    }

    @Override
    public void sendMessage(String msg, String serial) {
        socketManager.sendMessage(msg,serial);
    }

    @Override
    public void sendMessage(byte[] message) {
        socketManager.sendMessage(message);
    }

    @Override
    public void setOnSocketListener(OnSocketListener listener) {
        socketManager.setOnSocketListener(listener);
    }

    @Override
    public void setOnConnectTimeOutListener(OnConnectTimeOutListener listener) {
        socketManager.setOnConnectTimeOutListener(listener);
    }

    @Override
    public void startService(int port) {
        socketManager.startService(port);
    }

    @Override
    public void close() {
        socketManager.close();
    }

    @Override
    public boolean isServiceRun() {
        return socketManager.isServiceRun();
    }

    @Override
    public void setServiceRun(boolean run) {
        socketManager.setServiceRun(run);
    }

    @Override
    public boolean hasClient() {
        return socketManager.hasClient();
    }
}

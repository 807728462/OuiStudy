package mo.singou.vehicle.ai.communicate.communicate;

import android.content.Context;

import mo.singou.vehicle.ai.communicate.communicate.socket.TcpClientManager;
import mo.singou.vehicle.ai.communicate.communicate.socket.TcpServerManager;
import mo.singou.vehicle.ai.communicate.communicate.socket.WebManager;
import mo.singou.vehicle.ai.communicate.communicate.usb.USBAccessoryManager;
import mo.singou.vehicle.ai.communicate.communicate.usb.USBHostMonitor;
import mo.singou.vehicle.ai.communicate.usb.IUSB;

public class CommunicateManager implements ICommunicateManager {
    private ICommunicateManager communicateManager;

    private CommunicateManager(ICommunicateManager communicateManager) {
        this.communicateManager = communicateManager;
    }

    public static CommunicateManager getInstance(Context context,@ConnectType int type) {

        switch (type) {
            case TYPE_TCP_SERVER:
                return new CommunicateManager(new TcpServerManager(context));
            case TYPE_TCP_CLIENT:
                return new CommunicateManager(new TcpClientManager(context));
            case TYPE_USB_HOST:
                return new CommunicateManager(new USBHostMonitor(context,IUSB.TYPE_HOST));
            case TYPE_USB_ACCESSORY:
                return new CommunicateManager(new USBAccessoryManager(context,IUSB.TYPE_ACCESSORY));
            case TYPE_WEB_SOCKET:
                return new CommunicateManager(new WebManager(8080,context));
            default:
                throw new IllegalArgumentException("no such type --->" + type);
        }
    }

    @Override
    public void init(String tag, String ack) {
        communicateManager.init(tag, ack);
    }

    @Override
    public void start() {
        communicateManager.start();
    }

    @Override
    public void stop() {
        communicateManager.stop();
    }

    @Override
    public void sendMessage(String serial, String message, OnResponseListener listener) {
        communicateManager.sendMessage(serial, message, listener);
    }

    @Override
    public void sendMessage(String message) {
        communicateManager.sendMessage(message);
    }

    @Override
    public void sendMessage(byte[] message) {
        communicateManager.sendMessage(message);
    }

    @Override
    public void setOnRemoteMessageListener(OnRemoteMessageListener listener) {
        communicateManager.setOnRemoteMessageListener(listener);
    }

    @Override
    public void setOnAddressListener(OnAddressListener listener) {
        communicateManager.setOnAddressListener(listener);
    }


}

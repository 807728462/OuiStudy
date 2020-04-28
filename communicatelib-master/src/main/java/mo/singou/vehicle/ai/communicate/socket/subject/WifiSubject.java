package mo.singou.vehicle.ai.communicate.socket.subject;

public interface WifiSubject {
    void notifyWifiConnected();

    void notifyWifiDisconnected();

    void attachObserver(WifiObsever obsever);

    void detachObserver(WifiObsever obsever);
}

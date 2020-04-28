package mo.singou.vehicle.ai.communicate.socket.subject;

public class WifiObserverManager {
    private WifiSubject mSubject;
    private static final WifiObserverManager manager = new WifiObserverManager();

    private WifiObserverManager() {
        mSubject = new WifiSubjectImpl();
    }

    public static WifiObserverManager getInstance() {
        return manager;
    }

    public void notifyWifiConnected() {
        mSubject.notifyWifiConnected();
    }

    public void notifyWifiDisconnected() {
        mSubject.notifyWifiDisconnected();
    }

    public void attachObserver(WifiObsever obsever) {
        mSubject.attachObserver(obsever);
    }

    public void detachObserver(WifiObsever obsever) {
        mSubject.detachObserver(obsever);
    }
}

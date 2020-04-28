package mo.singou.vehicle.ai.communicate.socket.subject;

import java.util.ArrayList;
import java.util.List;

public class WifiSubjectImpl implements WifiSubject {
    private List<WifiObsever> mObservers = new ArrayList<>();
    WifiSubjectImpl(){}
    @Override
    public void notifyWifiConnected() {
        for (WifiObsever obsever:mObservers){
            obsever.notifyWifiConnected();
        }
    }

    @Override
    public void notifyWifiDisconnected() {
        for (WifiObsever obsever:mObservers){
            obsever.notifyWifiDisconnected();
        }
    }

    @Override
    public void attachObserver(WifiObsever obsever) {
        if (!mObservers.contains(obsever)) {
            mObservers.add(obsever);
        }
    }

    @Override
    public void detachObserver(WifiObsever obsever) {
        mObservers.remove(obsever);
    }
}

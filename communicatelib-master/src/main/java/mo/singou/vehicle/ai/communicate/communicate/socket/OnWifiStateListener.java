package mo.singou.vehicle.ai.communicate.communicate.socket;

public interface OnWifiStateListener {

    void onWifiScaned();

    void onWifiOpened();

    void onWifiClosed();

    void onWifiConnected();

    void onWifiDisconnected();
}

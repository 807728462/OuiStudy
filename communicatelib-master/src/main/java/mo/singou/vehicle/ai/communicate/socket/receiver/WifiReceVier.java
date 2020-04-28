package mo.singou.vehicle.ai.communicate.socket.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.maikel.logger.LogTools;

import mo.singou.vehicle.ai.communicate.communicate.socket.OnWifiStateListener;
import mo.singou.vehicle.ai.communicate.socket.subject.WifiObserverManager;


public class WifiReceVier extends BroadcastReceiver {
    private static final String TAG = WifiReceVier.class.getSimpleName();
    private OnWifiStateListener mListener;
    public void setOnWifiStateListener(OnWifiStateListener listener){
        this.mListener = listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogTools.e(TAG, "action:" + action);
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            //获取当前的wifi状态int类型数据
            int mWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            LogTools.p(TAG, "mWifiState:" + mWifiState);
            switch (mWifiState) {
                case WifiManager.WIFI_STATE_ENABLED://wifi已经打开
                    if (mListener!=null)mListener.onWifiOpened();
                    break;
                case WifiManager.WIFI_STATE_ENABLING://wifi正在打开
                    break;
                case WifiManager.WIFI_STATE_DISABLED://wifi已经关闭
                    if (mListener!=null)mListener.onWifiClosed();
                    break;
                case WifiManager.WIFI_STATE_DISABLING://wifi关闭中
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN://wifi未知状态
                    break;
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo!=null){
                NetworkInfo.State state = networkInfo.getState();
                switch (state){
                    case CONNECTED://wifi已经连接
                        WifiObserverManager.getInstance().notifyWifiConnected();

                        if (mListener!=null)mListener.onWifiConnected();

                        break;
                    case CONNECTING://wifi正在连接
                        break;
                    case DISCONNECTED://wifi已经断开连接
                        WifiObserverManager.getInstance().notifyWifiDisconnected();

                        if (mListener!=null)mListener.onWifiDisconnected();

                        break;
                    case UNKNOWN://wifi未知状态
                        break;
                    case SUSPENDED://wifi挂起
                        break;
                    case DISCONNECTING://wifi正在断开
                        break;
                }
            }

        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {//扫描完成
            LogTools.e(TAG, "action:" + action);
            if (mListener!=null)mListener.onWifiScaned();
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            LogTools.e(TAG, "action:" + action);
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info != null && info.isConnected()) {
                WifiObserverManager.getInstance().notifyWifiConnected();
                if (mListener!=null)mListener.onWifiConnected();
            } else {
                LogTools.p(TAG,"wifi not connected");
                WifiObserverManager.getInstance().notifyWifiDisconnected();
                if (mListener!=null)mListener.onWifiDisconnected();
            }
        }
    }
}

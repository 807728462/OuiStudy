package mo.singou.vehicle.ai.communicate.communicate.socket;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.text.TextUtils;

import com.maikel.logger.LogTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mo.singou.vehicle.ai.base.utils.IPUtils;
import mo.singou.vehicle.ai.base.utils.SharedPreferencesUtils;
import mo.singou.vehicle.ai.communicate.communicate.ICommunicateManager;
import mo.singou.vehicle.ai.communicate.socket.receiver.WifiReceVier;
import mo.singou.vehicle.ai.communicate.socket.udp.UDPManager;
import mo.singou.vehicle.ai.communicate.tools.MetadataTools;

public class BaseTcpManager implements ICommunicateManager, OnWifiStateListener, UDPManager.OnIPAddressListener {
    private static final String TAG = "BaseTcpManager";
    protected OnRemoteMessageListener mListener;
    protected Map<String, OnResponseListener> responseListenerMap = new HashMap<>();
    protected UDPManager mUdpManager;
    protected int mTcpType;
    protected int mPort = 6666;
    private WifiReceVier wifiReceVier;
    protected String mAck = "1";

    private WifiManager mWifiManager;
    private String destSsid;
    private String destPwd;
    private static final int STATE_NONE = 0;
    private static final int STATE_CLOSED = 1;
    private static final int STATE_CLOSING = 2;
    private static final int STATE_OPENED = 3;
    private static final int STATE_OPENING = 4;
    private static final int STATE_SCANING = 5;
    private static final int STATE_SCANED = 6;
    private static final int STATE_CONNECTED = 7;
    private static final int STATE_CONNECTING = 8;
    private static final int STATE_DISCONNECTED = 9;
    private static final int STATE_DISCONNECTING = 10;
    private int mWifiState = STATE_NONE;
    private HandlerThread mHandlerThread;
    protected Handler mTcpHandler;
    protected OnAddressListener mOnAddressListener;
    protected static final String EXTRA_IP = "extra.ip";
    protected Context mContext;

    public BaseTcpManager(Context context) {
        mContext = context.getApplicationContext();
        handleConstruct();
        mUdpManager = UDPManager.Instance.getInstance(mTcpType);
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        registWifiReceiver();
        mHandlerThread = new HandlerThread("tcp-connect");
        mHandlerThread.start();
        mTcpHandler = new Handler(mHandlerThread.getLooper());
        mUdpManager.setOnIpAddressListener(this);
    }


    private void registWifiReceiver() {
        if (wifiReceVier == null) {
            IntentFilter mIntentFilter = new IntentFilter();
            wifiReceVier = new WifiReceVier();
            mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            //mIntentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
            //mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
            //mIntentFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
            //mIntentFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            //mIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
            mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            mContext.registerReceiver(wifiReceVier, mIntentFilter);
            wifiReceVier.setOnWifiStateListener(this);
        }
    }

    protected void handleConstruct() {

    }

    private void openWifi() {
        if (mWifiManager == null) {
            LogTools.p(TAG, "mWifiManager is null ---->");
            return;
        }
        LogTools.p(TAG, "open Wifi state--->" + mWifiState);
        if (mWifiState != STATE_NONE) {
            return;
        }
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
            mWifiState = STATE_OPENING;
            LogTools.p(TAG, "start to open Wifi ---->");
        } else {
            mWifiState = STATE_OPENED;
            LogTools.p(TAG, " Wifi has opened");
            if (mWifiState == STATE_CONNECTED) {
                dealWifiConnected();
            } else {
                LogTools.p(TAG, "wifi is opened start scan");
                mWifiManager.startScan();
                mWifiState = STATE_CONNECTING;
            }
        }
    }

    private void dealWifiConnected() {
        if (mWifiState == STATE_CONNECTED) {
            LogTools.p(TAG, "has dealWifiConnected ");
            return;
        }
        if (isTargetWifi()) {
            SystemClock.sleep(1000);
            LogTools.p(TAG, "has connected target wifi");
            mWifiState = STATE_CONNECTED;
            if (TextUtils.isEmpty(mAck)) {
                startUdp(true);
            } else {
                String realIp = IPUtils.getIPAddress(mContext);
                String key = mAck + EXTRA_IP;
                String lastConnectIp = SharedPreferencesUtils.getString(mContext, key, "");
                if (TextUtils.isEmpty(lastConnectIp)) {
                    LogTools.p(TAG, "handleOnWifiConnected --->last ip not equals realIp realIp=" + realIp + ",lastConnectIp=" + lastConnectIp);
                    SharedPreferencesUtils.putString(mContext, key, realIp);
                    startUdp(false);
                    return;
                }
                if ((!lastConnectIp.equals(realIp))){
                    LogTools.p(TAG, "handleOnWifiConnected --->last ip not equals realIp realIp=" + realIp + ",lastConnectIp=" + lastConnectIp);
                    SharedPreferencesUtils.putString(mContext, key, realIp);
                    startUdp(true);
                    return;
                }
                String remoteIp = SharedPreferencesUtils.getString(mContext, mAck, "");
                int index = remoteIp.lastIndexOf('.');
                String ipStr = "";
                if (index != -1) {
                    ipStr = remoteIp.substring(0, index);
                } else {
                    SharedPreferencesUtils.putString(mContext, mAck, "");
                    startUdp(false);
                    return;
                }
                if (!TextUtils.isEmpty(remoteIp) && realIp.startsWith(ipStr)) {
                    startTcp(remoteIp);
                } else {
                    SharedPreferencesUtils.putString(mContext, mAck, "");
                    startUdp(true);
                }
            }
        } else {
            connectTargetWifi();
        }
    }

    protected void startTcp(String ip) {

    }

    protected void startUdp(boolean isDestroy) {

    }

    @Override
    public void init(String tag, String ack) {
        try {
            mPort = Integer.parseInt(tag);
        } catch (Exception e) {
            LogTools.p(TAG, e, "tag is not an number");
        }
        destSsid = MetadataTools.getApplicationMetaValue(mContext, "ssid");
        destPwd = MetadataTools.getApplicationMetaValue(mContext, "pwd");
        mAck = ack;
        openWifi();
    }

    protected void handleIp(String ip) {
    }

    @Override
    public void start() {
        if (destSsid == null || destPwd == null) {
            destSsid = MetadataTools.getApplicationMetaValue(mContext, "ssid");
            destPwd = MetadataTools.getApplicationMetaValue(mContext, "pwd");
        }
        handleStart();
    }

    @Override
    public void stop() {
        handleStop();
        unRegistWifiReceiver();
        if (mUdpManager != null)
            mUdpManager.destroy();
    }

    private void unRegistWifiReceiver() {
        if (wifiReceVier != null) {
            mContext.unregisterReceiver(wifiReceVier);
            wifiReceVier = null;
        }
    }

    protected void handleStart() {

    }

    protected void handleStop() {

    }

    @Override
    public void sendMessage(String serial, String message, OnResponseListener listener) {
        responseListenerMap.remove(serial);
        responseListenerMap.put(serial, listener);
        sendTo(serial, message);
    }

    @Override
    public void sendMessage(String message) {
        sendTo(String.valueOf(System.currentTimeMillis()), message);
    }

    @Override
    public void sendMessage(byte[] message) {
        sendTo(message);
    }

    protected void sendTo(String serial, String message) {

    }

    protected void sendTo(byte[] message) {

    }

    @Override
    public void setOnRemoteMessageListener(OnRemoteMessageListener listener) {
        mListener = listener;
    }

    @Override
    public void setOnAddressListener(OnAddressListener listener) {
        this.mOnAddressListener = listener;
    }


    @Override
    public void onWifiScaned() {
        LogTools.p(TAG, "wifi scan result mWifiState--->" + mWifiState);
        if (mWifiState == STATE_CONNECTED) {
            LogTools.p(TAG, "onWifiScaned wifi is connected return");
            return;
        }
        if (mWifiState == STATE_SCANING) {
            mWifiState = STATE_SCANED;
        }
        mWifiState = STATE_CONNECTING;
        connectTargetWifi();
    }

    @Override
    public void onWifiOpened() {
        LogTools.p(TAG, "wifi is opened mWifiState--->" + mWifiState);
        if (mWifiState == STATE_CONNECTED) {
            LogTools.p(TAG, "onWifiOpened wifi is connected return");
            return;
        }
        mWifiState = STATE_OPENED;
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.startScan();
            mWifiState = STATE_SCANING;
            LogTools.p(TAG, "wifi is opened start to scan");
        }
    }

    @Override

    public void onWifiClosed() {
        mWifiState = STATE_CLOSED;
        mUdpManager.setRun(false);
        handleOnWifiDisconnected();
    }

//    private boolean isConnected(){
//        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = connectivity.getActiveNetworkInfo();
//        return info!=null&&info.isConnected();
//    }

    @Override
    public void onWifiConnected() {
        LogTools.p(TAG, "wifi is connected  mWifiState---》" + mWifiState);
        if (mWifiState == STATE_CONNECTED) {
            LogTools.p(TAG, "onWifiOpened wifi is connected return");
            return;
        }
        dealWifiConnected();
    }

    /**
     * 目前连接的wifi是否是目标热点
     *
     * @return 是返回true，否返回false
     */
    private boolean isTargetWifi() {
        String connectSsid = getWIFISSID();
        if (connectSsid.equals("unknown_id")) {
            connectSsid = getWIFISSID(mContext);
        }
        LogTools.p(TAG, "connectSsid---->" + connectSsid + ",----->destSsid:" + destSsid);
        boolean isTargetWifi = connectSsid != null && connectSsid.equals("\"" + destSsid + "\"");
        if (!isTargetWifi) {
            LogTools.p(TAG, "is not target wifi delete wifi");
            mWifiManager.disconnect();
            //删除wifi
            WifiConfiguration tempConfig = isExsits(connectSsid);
            if (tempConfig != null) {
                mWifiManager.removeNetwork(tempConfig.networkId);
                mWifiManager.saveConfiguration();
            }
        }
        return isTargetWifi;
    }

    /**
     * 获取SSID
     *
     * @return WIFI 的SSID
     */
    private String getWIFISSID() {
        String ssid = "unknown_id";
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return ssid;
        }
        int netWorkId = wifiInfo.getNetworkId();
        List<WifiConfiguration> configurations = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration configuration : configurations) {
            if (netWorkId == configuration.networkId) {
                ssid = configuration.SSID;
                break;
            }
        }
        return ssid;
    }

    /**
     * 获取SSID
     *
     * @param context 上下文
     * @return WIFI 的SSID
     */
    public String getWIFISSID(Context context) {
        String ssid = "unknown_id";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O || Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            WifiInfo info = mWifiManager.getConnectionInfo();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return info.getSSID();
            } else {
                return info.getSSID().replace("\"", "");
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {
            ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connManager != null;
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo() != null) {
                    return networkInfo.getExtraInfo().replace("\"", "");
                }
            }
        }
        return ssid;
    }

    /**
     * 连接目标热点
     */
    private void connectTargetWifi() {
        //连接目标ssid
        LogTools.p(TAG, "target wifi is " + destSsid);
        WifiConfiguration wifiConfig = createWifiInfo(destSsid, destPwd, WifiCipherType.WIFICIPHER_WPA);
        int netID = mWifiManager.addNetwork(wifiConfig);
        boolean enabled = mWifiManager.enableNetwork(netID, true);
        //boolean connected = mWifiManager.reconnect();
        LogTools.p(TAG, "realConnectSSID enableNetwork=" + enabled);
        if (enabled) {
            mWifiManager.saveConfiguration();
        }
        //LogTools.p(TAG, "realConnectSSID reconnect=" + connected);
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password, WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // nopass
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        // wep
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // wpa
        if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();
        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }
        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }
        return true;
    }

    private WifiConfiguration isExsits(String ssid) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null && existingConfigs.size() > 0) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    @Override
    public void onWifiDisconnected() {
        if (mWifiState == STATE_DISCONNECTED) {
            LogTools.p(TAG, "has deal onWifiDisconnected event");
            return;
        }
        mWifiState = STATE_DISCONNECTED;
        mUdpManager.setRun(false);
        handleOnWifiDisconnected();
    }


    protected void handleOnWifiDisconnected() {

    }

    @Override
    public void onIPAddress(String ip) {
        LogTools.p(TAG, "收到ip：" + ip);
        if (!TextUtils.isEmpty(mAck)) {
            SharedPreferencesUtils.putString(mContext, mAck, ip);
        }
        if (mOnAddressListener != null) {
            mOnAddressListener.onAddress(ip);
        }
        handleIp(ip);
    }
}

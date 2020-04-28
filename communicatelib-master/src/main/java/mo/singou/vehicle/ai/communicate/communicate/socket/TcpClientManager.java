package mo.singou.vehicle.ai.communicate.communicate.socket;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.maikel.logger.LogTools;

import mo.singou.vehicle.ai.base.BaseApplication;
import mo.singou.vehicle.ai.base.utils.IPUtils;
import mo.singou.vehicle.ai.base.utils.SharedPreferencesUtils;
import mo.singou.vehicle.ai.communicate.socket.tcp.OnConnectTimeOutListener;
import mo.singou.vehicle.ai.communicate.socket.tcp.client.InteractManagerableProxy;
import mo.singou.vehicle.ai.communicate.socket.tcp.client.TCPInteract;
import mo.singou.vehicle.ai.communicate.socket.tcp.client.TCPInteractiveFactory;
import mo.singou.vehicle.ai.communicate.socket.udp.UDPManager;


public class TcpClientManager extends BaseTcpManager implements TCPInteract.RpcInteractListener, OnConnectTimeOutListener {
    private static final String TAG = TcpClientManager.class.getSimpleName();
    private InteractManagerableProxy interactManagerableProxy;

    public TcpClientManager(Context context) {
        super(context);
    }

    @Override
    protected void handleConstruct() {
        mTcpType = UDPManager.TYPE_CLIENT;
        interactManagerableProxy = TCPInteractiveFactory.getTcpManager().create();
        interactManagerableProxy.setRpcInteractListener(this);
        interactManagerableProxy.setOnConnectTimeOutListener(this);
    }

    @Override
    protected void handleIp(String ip) {
        if (!interactManagerableProxy.isConnected()) {
            LogTools.p(TAG, "start to connect server ip-->" + ip + ",mPort:" + mPort);
            interactManagerableProxy.initDataServer(ip, mPort);
        }else {
            LogTools.p(TAG, "already connected-->" + ip + ",mPort:" + mPort);
        }
    }

    @Override
    protected void handleStart() {
    }

    @Override
    protected void handleStop() {
        interactManagerableProxy.release();
    }


    @Override
    protected void sendTo(String serial, String message) {
        interactManagerableProxy.sendMessage(serial, message);
    }

    @Override
    protected void sendTo(byte[] message) {
        interactManagerableProxy.sendMessage(message);
    }

    @Override
    public void onRpcConnectedListener() {
        if (mListener != null) mListener.onConnected();
        if (mUdpManager.isRun()){
            mUdpManager.setRun(false);
        }
        LogTools.p(TAG,"连接成功断开udp --->");
    }

    @Override
    public void onRpcDisconnectListener() {
        if (mListener != null) mListener.onDisconnected();
    }

    @Override
    public void onRpcSendTimeOut(String serial) {
        onError(serial, "连接超时或者发送超时");
        startUdp(false);
    }

    @Override
    public void onError(String serial, String error) {
        OnResponseListener listener = responseListenerMap.get(serial);
        if (listener != null) {
            listener.onError(error);
            responseListenerMap.remove(serial);
        }
    }

    @Override
    public void onMessage(int eventId, String serial, String response) {
        OnResponseListener listener = responseListenerMap.get(serial);
        if (listener != null) {
            listener.onSuccess(response);
            responseListenerMap.remove(serial);
        } else {
            if (mListener != null) mListener.onMessage(eventId, serial, response);
        }
    }

    @Override
    protected void startTcp(String ip) {
        LogTools.p(TAG,"startTcp --->"+ip);
        mUdpManager.setRun(false);
        interactManagerableProxy.initDataServer(ip, mPort);
        if (mOnAddressListener != null) {
            mOnAddressListener.onAddress(ip);
        }
    }

    @Override
    protected void startUdp(boolean destroy) {
        if (!mUdpManager.isRun()) {
            LogTools.p(TAG, "handleOnWifiConnected：开启  udp");
            mUdpManager.start(mPort, mAck);
        } else {
            if (destroy) {
                LogTools.p(TAG, "handleOnWifiConnected：已经开启 先关闭--》  udp");
                mUdpManager.destroy();
                SystemClock.sleep(1000);
                mUdpManager.start(mPort, mAck);
            } else {
                LogTools.p(TAG, "handleOnWifiConnected： udp 已经开启");
            }
        }
        mUdpManager.setOnIpAddressListener(this);
    }

    @Override
    public void onTimeOut() {
        LogTools.p(TAG, "not connect to server start udp");
        startUdp(true);
    }
}

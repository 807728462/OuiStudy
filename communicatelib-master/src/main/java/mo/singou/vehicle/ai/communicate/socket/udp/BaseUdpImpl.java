package mo.singou.vehicle.ai.communicate.socket.udp;


import android.os.SystemClock;

import com.maikel.logger.LogTools;

import mo.singou.vehicle.ai.communicate.socket.constants.Constants;
import mo.singou.vehicle.ai.communicate.tools.ThreadPoolTools;

public abstract class BaseUdpImpl implements UDPManager, Runnable {
    protected OnIPAddressListener mListener;
    protected OnErrorListener mErrorListener;
    protected int mPort = Constants.LISTEN_PORT;
    protected boolean isRun = false;
    protected String ack;
    //protected byte[] data;
    protected Thread udpThread;
    protected BaseUdpImpl() {
    }

    //protected boolean recycle = true;

    @Override
    public synchronized void start(int port, String ack) {
        if (isRun) return;
        mPort = port;
        isRun = true;
        this.ack = ack;
        if (udpThread!=null){
            udpThread.interrupt();
            udpThread = null;
        }
        SystemClock.sleep(50);
        udpThread = new Thread(this);
        udpThread.start();
    }

//    @Override
//    public synchronized void start(int port, byte[] data) {
//        if (isRun) return;
//        mPort = port;
//        isRun = true;
//        this.data = data;
//        if (udpThread!=null){
//            udpThread.interrupt();
//            udpThread = null;
//        }
//        SystemClock.sleep(50);
//        udpThread = new Thread(this);
//        udpThread.start();
//    }

    @Override
    public void setOnIpAddressListener(OnIPAddressListener listener) {
        if (listener == null){
            LogTools.p("BaseUdpImpl","setOnIpAddressListener listener is null");
            return;
        }
        mListener = listener;
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        if (listener == null) {
            LogTools.p("BaseUdpImpl", "setOnErrorListener listener is null");
            return;
        }
        mErrorListener = listener;
    }


    @Override
    public synchronized boolean isRun() {
        return isRun;
    }

    @Override
    public synchronized void setRun(boolean run) {
        isRun = run;
        if (!isRun) {
            handleDisconnect();
        }
    }

    protected synchronized void handleDisconnect() {
        if (udpThread!=null){
            udpThread.interrupt();
            udpThread = null;
        }
    }

    @Override
    public synchronized void destroy() {
        if (udpThread!=null){
            udpThread.interrupt();
            udpThread = null;
        }
        SystemClock.sleep(50);
        mListener = null;
        isRun = false;
        closeSocket();
    }

    protected void closeSocket() {

    }

    public void setData(byte[] data) {
        //this.data = data;
    }
}

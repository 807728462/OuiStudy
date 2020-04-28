package mo.singou.vehicle.ai.communicate.socket.udp;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

import com.maikel.logger.LogTools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class ServiceImpl extends BaseUdpImpl {
    private static final String TAG = "zbq-test";
    DatagramSocket socket;
    private Handler mHandler;
    private HandlerThread ht;
    private boolean hasReceiverAckOk = false;

    @Override
    public void run() {
        if (ack == null) {
            LogTools.p(TAG, "ack为空");
            return;
        }
        try {
            socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(mPort));
            LogTools.p(TAG, "udp开始监听mPort=" + mPort + ",ack=" + ack + ",isRun-->" + isRun);
            byte[] buffer = new byte[64];
            DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
            String ackOk = ack + "1";
            String ackConnect = ack + "2";
            while (isRun && socket != null && !socket.isClosed() && udpThread != null && !udpThread.isInterrupted()) {
                try {
                    socket.receive(packet);
                    String receStr = new String(packet.getData(), 0, packet.getLength(), "utf-8");
                    String ip = packet.getAddress().getHostAddress();
                    LogTools.d(TAG, "接收到数据包：" + receStr + ",Ip:" + ip);
                    if (ack.equals(receStr)) {
                        byte[] bt = ackOk.getBytes();
                        packet.setData(bt);
                        socket.send(packet);
                        LogTools.d(TAG, "udp服务端发送确认包：" + ackOk);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (hasReceiverAckOk || !isRun) {
                                    mHandler.removeCallbacks(this);
                                    hasReceiverAckOk = false;
                                    LogTools.d(TAG, "已经接收到ackOk");
                                    return;
                                }
                                mHandler.postDelayed(this, 2000);
                                LogTools.d(TAG, "isrun:" + isRun);
                                try {
                                    if (socket != null)
                                        socket.send(packet);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 2000);
                    } else if (ackOk.equals(receStr)) {
                        byte[] bt = ackConnect.getBytes();
                        packet.setData(bt);
                        socket.send(packet);
                        hasReceiverAckOk = true;
                        if (mListener != null) {
                            mListener.onIPAddress(ip);
                        }
                        while (isRun && udpThread != null && !udpThread.isInterrupted()) {
                            LogTools.d(TAG, "继续发送ack连接报文 确保客户端收到");
                            socket.send(packet);
                            SystemClock.sleep(1000);
                        }
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogTools.p(TAG, e,"udp线程出现异常：" + e.toString());
                }
            }
            if (socket!=null){
                if (!socket.isClosed()){
                    socket.close();
                }
                socket = null;
            }
            LogTools.p(TAG,"udp线程结束-->");

        } catch (IOException e) {
            e.printStackTrace();
            LogTools.p(TAG, "udp线程出现异常：" + e.toString());
        }
        isRun = false;
    }

    ServiceImpl() {
        ht = new HandlerThread("serviceImpl");
        ht.start();
        mHandler = new Handler(ht.getLooper());
    }

    @Override
    protected synchronized void closeSocket() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        if (ht != null)
            ht.quit();
        if (socket != null && !socket.isClosed()) {
            socket.close();
            socket = null;
        }
    }
}

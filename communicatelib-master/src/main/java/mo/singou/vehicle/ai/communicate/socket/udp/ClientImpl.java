package mo.singou.vehicle.ai.communicate.socket.udp;


import android.os.SystemClock;

import com.maikel.logger.LogTools;
import com.maikel.logger.utils.IOUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ClientImpl extends BaseUdpImpl {
    private static final String TAG = "zbq-test";
    DatagramSocket datagramSocket = null;
    private volatile boolean hasReceiverData = false;

    ClientImpl() {
    }

    @Override
    public void run() {
        if (ack == null) {
            LogTools.p(TAG, "ack 为空");
            return;
        }

        try {
            datagramSocket = new DatagramSocket();
            datagramSocket.setReuseAddress(true);
            new Thread(new ReceiverRunnable(datagramSocket)).start();
            byte[] buf = ack.getBytes();
            InetAddress address = InetAddress.getByName("255.255.255.255");
            LogTools.d(TAG, "udp客户端开始监听port:" + mPort + ",ack:" + ack + ",isRun--->" + isRun);
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, address, mPort);
            byte[] bufOk = (ack + "1").getBytes();
            DatagramPacket datagramPacketOk = new DatagramPacket(bufOk, bufOk.length, address, mPort);
            int sendCount = 0;
            while (isRun && datagramSocket != null && !datagramSocket.isClosed()&&udpThread!=null&&!udpThread.isInterrupted()) {
                if (hasReceiverData){
                    if (sendCount>=3){
                        hasReceiverData = false;
                    }
                    try {
                        datagramSocket.send(datagramPacketOk);
                    } catch (IOException e) {
                        e.printStackTrace();
                        LogTools.p(TAG, e, "出错");
                    }
                    sendCount++;
                    LogTools.d(TAG, "发送" + (ack+"1"));
                }else {
                    try {
                        datagramSocket.send(datagramPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                        LogTools.p(TAG, e, "出错");
                    }
                    LogTools.d(TAG, "发送" + ack);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            LogTools.p(TAG, e, "出错");
        } catch (Exception e) {
            e.printStackTrace();
            LogTools.p(TAG, e, "出错");
        }

    }

    private class ReceiverRunnable implements Runnable {
        DatagramSocket datagramSocket = null;

        private ReceiverRunnable(DatagramSocket datagramSocket) {
            this.datagramSocket = datagramSocket;
        }

        @Override
        public void run() {
            byte[] receBuf = new byte[64];
            DatagramPacket recePacket = new DatagramPacket(receBuf, 0, receBuf.length);

            LogTools.p(TAG, "启动线程接收 --isRun:" + isRun);
            String ackOk = ack +"1";
            String ackConnect = ack +"2";
            while (isRun && datagramSocket != null && !datagramSocket.isClosed()&&udpThread!=null&&!udpThread.isInterrupted()) {
                try {
                    datagramSocket.receive(recePacket);
                    String receStr = new String(recePacket.getData(), 0, recePacket.getLength());
                    LogTools.d(TAG, "接收到数据-->" + receStr);
                    if (ackOk.equals(receStr)) {
                        hasReceiverData = true;
                        LogTools.p(TAG, "udp客户端确认");
                    }else if (ackConnect.equals(receStr)){
                        LogTools.p(TAG, "udp客户端连接");
                        isRun = false;
                        hasReceiverData = false;
                        SystemClock.sleep(3000);
                        String serverIp = recePacket.getAddress().getHostAddress();
                        if (mListener!=null){
                            mListener.onIPAddress(serverIp);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogTools.p(TAG, e, "出错--" + e.getMessage());
                }
            }
            IOUtils.closeQuietly(datagramSocket);
        }
    }

    @Override
    protected void closeSocket() {
        if (datagramSocket!=null&&!datagramSocket.isClosed()){
            datagramSocket.close();
            datagramSocket = null;
        }
    }
}

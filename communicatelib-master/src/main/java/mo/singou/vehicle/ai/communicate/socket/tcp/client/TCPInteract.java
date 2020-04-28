package mo.singou.vehicle.ai.communicate.socket.tcp.client;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

import com.maikel.logger.LogTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mo.singou.vehicle.ai.base.bean.Email;
import mo.singou.vehicle.ai.base.utils.JsonUtils;
import mo.singou.vehicle.ai.base.utils.StringUtlis;
import mo.singou.vehicle.ai.communicate.pack.PacketerFactory;
import mo.singou.vehicle.ai.communicate.parser.IByteParserManager;
import mo.singou.vehicle.ai.communicate.socket.bean.InteractMsg;
import mo.singou.vehicle.ai.communicate.socket.tcp.OnConnectTimeOutListener;
import mo.singou.vehicle.ai.communicate.tools.ByteUtils;
import mo.singou.vehicle.ai.communicate.tools.ThreadPoolTools;


public class TCPInteract implements IByteParserManager.OnDataListener {
    private static final String tag = TCPInteract.class.getSimpleName();
    private Map<String, String> messageMap = new HashMap<>();
    private Map<String, Runnable> timeOutMap = new HashMap<>();
    /**
     * 最大超时次数
     **/
    private static final int MAX_TIME_OUT_SEND_COUNT = 0x3;

    /**
     * 心跳频率
     **/
    public static final long HEART_BEAT_TIMES = 1000 * 30;
    /**
     * 消息发送超时时间
     **/
    private static final long rpcMsgTimeOutTime = 10 * 1000;
    /**
     * 处理心跳消息
     **/
    private static final int MSG_HEART_BEAT = 131;
    /**
     * 处理超时消息
     **/
    private static final int MSG_SEND_SERVER_TIME_OUT = 132;
    /**
     * 延迟处理连接
     **/
    private static final int MSG_SERVER_CONNECT_DELEY = 133;

    private RpcInteractStreamFetch rpcStreamFetch;

    private Socket mSocket;
    /**
     * 心跳空字符串
     **/
    private static final String HEART_BEAT_TEST = "{\"cmd\":1}";
    /**
     * 超时连接间隔时间
     **/
    private static final long CONNECT_TIME_OUT = 2000;
    /**
     * 心跳重置
     **/
    private long heart_beat_reset = 0;
    /**
     * socket状态
     **/
    private volatile int socketState = InteractManagerable.STATE_NONE;

    /**
     * 连接成功监听接口
     **/
    private RpcInteractListener rpcInteractListener;
    /**
     * 发送数据的超时次数
     **/
    private int timeOutCount = 0;
    /**
     * 套接字超时时间
     **/
    private static final int SOCKET_CONNECT_TIME_OUT = 3 * 1000;

    private static final int LIMIT_MAX_DATA_LEN = 1024*1024*100;//100M

    private PacketerFactory tcpDataPack;

    private int mPort;
    private String mAddress;
    private OnConnectTimeOutListener timeOutListener;
    private HandlerThread mT;
    private Handler mHandler;
    private int mConnectCount = 0;//连接次数
    private ConnectServerThread mConnectThread;
    TCPInteract() {
        tcpDataPack = PacketerFactory.getInstance();
        if (mT != null) {
            mT.quit();
            mT = null;
        }
        mT = new HandlerThread("tcp-->" + System.currentTimeMillis());
        mT.start();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mHandler = new Handler(mT.getLooper()); /*{
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_SEND_SERVER_TIME_OUT: {
                    }
                    break;
                    case MSG_SERVER_CONNECT_DELEY: {
                        createInteract();
                    }
                    break;
                }
            }
        };*/
    }

    void setRpcInteractListener(RpcInteractListener rpcInteractListener) {
        this.rpcInteractListener = rpcInteractListener;
    }

    void setOnTimeOutListener(OnConnectTimeOutListener listener) {
        timeOutListener = listener;
    }

    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            sendToTag("", HEART_BEAT_TEST);
            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_TIMES);
        }
    };


    /**
     * socket是否处于连接状态
     **/
    private boolean isConnecting() {
        return socketState != InteractManagerable.STATE_NONE
                && socketState != InteractManagerable.STATE_CONNECT_FAILER;
    }

    protected boolean isConnected() {
        return socketState == InteractManagerable.STATE_CONNECTED;
    }

    protected synchronized int getSocketState() {
        return socketState;
    }


    /**
     * reset...
     */
    private void reset() {
        closeSocket();
        rpcStreamFetch = null;
        socketState = InteractManagerable.STATE_NONE;
    }

    /**
     * socket重连
     */
    private synchronized void doReconnect() {
        if (socketState == InteractManagerable.STATE_FINISHED) {
            LogTools.p(tag, "重新连接----");
//            mHandler.removeMessages(MSG_SERVER_CONNECT_DELEY);
//            mHandler.sendEmptyMessageDelayed(MSG_SERVER_CONNECT_DELEY,
//                    CONNECT_TIME_OUT);
            socketState = InteractManagerable.STATE_PREPARING_CONNECT;
            createInteract();
            messageMap.clear();
        } else {
            LogTools.e(tag, "socket state not correct");
        }
    }

    /**
     * stop socket to read and write
     */
    private void releaseRpcThreads() {
        rpcStreamFetch = null;
        mHandler.removeMessages(MSG_HEART_BEAT);
        mHandler.removeMessages(MSG_SEND_SERVER_TIME_OUT);
    }

    /**
     * close socket
     */
    private synchronized void closeSocket() {
        if (null != mSocket) {
            try {
                mSocket.shutdownInput();
                mSocket.shutdownOutput();
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected synchronized void release() {
        releaseConnect();
        LogTools.e("synchronized", "释放socket");

    }

    /**
     * 释放socket占用资源
     */
    private synchronized void releaseConnect() {
        socketState = InteractManagerable.STATE_RELEASE;
        closeSocket();
        releaseRpcThreads();
        tcpDataPack = null;
        System.gc();
    }

    protected void netDisConnected() {
//		socketState = SocketState.STATE_DISCONNECTED;
//		releaseRpcThreads();
//		closeSocket();
    }

    @Override
    public void onDataResponse(String response) {
        if (StringUtlis.isEmpty(response)) {
            LogTools.p(tag, "请求内容为空！");
            return;
        }
        Email responseBase = JsonUtils.parse(response, Email.class);
        if (responseBase == null) {
            LogTools.p(tag, "ProcessEmail 对象为空!---->" + response);
            return;
        }
        String serial = responseBase.serial;
        removeTimeOutCallback(serial);
        if (rpcInteractListener != null)
            rpcInteractListener.onMessage(responseBase.eventId, serial, responseBase.data);
    }

    /**
     * 启动一个线程，该线程初始化socket，并启动一个新的线程用于读取从socket中读取的数据
     * 完成socket，启动读线程以后，该线程循环从队列中读取数据写入到socket中，
     */

    private class ConnectServerThread extends Thread {
        @Override
        public void run() {
            InetAddress addr;
            mConnectCount = 0;
            while (!isInterrupted()&&(socketState == InteractManagerable.STATE_CONNECTING||
                    socketState==InteractManagerable.STATE_CONNECT_FAILER)) {
                try {
                    socketState = InteractManagerable.STATE_CONNECTING;
                    addr = InetAddress.getByName(mAddress);
                    mSocket = new Socket();
                    mSocket.setReuseAddress(true);
                    mSocket.connect(new InetSocketAddress(addr, mPort),
                            SOCKET_CONNECT_TIME_OUT);
                    socketState = InteractManagerable.STATE_CONNECTED;
                    LogTools.p(tag, "socket connect success");

                    /** 开启一个线程，获取socket读流 */
                    rpcStreamFetch = new RpcInteractStreamFetch();
                    new Thread(rpcStreamFetch).start();
                    if (rpcInteractListener != null) {
                        rpcInteractListener.onRpcConnectedListener();
                    }
                    /** 通知心跳线程 登录成功 */
                    mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_TIMES);
                    sendOffLineMessage();
                    mConnectCount = 0;
                    return;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    LogTools.p(tag, e, "connect server thread--host is unknown,host:"
                            + mAddress + ",port:"
                            + mPort + ",error:" + e.getMessage());
                    onSendError(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    LogTools.p(tag,
                            e, "connect server thread--has occured IO exception:" + e.getMessage());
                    onSendError(e.getMessage());
                }catch (Exception e){
                  LogTools.p(tag,"mAddress:"+mAddress+",mPort:"+mPort+", error:"+e.getMessage());
                }
                LogTools.e(tag, "can't connect to server");
                socketState = InteractManagerable.STATE_CONNECT_FAILER;
                mConnectCount++;
                if (mConnectCount >= 3) {
                    mConnectCount = 0;
                    socketState = InteractManagerable.STATE_NONE;
                    releaseRpcThreads();
                    messageMap.clear();
                    timeOutMap.clear();
                    mConnectThread = null;
                    if (timeOutListener != null) timeOutListener.onTimeOut();
                    LogTools.p(tag, "没有连上服务端--->");
                    LogTools.e(tag, "连接3次 无法连接成功 address:"+mAddress+",port:"+mPort);
                    return;
                }
                SystemClock.sleep(CONNECT_TIME_OUT);
            }
            // mHandler.sendEmptyMessageDelayed(MSG_SERVER_CONNECT_DELEY, CONNECT_TIME_OUT);

        }
    }


    private void sendOffLineMessage() {
        if (!messageMap.isEmpty()) {
            Iterator iter = messageMap.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String val = messageMap.get(key);
                sendToServer(key, val);
            }
            messageMap.clear();
        }
    }

    private void onSendError(String error) {
        if (!timeOutMap.isEmpty()) {
            Iterator iter = timeOutMap.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                Runnable runnable = timeOutMap.get(key);
                mHandler.removeCallbacks(runnable);
            }
            timeOutMap.clear();
        }
        if (!messageMap.isEmpty()) {
            Iterator iter = messageMap.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                onSendFailer(key, error);
            }
            messageMap.clear();
        }
    }

    /**
     * try to connect again
     */
    private void tryToConnect() {
        closeSocket();
        try {
            Thread.sleep(CONNECT_TIME_OUT);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LogTools.p(tag, "--start to reconnect--");
        createInteract();
    }

    /**
     * init
     */
    protected synchronized void initDataServer(int port, String remoteAddress) {
        if (remoteAddress == null || remoteAddress.length() == 0) {
            LogTools.p("TCPInteract", "address is empty ");
            return;
        }
        mAddress = remoteAddress;
        mPort = port;
        if (socketState == InteractManagerable.STATE_CONNECTING){
            LogTools.p(tag,"socket is connecting");
            return;
        }
        mConnectCount = 0;
        socketState = InteractManagerable.STATE_NONE;
        mAddress = remoteAddress;
        mPort = port;
        init();
    }

    /**
     * init socket
     **/
    private void init() {
        if (socketState == InteractManagerable.STATE_NONE) {// first to connect socket
            socketState = InteractManagerable.STATE_PREPARING_CONNECT;
            startConnectServer();
        } else {
            LogTools.e(tag, "socket state not correct!!!!" + socketState);
            // reset();
        }
    }

    /**
     * resume to connect
     */
    protected void resumeConnect() {
        if (socketState == InteractManagerable.STATE_DISCONNECTED) {
            socketState = InteractManagerable.STATE_PREPARING_CONNECT;
            startConnectServer();
        } else {
            LogTools.e(tag, "socket state not correct!!!!" + socketState);
            // reset();
        }
    }

    /***
     * start to connect
     */
    private void startConnectServer() {
        if (socketState == InteractManagerable.STATE_PREPARING_CONNECT) {
            createInteract();
        } else {
            LogTools.e(tag, "socket state not correct!!!!" + socketState);
            // reset();
        }
    }

    /**
     * create socket interact
     */
    private void createInteract() {
        if (socketState == InteractManagerable.STATE_PREPARING_CONNECT) {
            socketState = InteractManagerable.STATE_CONNECTING;
            if (mConnectThread != null){
                mConnectThread.interrupt();
                mConnectThread = null;
            }
            SystemClock.sleep(50);
            mConnectThread = new ConnectServerThread();
            mConnectThread.start();
        } else {
            LogTools.e(tag, "socket state not correct!!!!" + socketState);
            // reset();
        }
    }

    void sendToServer(InteractMsg message) {

    }

    void sendToServer(byte[]message){
        ThreadPoolTools.execute(new Runnable() {
            @Override
            public void run() {
                OutputStream outputStream = null;
                if (mSocket != null&&socketState ==InteractManagerable.STATE_CONNECTED) {
                    try {
                        outputStream = mSocket.getOutputStream();
                        outputStream.write(message);
                        outputStream.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                        mHandler.removeCallbacks(heartBeatRunnable);
                        socketState = InteractManagerable.STATE_FINISHED;
                        releaseRpcThreads();
                        closeSocket();
                        doReconnect();
                    }
                }
            }
        });
    }
    void sendToServer(String serial, String message) {
        sendToTag(serial, message);
    }

    private void removeTimeOutCallback(String serial) {
        Runnable timeOutRunnable = timeOutMap.get(serial);
        mHandler.removeCallbacks(timeOutRunnable);
        timeOutMap.remove(serial);
    }

    /**
     * send a message
     *
     * @param message message body
     */
    private void sendToTag(final String serial, final String message) {
        ThreadPoolTools.execute(new Runnable() {
            @Override
            public void run() {
                OutputStream outputStream = null;
                if (mSocket != null&&socketState ==InteractManagerable.STATE_CONNECTED) {
                    byte[] data = tcpDataPack.packData(message);
                    try {
                        outputStream = mSocket.getOutputStream();
                        outputStream.write(data);
                        outputStream.flush();
                        onSendSuccess(serial);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onSendFailer(serial, e.getMessage());
                        mHandler.removeCallbacks(heartBeatRunnable);
                        socketState = InteractManagerable.STATE_FINISHED;
                        releaseRpcThreads();
                        closeSocket();
                        doReconnect();
                        removeTimeOutCallback(serial);
                    }
                } else {
                    messageMap.put(serial, message);
                    TimeOutRunnable timeOutRunnable = new TimeOutRunnable(serial);
                    mHandler.postDelayed(timeOutRunnable, rpcMsgTimeOutTime);
                    timeOutMap.put(serial, timeOutRunnable);
                }
            }
        });

    }

    private class RpcInteractStreamFetch implements Runnable {

        @Override
        public void run() {
            LogTools.e(tag, "开启读取线程--------");
            if (mSocket != null) {
//                ParserManagerFactory parserFactory = ParserManagerFactory.getInstance();
//                parserFactory.startParser();
//                parserFactory.setOnDataListener(TCPInteract.this);
//                byte[] receiverBuf = new byte[1024];
                InputStream inputStream = null;
                try {
                    inputStream = mSocket.getInputStream();
                    while (null != mSocket && !mSocket.isClosed()
                            && mSocket.isConnected() &&
                            socketState == InteractManagerable.STATE_CONNECTED) {
//                    try {
//                        inputStream = mSocket.getInputStream();
//                        int len = inputStream.read(receiverBuf);
//                        if (len == -1) {
//                            rpcFetchStart = false;
//                            break;
//                        }
//                        parserFactory.byteArrayParser(receiverBuf, len);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        closeSocket();
//                        rpcFetchStart = false;
//                        LogTools.e(tag, "send a heart beat to test ----");
//                    } catch (NullPointerException no) {
//                        closeSocket();
//                        rpcFetchStart = false;
//                    }
                        int len = inputStream.available();
                        if (len > 0) {
                            // 获取packet载荷长度
                            byte[] packageLenData = new byte[4];
                            inputStream.read(packageLenData, 0, 4);
                            int payloadLen = ByteUtils.byteArrayToInt(packageLenData);
                            if (payloadLen>LIMIT_MAX_DATA_LEN){
                                LogTools.p(tag,"is to length payloadLen:"+payloadLen+",packageLenData:"+ Arrays.toString(packageLenData));
                                doFinishThread();
                                return;
                            }
                            if (payloadLen > 0) {
                                byte[] packet = new byte[payloadLen];
                                System.arraycopy(packageLenData, 0, packet, 0, 4);
                                int leftLen = payloadLen - 4;
                                int readLen = inputStream.read(packet, 4, leftLen);
                                leftLen -= readLen;
                                int offset = readLen + 4;
                                // 一条长消息有可能分多次到达，所以要分多次读取
                                while (0 != leftLen) {
                                    readLen = inputStream.read(packet, offset, leftLen);
                                    leftLen -= readLen;
                                    offset += readLen;
                                }
                                processPacket(packet, packet.length);
                            }
                        } else {
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                parserFactory.stopParser();
            }
            LogTools.e(tag, "完成读取线程-----");
            doFinishThread();
        }

        private void doFinishThread() {
            closeSocket();
            releaseRpcThreads();
            workRestart();
        }
    }

    /**
     * 解析函数
     *
     * @param buf
     * @param bufLen
     */
    private void processPacket(byte[] buf, int bufLen) {
//        byte crc = ByteUtils.getCRCByte(buf, (bufLen - 1));
//        if (crc != buf[bufLen - 1]) {
//            LogTools.e(tag, "CRC校验失败！判断为非法数据！");
//            return;
//        }
        String paserStr = new String(buf, 4, bufLen - 5, StandardCharsets.UTF_8);
        if (StringUtlis.isEmpty(paserStr)) {
            LogTools.p(tag, "请求内容为空！");
            return;
        }
        Email responseBase = JsonUtils.parse(paserStr, Email.class);
        if (responseBase == null) {
            LogTools.p(tag, "ProcessEmail 对象为空!---->" + paserStr);
            if (rpcInteractListener != null)
                rpcInteractListener.onMessage(0, null, paserStr);
            return;
        }
        String serial = responseBase.serial;
        removeTimeOutCallback(serial);
        if (rpcInteractListener != null)
            rpcInteractListener.onMessage(responseBase.eventId, serial, responseBase.data);
    }

    private synchronized void workRestart() {
        if (socketState == InteractManagerable.STATE_CONNECTED) {
            if (rpcInteractListener != null)
                rpcInteractListener.onRpcDisconnectListener();
            LogTools.p(tag, "workRestart-------");
            mHandler.removeCallbacks(heartBeatRunnable);
            socketState = InteractManagerable.STATE_FINISHED;
            doReconnect();
        }
    }

    /**
     * 发送成功
     */
    private void onSendSuccess(String seria) {

    }

    /**
     * 重发机制
     */
    private void onSendFailer(String serial, String error) {
        if (rpcInteractListener != null) {
            rpcInteractListener.onError(serial, error);
        }
    }

    /**
     * RPC 服务监听
     *
     * @author
     */
    public interface RpcInteractListener {

        void onRpcConnectedListener();

        void onRpcDisconnectListener();

        void onRpcSendTimeOut(String serial);

        void onError(String serial, String error);

        void onMessage(int eventId, String serial, String response);
    }

    class TimeOutRunnable implements Runnable {
        private String serial;

        TimeOutRunnable(String serial) {
            this.serial = serial;
        }

        @Override
        public void run() {
            messageMap.remove(serial);
            if (rpcInteractListener != null&&socketState !=InteractManagerable.STATE_CONNECTED) {
                LogTools.p(tag,"TimeOutRunnable");
                rpcInteractListener.onRpcSendTimeOut(serial);
            }
            timeOutMap.remove(serial);
        }
    }

}

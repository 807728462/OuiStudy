package mo.singou.vehicle.ai.communicate.socket.tcp.service;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

import com.maikel.logger.LogTools;
import com.maikel.logger.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import mo.singou.vehicle.ai.base.utils.StringUtlis;
import mo.singou.vehicle.ai.communicate.pack.PacketerFactory;
import mo.singou.vehicle.ai.communicate.parser.IByteParserManager;
import mo.singou.vehicle.ai.communicate.socket.constants.Constants;
import mo.singou.vehicle.ai.communicate.socket.tcp.OnConnectTimeOutListener;
import mo.singou.vehicle.ai.communicate.tools.ByteUtils;
import mo.singou.vehicle.ai.communicate.tools.ThreadPoolTools;


public class SocketManagerImpl implements Runnable, SocketManager, IByteParserManager.OnDataListener {
    private int port;
    private static final String TAG = "zbq-test";
    private OnSocketListener mListener = null;
    private final List<String> sendBuffList = new ArrayList<>();
    private boolean isRun;
    private Socket mSocket;
    private PacketerFactory packeterFactory;
    private ServerSocket serverSocket;
    private OnConnectTimeOutListener timeOutListener;
    private Handler mHandler;
    private HandlerThread ht;
    private static final long HEART_CHECK_TIME = 60*1000;
    private static final String HEART_BEAT_TEST = "{\"cmd\":1}";
    private Thread connectThread;
    private Thread readThread;
    protected SocketManagerImpl() {
        packeterFactory = PacketerFactory.getInstance();
        ht = new HandlerThread(String.valueOf(System.currentTimeMillis()));
        ht.start();
        mHandler = new Handler(ht.getLooper());
    }

    private Runnable connectTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(this);
            if (mSocket == null||mSocket.isClosed()){
                close();
                if (timeOutListener != null) timeOutListener.onTimeOut();
                LogTools.p(TAG, "没有连上客户端--->"+port);
            }

        }
    };

    @Override
    public void sendMessage(final String msg) {
        sendMessage(msg,"");
    }

    @Override
    public void sendMessage(String msg, String serial) {
        if (mSocket == null||mSocket.isClosed()) {
            if (mListener != null) mListener.onSendFailure(serial,"客户端未连接"+port);
            return;
        }
        ThreadPoolTools.execute(new Runnable() {
            @Override
            public void run() {
                if (mSocket != null) {
                    byte[] data = packeterFactory.packData(msg);
                    OutputStream outputStream = null;
                    if (mSocket != null && !mSocket.isClosed()) {
                        try {
                            outputStream = mSocket.getOutputStream();
                            outputStream.write(data);
                            outputStream.flush();

                        } catch (IOException e) {
                            e.printStackTrace();
                            close();
                            if (mListener != null) mListener.onSendFailure(serial,"客户端已断开"+port);
                        }
                    }

                }
            }
        });
    }

    @Override
    public void sendMessage(byte[] message) {
        if (mSocket == null||mSocket.isClosed()) {
            if (mListener != null) mListener.onSendFailure("","客户端未连接");
            return;
        }
        ThreadPoolTools.execute(new Runnable() {
            @Override
            public void run() {
                if (mSocket != null&& !mSocket.isClosed()) {
                    OutputStream outputStream = null;
                        try {
                            outputStream = mSocket.getOutputStream();
                            outputStream.write(message);
                            outputStream.flush();

                        } catch (IOException e) {
                            e.printStackTrace();
                            close();
                            if (mListener != null) mListener.onSendFailure("","客户端已断开");
                        }
                }
            }
        });
    }

    @Override
    public void setOnSocketListener(OnSocketListener listener) {
        mListener = listener;
    }

    @Override
    public void setOnConnectTimeOutListener(OnConnectTimeOutListener listener) {
        timeOutListener = listener;
    }

    @Override
    public void startService(int port) {
        isRun = true;
        this.port = port;
        LogTools.p(TAG, "port-->" + port);
        if (connectThread!=null){
            connectThread.interrupt();
            connectThread = null;
        }
        SystemClock.sleep(50);
        connectThread = new Thread(this);
        connectThread.start();
        mHandler.removeCallbacks(connectTimeOutRunnable);
        mHandler.postDelayed(connectTimeOutRunnable, Constants.TCP_CONNECT_TIME_OUT);
    }

    @Override
    public void close() {
//        synchronized (sendBuffList) {
//            sendBuffList.clear();
//            sendBuffList.notify();
//        }
        isRun = false;
        if (readThread!=null){
            readThread.interrupt();
        }
        SystemClock.sleep(50);
        if (connectThread!=null){
            connectThread.interrupt();
        }
        SystemClock.sleep(50);
        closeSocket();
    }

    @Override
    public boolean isServiceRun() {
        return isRun;
    }

    @Override
    public void setServiceRun(boolean run) {
        isRun = run;
        if (!isRun) {
            closeSocket();
        }
    }

    @Override
    public boolean hasClient() {
        return mSocket!=null&&!mSocket.isClosed();
    }

    private void closeSocket() {
        if (mSocket != null && !mSocket.isClosed()) {
            IOUtils.closeQuietly(mSocket);
            mSocket = null;
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            IOUtils.closeQuietly(serverSocket);
            serverSocket = null;
        }
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            LogTools.p(TAG, "开启socket 接收 线程 isRun->" + isRun);
            if (mListener != null) mListener.onStateMessage("服务已经开启", CODE_SERVER_START);
            while (isRun&&connectThread!=null&&!connectThread.isInterrupted()) {
                Socket socket = serverSocket.accept();
                mSocket = socket;
                mSocket.setKeepAlive(true);
                if (readThread!=null){
                    readThread.interrupt();
                    readThread = null;
                }
                SystemClock.sleep(50);
                readThread = new Thread(new ReadRunnable(socket));
                readThread.start();
                mHandler.removeCallbacks(connectTimeOutRunnable);
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                IOUtils.closeQuietly(serverSocket);
            }
            LogTools.p(TAG, "线程结束----》"+port);
        } catch (IOException e) {
            LogTools.p(TAG, e, "线程结束----》" + e.getMessage()+",port:"+port);
            if (mListener != null) mListener.onStateMessage(e.getMessage(), CODE_SERVER_ERROR);
            isRun = false;
        }
    }

    @Override
    public void onDataResponse(String response) {
        if (mListener != null) mListener.onClientMessage(response);
    }


    private Runnable heartBeatCheck = new Runnable() {
        @Override
        public void run() {
            LogTools.p(TAG,"没有接收到心跳包-客户端可能断开了连接");
            close();
            startService(port);
        }
    };

    class ReadRunnable implements Runnable {
        private Socket mSocket;

        private ReadRunnable(Socket socket) {
            mSocket = socket;
            mHandler.postDelayed(heartBeatCheck,HEART_CHECK_TIME);
        }

        @Override
        public void run() {
            if (mSocket != null) {
//                byte[] buffer = new byte[1024];
                InputStream inputStream = null;
                if (mListener != null) mListener.onStateMessage("connected",CODE_CONNECTED);
//                ParserManagerFactory parserFactory = ParserManagerFactory.getInstance();
//                parserFactory.setOnDataListener(SocketManagerImpl.this);
//                parserFactory.startParser();
//                while (isRun && !mSocket.isClosed()) {
//                    try {
//                        inputStream = mSocket.getInputStream();
//                        int len = inputStream.read(buffer);
//                        if (len == -1) {
//                            break;
//                        }
//                        parserFactory.byteArrayParser(buffer, len);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
                try {
                    inputStream = mSocket.getInputStream();
                    while (null != mSocket && !mSocket.isClosed()
                            && mSocket.isConnected() && isRun&&readThread!=null&&!readThread.isInterrupted()) {
                        int len = inputStream.available();
                        if (len > 0) {
                            // 获取packet载荷长度
                            byte[] packageLenData = new byte[4];
                            inputStream.read(packageLenData, 0, 4);
                            int payloadLen = ByteUtils.byteArrayToInt(packageLenData);
                            if (payloadLen > 0) {
                                byte[] packet = new byte[payloadLen];
                                System.arraycopy(packageLenData, 0, packet, 0, 4);
                                int leftLen = payloadLen - 4;
                                int readLen = inputStream.read(packet, 4, leftLen);
                                leftLen -= readLen;
                                int offset = readLen + 4;
                                // 一条长消息有可能分多次到达，所以要分多次读取
                                while (0 != leftLen) {
                                    try {
                                        Thread.sleep(20);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                parserFactory.stopParser();
                IOUtils.closeQuietly(inputStream);
                if (mSocket != null && !mSocket.isClosed()) {
                    IOUtils.closeQuietly(mSocket);
                    mSocket = null;
                }
            }
            if (mListener != null) mListener.onStateMessage("disconnected", CODE_DISCONNECTED);
            LogTools.p(TAG, "读取线程结束---》");
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
//            LogTools.e(TAG, "CRC校验失败！判断为非法数据！buf[bufLen - 1]=" + buf[bufLen - 1] + ",crc->" + crc);
//            return;
//        }
        String paserStr = new String(buf, 4, bufLen - 5, StandardCharsets.UTF_8);
        if (StringUtlis.isEmpty(paserStr)) {
            LogTools.p(TAG, "请求内容为空！");
            return;
        }
        if (HEART_BEAT_TEST.equals(paserStr)) {
            LogTools.d(TAG,"心跳包");
            mHandler.removeCallbacks(heartBeatCheck);
            mHandler.postDelayed(heartBeatCheck,HEART_CHECK_TIME);
            return;
        }
        if (mListener != null) mListener.onClientMessage(paserStr);
    }

    class WriteRunnable implements Runnable {

        private Socket mSocket;

        private WriteRunnable(Socket socket) {
            mSocket = socket;
        }

        @Override
        public void run() {
            if (mSocket != null) {
                byte[] buffer = null;
                OutputStream outputStream = null;
                PacketerFactory packeterFactory = PacketerFactory.getInstance();
                while (isRun && !mSocket.isClosed()) {
                    try {
                        outputStream = mSocket.getOutputStream();
                        synchronized (sendBuffList) {
                            if (sendBuffList.isEmpty() && isRun) {
                                sendBuffList.wait();
                            }
                            if (!sendBuffList.isEmpty()) {
                                buffer = packeterFactory.packData(sendBuffList.remove(0));
                            }
                        }
                        if (buffer != null) {
                            outputStream.write(buffer, 0, buffer.length);
                            outputStream.flush();
                            buffer = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (mListener != null) {
                            mListener.onSendFailure("",e.getMessage());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (!mSocket.isClosed())
                        mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

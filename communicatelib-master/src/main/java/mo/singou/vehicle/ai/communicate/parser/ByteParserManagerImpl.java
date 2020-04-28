package mo.singou.vehicle.ai.communicate.parser;


import com.maikel.logger.LogTools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import mo.singou.vehicle.ai.communicate.pack.PacketManager;
import mo.singou.vehicle.ai.communicate.tools.ByteUtils;
import mo.singou.vehicle.ai.communicate.tools.ThreadPoolTools;


public class ByteParserManagerImpl implements IByteParserManager {
    protected ByteParserManagerImpl() {
        rx_slip_state = Rx_Slip_State.rxstate_nosync;
    }

    private static final String tag = ByteParserManagerImpl.class
            .getSimpleName();
    /**
     * 数据字节长度定义
     **/
    private static final int PACKAT_LEN = 0x4;
//    /**
//     * 循环数组总长度
//     **/
//    private static final int RXMSG_MAX_PAYLOAD_LEN = 1024 * 8;
//    /**
//     * 循环数组
//     **/
//    private byte rx_bcsp[] = new byte[RXMSG_MAX_PAYLOAD_LEN];
//    /**
//     * 消息体（不包含消息头）
//     **/
//    private byte[] rx_real_body = new byte[RXMSG_MAX_PAYLOAD_LEN];
    /**
     * 当前存储位置
     **/
    private int rx_pos;
    /**
     * 消息长度字节数组
     **/
    private byte rx_head_buf[] = new byte[PACKAT_LEN];
    /**
     * 消息长度字节存储位置
     **/
    private volatile int head_pos;
    /**
     * 完整的一包数据（消息头与消息体）
     **/
    private volatile byte[] rxslip_buf ;
    // private boolean rx_msg_payload_null_flag = true;
    /**
     * 解析状态
     **/
    private volatile Rx_Slip_State rx_slip_state = Rx_Slip_State.rxstate_nosync;
    /**
     * 消息长度
     **/
    private volatile int pkt_length = 0;

    private OnDataListener mListener;

    private boolean isParsering = false;

    /**
     * 2个字节转为一个短整数
     *
     * @param buf
     * @return
     */
    private short pkt_len(byte[] buf) {

        return (short) (((buf[0] & 0xff) << 8) | (buf[1] & 0xff));
    }

//    /**
//     * 4个字节转为一个整数
//     *
//     * @param res
//     * @return
//     */
//    public static int ByteArrayToInt(byte[] res) {
//        return (res[3] & 0xff) | ((res[2] << 8) & 0xff00) // | 表示安位或
//                | ((res[1] << 24) >>> 8) | (res[0] << 24);
//    }

    @Deprecated
    private int pkt_len_int(byte[] buf) {
        int high = buf[1] >= 0 ? buf[1] : 256 + buf[1];
        int low = buf[0] >= 0 ? buf[0] : 256 + buf[0];
        return (low | (high << 8));
    }

    /**
     * 停止解析时释放
     */
    private synchronized void release() {
        rx_slip_state = Rx_Slip_State.rxstate_body_esc;
        rx_pos = 0;
        rx_head_buf = null;
        head_pos = 0;
        rxslip_buf = null;
        pkt_length = 0;
    }

    /**
     * 开始解析
     *
     * @param c
     */
    private synchronized boolean server_recv_slip_byte(byte c) {
        switch (rx_slip_state) {
            case rxstate_body_esc:
                return true;
            case rxstate_nosync:/* 获取消息字节长度 */
                isParsering = true;
                if (head_pos == PACKAT_LEN) {
                    pkt_length = ByteUtils.byteArrayToInt(rx_head_buf.clone());
                    rx_slip_state = Rx_Slip_State.rxstate_start;
                    head_pos = 0;
                    //LogTools.i(tag, "数据长度已经足够转到body");
                } else {
                    rx_head_buf[head_pos++] = c;
                    //rx_pos++;
                    return false;
                }
            case rxstate_start:/* 截取到消息长度后为一包数据即为消息体 */
                if (pkt_length == 0) {
                    LogTools.e(tag, "长度为0-----");
                    error_code();
                    return true;
                }
                if (rxslip_buf!=null){
                    rxslip_buf = null;
                }
                LogTools.d(tag, "总长度为:" + pkt_length);
                rxslip_buf = new byte[pkt_length];
                System.arraycopy(rx_head_buf, 0, rxslip_buf, 0, rx_head_buf.length);
                rx_pos = rx_head_buf.length;
                rx_slip_state = Rx_Slip_State.rxstate_body;
            case rxstate_body:/* 消息体 */
                if (rx_pos<pkt_length){
                    rxslip_buf[rx_pos++] = c;// 循环放入buffer里面
                }
                if (rx_pos == pkt_length) {
                    int len = pkt_length;
                    parserByte(rxslip_buf.clone(), len);
                    rx_pos = 0;
                    pkt_length = 0;
                    Arrays.fill(rx_head_buf,(byte) 0);
                    isParsering = false;
                    rx_slip_state = Rx_Slip_State.rxstate_nosync;
                }
                return false;
            default:
                return true;
        }
    }

    private void error_code() {
        rx_slip_state = Rx_Slip_State.rxstate_nosync;
        rx_pos = 0;
        head_pos = 0;
        pkt_length = 0;
        rxslip_buf = null;
        isParsering = false;
        // rx_msg_payload_null_flag = true;
    }

    @Override
    public void startParser() {
        if (rx_head_buf == null){
            rx_head_buf = new byte[PACKAT_LEN];
        }
        rx_slip_state = Rx_Slip_State.rxstate_nosync;
        rx_pos = 0;
        head_pos = 0;
        pkt_length = 0;
    }

    /**
     * 一个字节一个字节的解析
     */
    @Override
    public synchronized void byteArrayParser(byte[] parserBuf, int bufLen){
        if (parserBuf == null || bufLen <= 0) {
            error_code();
            return;
        }
        for (int i = 0; i < bufLen; i++) {
            if (server_recv_slip_byte(parserBuf[i])) {
                LogTools.e(tag, "data error ！！！！");
                break;
            }
        }
    }

    @Override
    public void byteArrayParser(final byte[] parserBuf) throws IOException {
        if (parserBuf == null || parserBuf.length == 0) {
            throw new IOException("data error!!!");
        }
//        ThreadPoolTools.execute(new Runnable() {
//            @Override
//            public void run() {
//                int bufLen = parserBuf.length;
//                byte crc = PacketManager.getCRCByte(parserBuf, (bufLen - 1));
//                System.arraycopy(parserBuf, 0, rx_head_buf, 0, PACKAT_LEN);
//                int pkt_length = ByteArrayToInt(rx_head_buf);
//                //LogTools.p(tag, "解析的数据长度："+pkt_length+",传过来的数据长度："+bufLen);
//                if (crc != parserBuf[bufLen - 1]) {
//                    LogTools.p(tag, "CRC校验失败！判断为非法数据！");
//                    error_code();
//                    return;
//                }
//                String paserStr = new String(parserBuf, 4, bufLen-5, StandardCharsets.UTF_8);
//                //LogTools.p(tag,"接收的数据为："+paserStr);
//                if (mListener != null) mListener.onDataResponse(paserStr);
//            }
//        });

    }

    @Override
    public void setOnDataListener(OnDataListener dataListener) {
        mListener = dataListener;
    }

    /**
     * 网络断开需停止解析释放内存
     */
    @Override
    public synchronized void stopParser() {
        release();
        LogTools.i(tag, "停止解析----");
    }

    @Override
    public synchronized Rx_Slip_State getSlipState() {
        return rx_slip_state;
    }

    /**
     * 解析函数
     *
     * @param buf
     * @param bufLen
     */
    private void parserByte(byte[] buf, int bufLen) {
//        byte crc = ByteUtils.getCRCByte(buf, (bufLen - 1));
//        if (crc != buf[bufLen - 1]) {
//            LogTools.e(tag, "CRC校验失败！判断为非法数据！");
//            // interactProxy.writeLog(TimeUtils.getDateFromLong(
//            // TimeUtils.MINUS_YYMMDDHHMMSS, System.currentTimeMillis())
//            // + "CRC check false！" + new String(buf, 0, bufLen));
//            error_code();
//            return;
//        }
        String paserStr = new String(buf, PACKAT_LEN, bufLen-5, StandardCharsets.UTF_8);
        if (mListener != null) mListener.onDataResponse(paserStr);
    }
}

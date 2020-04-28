package mo.singou.vehicle.ai.communicate.pack;



import com.maikel.logger.LogTools;

import java.nio.charset.StandardCharsets;

import mo.singou.vehicle.ai.communicate.socket.bean.InteractMsg;
import mo.singou.vehicle.ai.communicate.socket.bean.InteractMsgHeader;
import mo.singou.vehicle.ai.communicate.socket.constants.Constants;
import mo.singou.vehicle.ai.communicate.tools.ByteUtils;


public class PacketManager implements IPacketManager {
    PacketManager() {
    }

    /**
     * packet InteractMsg to be a byte array
     *
     * @param interactMsg a message that to be packet
     * @return byte array
     */
    @Override
    public byte[] packet_data(InteractMsg interactMsg) {
        // TODO Auto-generated method stub
        if (interactMsg == null) {
            return null;
        }
        byte[] dataPacket = null;
        switch (interactMsg.getMsgType()) {
            case PACK_BYTEARRAY:
                dataPacket = packet_data((byte[]) interactMsg.getBody(),
                        (byte[]) interactMsg.getHead());
                break;
            case PACK_BYTEARRAY_BODY:
                dataPacket = packet_data((byte[]) interactMsg.getBody(),
                        (InteractMsgHeader) interactMsg.getHead());
                break;
            case PACK_STRING_BODY:
                dataPacket = packet_data((String) interactMsg.getBody(),
                        (InteractMsgHeader) interactMsg.getHead());
                break;

            default:
                // dataPacket = packet_data((String) interactMsg.getBody(),
                // (InteractMsgHeader) interactMsg.getHead());
                throw new IllegalArgumentException(
                        "no such InteractMsg type,please chekc it!!!");
        }
        return dataPacket;
    }

    /**
     * packet a message that message's body is string message's head is
     * InteractMsgHeader
     *
     * @param body message body
     * @return a byte array
     */
    @Override
    public byte[] packet_data(String body) {
        byte[] message;
//        String msg = body.replaceAll("\\\\", "");
        String tempMsg = body;
        message = tempMsg.getBytes(StandardCharsets.UTF_8);
        /**
         * 打包消息长度为包体消息长度加4（body长度）+1（校验位）
         *
         * **/
        byte[] packet_data = new byte[(message.length + 5)];
        /** message length with 4 byte array is real length add one CRC byte **/
        byte[] pk_len_head = ByteUtils.intToByteArray(packet_data.length);
        System.arraycopy(pk_len_head, 0, packet_data, 0, pk_len_head.length);
        System.arraycopy(message, 0, packet_data, pk_len_head.length,
                message.length);
        /** the last byte is CRC **/
        packet_data[packet_data.length - 1] = ByteUtils.getCRCByte(packet_data,
                packet_data.length - 1);
        return packet_data;

    }

    private static final StringBuffer HEAD_BUFF = new StringBuffer();
    private static final String HEAD_STRING = "\"header\":\"";

    /**
     * a short with 2 byte
     *
     * @param len a integer that in(-32768~32767)
     * @return 2 length byte array
     */
    @Deprecated
    public byte[] getPacketLenByte(short len) {
        byte[] head = new byte[2];
        head[1] = (byte) (len & 0xff);
        head[0] = (byte) ((len >> 8) & 0xff);
        return head;
    }

//    /**
//     * a integer with 2 byte
//     *
//     * @param len a integer
//     * @return 2 length byte array
//     */
//    public byte[] getPacketLenByte(int len) {
//        byte[] head = new byte[2];
//        head[1] = (byte) (len & 0xff);
//        head[0] = (byte) ((len >> 8) & 0xff);
//        return head;
//    }

//    /**
//     * get CRC byte from buffer
//     *
//     * @param buf byte array buffer
//     * @param n   the length of buffer
//     * @return a byte
//     */
//    public static byte getCRCByte(byte[] buf, int n) {
//        byte crc = buf[0];
//        for (int i = 1; i < n; i++) {
//            crc ^= buf[i];
//        }
//        return crc;
//
//        // if (n == 0) {
//        // return buf[0];
//        // } else {
//        // return (byte) (buf[n] ^ getCRCByte(buf, n - 1));
//        // }
//
//    }

//    /**
//     * a integer with 4 byte
//     *
//     * @param i integer
//     * @return 4 length byte array
//     */
//    public byte[] intToByteArray(int i) {
//        byte[] targets = new byte[4];
//        targets[3] = (byte) (i & 0xFF);
//        targets[2] = (byte) (i >> 8 & 0xFF);
//        targets[1] = (byte) (i >> 16 & 0xFF);
//        targets[0] = (byte) (i >> 24 & 0xFF);
//        return targets;
//    }

    /**
     * to packet a message head
     *
     * @param head a object array
     */
    private void appendHead(Object... head) {
        HEAD_BUFF.append(HEAD_STRING);
        int lastIndex = head.length - 1;
        for (int k = 0; k < lastIndex; k++) {
            HEAD_BUFF.append(head[k]).append(",");
        }
        HEAD_BUFF.append(head[lastIndex]);
        HEAD_BUFF.append("\"");
        HEAD_BUFF.append(Constants.CMD_WORD.TO_SERVER_HEAD_CMD);
    }

    /**
     * packet a message that message's body is string message's head is
     * InteractMsgHeader
     *
     * @param body message body
     * @param head message head
     * @return a byte array
     */
    private byte[] packet_data(String body, InteractMsgHeader head) {
        /** append message head **/
        appendHead(head.getCmd(), head.getTerminalId(), head.getToken(),
                head.getVersion(), head.getSerialNum(), head.getKey());
        HEAD_BUFF.append(body).append("\"");
        byte[] message;
        message = HEAD_BUFF.toString().getBytes();
        /**
         * message length is real length add message length with 4 byte array
         * add one CRC byte so total length is real length add 5
         * **/
        byte[] packet_data = new byte[(message.length + 5)];
        /** message length with 4 byte array is real length add one CRC byte **/
        byte[] pk_len_head = ByteUtils.intToByteArray((message.length + 1));
        System.arraycopy(pk_len_head, 0, packet_data, 0, pk_len_head.length);
        System.arraycopy(message, 0, packet_data, pk_len_head.length,
                message.length);
        /** the last byte is CRC **/
        packet_data[packet_data.length - 1] = ByteUtils.getCRCByte(packet_data,
                packet_data.length - 1);
        HEAD_BUFF.setLength(0);
        return packet_data;

    }

    /**
     * packet a message that message's body is byte array message's head is
     * InteractMsgHeader
     *
     * @param body message body byte array
     * @param head message head
     * @return a byte array
     */
    private byte[] packet_data(byte[] body, InteractMsgHeader head) {
        appendHead(head.getCmd(), head.getTerminalId(), head.getToken(),
                head.getVersion(), head.getSerialNum(), head.getKey());
        byte[] headByte = HEAD_BUFF.toString().getBytes();
        int bodyLen = headByte.length + body.length + 1;
        byte[] pk_len_head = ByteUtils.getPacketLenByte(bodyLen);
        bodyLen += 2;
        byte[] packet_data = new byte[bodyLen];

        System.arraycopy(pk_len_head, 0, packet_data, 0, 2);
        System.arraycopy(headByte, 0, packet_data, 2, headByte.length);
        System.arraycopy(body, 0, packet_data, (2 + headByte.length),
                body.length);
        packet_data[bodyLen - 1] = ByteUtils.getCRCByte(packet_data, bodyLen - 1);
        HEAD_BUFF.setLength(0);
        return packet_data;

    }

    /**
     * packet a message that message's body is byte array message's head is
     * byte array
     *
     * @param body message body
     * @param head message head
     * @return a byte array
     */
    private byte[] packet_data(byte[] body, byte[] head) {
        int bodyLen = head.length + body.length + 1;
        byte[] pk_len_head = ByteUtils.getPacketLenByte(bodyLen);
        bodyLen += 2;
        byte[] packet_data = new byte[bodyLen];
        System.arraycopy(pk_len_head, 0, packet_data, 0, 2);
        System.arraycopy(head, 0, packet_data, 2, head.length);
        System.arraycopy(body, 0, packet_data, (2 + head.length), body.length);
        packet_data[bodyLen - 1] = ByteUtils.getCRCByte(packet_data, bodyLen - 1);
        return packet_data;

    }
//
//    public static String byte2hex(byte[] b) {
//        StringBuffer sb = new StringBuffer();
//        String stmp = "";
//        for (int n = 0; n < b.length; n++) {
//            stmp = Integer.toHexString(b[n] & 0XFF);
//            if (stmp.length() == 1) {
//                sb.append("0" + stmp);
//            } else {
//                sb.append(stmp);
//            }
//
//        }
//        return sb.toString();
//    }
}

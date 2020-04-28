package mo.singou.vehicle.ai.communicate.tools;

import com.maikel.logger.LogTools;

public class ByteUtils {
    private ByteUtils(){}
    /**
     * a integer with 2 byte
     *
     * @param len a integer
     * @return 2 length byte array
     */
    public static byte[] getPacketLenByte(int len) {
        byte[] head = new byte[2];
        head[1] = (byte) (len & 0xff);
        head[0] = (byte) ((len >> 8) & 0xff);
        return head;
    }

    /**
     * get CRC byte from buffer
     *
     * @param buf byte array buffer
     * @param n   the length of buffer
     * @return a byte
     */
    public static byte getCRCByte(byte[] buf, int n) {
        byte crc = buf[0];
        for (int i = 1; i < n; i++) {
            crc ^= buf[i];
        }
        return crc;

        // if (n == 0) {
        // return buf[0];
        // } else {
        // return (byte) (buf[n] ^ getCRCByte(buf, n - 1));
        // }

    }

    /**
     * a integer with 4 byte
     *
     * @param i integer
     * @return 4 length byte array
     */
    public static byte[] intToByteArray(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

    /**
     * 4个字节转为一个整数
     *
     * @param res
     * @return
     */
    public static int byteArrayToInt(byte[] res) {
        return (res[3] & 0xff) | ((res[2] << 8) & 0xff00) // | 表示安位或
                | ((res[1] << 24) >>> 8) | (res[0] << 24);
    }

    public static String byte2hex(byte[] b) {
        StringBuffer sb = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                sb.append("0" + stmp);
            } else {
                sb.append(stmp);
            }

        }
        return sb.toString();
    }
}

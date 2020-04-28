package mo.singou.vehicle.ai.communicate.socket.constants;

public class Constants {
    private Constants(){}

    public static final int LISTEN_PORT = 8888;

    public static final int TCP_CONNECT_TIME_OUT = 1000*10;//tcp 30秒超时
    /**
     * 功能码，命令字
     *
     * @author
     *
     */
    public static final class CMD_WORD {

        public static final String TO_SERVER_HEAD_CMD = ",\"data\":\"";
        /** 应答服务器命令字 **/
        public static final String REPLY_TO_SERVER_CMD = "000";
        /** 服务器统一应答命令字 **/
        public static final String SERVER_REPLY_CMD = "600";
        /** 服务器推送命令字 **/
        public static final String SERVER_PULL_CMD = "601";
        /** 心跳命令字 **/
        public static final String HEART_BEAT_CMD = "001";
        /** 消息成功 **/
        public static final String SUCCESS_STR = "true";
        /** 消息失败 **/
        public static final String FAILER_STR = "false";

    }
}

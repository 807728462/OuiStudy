package mo.singou.vehicle.ai.communicate.socket.udp;


public interface UDPManager {
    public static final int TYPE_CLIENT = 1;
    public static final int TYPE_SERVER = 2;

    void start(int port, String ack);

    boolean isRun();

    void setRun(boolean isRun);

    void setOnIpAddressListener(OnIPAddressListener listener);

    void setOnErrorListener(OnErrorListener listener);

    void destroy();

    interface OnIPAddressListener {
        void onIPAddress(String ip);
    }

    interface OnErrorListener {
        void onError(Exception e);
    }

    class Instance {
        private Instance() {
        }


        public static UDPManager getInstance(int type) {
            if (type == TYPE_CLIENT) {
                return new ClientImpl();
            } else {
                return new ServiceImpl();
            }
        }
    }
}

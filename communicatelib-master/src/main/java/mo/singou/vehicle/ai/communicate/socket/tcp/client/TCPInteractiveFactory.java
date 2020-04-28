package mo.singou.vehicle.ai.communicate.socket.tcp.client;


public class TCPInteractiveFactory implements InteractManagerFactory {

    private static TCPInteractiveFactory tcpManagerFactory;

    private TCPInteractiveFactory() {
    }

   public static TCPInteractiveFactory getTcpManager() {
        if (tcpManagerFactory == null) {
            synchronized (TCPInteractiveFactory.class) {
                if (tcpManagerFactory == null) {
                    tcpManagerFactory = new TCPInteractiveFactory();
                }
            }
        }
        return tcpManagerFactory;
    }

    @Override
    public InteractManagerableProxy create() {
        return new TCPManagerProxy();
    }


}

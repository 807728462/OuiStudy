package mo.singou.vehicle.ai.communicate.socket.tcp.client;


import mo.singou.vehicle.ai.communicate.socket.bean.InteractMsg;
import mo.singou.vehicle.ai.communicate.socket.tcp.OnConnectTimeOutListener;

public interface InteractManagerableProxy {

	void initDataServer(String address, int port);

	void release();

	void setConnectTcpServer(boolean connectTcpServer);

	boolean isConnected();

	int getSocketState();

	void resumeConnect();

	void netDisConnected();

	void setRpcInteractListener(TCPInteract.RpcInteractListener interactListener);

	void setOnConnectTimeOutListener(OnConnectTimeOutListener listener);

	void sendMessage(InteractMsg interactMsg);

	void sendMessage(String serial, String interactMsg);

	void sendMessage(byte[]message);
}

package mo.singou.vehicle.ai.communicate.socket.tcp.client;


import mo.singou.vehicle.ai.communicate.socket.bean.InteractMsg;
import mo.singou.vehicle.ai.communicate.socket.tcp.OnConnectTimeOutListener;

public interface InteractManagerable {


	int STATE_NONE = 0;//【初始值、无任何动作】
	int STATE_PREPARING_CONNECT = 1;//【准备连接】
	int STATE_CONNECTING = 2;//【正在连接】
	int STATE_CONNECT_FAILER = 3;//【连接失败】
	int STATE_CONNECTED = 4;//【连接成功】
	int STATE_DISCONNECTED = 5;//【异常断开连接】
	int STATE_FINISHED = 6;//
	int STATE_RELEASE = 7;//【释放不再连接】

	@Deprecated
	int STRING_MSG_BODY = 0x1;
	@Deprecated
	int BYTEARRAY_MSG_BODY = 0x2;
	@Deprecated
	int CMD_MSG = 0x3;

	/** send a InteractMsg to server @see InteractMsg **/
	void sendMessage(InteractMsg message);

	/** send a InteractMsg to server @see InteractMsg **/
	void sendMessage(String serial, String message);

	void sendMessage(byte[]message);

	/** this is connect server method **/
	void initDataServer(String address, int port);

	/** release socket **/
	void release();

	/** socket is connect or not **/
	boolean isConnected();

	/** return socket state **/
	int getSocketState();

	void setConnectTcpServer(boolean connectTcpServer);

	void setOnConnectTimeOutListener(OnConnectTimeOutListener listener);

	/** when net is reconnected socket to reconnect **/
	@Deprecated
	void resumeConnect();

	/** when net is disconnected initiative to break socket connect **/
	@Deprecated
	void netDisConnected();

	void setRpcInteractListener(TCPInteract.RpcInteractListener rpcInteractListener);
}

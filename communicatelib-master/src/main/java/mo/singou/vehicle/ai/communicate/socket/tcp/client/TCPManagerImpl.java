package mo.singou.vehicle.ai.communicate.socket.tcp.client;


import mo.singou.vehicle.ai.communicate.socket.bean.InteractMsg;
import mo.singou.vehicle.ai.communicate.socket.tcp.OnConnectTimeOutListener;

public class TCPManagerImpl implements InteractManagerable {
	protected TCPManagerImpl() {
		tcpInteract = new TCPInteract();
	}

	private TCPInteract tcpInteract;

	@Override
	public void initDataServer(String address,int port) {
		tcpInteract.initDataServer(port,address);
	}

	@Override
	public void release() {
		tcpInteract.release();
		tcpInteract = null;
	}

	@Override
	public boolean isConnected() {
		return tcpInteract.isConnected();
	}

	@Override
	public synchronized int getSocketState() {
		return tcpInteract.getSocketState();
	}

	@Override
	public void setConnectTcpServer(boolean connectTcpServer) {
		//tcpInteract.setConnectTcpServer(connectTcpServer);
	}

	@Override
	public void setOnConnectTimeOutListener(OnConnectTimeOutListener listener) {
		tcpInteract.setOnTimeOutListener(listener);
	}

	@Override
	public void resumeConnect() {
		tcpInteract.resumeConnect();
	}


	@Override
	public void netDisConnected() {
		tcpInteract.netDisConnected();
	}

	@Override
	public void setRpcInteractListener(TCPInteract.RpcInteractListener rpcInteractListener) {
		tcpInteract.setRpcInteractListener(rpcInteractListener);
	}
	
	@Override
	public void sendMessage(InteractMsg message) {
		tcpInteract.sendToServer(message);
	}

	@Override
	public void sendMessage(String serial,String message) {
		tcpInteract.sendToServer(serial,message);
	}

	@Override
	public void sendMessage(byte[] message) {
		tcpInteract.sendToServer(message);
	}
}

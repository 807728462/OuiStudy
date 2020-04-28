package mo.singou.vehicle.ai.communicate.socket.tcp.client;


import com.maikel.logger.LogTools;

import mo.singou.vehicle.ai.communicate.socket.bean.InteractMsg;
import mo.singou.vehicle.ai.communicate.socket.tcp.OnConnectTimeOutListener;


public class TCPManagerProxy implements InteractManagerableProxy {

	private InteractManagerable tcpManagerImplSub ;

	TCPManagerProxy() {
		tcpManagerImplSub = new TCPManagerImpl();
	}


	@Override
	public void initDataServer(String address,int port) {
		tcpManagerImplSub.initDataServer(address,port);
	}

	@Override
	public void release() {
		tcpManagerImplSub.release();
		LogTools.e("release", "释放socket");
	}

	@Override
	public void setConnectTcpServer(boolean connectTcpServer) {
		tcpManagerImplSub.setConnectTcpServer(connectTcpServer);
	}

	@Override
	public boolean isConnected() {
		return tcpManagerImplSub.isConnected();
	}

	@Override
	public synchronized int getSocketState() {
		return tcpManagerImplSub.getSocketState();
	}

	@Override
	public void resumeConnect() {
		tcpManagerImplSub.resumeConnect();
	}


	@Override
	public void netDisConnected() {
		tcpManagerImplSub.netDisConnected();
	}

	@Override
	public void setRpcInteractListener(TCPInteract.RpcInteractListener interactListener) {
		tcpManagerImplSub.setRpcInteractListener(interactListener);
	}

	@Override
	public void setOnConnectTimeOutListener(OnConnectTimeOutListener listener) {
		tcpManagerImplSub.setOnConnectTimeOutListener(listener);
	}

	@Override
	public void sendMessage(InteractMsg interactMsg) {
		tcpManagerImplSub.sendMessage(interactMsg);
	}

	@Override
	public void sendMessage(String serial,String message) {
		tcpManagerImplSub.sendMessage(serial,message);
	}

	@Override
	public void sendMessage(byte[] message) {
		tcpManagerImplSub.sendMessage(message);
	}

}

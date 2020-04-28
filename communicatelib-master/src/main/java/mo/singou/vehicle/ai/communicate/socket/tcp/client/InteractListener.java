package mo.singou.vehicle.ai.communicate.socket.tcp.client;

public interface InteractListener {
	
	void onSendSuccess(String seria);
	
	void onSendFailer(String seria);
	
}

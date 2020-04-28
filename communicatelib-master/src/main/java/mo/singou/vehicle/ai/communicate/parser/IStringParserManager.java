package mo.singou.vehicle.ai.communicate.parser;


import mo.singou.vehicle.ai.communicate.socket.tcp.client.InteractListener;

public interface IStringParserManager {

	void StringParser(String rpcStream);
	
	void setInteractListener(InteractListener able);
}

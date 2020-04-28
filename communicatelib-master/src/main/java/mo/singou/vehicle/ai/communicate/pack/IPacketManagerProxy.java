package mo.singou.vehicle.ai.communicate.pack;


import mo.singou.vehicle.ai.communicate.socket.bean.InteractMsg;

public interface IPacketManagerProxy {
	byte[] packet_data(InteractMsg interactMsg);
	byte[] packet_data(String message);
}

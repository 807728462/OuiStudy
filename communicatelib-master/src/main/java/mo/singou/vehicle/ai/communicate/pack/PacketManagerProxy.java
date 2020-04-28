package mo.singou.vehicle.ai.communicate.pack;


import mo.singou.vehicle.ai.communicate.socket.bean.InteractMsg;

public class PacketManagerProxy implements IPacketManagerProxy{

	protected PacketManagerProxy() {
		packetManager = new PacketManager();
	}

	private IPacketManager packetManager;

	@Override
	public byte[] packet_data(InteractMsg interactMsg) {
		return packetManager.packet_data(interactMsg);
	}

	@Override
	public byte[] packet_data(String message) {
		return packetManager.packet_data(message);
	}

}

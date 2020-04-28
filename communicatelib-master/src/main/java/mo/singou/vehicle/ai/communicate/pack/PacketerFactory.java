package mo.singou.vehicle.ai.communicate.pack;


import mo.singou.vehicle.ai.communicate.socket.bean.InteractMsg;

public final class PacketerFactory {
	private IPacketManagerProxy dataPack;
	private static PacketerFactory INSTANCE = new PacketerFactory();

	private PacketerFactory() {
		dataPack = new PacketManagerProxy();
	}

	public static PacketerFactory getInstance(){
		return INSTANCE;
	}

	public byte[] packData(InteractMsg interactMsg) {
		return dataPack.packet_data(interactMsg);
	}
	public byte[] packData(String message) {
		return dataPack.packet_data(message);
	}
}

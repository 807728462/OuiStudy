package mo.singou.vehicle.ai.communicate.pack;


import mo.singou.vehicle.ai.communicate.socket.bean.InteractMsg;

public interface IPacketManager {
	/**a message is that body is String and head is InteractMsgHeader**/
	int PACK_STRING_BODY = 0x1;
	/**a message is that body is byte array and head is InteractMsgHeader**/
	int PACK_BYTEARRAY_BODY = 0x2;
	/**a message is that both of the head and body is byte array**/
	int PACK_BYTEARRAY = 0x3;
	/**
	 * packet InteractMsg to be a byte array
	 * @param interactMsg a message that to be packet
	 * @return byte array
	 */
	byte[] packet_data(InteractMsg interactMsg);

	byte[] packet_data(String body);
}

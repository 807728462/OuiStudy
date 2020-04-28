package mo.singou.vehicle.ai.communicate.socket.bean;


import mo.singou.vehicle.ai.communicate.pack.IPacketManager;

public class InteractMsg {
	/**message body**/
	private Object body;
	/**message head**/
	private Object head;
	/**message type which to pack **/
	private int msgType;

	public InteractMsg() {

	}

	public InteractMsg(Object head, Object body) {
		this(IPacketManager.PACK_STRING_BODY, head, body);
	}

	public InteractMsg(int msgType, Object head, Object body) {
		this.msgType = msgType;
		this.head = head;
		this.body = body;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public Object getHead() {
		return head;
	}

	public void setHead(Object head) {
		this.head = head;
	}

}

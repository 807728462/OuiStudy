package mo.singou.vehicle.ai.communicate.socket.bean;

public class InteractMsgHeader {

	public InteractMsgHeader() {

	}

	public InteractMsgHeader(String cmd, long serialNum, String key) {
		this.cmd = cmd;
		this.serialNum = serialNum;
		this.key = key;
	}

	private static int terminalId;

	private static String token;

	private static String version;

	private String cmd;

	private long serialNum;

	private String key;

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public int getTerminalId() {
		return InteractMsgHeader.terminalId;
	}

	public void setTerminalId(int terminalId) {
		InteractMsgHeader.terminalId = terminalId;
	}

	public String getToken() {
		return InteractMsgHeader.token;
	}

	public void setToken(String token) {
		InteractMsgHeader.token = token;
	}

	public String getVersion() {
		return InteractMsgHeader.version;
	}

	public void setVersion(String version) {
		InteractMsgHeader.version = version;
	}

	public long getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(long serialNum) {
		this.serialNum = serialNum;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}

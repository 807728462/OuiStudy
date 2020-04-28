package mo.singou.vehicle.ai.communicate.socket.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class InteractResultBean implements Parcelable {

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	private int code;

	private String terminalId;

	private String token;

	private String version;

	private String serialNum;

	private String key;

	private int contentType;

	private String title;

	private String content;

	private int cpCode;

	private String values[];

	public String[] getValues() {
		return values;
	}

	protected void setValues(String[] values) {
		this.values = values;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getSerialNum() {
		return serialNum;
	}

	protected void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCpCode() {
		return cpCode;
	}

	public void setCpCode(int cpCode) {
		this.cpCode = cpCode;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

		dest.writeInt(code);
		dest.writeString(terminalId);
		dest.writeString(token);
		dest.writeString(version);
		dest.writeString(serialNum);
		dest.writeString(key);
		dest.writeInt(contentType);
		dest.writeString(title);
		dest.writeString(content);
		dest.writeInt(cpCode);
		if (values == null) {
			dest.writeInt(0);
		} else {
			dest.writeInt(values.length);
			dest.writeStringArray(values);
		}

	}

	public InteractResultBean() {

	}

	public InteractResultBean(Parcel in) {

		code = in.readInt();
		terminalId = in.readString();
		token = in.readString();

		version = in.readString();
		serialNum = in.readString();
		key = in.readString();
		contentType = in.readInt();
		title = in.readString();
		content = in.readString();
		cpCode = in.readInt();
		int len = in.readInt();
		if (len > 0) {
			in.readStringArray(values);
		}

	}

	public static final Creator<InteractResultBean> CREATOR = new Creator<InteractResultBean>() {

		@Override
		public InteractResultBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new InteractResultBean[size];
		}

		@Override
		public InteractResultBean createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new InteractResultBean(source);
		}
	};

}

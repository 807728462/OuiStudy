package mo.singou.vehicle.ai.communicate.parser;

import java.io.IOException;


public interface IByteParserManager {
	void startParser();

	void byteArrayParser(byte[] parserBuf, int bufLen)throws IOException ;

	void byteArrayParser(byte[] parserBuf)throws IOException ;

	void setOnDataListener(OnDataListener dataListener);

	void stopParser();

	Rx_Slip_State getSlipState();

	interface OnDataListener{
		void onDataResponse(String response);
	}
}

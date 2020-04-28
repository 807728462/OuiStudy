package mo.singou.vehicle.ai.communicate.parser;

import java.io.IOException;

public interface IParserManagerProxy {
	
	void byteArrayParser(byte[] parserBuf, int bufLen)throws IOException ;
	void byteArrayParser(byte[] parserBuf)throws IOException ;
	void setOnDataLisenter(IByteParserManager.OnDataListener lisenter);
	void startParser();
	void stopParser();
	Rx_Slip_State getSlipState();
}

package mo.singou.vehicle.ai.communicate.parser;

import java.io.IOException;


public class ParserManagerProxy implements IParserManagerProxy {
	private IByteParserManager byteParserManager;

	protected ParserManagerProxy() {
		byteParserManager = new ByteParserManagerImpl();
	}

	@Override
	public void byteArrayParser(byte[] parserBuf, int bufLen) throws IOException {
		byteParserManager.byteArrayParser(parserBuf, bufLen);
	}

	@Override
	public void byteArrayParser(byte[] parserBuf) throws IOException {
		byteParserManager.byteArrayParser(parserBuf);
	}

	@Override
	public void setOnDataLisenter(IByteParserManager.OnDataListener dataListener) {
		byteParserManager.setOnDataListener(dataListener);
	}

	@Override
	public void startParser() {
		byteParserManager.startParser();
	}

	@Override
	public void stopParser() {
		byteParserManager.stopParser();
	}

	@Override
	public Rx_Slip_State getSlipState() {
		return byteParserManager.getSlipState();
	}


}

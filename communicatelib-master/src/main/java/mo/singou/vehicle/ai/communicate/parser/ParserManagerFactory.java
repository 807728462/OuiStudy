package mo.singou.vehicle.ai.communicate.parser;

import java.io.IOException;


public final class ParserManagerFactory {
	private IParserManagerProxy parser;

	private static ParserManagerFactory INSTANCE;

	private ParserManagerFactory() {
		parser = new ParserManagerProxy();
	}


	public static ParserManagerFactory getInstance(){
		if (INSTANCE == null){
			synchronized (ParserManagerFactory.class){
				if (INSTANCE == null){
					INSTANCE = new ParserManagerFactory();
				}
			}
		}
		return INSTANCE;
	}


	public void byteArrayParser(byte[] parserBuf, int bufLen) throws IOException {
		parser.byteArrayParser(parserBuf, bufLen);
	}
	public void byteArrayParser(byte[] parserBuf) throws IOException {
		parser.byteArrayParser(parserBuf);
	}
	public void setOnDataListener(IByteParserManager.OnDataListener listener){
		parser.setOnDataLisenter(listener);
	}
	public void startParser(){
		parser.startParser();
	}
	public void stopParser(){
		parser.stopParser();
	}
	public Rx_Slip_State getSlipState() {
		return parser.getSlipState();
	}
}

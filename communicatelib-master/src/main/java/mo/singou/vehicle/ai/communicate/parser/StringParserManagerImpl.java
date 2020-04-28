package mo.singou.vehicle.ai.communicate.parser;


import com.maikel.logger.LogTools;

import mo.singou.vehicle.ai.communicate.socket.tcp.client.InteractListener;
import mo.singou.vehicle.ai.communicate.socket.bean.InteractResultBean;
import mo.singou.vehicle.ai.communicate.socket.constants.Constants;


public class StringParserManagerImpl extends StringParserManager {
	StringParserManagerImpl() {
		// interactProxy = RemoteInteract.getInteract();
	}

	private static final String tag = StringParserManagerImpl.class
			.getSimpleName();

	private static final String DATA_SPLIT_REGX = ",\"data\":";
	private static final String CONTENT_SPLIT_REGX = ",content:";
	private static final String TITLE_SPLIT_REGX = "title:";
	private static final String CPCODE_SPLIT_REGX = ",cpCode:";
	private static final String HEAD_SPLIT_REGX = "header:";
	private static final int HEAD_MAX_LENGTH = 20;

	/** 数据发送监听接口 **/
	private InteractListener able;

	// private RemoteInteract interactProxy ;

	@Override
	public void setInteractListener(InteractListener able) {
		this.able = able;
	}

	@Override
	public void StringParser(String rpcStream) {
		// TODO Auto-generated method stub
		String paserStr = rpcStream;
		String seria = "";
		try {
			// paserStr = replace(rpcStream, "\"", "");
			int rexIndex = paserStr.indexOf(DATA_SPLIT_REGX);
			if (rexIndex < 0)
				return;
			String headSrc = paserStr.substring(0, rexIndex);
			headSrc = replace(headSrc, "\"", "");
			// headSrc = replace(headSrc, HEAD_SPLIT_REGX, "");

			String valueSrc = paserStr.substring((rexIndex + DATA_SPLIT_REGX
					.length()));
			// String[] sourceData = paserStr.replace("\"", "").split("data:");
			String[] headCmdSrc = new String[HEAD_MAX_LENGTH];

			int realHeadLen = getValuesBySlip(headCmdSrc, headSrc, ",");

			seria = headCmdSrc[realHeadLen - 2];
			/** 服务器返回心跳命令字 **/
			if (headCmdSrc[0].endsWith(Constants.CMD_WORD.SERVER_REPLY_CMD)) {

				if (valueSrc.endsWith(Constants.CMD_WORD.FAILER_STR)) {
					LogTools.e(tag, "cilent send failer：seria" + seria);
					// 如此做重发机制
					able.onSendFailer(seria);
				} else {
					able.onSendSuccess(seria);
				}
				return;
			}
			InteractResultBean interactResultBean = new InteractResultBean();
			String cmd = replace(headCmdSrc[0], HEAD_SPLIT_REGX, "");
			interactResultBean.setCode(Integer.parseInt(cmd));
			interactResultBean.setTerminalId(headCmdSrc[1]);
			interactResultBean.setToken(headCmdSrc[2]);
			interactResultBean.setVersion(headCmdSrc[3]);
			String key = headCmdSrc[realHeadLen - 1];
			interactResultBean.setKey(key);
			int contentType = 0;
			try {
				contentType = Integer.parseInt(headCmdSrc[realHeadLen - 2]);
			} catch (NumberFormatException exception) {
				LogTools.e(tag, "内容类型解析不成功,contentType:" + headCmdSrc[realHeadLen - 2]);
			}
			interactResultBean.setContentType(contentType);
			/** 服务器推送命令字 **/
			if (headCmdSrc[0].endsWith(Constants.CMD_WORD.SERVER_PULL_CMD)) {

				int dataValueIndex = valueSrc.indexOf(CONTENT_SPLIT_REGX);
				
				String titleSrc = valueSrc.substring(0, dataValueIndex);

				String dataValue = valueSrc
						.substring((dataValueIndex + CONTENT_SPLIT_REGX
								.length()));
				int titleIndex = titleSrc.indexOf(TITLE_SPLIT_REGX);
				String title = titleSrc.substring(titleIndex
						+ TITLE_SPLIT_REGX.length());
				interactResultBean.setTitle(title);
				int cpCodeIndex = dataValue.indexOf(CPCODE_SPLIT_REGX);
				String content;
				int cpCode = 0;
				if (cpCodeIndex >= 0) {
					content = dataValue.substring(0, cpCodeIndex);
					String cpCodeSrc = dataValue.substring(cpCodeIndex
							+ CPCODE_SPLIT_REGX.length());
					cpCodeSrc = replace(cpCodeSrc, "\"", "");
					try {
						cpCode = Integer.parseInt(cpCodeSrc);
					} catch (Exception e) {

					}
				} else {
					content = dataValue;
				}
				interactResultBean.setCpCode(cpCode);

				interactResultBean.setContent(content);
				// contentShowFactory.notifyServerReply(interactResultBean);
			} else {
				interactResultBean.setContent(valueSrc);
				able.onSendSuccess(seria);
			}
			
			// interactProxy.writeLogMsg(TimeUtils.getDateFromLong(
			// TimeUtils.MINUS_YYMMDDHHMMSS, System.currentTimeMillis())
			// + "data：" + paserStr + "\n");
		} catch (Exception e) {
			// TODO: handle exception
			able.onSendSuccess(seria);
			LogTools.e(tag, "解析出错：paserStr:" + paserStr + "," + e.getMessage());
		}
	}

	/**
	 * @describe:可以替换特殊字符的替换方法,replaceAll只能替换普通字符串,含有特殊字符的不能替换
	 * @param strSource
	 *            用户输入的字符串
	 * @param strFrom
	 *            数据库用需要替换的字符
	 * @param strTo
	 *            需要替换的字符替换为该字符串
	 * @return
	 */
	public static String replace(String strSource, String strFrom, String strTo) {
		if (strSource == null) {
			return null;
		}
		int i = 0;
		if ((i = strSource.indexOf(strFrom, i)) >= 0) {
			char[] cSrc = strSource.toCharArray();
			char[] cTo = strTo.toCharArray();
			int len = strFrom.length();
			StringBuffer buf = new StringBuffer(cSrc.length);
			buf.append(cSrc, 0, i).append(cTo);
			i += len;
			int j = i;
			while ((i = strSource.indexOf(strFrom, i)) > 0) {
				buf.append(cSrc, j, i - j).append(cTo);
				i += len;
				j = i;
			}
			buf.append(cSrc, j, cSrc.length - j);
			return buf.toString();
		}
		return strSource;
	}

	/**
	 * 分割数据
	 * 
	 * @param values
	 *            结果数组
	 * @param dataSrc
	 *            要分割的字符串
	 * @param regx
	 *            分割表达式
	 * @return 返回有多少个结果
	 */
	public static int getValuesBySlip(String[] values, String dataSrc,
			String regx) {
		if (values == null || values.length <= 0)
			return 0;
		int i = 0;
		while (true) {
			int index = dataSrc.indexOf(regx);
			if (index < 0) {
				values[i++] = dataSrc;
				break;
			}
			values[i++] = dataSrc.substring(0, index);
			dataSrc = dataSrc.substring(index + 1);
		}
		return i;
	}

}

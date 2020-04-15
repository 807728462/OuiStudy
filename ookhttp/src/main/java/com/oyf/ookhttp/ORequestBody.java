package com.oyf.ookhttp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @创建者 oyf
 * @创建时间 2020/4/11 10:54
 * @描述
 **/
public class ORequestBody {

    private final String ENC = "utf-8";

    // 请求体集合  a=123&b=666
    private Map<String, String> bodys = new HashMap<>();

    /**
     * 添加请求体信息
     *
     * @param key
     * @param value
     */
    public void addBody(String key, String value) {
        // 需要URL编码
        try {
            bodys.put(URLEncoder.encode(key, ENC), URLEncoder.encode(value, ENC));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到请求体信息
     */
    public String getBody() {
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, String> stringStringEntry : bodys.entrySet()) {
            // a=123&b=666&
            stringBuffer.append(stringStringEntry.getKey())
                    .append("=")
                    .append(stringStringEntry.getValue())
                    .append("&");
        }
        // a=123&b=666& 删除&
        if (stringBuffer.length() != 0) {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        return stringBuffer.toString();
    }


}

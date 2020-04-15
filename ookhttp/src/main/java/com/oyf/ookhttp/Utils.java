package com.oyf.ookhttp;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

/**
 * @创建者 oyf
 * @创建时间 2020/4/14 15:38
 * @描述
 **/
public class Utils {

    public static final String K = " ";
    public static final String VIERSION = "HTTP/1.1";
    public static final String GRGN = "\r\n";


    /**
     * 获取请求头中的host
     *
     * @param url
     * @return
     */
    public static String hostHeader(String url) {
        try {
            URL mUrl = new URL(url);
            return mUrl.getHost();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * 获取端口号
     *
     * @param url
     * @return
     */
    public static int getPort(String url) {
        try {
            URL mUrl = new URL(url);
            int port = mUrl.getPort();
            return port == -1 ? mUrl.getDefaultPort() : port;
        } catch (MalformedURLException e) {
            return 0;
        }
    }

    /**
     * 获取请求头额度所有信息
     *
     * @param request
     * @return
     */
    public static String getRequestHeaderAll(ORequest request) {
        URL mUrl = null;
        try {
            mUrl = new URL(request.url());

        } catch (MalformedURLException e) {

        }
        String file = mUrl.getFile();
        // TODO 拼接 请求头 的 请求行  GET /v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2 HTTP/1.1\r\n
        StringBuilder sb = new StringBuilder();
        sb.append(request.method()) // GET or POST
                .append(K)
                .append(file)
                .append(K)
                .append(VIERSION)
                .append(GRGN);

        // TODO 获取请求集 进行拼接
        /**
         * Content-Length: 48\r\n
         * Host: restapi.amap.com\r\n
         * Content-Type: application/x-www-form-urlencoded\r\n
         */
        if (!request.headers().isEmpty()) {
            Map<String, String> mapList = request.headers().getHeads();
            for (Map.Entry<String, String> entry : mapList.entrySet()) {
                sb.append(entry.getKey())
                        .append(":").append(K)
                        .append(entry.getValue())
                        .append(GRGN);
            }
            // 拼接空行，代表下面的POST，请求体了
            sb.append(GRGN);
        }
        return sb.toString();
    }
}

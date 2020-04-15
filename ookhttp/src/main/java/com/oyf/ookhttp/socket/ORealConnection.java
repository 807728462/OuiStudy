package com.oyf.ookhttp.socket;

import com.oyf.ookhttp.ORequest;
import com.oyf.ookhttp.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * @创建者 oyf
 * @创建时间 2020/4/15 10:30
 * @描述
 **/
public class ORealConnection {

    private Socket socket;

    private HttpCodec mHttpCodec;
    private ORequest mORequest;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public ORealConnection(HttpCodec httpCodec) {
        mHttpCodec = httpCodec;
        mORequest = httpCodec.getORequest();
    }

    public void connect() throws IOException {
        socket = new Socket(Utils.hostHeader(mORequest.url()), Utils.getPort(mORequest.url()));
    }

    /**
     * 写入请求信息
     *
     * @throws IOException
     */
    public void writeRequest() throws IOException {
        if (null != socket) {
            // output
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writeRequestHeaders(bufferedWriter);
            if (ORequest.POST.equalsIgnoreCase(mHttpCodec.getORequest().method())) {
                writeRequestBody(bufferedWriter);
            }
        }
    }

    /**
     * 写入请求头信息
     *
     * @param bufferedWriter
     * @throws IOException
     */
    private void writeRequestHeaders(BufferedWriter bufferedWriter) throws IOException {
        String headers = mHttpCodec.writeRequestHeaders();
        bufferedWriter.write(headers); // 给服务器发送请求 --- 请求头信息 所有的
        bufferedWriter.flush(); // 真正的写出去...

    }

    /**
     * 写入请求体
     *
     * @param bufferedWriter
     * @throws IOException
     */
    private void writeRequestBody(BufferedWriter bufferedWriter) throws IOException {
        String body = mHttpCodec.createRequestBody();
        bufferedWriter.write(body); // 给服务器发送请求 --- 请求头信息 所有的
        bufferedWriter.flush(); // 真正的写出去...
    }

    /**
     * 读取响应头
     *
     * @return
     * @throws IOException
     */
    public int readResponseCode() throws IOException {
        if (null != socket) {
            int code = -1;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // todo 取出 响应码
                String readLine = bufferedReader.readLine(); // 读取第一行 响应头信息
                // 服务器响应的:HTTP/1.1 200 OK
                String[] strings = readLine.split(" ");
                code = Integer.parseInt(strings[1]);
            } catch (IOException e) {
                release();
                throw e;
            }
            return code;
        } else {
            return 400;
        }
    }

    /**
     * 读取响应体
     *
     * @return
     * @throws IOException
     */
    public String readResponseBody() throws IOException {
        // todo 取出响应体，只要是空行下面的，就是响应体
        try {
            String readerLine = null;
            while ((readerLine = bufferedReader.readLine()) != null) {
                if ("".equals(readerLine)) {
                    // 读到空行了，就代表下面就是 响应体了
                    String re = bufferedReader.readLine();
                    bufferedReader.close();
                    return re;
                }
            }
        } catch (IOException e) {
            bufferedReader.close();
            bufferedReader = null;
            throw e;
        }
        return "";
    }

    /**
     * 回收
     *
     * @throws IOException
     */
    public void release() throws IOException {
        if (null != bufferedWriter) {
            bufferedWriter.close();
            bufferedWriter = null;
        }
        if (null != bufferedReader) {
            bufferedReader.close();
            bufferedReader = null;
        }
        if (null != socket) {
            if (socket.isConnected()) {
                socket.close();
                socket = null;
            }
        }
    }
}

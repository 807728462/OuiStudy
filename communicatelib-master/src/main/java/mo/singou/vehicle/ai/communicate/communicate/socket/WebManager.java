/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mo.singou.vehicle.ai.communicate.communicate.socket;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import mo.singou.vehicle.ai.communicate.communicate.ICommunicateManager;


/**
 * Implementation of a very basic HTTP server. The contents are loaded from the assets folder. This
 * server handles one request at a time. It only supports GET method.
 */
public class WebManager implements Runnable, ICommunicateManager {

    private static final String TAG = "WebManager";
    private Map<Long, Socket> sessionMap = new HashMap<>();

    interface Header {
        String CONTENT_TYPE = "Content-Type";
        String CONTENT_LENGTH = "Content-Length";
    }

    interface Method {
        String GET = "GET";
        String POST = "POST";
    }

    /**
     * The port number we listen to
     */
    private final int mPort;

    /**
     * {@link AssetManager} for loading files to serve.
     */
    private AssetManager mAssets;

    /**
     * True if the server is running.
     */
    private boolean mIsRunning;

    /**
     * The {@link ServerSocket} that we listen to.
     */
    private ServerSocket mServerSocket;

    private Context mContext;

    private static final int MAX_THREAD_NUM = 8;
    private Executor mExecutor;

    private OnRemoteMessageListener mListener;

    /**
     * WebServer constructor.
     */
    public WebManager(int port, Context context) {
        mPort = port;
        mContext = context;
    }

    @Override
    public void init(String tag,String ack) {
        mAssets = mContext.getAssets();
        mExecutor = Executors.newFixedThreadPool(MAX_THREAD_NUM);
    }

    /**
     * This method starts the web server listening to the specified port.
     */
    @Override
    public void start() {
        if (mAssets == null) {
            throw new RuntimeException("please init first!");
        }
        mIsRunning = true;
        new Thread(this).start();
    }


    /**
     * This method stops the web server
     */
    @Override
    public void stop() {
        try {
            mIsRunning = false;
            if (null != mServerSocket) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing the server socket.", e);
        }
    }

    @Override
    public void sendMessage(String serial,String message, OnResponseListener listener) {

    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void sendMessage(byte[] message) {

    }

    @Override
    public void setOnRemoteMessageListener(OnRemoteMessageListener listener) {
        mListener = listener;
    }

    @Override
    public void setOnAddressListener(OnAddressListener listener) {

    }

    public int getPort() {
        return mPort;
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket(mPort);

            while (mIsRunning) {
                final Socket socket = mServerSocket.accept();

                // 放入線程池中執行
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            handle(socket);
                            socket.close();
                        } catch (SocketException e) {
                            // The server was stopped; ignore.
                        } catch (IOException e) {
                            Log.e(TAG, "Web server error.", e);
                        }
                    }
                });
            }
        } catch (SocketException e) {
            // The server was stopped; ignore.
            Log.e(TAG, "Web server error.", e);
        } catch (IOException e) {
            Log.e(TAG, "Web server error.", e);
        }
    }

    /**
     * Respond to a request from a client.
     *
     * @param socket The client socket.
     * @throws IOException
     */
    private void handle(Socket socket) throws IOException {
        BufferedReader reader = null;
        PrintStream output = null;
        try {
            String route = null;

            // Read HTTP headers and parse out the route.
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String method = null;
            String line;

            while ((line = reader.readLine()) != null) {
                Log.i(TAG, "readLine: " + line);

                if (line.startsWith("GET /")) {
                    method = Method.GET;
                    int start = line.indexOf('/') + 1;
                    int end = line.indexOf(' ', start);
                    route = line.substring(start, end);
                    break;
                } else if (line.startsWith("POST /")) {
                    method = Method.POST;
                    int start = line.indexOf('/') + 1;
                    int end = line.indexOf(' ', start);
                    route = line.substring(start, end);
                    break;
                } else {
                    break;
                }
            }

            // Output stream that we send the response to
            output = new PrintStream(socket.getOutputStream());

            // Prepare the content to send.
            Log.i(TAG, "Received http request: " + route);
            byte[] bytes = null;
            if (Method.GET.equals(method)) { // Do GET
                if (TextUtils.isEmpty(route)) {
                    route = "index.html";
                }
                if (route.startsWith("aiui")) {
                    HashMap<String, String> args = getArgs(route);
                    String requestJson = args.get("value");
                    if (mListener != null) {
                        mListener.onMessage(2,"",requestJson);
                    }
                    bytes = "ok".getBytes();
                } else if (route.startsWith("speak")) {
                    HashMap<String, String> args = getArgs(route);
                    String ttsJson = args.get("value");
//                    if (mListener != null) {
//                        mListener.onTts(ttsJson);
//                    }
                    bytes = "ok".getBytes();
                }else if(route.startsWith("AiuiStatus")){
                    HashMap<String, String> args = getArgs(route);
                    String ttsJson = args.get("value");
//                    if (mListener != null) {
//                        mListener.onWakeStatus(ttsJson);
//                    }
                    bytes = "ok".getBytes();
                } else {
                    bytes = loadContent(route);
                }
            } else if (Method.POST.equals(method)) { // Do POST
                if (route.startsWith("photo")) {
                    HashMap<String, String> headers = new HashMap<>();
                    while ((line = reader.readLine()) != null) {
                        if ("".equals(line.trim())) {
                            break;
                        }
                        int index = line.indexOf(": ");
                        String key = line.substring(0, index);
                        String value = line.substring(index + 2);
                        headers.put(key, value);
                    }
                    int contentLength = Integer.parseInt(headers.get(Header.CONTENT_LENGTH));
                    StringBuilder builder = new StringBuilder();
                    char[] buffer = new char[1024 * 8];
                    int len = 0;
                    while (len < contentLength) {
                        int readLen = reader.read(buffer);
                        builder.append(buffer, 0, readLen);
                        len += readLen;
                    }
//                    for (int i = 0; i * buffer.length <= contentLength; i++) {
//                        int len = reader.read(buffer);
//                        builder.append(buffer, 0, len);
//                    }
                    String base64 = builder.toString();
                    try {
                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/singou/image";
                        // FileUtils.delete(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/singou/image/"));
                        // 保存为文件
//                        byte[] fileContent = ByteUtils.hexStringToBytes(base64);
                        byte[] fileContent = Base64.decode(base64, Base64.NO_WRAP);
                        //Bitmap bitmap = ImageUtils.byteArray2Bitmap(fileContent);
                        String storagePath = ImageUtils.saveByteArrayToFile(filePath,
                                System.currentTimeMillis() + ".jpg", fileContent);
                        if (mListener != null) {
                            mListener.onMessage(1,"",storagePath);
                        }
                        bytes = "ok".getBytes();
                    } catch (Exception e) {
                        LogTools.p(TAG, e, "失败");
                    }
                }
            }
            if (null == bytes) {
                writeServerError(output);
                return;
            }
            // Send out the content.
            output.println("HTTP/1.0 200 OK");
            output.println("Content-Type: " + detectMimeType(route));
            output.println("Content-Length: " + bytes.length);
            output.println();
            output.write(bytes);
            output.flush();
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(reader);
        }
    }

    private HashMap<String, String> getArgs(String route) throws UnsupportedEncodingException {
        HashMap<String, String> args = new HashMap<>();
        if (route.contains("?")) {
            String paramString = route.substring(route.indexOf("?") + 1);
            if (!TextUtils.isEmpty(paramString)) {
                String[] params = paramString.split("&");

                for (String param : params) {
                    String[] split = param.split("=");
                    if (split != null && split.length > 1) {
                        args.put(split[0], URLDecoder.decode(split[1], "UTF-8"));
                    }
                }
            }
        }
        return args;
    }

    /**
     * Writes a server error response (HTTP/1.0 500) to the given output stream.
     *
     * @param output The output stream.
     */
    private void writeServerError(PrintStream output) {
        output.println("HTTP/1.0 500 Internal Server Error");
        output.flush();
    }

    /**
     * Loads all the content of {@code fileName}.
     *
     * @param fileName The name of the file.
     * @return The content of the file.
     * @throws IOException
     */
    private byte[] loadContent(String fileName) throws IOException {
        InputStream input = null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            input = mAssets.open(fileName);
            byte[] buffer = new byte[1024];
            int size;
            while (-1 != (size = input.read(buffer))) {
                output.write(buffer, 0, size);
            }
            output.flush();
            return output.toByteArray();
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            if (null != input) {
                input.close();
            }
        }
    }

    /**
     * Detects the MIME type from the {@code fileName}.
     *
     * @param fileName The name of the file.
     * @return A MIME type.
     */
    private String detectMimeType(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        } else if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".xls")) {
            return "application/vnd.ms-excel";
        } else if (fileName.contains(".json")) {
            return "text/json";
        } else {
            return "text/plain";
        }
    }
}

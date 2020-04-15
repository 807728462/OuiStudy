package com.oyf.ookhttp.socket;

/**
 * @创建者 oyf
 * @创建时间 2020/4/15 9:45
 * @描述
 **/
public class SocketManager {
    private static SocketManager instance;

    private SocketManager() {
    }

    public static SocketManager getInstance() {
        if (null == instance) {
            synchronized (SocketManager.class) {
                if (null == instance) {
                    instance = new SocketManager();
                }
            }
        }
        return instance;
    }

    public void connectServer(){

    }
}

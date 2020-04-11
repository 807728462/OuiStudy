package com.oyf.ookhttp;

/**
 * @创建者 oyf
 * @创建时间 2020/4/11 10:38
 * @描述
 **/
public class ODispatcher {

    public void enqueue(AsyncCall call) {

    }


    public static final class AsyncCall implements Runnable {
        private OCallBack mOCallBack;

        public AsyncCall(OCallBack OCallBack) {
            mOCallBack = OCallBack;
        }

        @Override
        public void run() {

        }
    }
}

package com.oyf.ookhttp;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.oyf.ookhttp.ORealCall.AsyncCall;

/**
 * @创建者 oyf
 * @创建时间 2020/4/11 10:38
 * @描述
 **/
public class ODispatcher {

    private int maxRequests = 64;
    private int maxRequestsPerHost = 5;

    private ExecutorService mExecutorService;

    /**
     * 用于存储等待中的请求队列
     */
    private final Deque<AsyncCall> mReadyAsyncCalls = new ArrayDeque<>();
    /**
     * 正在运行中的异步请求队列
     */
    private final Deque<AsyncCall> mRunningAsyncCalls = new ArrayDeque<>();
    /**
     * 运行中的同步请求队列
     */
    private final Deque<ORealCall> mRunningSyncCalls = new ArrayDeque<>();


    public ODispatcher(ExecutorService executorService) {
        this.mExecutorService = executorService;
    }

    public ODispatcher() {
    }

    public synchronized ExecutorService executorService() {
        if (mExecutorService == null) {
            mExecutorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("ODispatcher");
                    thread.setDaemon(false);
                    return thread;
                }
            });
        }
        return mExecutorService;
    }

    /**
     * 执行异步请求
     * 将请求加入执行队列 ，如果满了添置缓存队列
     *
     * @param call
     */
    public void enqueue(AsyncCall call) {
        if (mRunningAsyncCalls.size() < maxRequests) {
            mRunningAsyncCalls.add(call);
            executorService().execute(call);
        } else {
            mReadyAsyncCalls.add(call);
        }
    }

    /**
     * 结束执行的请求
     *
     * @param asyncCall
     */
    public void finished(AsyncCall asyncCall) {
        synchronized (this) {
            if (!mRunningAsyncCalls.remove(asyncCall))
                throw new AssertionError("Call wasn't in-flight!");
        }

    }

    /**
     * 正在运行额请求个数
     *
     * @return
     */
    private int runningCallsCount() {
        return mRunningAsyncCalls.size() + mRunningSyncCalls.size();
    }

    public synchronized void cancelAll() {
        for (AsyncCall call : mReadyAsyncCalls) {
            call.get().cancel();
        }

        for (AsyncCall call : mRunningAsyncCalls) {
            call.get().cancel();
        }

        for (ORealCall call : mRunningSyncCalls) {
            call.cancel();
        }
    }
}

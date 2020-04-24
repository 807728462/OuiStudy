package com.oyf.oglide;

import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @创建者 oyf
 * @创建时间 2020/4/24 14:53
 * @描述
 **/
public class ExecutorUtils {
    private static ExecutorUtils instance;

    private Executor mExecutor;

    private ExecutorUtils() {
        mExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>()
        );
    }

    public static ExecutorUtils getInstance() {
        if (null == instance) {
            instance = new ExecutorUtils();
        }
        return instance;
    }


    public void execute(Runnable runnable) {
        if (null != mExecutor) {
            mExecutor.execute(runnable);
        }
    }
}

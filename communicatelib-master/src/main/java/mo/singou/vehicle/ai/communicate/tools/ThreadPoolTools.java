package mo.singou.vehicle.ai.communicate.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolTools {

    private static final int CORE_SIZE = 5;
    private static final int THREAD_SIZE = 5;

    private static final ThreadFactory TF = new ThreadFactory() {
        private AtomicInteger atomicInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "thread" + atomicInteger.getAndIncrement());
        }
    };
    private static final ExecutorService TASK_EXECUTOR =  new ThreadPoolExecutor(CORE_SIZE,THREAD_SIZE,60L,TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(),TF);

    private ThreadPoolTools(){}

    public static void execute(Runnable r) {
        if (!TASK_EXECUTOR.isShutdown()) {
            TASK_EXECUTOR.execute(r);
        }
    }

    public static void release(){
        if (!TASK_EXECUTOR.isShutdown()) {
            TASK_EXECUTOR.shutdownNow();
        }
    }
}

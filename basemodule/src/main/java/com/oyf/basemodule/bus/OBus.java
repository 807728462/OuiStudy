package com.oyf.basemodule.bus;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class OBus {

    private static OBus instance;

    private OBus() {
        mCacheMap = new HashMap<>();
        handler = new Handler();
        executorService = new ThreadPoolExecutor(5,
                Integer.MAX_VALUE,
                5000,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(5));
    }

    public static OBus getDefault() {
        if (instance == null) {
            synchronized (OBus.class) {
                if (instance == null) {
                    instance = new OBus();
                }
            }
        }
        return instance;
    }

    private Map<Object, List<OSubscriberMethod>> mCacheMap;
    private Handler handler;
    private ExecutorService executorService;

    public void register(Object obj) {
        List<OSubscriberMethod> subscriberMethods = mCacheMap.get(obj);
        if (subscriberMethods == null) {
            subscriberMethods = findSubscriberMethods(obj);
        } else {
            throw new RuntimeException("Subscriber " + obj
                    + " 请勿重复注册");
        }
        mCacheMap.put(obj, subscriberMethods);
    }

    private List<OSubscriberMethod> findSubscriberMethods(Object obj) {
        List<OSubscriberMethod> lists = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            String clazzName = clazz.getName();
            //如果是系统类直接跳过
            if (clazzName.startsWith("java.") || clazzName.startsWith("javax.") ||
                    clazzName.startsWith("android.") || clazzName.startsWith("androidx..")) {
                break;
            }
            //获取此类中所有的方法getDeclaredMethods，获取此类包括父类的所有方法getMethods
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                //获取方法上面的是否有OSubscribe注解
                OSubscribe oSubscribe = method.getAnnotation(OSubscribe.class);
                if (oSubscribe == null) {
                    continue;
                }
                //获取方法的参数类型
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1) {//只能传一个参数
                    OThreadMode threadMode = oSubscribe.threadMode();
                    OSubscriberMethod subscriberMethod = new OSubscriberMethod(method, threadMode, parameterTypes[0]);
                    lists.add(subscriberMethod);
                }
            }
            //获取类的父类，递归去获取订阅的方法
            clazz = clazz.getSuperclass();
        }
        if (lists.isEmpty()) {
            throw new RuntimeException("Subscriber " + obj
                    + " and its super classes have no public methods with the @Subscribe annotation");
        }
        return lists;
    }


    public void post(Object clazz) {
        Set<Object> keySet = mCacheMap.keySet();
        for (Object obj : keySet) {
            List<OSubscriberMethod> oSubscriberMethods = mCacheMap.get(obj);
            for (OSubscriberMethod oSubscriberMethod : oSubscriberMethods) {
                if (oSubscriberMethod.getClazz().isAssignableFrom(clazz.getClass())) {
                    invokeMethed(obj, oSubscriberMethod, clazz);
                }
            }
        }
    }

    private void invokeMethed(Object obj, OSubscriberMethod oSubscriberMethod, Object clazz) {
        switch (oSubscriberMethod.getoThreadMode()) {
            case MAIN:
                runMain(obj, oSubscriberMethod, clazz);
                break;
            case BACKGROUND:
                runAsynchronous(obj, oSubscriberMethod, clazz);
                break;
        }
    }

    private void runMain(Object obj, OSubscriberMethod oSubscriberMethod, Object clazz) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            invoke(obj, oSubscriberMethod, clazz);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    invoke(obj, oSubscriberMethod, clazz);
                }
            });
        }
    }

    private void runAsynchronous(Object obj, OSubscriberMethod oSubscriberMethod, Object clazz) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                invoke(obj, oSubscriberMethod, clazz);
            }
        });
    }

    private void invoke(Object obj, OSubscriberMethod oSubscriberMethod, Object clazz) {
        try {
            oSubscriberMethod.getMethod().invoke(obj, clazz);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void unRegister(Object obj) {
        List<OSubscriberMethod> oSubscriberMethods = mCacheMap.get(obj);
        if (oSubscriberMethods != null) {
            mCacheMap.remove(obj);
        }
    }
}

package com.oyf.codecollection.test;

import java.util.concurrent.CountDownLatch;

/**
 * @创建者 oyf
 * @创建时间 2020/7/14 15:56
 * @描述
 **/
public class TestCountDownLatch {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for (int i = 0; i < 4; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    log("hello");
                    log("number===" + countDownLatch.getCount() + "");
                    try {
                        countDownLatch.countDown();
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    log("word");
                }
            }).start();
        }
    }

    private static void log(String string) {
        System.out.println(string);
    }
}

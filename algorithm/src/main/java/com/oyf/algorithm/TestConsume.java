package com.oyf.algorithm;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 **/
public class TestConsume {

    public static void main(String[] args) {
        final Storage storage = new Storage();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        storage.put();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Object o = storage.get();
                        if (Integer.parseInt(o.toString()) > 20) {
                            Thread.sleep(200);
                        } else {
                            Thread.sleep(2000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    static class Storage {

        private LinkedList mLinkedList;
        private int max = 10;
        private volatile int i = 0;

        public Storage() {
            mLinkedList = new LinkedList();
        }

        public synchronized void put() {
            while (mLinkedList.size() == max) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mLinkedList.add(i);
            System.out.println("create=" + i + ",size=" + mLinkedList.size());
            i++;
            notifyAll();
        }

        public synchronized Object get() {
            while (mLinkedList.size() == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Object o = mLinkedList.removeFirst();
            System.out.println("use=" + o + ",size=" + mLinkedList.size());
            notifyAll();
            return o;
        }
    }
    /**
     * 使用队列形式的生产者消费者模式
     * */
/*    public static void main(String[] args) {
        final ArrayBlockingQueue<String> blockingDeque = new ArrayBlockingQueue<String>(10);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                        String take = blockingDeque.take();

                        System.out.println("use=" + take + ",over=" + blockingDeque.size());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("create=num=" + i);
                        blockingDeque.put("num=" + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
            }
        }).start();
    }*/
}

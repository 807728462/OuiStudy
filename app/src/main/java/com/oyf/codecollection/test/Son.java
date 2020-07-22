package com.oyf.codecollection.test;

import com.oyf.basemodule.log.LogUtils;

/**
 * @创建者 oyf
 * @创建时间 2020/7/9 16:40
 * @描述
 **/
public class Son extends Person {
    static {
        System.out.println("test-----Son的，静态代码块");
    }

     {
        System.out.println("test-----Son的普通代码块");
    }

    public Son() {
        System.out.println("test-----Son的构造方法");
    }

    private static String staticVar = getStatic();
    public String parent = getUnStatic();

    private String getUnStatic() {
        System.out.println("test-----Son的普通方法");
        return "";
    }

    private static String getStatic() {
        System.out.println("test-----Son   静态方法");
        return "";
    }
}

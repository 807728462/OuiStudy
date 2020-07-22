package com.oyf.codecollection.test;

import com.oyf.basemodule.log.LogUtils;

/**
 * @创建者 oyf
 * @创建时间 2020/7/9 16:36
 * @描述
 **/
public class Person {
    static {
         System.out.println("test-----person的static，静态代码块");
    }

     {
         System.out.println("test-----person的普通代码块");
    }

    public Person() {
         System.out.println("test-----person的构造方法");
    }
    public String parent = getUnStatic();
    private static String staticVar = getStatic();
    private String getUnStatic() {
        System.out.println("test-----person的普通方法");
        return "";
    }

    private static String getStatic() {
        System.out.println("test-----person   静态方法");
        return "";
    }

}

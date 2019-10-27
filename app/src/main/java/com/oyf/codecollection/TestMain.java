package com.oyf.codecollection;

import android.app.Activity;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class TestMain {

    public static void main(String[] args){
        List<String> name = new ArrayList<String>();
        List<Integer> age = new ArrayList<Integer>();
        List<Number> number = new ArrayList<Number>();

        name.add("icon");
        age.add(18);
        number.add(314);
        ArrayList<Number> list = new ArrayList<>();
        list.add(1);
        ArrayList<? super Number> list1 = new ArrayList<>();

        list1.add(new Integer(1)); //error
        list1.add(new Float(1.2f));
        Object object = list1.get(0);
        System.out.println("***************"+getData(age));

//        getUperNumber(name);//1
        getUperNumber(age);//2
        getUperNumber(number);//3

    }
    public static <T extends Object> T getData(List<T> data) {
       return data.get(0);
    }

    public static void getUperNumber(List data) {
        System.out.println("data :" + data.get(0));
    }
}

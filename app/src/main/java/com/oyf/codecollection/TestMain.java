package com.oyf.codecollection;

import android.app.Activity;
import android.graphics.Region;
import android.util.Log;

import com.oyf.basemodule.weight.ProgressView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestMain {

    public static volatile int a = 0;

    public static void main(String[] args) {

      /*  String property = System.getProperty("sun.boot.class.path");
        System.out.println(property);
        System.out.println("----------------");
        System.out.println(System.getProperty("java.ext.dirs"));
        System.out.println("----------------");
        ClassLoader classLoader = TestMain.class.getClassLoader();

        while (classLoader != null) {

            System.out.println("classloder=" + classLoader.toString());
            classLoader = classLoader.getParent();
        }

        MyClassLoder classLoder = new MyClassLoder();

        try {
            Class<?> aClass = classLoder.loadClass("TestClass.class");
            if (aClass != null) {
                Object o = aClass.newInstance();
                Method say = aClass.getDeclaredMethod("say", null);
                say.invoke(o, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static class MyClassLoder extends ClassLoader {
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            Class<?> clazz = null;
            byte[] bytes = loadClassData(name);
            if (bytes != null) {
                clazz = defineClass(name, bytes, 0, bytes.length);
            }
            return clazz;
        }

        private byte[] loadClassData(String name) {
            File file = new File("D:\\company\\tcode\\OuiStudy\\app\\src\\main\\java\\com\\oyf\\codecollection\\" + name);
            FileInputStream in = null;
            ByteArrayOutputStream out = null;
            try {
                in = new FileInputStream(file);
                out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                    out.flush();
                }
                return out.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) in.close();
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

    }


    public static <T extends Object> T getData(List<T> data) {
        return data.get(0);
    }

    public static void getUperNumber(List data) {
        System.out.println("data :" + data.get(0));
    }
}

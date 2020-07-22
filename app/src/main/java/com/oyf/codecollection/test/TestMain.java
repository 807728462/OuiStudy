package com.oyf.codecollection.test;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.transform.Templates;

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

       /* SingleNode singleNode = new SingleNode();
        singleNode.add(new Node(1));
        singleNode.add(new Node(2));
        singleNode.add(new Node(3));
        singleNode.list();
        System.out.println("node.size=" + singleNode.getLength());
        SingleNode reservt = singleNode.reservt();
        reservt.list();*/
       /* CircleSingleNode circleSingleNode = new CircleSingleNode();
        circleSingleNode.add(new Node(1));
        circleSingleNode.add(new Node(2));
        circleSingleNode.add(new Node(3));
        circleSingleNode.add(new Node(4));
        circleSingleNode.add(new Node(5));
        circleSingleNode.list();

        System.out.println("node.size=" + circleSingleNode.getLength());
        List count = circleSingleNode.count(1, 2);
        for (Object o : count) {
            System.out.println("node=" + o.toString());
        }
        Stack stack=new Stack();
        stack.pop();
        stack.push(1);
        stack.peek();*/

    }

    public static void change(String str) {
        str = "123";
    }

    public static void changeNode(Node node) {
        node = null;
    }

    static class SingleNode {
        Node head = new Node(0);

        public SingleNode() {
        }

        public void add(Node node) {
            Node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = node;
        }

        public void list() {
            Node node = head.next;
            while (node != null) {
                System.out.println("node.num=" + node.num);
                node = node.next;
            }
        }

        public int getLength() {
            int le = 0;
            Node node = head.next;
            while (node != null) {
                node = node.next;
                le++;
            }
            return le;
        }

        public SingleNode reservt() {
            Node node = head.next;
            if (node == null && getLength() < 1) {
                System.out.println("链表");
                return null;
            }
            Node cur = head.next;
            Node next;
            Node newNode = new Node(0);

            while (cur != null) {
                next = cur.next;
                cur.next = newNode.next;
                newNode.next = cur;

                cur = next;
            }

            SingleNode singleNode = new SingleNode();
            singleNode.head.next = newNode;
            return singleNode;
        }
    }

    static class CircleSingleNode {
        Node frist;

        public void add(Node node) {
            if (frist == null) {
                frist = node;
                frist.next = frist;
                return;
            }
            Node temp = frist;
            while (temp.next != frist) {
                temp = temp.next;
            }
            node.next = temp.next;
            temp.next = node;
            frist = node.next;
        }

        public int getLength() {
            int le = 0;
            Node node = frist;
            while (true) {
                le++;
                node = node.next;
                if (node == frist) {
                    break;
                }
            }
            return le;
        }

        public List count(int start, int num) {
            if (start > getLength()) {
                return null;
            }

            Node helper = frist;
            while (helper.next != frist) {
                helper = helper.next;
            }

            for (int i = 0; i < start - 1; i++) {
                helper = helper.next;
                frist = frist.next;
            }

            List list = new ArrayList();
            while (true) {
                if (helper == frist) {
                    //已经到最后一个了
                    break;
                }

                for (int i = 0; i < num - 1; i++) {
                    helper = helper.next;
                    frist = frist.next;
                }
                list.add(frist);
                helper.next = frist.next;
                frist = frist.next;

            }
            list.add(helper);
            return list;
        }

        public void list() {
            Node node = frist;
            while (node.next != frist) {
                System.out.println("node.num=" + node.num);
                node = node.next;
            }
            System.out.println("node.num=" + node.num);
        }

    }

    static class Node {
        int num;
        Node next;

        public Node(int num) {
            this.num = num;
        }

        @NonNull
        @Override
        public String toString() {
            return num + "";
        }
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

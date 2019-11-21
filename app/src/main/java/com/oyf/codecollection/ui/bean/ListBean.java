package com.oyf.codecollection.ui.bean;

/**
 * @创建者 oyf
 * @创建时间 2019/11/16 9:36
 * @描述
 **/
public class ListBean {
    public String title;
    public String details;
    public  Class<?> clazz;

    public ListBean(String title, String details, Class<?> clazz) {
        this.title = title;
        this.details = details;
        this.clazz = clazz;
    }
}

package com.oyf.skin.bean;

import android.content.res.TypedArray;
import android.util.SparseIntArray;

/**
 * @创建者 oyf
 * @创建时间 2020/6/4 15:28
 * @描述
 **/
public class AttrsBean {
    //使用map存储需要更换的属性
    private SparseIntArray resourcesMap;
    private static final int DEFAULT_VALUE = -1;

    public AttrsBean() {
        resourcesMap = new SparseIntArray();
    }

    /**
     * 存储 储控件的key、value
     *
     * @param typedArray 控件属性的类型集合，如：background / textColor
     * @param styleable  自定义属性，参考value/attrs.xml
     */
    public void saveViewResource(TypedArray typedArray, int[] styleable) {
        for (int i = 0; i < typedArray.length(); i++) {
            int key = styleable[i];
            int resourceId = typedArray.getResourceId(i, DEFAULT_VALUE);
            resourcesMap.put(key, resourceId);
        }
    }

    /**
     * 获取控件某属性的resourceId
     *
     * @param styleable 自定义属性，参考value/attrs.xml
     * @return 某控件某属性的resourceId
     */
    public int getViewResource(int styleable) {
        return resourcesMap.get(styleable);
    }
}

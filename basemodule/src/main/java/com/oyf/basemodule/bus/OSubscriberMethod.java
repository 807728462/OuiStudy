package com.oyf.basemodule.bus;

import java.lang.reflect.Method;

public class OSubscriberMethod {

    private Method method;

    private OThreadMode oThreadMode;

    private Class<?> clazz;

    public OSubscriberMethod(Method method, OThreadMode oThreadMode, Class<?> clazz) {
        this.method = method;
        this.oThreadMode = oThreadMode;
        this.clazz = clazz;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public OThreadMode getoThreadMode() {
        return oThreadMode;
    }

    public void setoThreadMode(OThreadMode oThreadMode) {
        this.oThreadMode = oThreadMode;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
}

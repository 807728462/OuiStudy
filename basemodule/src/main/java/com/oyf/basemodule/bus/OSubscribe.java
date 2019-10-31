package com.oyf.basemodule.bus;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OSubscribe {
    OThreadMode threadMode() default OThreadMode.MAIN;
}

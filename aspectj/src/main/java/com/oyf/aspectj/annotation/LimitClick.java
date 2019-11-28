package com.oyf.aspectj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @创建者 oyf
 * @创建时间 2019/11/27 10:30
 * @描述
 **/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitClick {

    long value() default 500l;
}

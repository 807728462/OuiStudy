package com.oyf.aspectj;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.oyf.aspectj.annotation.LimitClick;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @创建者 oyf
 * @创建时间 2019/11/27 10:34
 * @描述
 **/
@Aspect
public class LimitClickAspectJ {

    private volatile long lastTime = 0;

    @Pointcut("execution(@com.oyf.aspectj.annotation.LimitClick * *(..))")
    public void methodLimit() {
    }


    @Around("methodLimit()")
    public Object limitClick(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取context
        Context context = (Context) joinPoint.getThis();
        //获取
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            return joinPoint.proceed();
        }
        //获取方法的签名
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        //获取方法上的注解
        LimitClick annotation = method.getAnnotation(LimitClick.class);
        if (annotation == null) {
            return joinPoint.proceed();
        }
        long value = annotation.value();
        long current = System.currentTimeMillis();
        if (current - lastTime < value) {
            return null;
        }
        lastTime = current;
        return joinPoint.proceed();
    }

}

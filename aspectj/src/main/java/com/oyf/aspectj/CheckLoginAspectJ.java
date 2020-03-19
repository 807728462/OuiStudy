package com.oyf.aspectj;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @创建者 oyf
 * @创建时间 2019/11/27 10:34
 * @描述 检查登录
 **/
@Aspect
public class CheckLoginAspectJ {

    @Around("execution(void *.oyf.aspectj.TestUtils.click* (..))")
    public Object check(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.d("test", "----------------CheckLoginAspectJ");
        return joinPoint.proceed();
    }
}

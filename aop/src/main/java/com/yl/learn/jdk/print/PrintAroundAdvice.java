package com.yl.learn.jdk.print;

import com.yl.learn.AroundAdvice;

import java.lang.reflect.Method;

/**
 * 打印方法调用 aop
 * @author YuanLi
 */
public class PrintAroundAdvice implements AroundAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("[Before Invoke Method] " + method.toString());
    }

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("[After Invoke Method] " + method.toString());
    }

    @Override
    public void handle(Object proxied, Method method, Object[] args, Throwable th) throws Throwable{
        System.out.println("[Error Invoke Method] " + method.toString() + " [Exception] " + th);
        throw th;
    }
}

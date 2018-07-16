package com.yl.learn.cglib;

import com.yl.learn.*;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class DefaultCglibDynamicProxy extends AbstractDynamicProxy implements CglibDynamicProxy {

    private Class proxiedClass;

    public DefaultCglibDynamicProxy(Class proxiedClass, MethodBeforeAdvice beforeAdvice
            , AfterReturningAdvice returningAdvice, ThrowsAdvice throwsAdvice) {

        super(null, beforeAdvice, returningAdvice, throwsAdvice);
        this.proxiedClass = proxiedClass;
    }

    public DefaultCglibDynamicProxy(Class proxiedClass, AroundAdvice aroundAdvice) {

        super(null, aroundAdvice);
        this.proxiedClass = proxiedClass;
    }

    @Override
    public <T> T getProxy() {
        return (T) Enhancer.create(proxiedClass, this);
    }

    @Override
    public Object intercept(Object proxied, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result = null;
        try {
            executeBefore(method, args, proxied);

            result = proxy.invokeSuper(proxied, args);

            executeReturning(result, method, args, proxied);
        }
        catch (Throwable th) {
            executeThrows(proxied, method, args, th);
        }
        return result;
    }
}

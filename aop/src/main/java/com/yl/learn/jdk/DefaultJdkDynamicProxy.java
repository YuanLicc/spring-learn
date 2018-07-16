package com.yl.learn.jdk;

import com.yl.learn.*;
import com.yl.test.*;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 抽象JDK dynamic Proxy
 * @author YuanLi
 */
public class DefaultJdkDynamicProxy extends AbstractDynamicProxy implements JdkDynamicProxy {

    public DefaultJdkDynamicProxy(Object proxied, MethodBeforeAdvice beforeAdvice
            , AfterReturningAdvice returningAdvice, ThrowsAdvice throwsAdvice) {

        super(proxied, beforeAdvice, returningAdvice, throwsAdvice);
    }

    public DefaultJdkDynamicProxy(Object proxied, AroundAdvice aroundAdvice) {

        super(proxied, aroundAdvice);
    }

    @Override
    public <T> T getProxy() {
        Class proxiedClass = proxied.getClass();

        return (T) Proxy.newProxyInstance(proxiedClass.getClassLoader(),
                proxiedClass.getInterfaces(),
                this);

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {
            executeBefore(method, args, proxied);

            result = method.invoke(proxied, args);

            executeReturning(result, method, args, proxied);
        }
        catch (Throwable th) {
            executeThrows(proxied, method, args, th);
        }
        return result;
    }
}

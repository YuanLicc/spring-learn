package com.yl.learn;

import java.lang.reflect.Method;

public abstract class AbstractDynamicProxy implements ProxyAvailable {

    protected Object proxied;

    protected AfterReturningAdvice returningAdvice;

    protected MethodBeforeAdvice beforeAdvice;

    protected ThrowsAdvice throwsAdvice;

    protected AroundAdvice aroundAdvice;

    public AbstractDynamicProxy(Object proxied, MethodBeforeAdvice beforeAdvice
            , AfterReturningAdvice returningAdvice, ThrowsAdvice throwsAdvice) {

        this.proxied = proxied;
        this.beforeAdvice = beforeAdvice;
        this.returningAdvice = returningAdvice;
        this.throwsAdvice = throwsAdvice;
    }

    public AbstractDynamicProxy(Object proxied, AroundAdvice aroundAdvice) {

        this.proxied = proxied;
        this.aroundAdvice = aroundAdvice;
    }

    @Override
    public abstract <T> T getProxy();

    protected void executeThrows(Object proxied, Method method, Object[] args, Throwable th) throws Throwable {
        if(aroundAdvice != null) {
            aroundAdvice.handle(proxied, method, args, th);
        }
        else if(throwsAdvice != null) {
            throwsAdvice.handle(proxied, method, args, th);
        }
    }

    protected void executeReturning(Object result, Method method, Object[] args, Object proxied) throws Throwable {
        if(aroundAdvice != null) {
            aroundAdvice.afterReturning(result, method, args, proxied);
        }
        else if(returningAdvice != null) {
            returningAdvice.afterReturning(result, method, args, proxied);
        }
    }

    protected void executeBefore(Method method, Object[] args, Object proxied) throws Throwable{
        if(aroundAdvice != null) {
            aroundAdvice.before(method, args, proxied);
        }
        else if(beforeAdvice != null) {
            beforeAdvice.before(method, args, proxied);
        }
    }

}

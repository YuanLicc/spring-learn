package com.yl.learn;

import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

public interface AfterReturningAdvice extends AfterAdvice {

    void afterReturning(@Nullable Object returnValue, Method method, Object[] args, @Nullable Object proxied) throws Throwable;

}

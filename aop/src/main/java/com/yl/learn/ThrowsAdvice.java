package com.yl.learn;

import java.lang.reflect.Method;

public interface ThrowsAdvice extends AfterAdvice {

    void handle(Object proxied, Method method, Object[] args, Throwable ex) throws Throwable;

}

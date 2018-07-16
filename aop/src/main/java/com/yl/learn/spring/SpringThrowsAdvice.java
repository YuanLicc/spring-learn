package com.yl.learn.spring;

import org.springframework.aop.ThrowsAdvice;

import java.lang.reflect.Method;

public interface SpringThrowsAdvice extends ThrowsAdvice {
    void handle(Object proxied, Method method, Object[] args, Throwable ex);
}

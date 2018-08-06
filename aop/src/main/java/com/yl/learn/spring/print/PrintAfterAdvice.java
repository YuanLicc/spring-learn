package com.yl.learn.spring.print;

import com.yl.common.util.PrintUtil;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class PrintAfterAdvice implements AfterReturningAdvice {
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        PrintUtil.template("After Invoke Method", () -> {
            PrintUtil.println("Invoke method: " + method.toString());
            PrintUtil.println("Return Value: " + returnValue);
        });
    }
}

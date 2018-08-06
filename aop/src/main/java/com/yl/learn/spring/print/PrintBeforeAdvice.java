package com.yl.learn.spring.print;

import com.yl.common.util.PrintUtil;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class PrintBeforeAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        PrintUtil.template("Before Invoke Method", () -> {
            PrintUtil.println("Invoke method: " + method.toString());
            PrintUtil.println("Parameters: " + args.toString());
        });
    }

}

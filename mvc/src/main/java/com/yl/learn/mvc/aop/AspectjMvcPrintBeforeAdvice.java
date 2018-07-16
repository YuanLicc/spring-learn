package com.yl.learn.mvc.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectjMvcPrintBeforeAdvice {

    @Before(value = "execution(* com.yl.test.mvc..*.*(..))")
    public void after(JoinPoint joinPoint) {

    }

}

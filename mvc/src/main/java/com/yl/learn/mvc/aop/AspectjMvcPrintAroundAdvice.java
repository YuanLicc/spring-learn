package com.yl.learn.mvc.aop;

import com.yl.learn.spring.aspectj.AspectJUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;

@Aspect
public class AspectjMvcPrintAroundAdvice {

    @Pointcut("execution(* com.yl.learn.mvc..*.*(..))")
    private void aspectjMethod(){}

    @Around("aspectjMethod()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = null;

        Object proxied = proceedingJoinPoint.getTarget();
        Object[] args = proceedingJoinPoint.getArgs();
        Method method = AspectJUtil.getMethod(proceedingJoinPoint);

        try {
            before(method, args, proxied);

            result = proceedingJoinPoint.proceed();

            afterReturning(result, method, args, proxied);
        }
        catch (Throwable throwable) {
            handle(proxied, method, args, throwable);
        }
        return result;
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("[Before Invoke Method] " + method.toString());
    }

    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("[After Invoke Method] " + method.toString());
    }

    public void handle(Object proxied, Method method, Object[] args, Throwable th) throws Throwable{
        System.out.println("[Error Invoke Method] " + method.toString() + " [Exception] " + th);
        throw th;
    }

}

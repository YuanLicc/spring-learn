package com.yl.learn.spring.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

public class AspectJUtil {

    public static Method getMethod(ProceedingJoinPoint proceedingJoinPoint) {
        Signature signature = proceedingJoinPoint.getSignature();

        MethodSignature methodSignature = null;

        if(signature instanceof MethodSignature) {
            return ((MethodSignature)signature).getMethod();
        }

        return null;
    }
}

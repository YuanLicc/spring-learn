package com.yl.learn.spring.trace;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.SimpleTraceInterceptor;
import org.springframework.stereotype.Component;

/**
 * 若谁看见了这个demo，请不要模仿，因为我重载的方法已经改变了默认实现的行为
 * @author YuanLi
 */
@Component
public class PrintTraceInterceptor extends SimpleTraceInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Log logger = getLoggerForInvocation(invocation);
        logger.info("before invoke:" + invocation.getMethod());
        return invokeUnderTrace(invocation, logger);
    }

}

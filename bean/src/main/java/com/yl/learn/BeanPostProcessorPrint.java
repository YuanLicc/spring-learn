package com.yl.learn;

import com.yl.common.util.PrintUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class BeanPostProcessorPrint implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        PrintUtil.template("Bean : " + beanName,
            () -> {
                PrintUtil.println("初始化前");
            }
        );
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        PrintUtil.template("Bean : " + beanName,
                () -> {
                    PrintUtil.println("初始化后");
                }
        );
        return bean;
    }
}

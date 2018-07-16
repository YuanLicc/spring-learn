package com.yl.learn;

/**
 * 代理对象可获取的，实现此接口标志着可获取代理对象
 * @author YuanLi
 */
@FunctionalInterface
public interface ProxyAvailable {
    <T> T getProxy();
}

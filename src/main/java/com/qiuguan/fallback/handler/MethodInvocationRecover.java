package com.qiuguan.fallback.handler;

/**
 * @author created by qiuguan on 2021/12/24 11:52
 */
public interface MethodInvocationRecover<T> {

    /**
     * recover
     * @param args
     * @param throwable
     * @return
     */
    T recover(Object[] args, Throwable throwable);
}

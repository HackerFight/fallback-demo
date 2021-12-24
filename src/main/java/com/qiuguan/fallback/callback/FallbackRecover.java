package com.qiuguan.fallback.callback;

import org.springframework.beans.factory.BeanFactory;

/**
 * @author created by qiuguan on 2021/12/23 18:24
 */
public interface FallbackRecover {

    /**
     * recover D
     * @param fallbackCallback
     * @param <T>
     * @param <E>
     * @return
     * @throws Throwable
     */
    <T, E extends Throwable> T recover(FallbackCallback<T, E> fallbackCallback) throws Throwable;

    /**
     * 22
     * @param fallbackCallback
     * @param recoverCallback
     * @param <T>
     * @param <E>
     * @return
     * @throws Throwable
     */
    <T, E extends Throwable> T recover(FallbackCallback<T, E> fallbackCallback, RecoverCallback<T> recoverCallback) throws Throwable;
}

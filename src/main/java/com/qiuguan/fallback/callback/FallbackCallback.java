package com.qiuguan.fallback.callback;

/**
 * @author created by qiuguan on 2021/12/24 11:21
 */
public interface FallbackCallback<T, E extends Throwable> {

    /**
     * execute
     * @param fallbackContext
     * @return
     * @throws E
     */
    T doExecute(FallbackContext fallbackContext) throws E;
}

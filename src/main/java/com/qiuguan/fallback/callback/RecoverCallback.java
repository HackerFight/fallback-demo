package com.qiuguan.fallback.callback;

/**
 * @author created by qiuguan on 2021/12/24 15:33
 */
public interface RecoverCallback<T> {

    /**
     * recover b
     * @param throwable
     * @return
     */
    T recover(FallbackContext fallbackContext);
}

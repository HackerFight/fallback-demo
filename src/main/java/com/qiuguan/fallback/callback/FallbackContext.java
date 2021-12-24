package com.qiuguan.fallback.callback;


/**
 * @author created by qiuguan on 2021/12/24 10:21
 */
public interface FallbackContext {

    /**
     * 注册异常
     * @param throwable
     */
    void registerThrowable(Throwable throwable);

    Throwable getThrowable();
}

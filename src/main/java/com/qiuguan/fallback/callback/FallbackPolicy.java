package com.qiuguan.fallback.callback;

/**
 * @author created by qiuguan on 2021/12/24 10:44
 */
public interface FallbackPolicy {

    FallbackContext getFallbackContext();


    /**
     * 能否fallback
     * @param throwable
     * @return
     */
    boolean canFallback(Throwable throwable);
}

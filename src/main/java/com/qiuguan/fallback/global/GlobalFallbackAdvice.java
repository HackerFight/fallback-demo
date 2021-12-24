package com.qiuguan.fallback.global;

import com.qiuguan.fallback.ann.FallbackRecover;
import com.qiuguan.fallback.ann.GlobalFallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author created by qiuguan on 2021/12/23 18:04
 */
@GlobalFallback
public class GlobalFallbackAdvice {

    private final Logger logger = LoggerFactory.getLogger(GlobalFallbackAdvice.class);

    @FallbackRecover
    public void globalExceptionCheck(Throwable throwable){
        logger.error("我进行了全局异常捕获：" + throwable);
    }
}

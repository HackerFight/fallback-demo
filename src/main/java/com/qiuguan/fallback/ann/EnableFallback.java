package com.qiuguan.fallback.ann;

import com.qiuguan.fallback.config.FallbackAutoProxyConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author created by qiuguan on 2021/12/23 17:02
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(FallbackAutoProxyConfiguration.class)
public @interface EnableFallback {
}

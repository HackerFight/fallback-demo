package com.qiuguan.fallback.ann;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author created by qiuguan on 2021/12/23 18:03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface GlobalFallback {

}

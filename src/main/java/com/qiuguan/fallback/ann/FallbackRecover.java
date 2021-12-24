package com.qiuguan.fallback.ann;

import java.lang.annotation.*;

/**
 * @author created by qiuguan on 2021/12/24 18:54
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface FallbackRecover {

}

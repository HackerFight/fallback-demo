package com.qiuguan.fallback.ann;

import java.lang.annotation.*;

/**
 * @author created by qiuguan on 2021/12/23 17:02
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Fallback {

    /**
     * 需要进行友好提示的异常
     * @return
     */
    Class<? extends Throwable>[] value() default {};

    /**
     * 当出现异常时，是否需要调用指定的方法
     * @return
     */
    String method() default "";
}

package com.qiuguan.fallback.filter.ex;

import java.io.Serializable;

/**
 * @author created by qiuguan on 2021/12/24 10:39
 */
public interface Classifier<T, C> extends Serializable {

    /**
     * 判断异常是否符合
     * @param classifiable
     * @return
     */
    C classify(T classifiable);
}

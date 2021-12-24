package com.qiuguan.fallback.filter.ex;

import java.util.Map;


/**
 * @author created by qiuguan on 2021/12/24 10:39
 */
public class FallbackExceptionClassifier extends GenericFallbackExceptionClassifier<Throwable, Boolean> {

    public FallbackExceptionClassifier(Boolean defaultValue) {
        super(defaultValue);
    }

    public FallbackExceptionClassifier(Map<Class<? extends Throwable>, Boolean> fallbackMap){
        super(fallbackMap, false);
    }

    public FallbackExceptionClassifier(Map<Class<? extends Throwable>, Boolean> fallbackMap, Boolean defaultValue){
        super(fallbackMap, defaultValue);
    }

    @Override
    public Boolean classify(Throwable classifiable) {
        Boolean classify = super.classify(classifiable);
        return classify;

    }
}

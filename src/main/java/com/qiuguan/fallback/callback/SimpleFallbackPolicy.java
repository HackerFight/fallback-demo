package com.qiuguan.fallback.callback;

import com.qiuguan.fallback.filter.ex.FallbackExceptionClassifier;

import java.util.Collections;
import java.util.Map;


/**
 * @author created by qiuguan on 2021/12/24 10:45
 */
public class SimpleFallbackPolicy implements FallbackPolicy {

    private Object fallbackMethod;

    private final FallbackContext fallbackContext = new SimpleFallbackContext();

    private final FallbackExceptionClassifier classifier;


    public SimpleFallbackPolicy(Object fallbackMethod) {
        this(fallbackMethod, Collections.singletonMap(Exception.class, true));
    }

    public SimpleFallbackPolicy(Object fallbackMethod, Map<Class<? extends Throwable>, Boolean> fallbackMap) {
        this.fallbackMethod = fallbackMethod;
        this.classifier = new FallbackExceptionClassifier(fallbackMap);
    }

    @Override
    public boolean canFallback(Throwable throwable) {
        return classifier.classify(throwable);
    }

    public void setFallbackMethod(Object fallbackMethod) {
        this.fallbackMethod = fallbackMethod;
    }

    @Override
    public FallbackContext getFallbackContext() {
        return fallbackContext;
    }

    private static final class SimpleFallbackContext implements FallbackContext {

        private Throwable lastException;

        @Override
        public void registerThrowable(Throwable throwable) {
            this.lastException = throwable;
        }

        @Override
        public Throwable getThrowable() {
            return lastException;
        }
    }
}

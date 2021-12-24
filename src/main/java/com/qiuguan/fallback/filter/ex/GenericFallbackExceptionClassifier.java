package com.qiuguan.fallback.filter.ex;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author created by qiuguan on 2021/12/24 14:13
 */
public class GenericFallbackExceptionClassifier<T, C> implements Classifier<T, C>{

    private Map<Class<? extends T>, C> classifyMap = new ConcurrentHashMap<>();

    private final C defaultValue;

    public GenericFallbackExceptionClassifier(){
        this(null);
    }

    public GenericFallbackExceptionClassifier(C defaultValue){
        this(new HashMap<Class<? extends T>, C>(), defaultValue);
    }


    public GenericFallbackExceptionClassifier(Map<Class<? extends T>, C> classifyMap, C defaultValue) {
        this.classifyMap = new ConcurrentHashMap<>(classifyMap);
        this.defaultValue = defaultValue;
    }

    public void setTypeMap(Map<Class<? extends T>, C> map) {
        this.classifyMap = new ConcurrentHashMap<Class<? extends T>, C>(map);
    }

    @Override
    public C classify(T classifiable) {
        if (classifiable == null) {
            return defaultValue;
        }

        @SuppressWarnings("unchecked")
        Class<? extends T> exceptionClass = (Class<? extends T>) classifiable.getClass();
        if(this.classifyMap.containsKey(exceptionClass)) {
            return this.classifyMap.get(exceptionClass);
        }

        //check for subclass, 默认是到 Exception.class
        C value = null;
        for (Class<?> cls = exceptionClass; !cls.equals(Object.class) && value == null; cls = cls.getSuperclass()) {
            value = this.classifyMap.get(cls);
        }

        if(value != null) {
            this.classifyMap.put(exceptionClass, value);
        }

        return value == null ? defaultValue : value;
    }
}

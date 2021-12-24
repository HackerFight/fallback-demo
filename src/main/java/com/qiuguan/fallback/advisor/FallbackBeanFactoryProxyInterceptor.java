package com.qiuguan.fallback.advisor;

import com.qiuguan.fallback.ann.Fallback;
import com.qiuguan.fallback.callback.FallbackPolicy;
import com.qiuguan.fallback.callback.FallbackRecover;
import com.qiuguan.fallback.callback.SimpleFallbackPolicy;
import com.qiuguan.fallback.callback.SimpleFallbackRecover;
import com.qiuguan.fallback.handler.RecoverAnnotationRecoveryHandler;
import com.qiuguan.fallback.interceptor.DefaultAutoProxyMethodInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author created by qiuguan on 2021/12/23 17:25
 */
public class FallbackBeanFactoryProxyInterceptor implements MethodInterceptor, BeanFactoryAware {

    private final Map<Object, Map<Method, MethodInterceptor>> delegateMap = new ConcurrentHashMap<>(256);

    private BeanFactory beanFactory;

    public FallbackBeanFactoryProxyInterceptor() {

    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        MethodInterceptor delegate = getDelegate(invocation.getThis(), invocation.getMethod());

        if(delegate != null) {
            return delegate.invoke(invocation);
        }

        return invocation.proceed();
    }

    private MethodInterceptor getDelegate(Object source, Method method) {
        if (!this.delegateMap.containsKey(source) || !this.delegateMap.get(source).containsKey(method)) {
            synchronized (this.delegateMap) {
                if (!this.delegateMap.containsKey(source)) {
                    this.delegateMap.put(source, new HashMap<>());
                }

                Map<Method, MethodInterceptor> icm = this.delegateMap.get(source);
                if (!icm.containsKey(method)) {
                    Fallback annotation = AnnotationUtils.findAnnotation(method, Fallback.class);
                    if(annotation == null) {
                        //去类上寻找
                        annotation = AnnotationUtils.findAnnotation(method.getDeclaringClass(), Fallback.class);
                    }

                    if(annotation == null) {
                        return icm.put(method, null);
                    }

                    MethodInterceptor mi = createInterceptor(source, method, annotation);
                    icm.put(method, mi);
                }
            }
        }

        return this.delegateMap.get(source).get(method);
    }

    private MethodInterceptor createInterceptor(Object source, Method method, Annotation annotation) {
        if(this.beanFactory == null) {
            throw new IllegalStateException("beanFactory must be not null");
        }
        DefaultAutoProxyMethodInterceptor mi = new DefaultAutoProxyMethodInterceptor();
        Map<String, Object> attrs = AnnotationUtils.getAnnotationAttributes(annotation);
        @SuppressWarnings("unchecked")
        Class<? extends Throwable>[] exs = (Class<? extends Throwable>[]) attrs.get("value");
        String fallbackMethod = (String) attrs.get("method");

        Map<Class<? extends Throwable>, Boolean> exMap = new HashMap<>(4);
        if (exs != null && exs.length > 0) {
            for (Class<? extends Throwable> ex : exs) {
                exMap.put(ex, true);
            }
        }

        FallbackPolicy fallbackPolicy;
        if(exMap.isEmpty()) {
            fallbackPolicy = new SimpleFallbackPolicy(fallbackMethod);
        } else {
            fallbackPolicy = new SimpleFallbackPolicy(fallbackMethod, exMap);
        }

        FallbackRecover recover = new SimpleFallbackRecover(fallbackPolicy);

        mi.setFallbackRecover(recover);

        mi.setMethodInvocationRecover(new RecoverAnnotationRecoveryHandler<>(source, method, this.beanFactory));

        return mi;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}

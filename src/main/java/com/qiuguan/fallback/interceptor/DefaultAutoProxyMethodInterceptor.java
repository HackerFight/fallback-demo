package com.qiuguan.fallback.interceptor;

import com.qiuguan.fallback.callback.*;
import com.qiuguan.fallback.handler.MethodInvocationRecover;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.BeanFactory;

/**
 * @author created by qiuguan on 2021/12/23 18:17
 */
public class DefaultAutoProxyMethodInterceptor implements MethodInterceptor {

    private FallbackRecover fallbackRecover;

    private MethodInvocationRecover<?> methodInvocationRecover;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {


        FallbackCallback<Object, Throwable> fallbackCallback = new FallbackCallback<Object, Throwable>() {
            @Override
            public Object doExecute(FallbackContext fallbackContext) throws Throwable {
                try {
                    return invocation.proceed();
                } catch (Throwable e) {
                    throw e;
                }
            }
        };

        if (methodInvocationRecover != null) {
            SimpleRecoverCallback simpleRecoverCallback =
                    new SimpleRecoverCallback(invocation.getArguments(), this.methodInvocationRecover);
            return this.fallbackRecover.recover(fallbackCallback, simpleRecoverCallback);
        }

        return this.fallbackRecover.recover(fallbackCallback);
    }

    public FallbackRecover getFallbackRecover() {
        return fallbackRecover;
    }

    public MethodInvocationRecover<?> getMethodInvocationRecover() {
        return methodInvocationRecover;
    }

    public void setMethodInvocationRecover(MethodInvocationRecover<?> methodInvocationRecover) {
        this.methodInvocationRecover = methodInvocationRecover;
    }

    public void setFallbackRecover(FallbackRecover fallbackRecover) {
        this.fallbackRecover = fallbackRecover;
    }
}

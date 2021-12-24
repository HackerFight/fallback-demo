package com.qiuguan.fallback.handler;


import com.qiuguan.fallback.ann.Fallback;
import com.qiuguan.fallback.ann.FallbackRecover;
import com.qiuguan.fallback.ann.GlobalFallback;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * @author created by qiuguan on 2021/12/24 11:52
 */
public class RecoverAnnotationRecoveryHandler<T> implements MethodInvocationRecover<T> {

    private final Object target;

    private final Method method;

    private final BeanFactory beanFactory;

    public RecoverAnnotationRecoveryHandler(Object target, Method method, BeanFactory beanFactory) {
        this.target = target;
        this.method = method;
        this.beanFactory = beanFactory;
    }


    /**
     * 并没有使用 @Fallback 注解中的 method 方法
     *
     * @param args
     * @param throwable
     * @return
     */
    @Override
    public T recover(Object[] args, Throwable throwable) {

        //再次寻找注解中的 method 属性值
        Fallback annotation = AnnotationUtils.findAnnotation(this.method, Fallback.class);
        Assert.notNull(annotation, "method: (" + this.method.getName() + ") not found annotation @Fallback");
        String fallbackMethodName = annotation.method();
        if (StringUtils.hasText(fallbackMethodName)) {


            //这里可以获取带 Throwable 的参数，因为我要将异常透传
            Method fallbackMethod = findSpecialMethod(fallbackMethodName);
            if (fallbackMethod == null) {
                //这里只能获取没有参数的方法
                fallbackMethod = ReflectionUtils.findMethod(this.target.getClass(), fallbackMethodName);
            }
            if (fallbackMethod == null) {
                throw new IllegalArgumentException("at method:(" + this.method.getName() + ") found @Fallback annotation, but not found 'method attribute' point to the method");
            }

            boolean isAccessible = fallbackMethod.isAccessible();
            try {
                ReflectionUtils.makeAccessible(fallbackMethod);

                //参数个数
                int capacity = fallbackMethod.getParameterCount();
                //可以将原始方法的参数传递给降级方法，但是我这里就不写了，只把异常传过去
                Object[] newArgs = new Object[capacity];
                if (capacity == 1 && Throwable.class.isAssignableFrom(fallbackMethod.getParameterTypes()[0])) {
                    newArgs[0] = throwable;
                }

                @SuppressWarnings("unchecked")
                T result = (T) ReflectionUtils.invokeMethod(fallbackMethod, this.target, newArgs);
                return result;
            } finally {
                fallbackMethod.setAccessible(isAccessible);
            }
        } else {
            //全局查找
            if (this.beanFactory instanceof ListableBeanFactory) {
                String[] beanNames = BeanFactoryUtils.beanNamesIncludingAncestors((ListableBeanFactory) this.beanFactory);
                for (String beanName : beanNames) {
                    if (beanName.startsWith("org.") || beanName.startsWith("java.")) {
                        continue;
                    }

                    if (isGlobalFallback(beanName)) {
                        return globalFallbackInvoke(beanName, throwable);
                    }
                }
            } else {
                throw new IllegalStateException("beanFactory :" + this.beanFactory + " is not ListableBeanFactory type");
            }

        }

        return null;
    }

    private Method findSpecialMethod(String fallbackMethodName) {
        /**
         * 找带这三个参数的 fallbackMethodName 方法
         * RuntimeException -> Exception -> Throwable
         */
        Class<? extends Throwable> cls = RuntimeException.class;
        Method m = null;
        for(Class<?> ex = cls; !ex.equals(Object.class) && m == null; ex = ex.getSuperclass()) {
            m = ReflectionUtils.findMethod(this.target.getClass(), fallbackMethodName, ex);
        }
        return m;
    }

    private T globalFallbackInvoke(String beanName, Throwable throwable) {
        Object bean = beanFactory.getBean(beanName);
        final AtomicBoolean found = new AtomicBoolean(false);
        final AtomicReference<Method> reference = new AtomicReference<>();
        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                if (found.get()) {
                    return;
                }

                FallbackRecover annotation = AnnotationUtils.findAnnotation(method, FallbackRecover.class);
                if (annotation != null) {
                    found.set(true);
                    reference.set(method);
                }
            }
        }, new ReflectionUtils.MethodFilter() {
            @Override
            public boolean matches(Method method) {
                return !ReflectionUtils.isObjectMethod(method);
            }
        });

        if (found.get()) {
            Method method = reference.get();
            ReflectionUtils.makeAccessible(method);

            try {
                int length = method.getParameterTypes().length;
                if (length > 1) {
                    throw new IllegalArgumentException("@FallbackRecover method only have one parameters and must be Throwable");
                }

                Object[] argsToUse = new Object[length];
                if (length > 0) {
                    argsToUse[0] = throwable;
                }
                @SuppressWarnings("unchecked")
                T result = (T) ReflectionUtils.invokeMethod(method, bean, argsToUse);
                return result;
            } catch (Exception e) {
                throw new IllegalArgumentException("invoke global fallback fail.......", e);
            }
        }

        throw new IllegalArgumentException("not found @FallbackRecover annotation method, please check ");
    }

    private boolean isGlobalFallback(String beanName) {
        Object bean = beanFactory.getBean(beanName);
        return AnnotatedElementUtils.hasAnnotation(bean.getClass(), GlobalFallback.class);
    }
}

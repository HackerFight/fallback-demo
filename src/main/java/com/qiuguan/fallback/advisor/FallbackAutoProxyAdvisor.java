package com.qiuguan.fallback.advisor;

import com.qiuguan.fallback.ann.Fallback;
import com.qiuguan.fallback.filter.FallbackAnnotationClassFilter;
import com.sun.org.apache.xml.internal.security.Init;
import org.aopalliance.aop.Advice;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.beans.factory.InitializingBean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author created by qiuguan on 2021/12/23 17:05
 */
public class FallbackAutoProxyAdvisor extends AbstractPointcutAdvisor implements InitializingBean {

    private Pointcut pointcut;

    private Advice advice;

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    public void configure(Advice advice) {
        this.advice = advice;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.pointcut = new FallbackProxyBeanPointcut(Fallback.class);
    }

    private static class FallbackProxyBeanPointcut extends StaticMethodMatcherPointcut {

        private final MethodMatcher methodResolver;

        public FallbackProxyBeanPointcut(Class<? extends Annotation> annotationType){
            this.methodResolver = new AnnotationMethodMatcher(annotationType);
            setClassFilter(new FallbackAnnotationClassFilter(annotationType));
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return this.methodResolver.matches(method, targetClass);
        }
    }
}

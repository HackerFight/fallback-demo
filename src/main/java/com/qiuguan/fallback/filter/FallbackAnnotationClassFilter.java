package com.qiuguan.fallback.filter;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author created by qiuguan on 2021/12/23 17:29
 */
public class FallbackAnnotationClassFilter extends AnnotationClassFilter  {

    private final AnnotationMethodResolver resolver;

    public FallbackAnnotationClassFilter(Class<? extends Annotation> annotationType) {
        super(annotationType);
        this.resolver = new AnnotationMethodResolver(annotationType);
    }

    @Override
    public boolean matches(Class<?> clazz) {
        return super.matches(clazz) || this.resolver.hasAnnotationMethod(clazz);
    }

    private static class AnnotationMethodResolver {

        private final  Class<? extends Annotation> annotationType;

        public AnnotationMethodResolver(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        public boolean hasAnnotationMethod(Class<?> clazz) {
            final AtomicBoolean found = new AtomicBoolean(false);
            ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
                @Override
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    if(found.get()) {
                        return;
                    }

                    Annotation annotation = AnnotationUtils.findAnnotation(method, annotationType);
                    if(annotation != null) {
                        found.set(true);
                    }
                }
            }, new ReflectionUtils.MethodFilter() {
                @Override
                public boolean matches(Method method) {
                    return !ReflectionUtils.isObjectMethod(method);
                }
            });

            return found.get();
        }
    }
}

package com.qiuguan.fallback.config;

import com.qiuguan.fallback.advisor.FallbackAutoProxyAdvisor;
import com.qiuguan.fallback.advisor.FallbackBeanFactoryProxyInterceptor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Role;
import org.springframework.core.type.AnnotationMetadata;


/**
 * @author created by qiuguan on 2021/12/23 17:02
 */
@Configuration
public class FallbackAutoProxyConfiguration implements ImportBeanDefinitionRegistrar {

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = "com.qiuguan.fallback.custom.advisor")
    public FallbackAutoProxyAdvisor advisor(){
        FallbackAutoProxyAdvisor advisor = new FallbackAutoProxyAdvisor();
        advisor.configure(advice());
        return advisor;
    }

    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean(name = "com.qiuguan.fallback.custom.advcie")
    public Advice advice(){
        FallbackBeanFactoryProxyInterceptor advice = new FallbackBeanFactoryProxyInterceptor();
        return advice;
    }

    /**
     * 注册最基础的后置处理器
     * @see org.springframework.aop.framework.autoproxy.InfrastructureAdvisorAutoProxyCreator
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
    }
}

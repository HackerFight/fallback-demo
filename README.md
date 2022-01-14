## 1.项目说明
这是一个自定义的基于AOP的服务降级项目(一个小demo, 目的是熟悉如何拓展AOP)

## 2. 自定义AOP的基本步骤（模型）
* AOP 离不开增强器，可以理解为增强器是完成AOP功能的核心组件；例如 FallbackAutoProxyAdvisor 类，还可以参考spring事务，缓存，异步等.
* 基本模型: 一个增强器(Advisor)中包含一个通知(Advice)和一个连接点(Pointcut), 连接点中包含一个ClassFilter 和 一个 MethodMatcher
~~~
   Advisor
     - Advice: 习惯上叫通知，实际上就是拦截器，MethodInterceptor 是他的子接口
     - Pointcut：连接点，就是来选择我们需要增强的类的
        -- ClassFilter: 用来过滤类的，比如哪些类才是我们需要增强的目标类
        -- MethodMatcher:用来匹配方法的，用来检测哪些方法才是需要我们增强的，比如@Transactional 方法才是我们关注的
~~~

## 3. 配置类注册bean
* 主要是注册一个后置处理器，在AOP中spring提供了3个处理器，优先级逐级升高
~~~
 package org.springframework.aop.config;
 
 public abstract class AopConfigUtils {
 
	private static final List<Class<?>> APC_PRIORITY_LIST = new ArrayList<>(3);

	static {
		// Set up the escalation list...
		APC_PRIORITY_LIST.add(InfrastructureAdvisorAutoProxyCreator.class);
		APC_PRIORITY_LIST.add(AspectJAwareAdvisorAutoProxyCreator.class);
		APC_PRIORITY_LIST.add(AnnotationAwareAspectJAutoProxyCreator.class);
	}
 }
 
~~~
我使用的是优先级最低的 InfrastructureAdvisorAutoProxyCreator 处理器
* 注册增强器和通知（切点我在增强器内部定义了）

## 4.增强器 FallbackAutoProxyAdvisor
### 4.1) 放到ioc容器中，在没有使用AspectJ的情况下，必须给增强器一个 @Role(BeanDefinition.ROLE_INFRASTRUCTURE) 角色
### 4.2) 切入点 FallbackProxyBeanPointcut
 * 继承了 StaticMethodMatcherPointcut, 所以天生具有 ClassFilter 和 MethodMatcher 的功能，然后重写方法即可,也可以在内部自己去定义
 > ClassFilter 用来匹配类<br>
 > MethodMatcher 用来匹配方法
 * 后置处理器 InfrastructureAdvisorAutoProxyCreator.postProcessAfterInitialization() 时，先判断 ClassFilter 决定类是否符合条件，然后判断 MethodMatcher，
### 4.3) 通知 FallbackBeanFactoryProxyInterceptor 
 * 代理对象工作时，会进入到这个通知(拦截器) 的 拦截方法，从而完成降级的逻辑
 * 这里可以看下代码逻辑


## 5. 后置处理器 InfrastructureAdvisorAutoProxyCreator
* 后置处理器在bean初始化后(所有bean都要经过)会进行判断是否需要增强，如果需要增强，则创建代理对象
* 通过 new ProxyFactory() 对象去创建代理对象，这里是设置增强器xxxAdvisor
* 这里spring会创建Cglib代理或者JDK代理，如果类实现了接口，就用JDK代理


## 6.其他
 * GenericFallbackExceptionClassifier 降级时异常的校验类
 * GlobalFallbackAdvice 全局的降级类

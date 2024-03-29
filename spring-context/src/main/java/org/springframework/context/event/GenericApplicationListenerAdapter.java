/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.event;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

/**
 * {@link GenericApplicationListener} adapter that determines supported event types
 * through introspecting the generically declared type of the target listener.
 *
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.0
 * @see org.springframework.context.ApplicationListener#onApplicationEvent
 */
public class GenericApplicationListenerAdapter implements GenericApplicationListener, SmartApplicationListener {

	private final ApplicationListener<ApplicationEvent> delegate;

	private final ResolvableType declaredEventType;


	/**
	 * Create a new GenericApplicationListener for the given delegate.
	 * @param delegate the delegate listener to be invoked
	 */
	@SuppressWarnings("unchecked")
	public GenericApplicationListenerAdapter(ApplicationListener<?> delegate) {
		Assert.notNull(delegate, "Delegate listener must not be null");
		this.delegate = (ApplicationListener<ApplicationEvent>) delegate;
		// 根据Listener解析事件类型
		this.declaredEventType = resolveDeclaredEventType(this.delegate);
	}


	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		this.delegate.onApplicationEvent(event);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean supportsEventType(ResolvableType eventType) {
		// 如果是SmartApplicationListener的子类实例
		// 比如：org.springframework.boot.logging.ClasspathLoggingApplicationListener.supportsEventType(ResolvableType)
		//			--> 判断ApplicationStartedEvent是否是ApplicationEnvironmentPreparedEvent,ApplicationFailedEvent 的子类, 很明显,返回false
		if (this.delegate instanceof SmartApplicationListener) {
			// 获取事件类型的class对象
			Class<? extends ApplicationEvent> eventClass = (Class<? extends ApplicationEvent>) eventType.resolve();
			// 直接调用监听器实例的方法
			return (eventClass != null && ((SmartApplicationListener) this.delegate).supportsEventType(eventClass));
		}
		else {
			// 比如：判断 ApplicationStartedEvent 是否是 SpringApplicationEvent 的子类, 判断 ApplicationStartedEvent 是否是 ContextRefreshedEvent 的子类
			return (this.declaredEventType == null || this.declaredEventType.isAssignableFrom(eventType));
		}
	}

	@Override
	public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
		return supportsEventType(ResolvableType.forClass(eventType));
	}

	@Override
	public boolean supportsSourceType(Class<?> sourceType) {
		// 如果不是SmartApplicationListener的子实例，返回true
		// 否则调用子实例的 supportsSourceType 方法
		// 比如：org.springframework.boot.logging.LoggingApplicationListener.supportsSourceType(Class<?>)
		//			--> // 判断SpringApplication 是否是 SpringApplication.class,ApplicationContext.class 的子类,很明显返回true
		return !(this.delegate instanceof SmartApplicationListener) ||
				((SmartApplicationListener) this.delegate).supportsSourceType(sourceType);
	}

	@Override
	public int getOrder() {
		return (this.delegate instanceof Ordered ? ((Ordered) this.delegate).getOrder() : Ordered.LOWEST_PRECEDENCE);
	}


	static ResolvableType resolveDeclaredEventType(Class<?> listenerType) {
		ResolvableType resolvableType = ResolvableType.forClass(listenerType).as(ApplicationListener.class);
		return (resolvableType.hasGenerics() ? resolvableType.getGeneric() : null);
	}

	private static ResolvableType resolveDeclaredEventType(ApplicationListener<ApplicationEvent> listener) {
		ResolvableType declaredEventType = resolveDeclaredEventType(listener.getClass());
		if (declaredEventType == null || declaredEventType.isAssignableFrom(
				ResolvableType.forClass(ApplicationEvent.class))) {
			Class<?> targetClass = AopUtils.getTargetClass(listener);
			if (targetClass != listener.getClass()) {
				declaredEventType = resolveDeclaredEventType(targetClass);
			}
		}
		return declaredEventType;
	}

}

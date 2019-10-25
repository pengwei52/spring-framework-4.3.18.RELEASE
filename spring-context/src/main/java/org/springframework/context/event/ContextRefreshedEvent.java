/*
 * Copyright 2002-2012 the original author or authors.
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

import org.springframework.context.ApplicationContext;

/**
 * <pre>
 * 当ApplicationContext容器初始化完成或者被刷新的时候，就会发布该事件。
 * 比如调用ConfigurableApplicationContext接口中的refresh()方法。
 * 此处的容器初始化指的是所有的Bean都被成功装载，后处理（post-processor）Bean被检测到并且激活，所有单例Bean都被预实例化，ApplicationContext容器已经可以使用。
 * 只要上下文没有被关闭，刷新可以被多次触发。XMLWebApplicationContext支持热刷新，GenericApplicationContext不支持热刷新。
 * </pre>
 * 
 * Event raised when an {@code ApplicationContext} gets initialized or refreshed.
 *
 * @author Juergen Hoeller
 * @since 04.03.2003
 * @see ContextClosedEvent
 */
@SuppressWarnings("serial")
public class ContextRefreshedEvent extends ApplicationContextEvent {

	/**
	 * Create a new ContextRefreshedEvent.
	 * @param source the {@code ApplicationContext} that has been initialized
	 * or refreshed (must not be {@code null})
	 */
	public ContextRefreshedEvent(ApplicationContext source) {
		super(source);
	}

}

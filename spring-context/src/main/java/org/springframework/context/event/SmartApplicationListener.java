/*
 * Copyright 2002-2015 the original author or authors.
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

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

/**
 * <pre>
 * 标准ApplicationListener接口的扩展变体，公开更多的元数据，如受支持的事件类型。
 * 强烈建议用户使用GenericApplicationListener接口，因为它提供了对基于泛型的事件类型的改进检测。
 * </pre>
 * 
 * Extended variant of the standard {@link ApplicationListener} interface,
 * exposing further metadata such as the supported event type.
 *
 * <p>Users are <bold>strongly advised</bold> to use the {@link GenericApplicationListener}
 * interface instead as it provides an improved detection of generics-based
 * event types.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see GenericApplicationListener
 */
// 支持监听器的顺序，即监听器执行的顺序，值越小优先级越高  
public interface SmartApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {

	/**
	 * 如果实现支持该事件类型 那么返回true  
	 * 
	 * Determine whether this listener actually supports the given event type.
	 */
	boolean supportsEventType(Class<? extends ApplicationEvent> eventType);

	/**
	 * 如果实现支持“目标”类型，那么返回true  
	 * 
	 * Determine whether this listener actually supports the given source type.
	 */
	boolean supportsSourceType(Class<?> sourceType);

}

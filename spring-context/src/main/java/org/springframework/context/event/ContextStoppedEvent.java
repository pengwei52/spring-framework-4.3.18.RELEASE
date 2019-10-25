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
 * 当ApplicationContext容器停止的时候发布事件，即调用 ConfigurableApplicationContext 的close方法的时候。
 * 这里的停止是指，所有被容器管理生命周期的Bean接到一个明确的停止信号。
 * </pre>
 * Event raised when an {@code ApplicationContext} gets stopped.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 * @see ContextStartedEvent
 */
@SuppressWarnings("serial")
public class ContextStoppedEvent extends ApplicationContextEvent {

	/**
	 * Create a new ContextStoppedEvent.
	 * @param source the {@code ApplicationContext} that has been stopped
	 * (must not be {@code null})
	 */
	public ContextStoppedEvent(ApplicationContext source) {
		super(source);
	}

}

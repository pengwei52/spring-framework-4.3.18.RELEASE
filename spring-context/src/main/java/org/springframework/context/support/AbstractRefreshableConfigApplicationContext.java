/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.context.support;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * AbstractRefreshableApplicationContext子类，用于添加指定配置文件位置的常见处理。
 * 用作基于XML的应用程序上下文实现的基类，例如ClassPathXmlApplicationContext和FileSystemXmlApplicationContext，
 * 以及org.springframework.web.context.support.XmlWebApplicationContext和org.springframework.web.portlet.context.XmlPortletApplicationContext。
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 * @see #setConfigLocation
 * @see #setConfigLocations
 * @see #getDefaultConfigLocations
 */
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext
		implements BeanNameAware, InitializingBean {

	private String[] configLocations;

	private boolean setIdCalled = false;


	/**
	 * Create a new AbstractRefreshableConfigApplicationContext with no parent.
	 */
	public AbstractRefreshableConfigApplicationContext() {
	}

	/**
	 * Create a new AbstractRefreshableConfigApplicationContext with the given parent context.
	 * @param parent the parent context
	 */
	public AbstractRefreshableConfigApplicationContext(ApplicationContext parent) {
		super(parent);
	}


	/**
	 * 以init-param样式设置此应用程序上下文的配置文件位置，即使用逗号，分号或空格分隔的不同位置。
	 * <p>
	 * 如果未设置，则实现可以根据需要使用默认值。
	 */
	public void setConfigLocation(String location) {
		// 即多个资源文件路径之间用 ",; \t\n" 分隔，解析成数组形式
		setConfigLocations(StringUtils.tokenizeToStringArray(location, CONFIG_LOCATION_DELIMITERS));
	}

	/**
	 * 设置此应用程序上下文的多个资源配置文件的位置。
	 * <p>
	 * 如果未设置，则实现可以根据需要使用默认值。
	 */
	public void setConfigLocations(String... locations) {
		if (locations != null) {
			Assert.noNullElements(locations, "Config locations must not be null");
			this.configLocations = new String[locations.length];
			for (int i = 0; i < locations.length; i++) {
				// resolvePath 为同一个类中将字符串解析为欧静的方法
				this.configLocations[i] = resolvePath(locations[i]).trim();
			}
		}
		else {
			this.configLocations = null;
		}
	}

	/**
	 * 返回一个资源文件位置数组，引用应该构建此上下文的XML bean定义文件。还可以包含配置文件位置的正则（pattern），这些pattern将通过ResourcePatternResolver解析。
	 * <p>默认实现返回null。子类可以重写此方法以提供一组资源文件位置以从中加载bean定义。
	 * 
	 * @return an array of resource locations, or {@code null} if none
	 * @see #getResources
	 * @see #getResourcePatternResolver
	 */
	protected String[] getConfigLocations() {
		return (this.configLocations != null ? this.configLocations : getDefaultConfigLocations());
	}

	/**
	 * 如果未指定显式配置文件位置，则返回要使用的默认配置文件位置。
	 * <p>默认实现返回null，需要明确的配置文件位置。
	 * 
	 * @return an array of default config locations, if any
	 * @see #setConfigLocations
	 */
	protected String[] getDefaultConfigLocations() {
		return null;
	}

	/**
	 * 解析给定路径，必要时用相应的环境变量属性值替换占位符。
	 * Applied to config locations.
	 * 
	 * @param path the original file path
	 * @return the resolved file path
	 * @see org.springframework.core.env.Environment#resolveRequiredPlaceholders(String)
	 */
	protected String resolvePath(String path) {
		return getEnvironment().resolveRequiredPlaceholders(path);
	}


	@Override
	public void setId(String id) {
		super.setId(id);
		this.setIdCalled = true;
	}

	/**
	 * 对于上下文实例本身定义为bean的情况，默认情况下将此上下文的id设置为bean名称。
	 */
	@Override
	public void setBeanName(String name) {
		if (!this.setIdCalled) {
			super.setId(name);
			setDisplayName("ApplicationContext '" + name + "'");
		}
	}

	/**
	 * 如果没有在具体上下文的构造函数中刷新，则触发refresh()。
	 */
	@Override
	public void afterPropertiesSet() {
		if (!isActive()) {
			refresh();
		}
	}

}

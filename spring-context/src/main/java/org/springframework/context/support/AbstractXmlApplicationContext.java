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

package org.springframework.context.support;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

/**
 * org.springframework.context.ApplicationContext实现的便捷基类, 
 * 
 * 从一个被 XmlBeanDefinitionReader理解的包含bean定义的xml文档中填充配置
 * drawing configuration from XML documents containing bean definitions
 * understood by an {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}.
 *
 * <p>子类只需要实现getConfigResources和(或)getConfigLocations方法。
 * 此外，它们可以覆盖getResourceByPath回调以特定于环境的方式解释相对路径，和(或)getResourcePatternResolver以扩展模式解析。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #getConfigResources
 * @see #getConfigLocations
 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {

	private boolean validating = true;


	/**
	 * Create a new AbstractXmlApplicationContext with no parent.
	 */
	public AbstractXmlApplicationContext() {
	}

	/**
	 * Create a new AbstractXmlApplicationContext with the given parent context.
	 * @param parent the parent context
	 */
	public AbstractXmlApplicationContext(ApplicationContext parent) {
		super(parent);
	}


	/**
	 * 设置是否使用XML校验。默认为true
	 */
	public void setValidating(boolean validating) {
		this.validating = validating;
	}


	/**
	 * 实现父类抽象的载入 Bean定义 的方法，通过 XmlBeanDefinitionReader 加载bean定义。
	 * 
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 * @see #initBeanDefinitionReader
	 * @see #loadBeanDefinitions
	 */
	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
		// 为给定的BeanFactory创建一个新的 XmlBeanDefinitionReader。
		// 即创建 Bean 读取器，并通过回调设置当容器中取，容器使用该读取器读取 Bean 定义资源
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

		// 为 Bean 读取器设置容器的环境配置 和 Spring 资源加载器
		// AbstractXmlApplicationContext 的祖先父类 AbstractApplicationContext 继承 DefaultResourceLoader，因此容器本身也是一个资源加载器
		beanDefinitionReader.setEnvironment(this.getEnvironment());
		beanDefinitionReader.setResourceLoader(this);
		// 为 Bean 读取器设置 SAX xml 解析器
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

		// 当 Bean 读取器读取 Bean 定义的 Xml 资源文件时，启用 Xml 的校验机制
		initBeanDefinitionReader(beanDefinitionReader);
		// Bean 读取器真正实现加载的方法
		loadBeanDefinitions(beanDefinitionReader);
	}

	/**
	 * 初始化用于加载此上下文的bean定义的 XmlBeanDefinitionReader。默认实现为空。
	 * <p>可以在子类中重写，例如用于关闭XML校验或使用不同的XmlBeanDefinitionParser实现。
	 * 
	 * @param reader the bean definition reader used by this context
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader#setDocumentReaderClass
	 */
	protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
		reader.setValidating(this.validating);
	}

	/**
	 * 使用给定的XmlBeanDefinitionReader加载bean定义。
	 * <p>bean工厂的生命周期由 refreshBeanFactory 方法处理;因此，这个方法只是加载 和(或) 注册 bean定义。
	 * 
	 * @param reader the XmlBeanDefinitionReader to use
	 * @throws BeansException in case of bean registration errors
	 * @throws IOException if the required XML document isn't found
	 * @see #refreshBeanFactory
	 * @see #getConfigLocations
	 * @see #getResources
	 * @see #getResourcePatternResolver
	 */
	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
		
		// 获取 Bean 定义资源
		Resource[] configResources = getConfigResources();
		if (configResources != null) {
			// Xml Bean 读取器调用其父类 AbstractBeanDefinitionReader 读取读取 Bean 定义资源
			reader.loadBeanDefinitions(configResources);
		}
		// 如果子类中获取的 Bean 资源文件位置为空，则获取 FileSystemXmlApplicationContext 构造方法中 setConfigLocations 方法设值的资源
		String[] configLocations = getConfigLocations();
		if (configLocations != null) {
			// Xml Bean 读取器调用其父类 AbstractBeanDefinitionReader 的方法读取指定位置的 Bean 定义资源
			reader.loadBeanDefinitions(configLocations);
		}
	}

	/**
	 * 返回一个Resource对象数组，仅 ClassPathXMLApplicationContext 实现
	 * 
	 * <p>默认实现返回null。 子类可以覆盖它以提供预构建的Resource对象而不是位置字符串。
	 * 
	 * @return an array of Resource objects, or {@code null} if none
	 * @see #getConfigLocations()
	 */
	protected Resource[] getConfigResources() {
		return null;
	}

}

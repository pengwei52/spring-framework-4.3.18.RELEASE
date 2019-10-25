/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.core.io;

import org.springframework.util.ResourceUtils;

/**
 * 定义资源加载器，主要应用于根据给定的资源文件地址返回对应的Resource。
 * 用于加载资源的策略接口（例如，类路径或文件系统资源）。
 * 需要 {@link org.springframework.context.ApplicationContext} 来提供此功能，
 * 以及扩展的 {@link org.springframework.core.io.support.ResourcePatternResolver} 支持。
 *
 * <p>
 * DefaultResourceLoader 是一个独立的实现，可以在ApplicationContext外部使用，也可以由ResourceEditor使用。
 *
 * <p>使用特定上下文的资源加载策略，在ApplicationContext中运行时，可以从Strings填充Resource和Resource数组类型的Bean属性。
 *
 * @author Juergen Hoeller
 * @since 10.03.2004
 * @see Resource
 * @see org.springframework.core.io.support.ResourcePatternResolver
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 */
public interface ResourceLoader {

	/** Pseudo URL prefix for loading from the class path: "classpath:" */
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;


	/**
	 * 返回指定资源位置的资源句柄。
	 * 
	 * <p>
	 * 句柄应始终是可重用的资源描述符，允许多个Resource.getInputStream（）调用。
	 * <p><ul>
	 * <li>必须支持完全限定的URL，例如 "file:C:/test.dat".
	 * <li>必须支持classpath的伪URL，例如 "classpath:test.dat".
	 * <li>应该支持相对文件路径，例如 "WEB-INF/test.dat".
	 * (这将是特殊的实现，通常由ApplicationContext实现提供。)
	 * </ul>
	 * <p>请注意，资源句柄并不意味着现有资源;您需要调用Resource.exists来检查是否存在。
	 * 
	 * @param location the resource location
	 * @return a corresponding Resource handle (never {@code null})
	 * @see #CLASSPATH_URL_PREFIX
	 * @see Resource#exists()
	 * @see Resource#getInputStream()
	 */
	Resource getResource(String location);

	/**
	 * 公开此ResourceLoader使用的ClassLoader。
	 * <p>
	 * 需要直接访问ClassLoader的客户端可以使用ResourceLoader以统一的方式执行此操作，而不是依赖于线程上下文ClassLoader。
	 * 
	 * @return the ClassLoader (only {@code null} if even the system
	 * ClassLoader isn't accessible)
	 * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
	 */
	ClassLoader getClassLoader();

}

/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.core.io.support;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * 用于将 location pattern（for example, an Ant-style path pattern）解析为Resource对象的策略接口。
 *
 * <p>这是 org.springframework.core.io.ResourceLoader 接口的扩展。
 * 可以检查传入的ResourceLoader（例如，在上下文中运行时通过org.springframework.context.ResourceLoaderAware传入的org.springframework.context.ApplicationContext）是否也实现了此扩展接口。
 *
 * <p>PathMatchingResourcePatternResolver 是一个独立的实现，可以在ApplicationContext外部使用，
 * 也可以由ResourceArrayPropertyEditor用于填充Resource数据bean属性。
 *
 * <p>可以与任何类型的 location pattern 一起使用（例如 "/WEB-INF/*-context.xml"）：输入模式必须与策略实现相匹配。
 * 此接口仅指定转换方法而不是特定的模式格式。
 *
 * <p>此接口还为类路径中的所有匹配资源建议了一个新的资源前缀 "classpath*:"。
 * 注意，在这种情况下，资源位置应该是没有占位符的路径（例如 "/beans.xml"）; JAR文件或类目录可以包含多个同名文件。
 *
 * @author Juergen Hoeller
 * @since 1.0.2
 * @see org.springframework.core.io.Resource
 * @see org.springframework.core.io.ResourceLoader
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 */
public interface ResourcePatternResolver extends ResourceLoader {

	/**
	 * 类路径中所有匹配资源的伪URL前缀："classpath*:" 
	 * 这与ResourceLoader的类路径URL前缀不同之处在于它检索给定名称的所有匹配资源（例如 "/beans.xml"），例如在根目录中所有已部署的JAR文件。
	 * 
	 * @see org.springframework.core.io.ResourceLoader#CLASSPATH_URL_PREFIX
	 */
	String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

	/**
	 * 将给定位置的 pattern 解析为Resource对象。
	 * <p>
	 * Overlapping resource entries that point to the same physical resource should be avoided, 
	 * as far as possible. The result should have set semantics.
	 * 
	 * @param locationPattern the location pattern to resolve
	 * @return the corresponding Resource objects
	 * @throws IOException in case of I/O errors
	 */
	Resource[] getResources(String locationPattern) throws IOException;

}

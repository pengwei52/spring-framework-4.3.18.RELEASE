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

package org.springframework.beans.factory.xml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
 * Default implementation of the {@link BeanDefinitionDocumentReader} interface that
 * reads bean definitions according to the "spring-beans" DTD and XSD format
 * (Spring's default XML bean definition format).
 *
 * <p>The structure, elements, and attribute names of the required XML document
 * are hard-coded in this class. (Of course a transform could be run if necessary
 * to produce this format). {@code <beans>} does not need to be the root
 * element of the XML document: this class will parse all bean definition elements
 * in the XML file, regardless of the actual root element.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Erik Wiersma
 * @since 18.12.2003
 */
public class DefaultBeanDefinitionDocumentReader implements BeanDefinitionDocumentReader {

	public static final String BEAN_ELEMENT = BeanDefinitionParserDelegate.BEAN_ELEMENT;

	public static final String NESTED_BEANS_ELEMENT = "beans";

	public static final String ALIAS_ELEMENT = "alias";

	public static final String NAME_ATTRIBUTE = "name";

	public static final String ALIAS_ATTRIBUTE = "alias";

	public static final String IMPORT_ELEMENT = "import";

	public static final String RESOURCE_ATTRIBUTE = "resource";

	public static final String PROFILE_ATTRIBUTE = "profile";


	protected final Log logger = LogFactory.getLog(getClass());

	// 
	private XmlReaderContext readerContext;

	// 
	private BeanDefinitionParserDelegate delegate;


	/**
	 * 此实现根据“spring-beans”XSD（或历史上的DTD）解析bean定义。
	 * 
	 * <p>
	 * 打开DOM文档; 然后初始化<beans />级别指定的默认设置; 然后解析包含的bean定义。
	 */
	@Override
	public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
		// 获取 XML 描述符
		this.readerContext = readerContext;
		logger.debug("Loading bean definitions");
		// 获得 Document 的根元素
		Element root = doc.getDocumentElement();
		// 真正的解析，执行 BeanDefinition 的注册
		doRegisterBeanDefinitions(root);
	}

	/**
	 * Return the descriptor for the XML resource that this parser works on.
	 */
	protected final XmlReaderContext getReaderContext() {
		return this.readerContext;
	}

	/**
	 * Invoke the {@link org.springframework.beans.factory.parsing.SourceExtractor} to pull the
	 * source metadata from the supplied {@link Element}.
	 */
	protected Object extractSource(Element ele) {
		return getReaderContext().extractSource(ele);
	}


	/**
	 * 在给定的根 <beans /> 元素中注册每个bean定义。可能会存在递归调用。
	 */
	protected void doRegisterBeanDefinitions(Element root) {
		// Any nested <beans> elements will cause recursion in this method. In
		// order to propagate and preserve <beans> default-* attributes correctly,
		// keep track of the current (parent) delegate, which may be null. Create
		// the new (child) delegate with a reference to the parent for fallback purposes,
		// then ultimately reset this.delegate back to its original (parent) reference.
		// this behavior emulates a stack of delegates without actually necessitating one.
		
		// 具体的解析过程由 BeanDefinitionParserDelegate 实现，BeanDefinitionParserDelegate 中定义了 Spring 定义 XML 文件的各种元素
		// 每一个beans标签使用独立的 BeanDefinitionParserDelegate 对象进行解析，因此对应有父子关系
		BeanDefinitionParserDelegate parent = this.delegate;
		this.delegate = createDelegate(getReaderContext(), root, parent);

		if (this.delegate.isDefaultNamespace(root)) {	// 是否包含 beans 默认命名空间
			// 处理 profile 属性
			// beans根节点下，可以有多个嵌套的 beans 元素节点，使用 profile 进行环境区分
			String profileSpec = root.getAttribute(PROFILE_ATTRIBUTE);
			if (StringUtils.hasText(profileSpec)) {
				String[] specifiedProfiles = StringUtils.tokenizeToStringArray(
						profileSpec, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
				if (!getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
					if (logger.isInfoEnabled()) {
						logger.info("Skipped XML bean definition file due to specified profiles [" + profileSpec +
								"] not matching: " + getReaderContext().getResource());
					}
					return;
				}
			}
		}

		// 在解析 Bean 定义之前，进行自定义的解析，增强解析过程的可扩展性
		preProcessXml(root);
		// 从 Document 的根元素开始进行 Bean 定义的 Document 对象			******************************
		parseBeanDefinitions(root, this.delegate);
		// 在解析 Bean 定义之后，进行自定义的解析，增加解析过程的可扩展性
		postProcessXml(root);

		this.delegate = parent;
	}

	// 创建 BeanDefinitionParserDelegate，用于完成真正的解析过程
	protected BeanDefinitionParserDelegate createDelegate(
			XmlReaderContext readerContext, Element root, BeanDefinitionParserDelegate parentDelegate) {

		BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);
		// BeanDefinitionParserDelegate 初始化 Document 根元素
		delegate.initDefaults(root, parentDelegate);
		return delegate;
	}

	/**
	 * 解析文档中根级别的元素：“import”，“alias”，“bean”。
	 * 使用 Spring 的 Bean 规则从 Document 的根元素开始进行 Bean 定义的 Document 解析，
	 * 
	 * @param root the DOM root element of the document
	 */
	protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
		// Bean 定义的 Document 对象使用了 Spring 默认的 XML 命名空间
		if (delegate.isDefaultNamespace(root)) {
			// 获取 Bean 定义的 Document 对象根元素的所有子节点
			NodeList nl = root.getChildNodes();
			// 遍历所有节点
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				// 获得 Document 节点是 XML 元素节点
				if (node instanceof Element) {
					Element ele = (Element) node;
					// Bean 定义的 Document 的元素节点使用的是 Spring 默认的 XML 命名空间
					if (delegate.isDefaultNamespace(ele)) {
						// 对于根节点或者子节点，如果是默认命名空间的话则采用 parseDefaultElement 解析
						// 使用 Spring 的 Bean 规则解析元素节点
						parseDefaultElement(ele, delegate);
					}
					else {
						// 对自定义命名空间进行解析
						// 没有使用 Spring 默认的 XML 命名空间，则使用用户自定义的解析规则解析元素节点, 比如 dubbo 跟spring的集成
						delegate.parseCustomElement(ele);
					}
				}
			}
		}
		else {
			// Document 的根节点没有使用 Spring 默认命名空间，则使用用户自定义的解析规则解析 Document 根节点
			delegate.parseCustomElement(root);
		}
	}

	// 使用 Spring 的 Bean 规则解析 Document 元素节点
	private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
		// 如果元素节点是 <import> 导入元素，进行导入解析
		if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
			importBeanDefinitionResource(ele);
		}
		// 如果元素节点是 <alias>别名元素，进行别名解析
		else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {
			processAliasRegistration(ele);
		}
		// 元素节点既不是导入元素，也不是别名元素，即普通的 <bean> 元素
		// 按照 Spring 的 Bean 规则解析元素
		else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
			processBeanDefinition(ele, delegate);
		}
		// 对 beans 标签的处理
		else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {
			// recurse
			// 递归调用
			doRegisterBeanDefinitions(ele);
		}
	}

	/**
	 * 解析 <import> 导入元素并将bean定义从给定资源加载到bean工厂中。
	 * 大致步骤：
	 * 	1.获取resource属性所表示的路径你；
	 *  2.解析路径中的系统属性，格式如"${user.dir}"；
	 *  3.判断location是绝对路径还是相对路径；
	 *  4.如果是绝对路径则递归调用bean的解析过程，进行另一次的解析；
	 *  5.如果是相对路径则计算出绝对路径并进行解析；
	 *  6.通知监听器，解析完成。
	 */
	protected void importBeanDefinitionResource(Element ele) {
		// 获取给定的导入元素的 resouce 属性
		String location = ele.getAttribute(RESOURCE_ATTRIBUTE);
		// 如果导入元素的 resouce 属性值为空，则没有导入任何资源，直接返回
		if (!StringUtils.hasText(location)) {
			getReaderContext().error("Resource location must not be empty", ele);
			return;
		}

		// 使用系统变量值解析 resouce 属性值: 例如 "${user.dir}"
		location = getReaderContext().getEnvironment().resolveRequiredPlaceholders(location);

		Set<Resource> actualResources = new LinkedHashSet<Resource>(4);

		// 标识给定的导入元素的 resouce 是绝对路径还是相对路径
		boolean absoluteLocation = false;
		try {
			absoluteLocation = ResourcePatternUtils.isUrl(location) || ResourceUtils.toURI(location).isAbsolute();
		}
		catch (URISyntaxException ex) {
			// cannot convert to an URI, considering the location relative
			// unless it is the well-known Spring prefix "classpath*:"
			// 给定的导入元素的 location 不是绝对路径
		}

		// Absolute or relative?
		// 给定的导入元素的 location 是绝对路径，则直接根据地址加载对应的配置文件
		if (absoluteLocation) {
			try {
				// 使用资源导入器加载给定路径的 Bean 定义资源
				int importCount = getReaderContext().getReader().loadBeanDefinitions(location, actualResources);
				if (logger.isDebugEnabled()) {
					logger.debug("Imported " + importCount + " bean definitions from URL location [" + location + "]");
				}
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error(
						"Failed to import bean definitions from URL location [" + location + "]", ele, ex);
			}
		}
		else {
			// No URL -> considering resource location as relative to the current file.
			// 给定的导入元素的 location 是相对路径
			try {
				int importCount;
				// 将给定导入元素的 location 封装为相对路径资源
				// Resource 存在多个子实现类，每个resource的createRelative方式实现都不一样，所以这里先使用子类的方法尝试解析
				Resource relativeResource = getReaderContext().getResource().createRelative(location);
				// 封装的相对路径资源存在
				if (relativeResource.exists()) {
					// 使用资源读入器加载 Bean 定义资源
					importCount = getReaderContext().getReader().loadBeanDefinitions(relativeResource);
					actualResources.add(relativeResource);
				}
				// 封装的相对路径资源不存在
				else {
					// 如果解析不成功，则使用默认的解析器 ResourcePatternResolver进行解析
					// 获取 Spring IOC 容器资源读入器的基本路径
					String baseLocation = getReaderContext().getResource().getURL().toString();
					// 根据 Spring IOC 容器资源读入器的基本路径加载给定导入路径的资源
					importCount = getReaderContext().getReader().loadBeanDefinitions(
							StringUtils.applyRelativePath(baseLocation, location), actualResources);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Imported " + importCount + " bean definitions from relative location [" + location + "]");
				}
			}
			catch (IOException ex) {
				getReaderContext().error("Failed to resolve current resource location", ele, ex);
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to import bean definitions from relative location [" + location + "]",
						ele, ex);
			}
		}
		Resource[] actResArray = actualResources.toArray(new Resource[actualResources.size()]);
		// 在解析完 <Import> 元素之后，发送 容器导入其他资源处理完成 事件
		getReaderContext().fireImportProcessed(location, actResArray, extractSource(ele));
	}

	/**
	 * 解析 <Alias> 别名元素，并向 Spring IOC 容器注册别名
	 * 
	 */
	protected void processAliasRegistration(Element ele) {
		// 获取<alias> 别名元素中 name 的属性值
		String name = ele.getAttribute(NAME_ATTRIBUTE);
		// 获取 <alias> 别名元素中 alias 的属性值
		String alias = ele.getAttribute(ALIAS_ATTRIBUTE);
		boolean valid = true;
		// <alias> 别名元素的 name 属性值为空
		if (!StringUtils.hasText(name)) {
			getReaderContext().error("Name must not be empty", ele);
			valid = false;
		}
		// <alias> 别名元素的 alias 属性值为空
		if (!StringUtils.hasText(alias)) {
			getReaderContext().error("Alias must not be empty", ele);
			valid = false;
		}
		if (valid) {
			try {
				// 向容器的资源读入器注册别名
				getReaderContext().getRegistry().registerAlias(name, alias);
			}
			catch (Exception ex) {
				getReaderContext().error("Failed to register alias '" + alias +
						"' for bean with name '" + name + "'", ele, ex);
			}
			// 在解析完 <alias> 元素之后，发送 容器别名处理完成 事件
			getReaderContext().fireAliasRegistered(name, alias, extractSource(ele));
		}
	}

	/**
	 * 解析 Bean 定义资源 Document 对象的普通元素并将其注册到注册表。
	 */
	protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
		// 对Element元素进行解析，返回 BeanDefinitionHolder（包含class、name、id、alias 之类的属性）  *********************
		BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
		// BeanDefinitionHolder 是对 BeanDefinition 的封装，即 Bean 定义的封装类
		// 对 Document 对象中 <Bean> 元素的解析由 BeanDefinitionParserDelegate 实现
		if (bdHolder != null) {
			// 如果有必要的话，就对BeanDefinition进行装饰
			bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
			try {
				// Register the final decorated instance.
				// 向 Spring IOC 容器注册解析得到的 Bean 定义，这是 Bean 定义向 IOC 容器注册的入口		*********************
				BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to register bean definition with name '" +
						bdHolder.getBeanName() + "'", ele, ex);
			}
			// Send registration event.
			// 在完成向 Spring IOC  容器注册解析得到的 Bean 定义之后，发送注册事件
			// 这里的实现只为扩展，当程序开发人员需要对注册BeanDefinition事件进行监听时可以通过注册监听器的方式并将处理逻辑写入监听器中，目前在spring中并没有对此事件做任何逻辑处理。
			getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
		}
	}


	/**
	 * Allow the XML to be extensible by processing any custom element types first,
	 * before we start to process the bean definitions. This method is a natural
	 * extension point for any other custom pre-processing of the XML.
	 * <p>The default implementation is empty. Subclasses can override this method to
	 * convert custom elements into standard Spring bean definitions, for example.
	 * Implementors have access to the parser's bean definition reader and the
	 * underlying XML resource, through the corresponding accessors.
	 * @see #getReaderContext()
	 */
	protected void preProcessXml(Element root) {
	}

	/**
	 * Allow the XML to be extensible by processing any custom element types last,
	 * after we finished processing the bean definitions. This method is a natural
	 * extension point for any other custom post-processing of the XML.
	 * <p>The default implementation is empty. Subclasses can override this method to
	 * convert custom elements into standard Spring bean definitions, for example.
	 * Implementors have access to the parser's bean definition reader and the
	 * underlying XML resource, through the corresponding accessors.
	 * @see #getReaderContext()
	 */
	protected void postProcessXml(Element root) {
	}

}

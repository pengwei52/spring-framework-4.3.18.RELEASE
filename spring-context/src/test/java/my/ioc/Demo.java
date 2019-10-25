/*
 * Copyright 2002-2018 the original author or authors.
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

package my.ioc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author pengwei
 * @since 4.3
 */
public class Demo {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"beanFactoryTest.xml");
		System.out.println("容器初始化完成");

		System.out.println(applicationContext.getBean("user"));

		System.out.println("准备销毁容器");
		applicationContext.destroy();
		
		// System.out.println(applicationContext.getBean("u1"));
		// System.out.println(applicationContext.getBean("&u1"));
	}
}

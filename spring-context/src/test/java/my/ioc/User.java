/*
 * Copyright 2002-2019 the original author or authors.
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

import org.springframework.beans.factory.DisposableBean;

/**
 * 
 * @author pengwei
 * @since 4.3
 */
public class User implements DisposableBean {

	private String name;

	private String password;

	private Dog dog;

	public User() {
		System.out.println("User 无参构造");
	}

	public User(Dog dog) {
		this.dog = dog;
	}

	public User(String name, String password) {
		System.out.println("实例化User构造函数");
		this.name = name;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Dog getDog() {
		return dog;
	}

	public void setDog(Dog dog) {
		this.dog = dog;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", password=" + password + ", dog=" + dog + "]";
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("销毁");
	}
	
	public void destroy2() throws Exception {
		System.out.println("销毁2");
	}
	
}

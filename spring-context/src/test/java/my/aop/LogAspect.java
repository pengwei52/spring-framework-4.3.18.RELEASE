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

package my.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 
 * @author pengwei
 * @since 4.3
 */
@Aspect
public class LogAspect {

	@Pointcut("execution(* *.div(..))")
	public void test() {
	};

	@Before("test()")
	public void before() {
		System.out.println("before");
	}

	@After("test()")
	public void after() {
		System.out.println("after");
	}

	@AfterReturning("test()")
	public void afterReturning() {
		System.out.println("after return");
	}

	@AfterThrowing("test()")
	public void afterThrowing() {
		System.out.println("after throwing");
	}

	@Around("test()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("around before");
		Object proceed = pjp.proceed();
		System.out.println("around after");
		return proceed;
	}
}

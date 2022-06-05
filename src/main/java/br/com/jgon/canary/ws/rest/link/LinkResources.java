/*
 * Copyright 2017 Jurandir C. Goncalves
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.jgon.canary.ws.rest.link;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permite a adicao de multiplos links e permite tambem a configuracao de links baseadas em outro metodo (evita duplicidade)
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LinkResources {
	/**
	 * Multiplos {@link LinkResource}
	 * @return LinkResource
	 */
	LinkResource[] value() default {};
	/**
	 * Classe onde esta o metodo que tera os links clonados
	 * @return {@link Class}
	 */
	Class<?> serviceClass() default void.class;
	/**
	 * Metodo que tera os links clonados
	 * @return {@link String}
	 */
	String serviceMethodName() default ""; 
		
}

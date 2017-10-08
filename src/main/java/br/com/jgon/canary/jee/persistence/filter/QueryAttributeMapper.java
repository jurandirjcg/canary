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
package br.com.jgon.canary.jee.persistence.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mapeamento para atributo da entidade 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0.0
 *
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.FIELD)
@Inherited
public @interface QueryAttributeMapper {
	
	/**
	 * Nome do atributo na base
	 * @return
	 */
	String value() default "";
	
	/**
	 * Indica se o attributo e do tipo enum
	 * @return
	 */
	boolean isEnum() default false;
	
	/**
	 * Utilizado em campos do tipo Collection, pois não é possível realizar reflection (Java generics)
	 */
	Class<?> collectionTarget() default void.class;
}

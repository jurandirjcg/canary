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
package br.com.jgon.canary.ws.rest.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
@Retention (RetentionPolicy.RUNTIME)
@Target ({ElementType.FIELD, })
@Inherited
public @interface WSAttribute {
	
	/**
	 * Nome do atributo na base
	 * @return
	 */
	String value() default "";
	/**
	 * Quando true não mapeia o objeto complexo 
	 */
	boolean fixed() default false;
	/**
	 * Indica se deverá se o atributo devera ser mapeado caso o pai seja declarado - default true
	 * Ex. atributo pai "pessoa" possui atributo "nome", se no servico for filtrado somente  "pessoa" este atributo indica o mapeamento tambem do atributo "nome", retornando no parse o valor pessoa.nome
	 */
	boolean autoInclude() default true;
	/**
	 * Utilizado em campos do tipo Collection, pois não é possível realizar reflection (Java generics)
	 */
	Class<?> collectionType() default void.class;
}

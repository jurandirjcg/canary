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
 * Permite a definicao do link
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LinkResource {

	String rel();
		
	String type() default "";
		
	String title() default "";
		
	/**
	 * Indica qual sera o path inicial do link, default LinkResouceBasePath.COMPLETE
	 * Ex: /page/recurso ou http://dominio.com/page/recurso
	 * @return
	 */
	LinkResouceBasePath basePath() default LinkResouceBasePath.COMPLETE;
		
	/**
	 * Indica se inclui os query params vindos na requisicao, default false
	 * @return
	 */
	boolean includeRequestQueryParams() default false;	
	/**
	 * define parametros PATH - faz replace na ordem que esta definida na anotation Path.
	 * Ex. path = /system/{id} e pathParameters = /user/${atributo_variavel} apos replace = /system/user/ATRIBUTO_VARIAVEL
	 * Para obter o mesmo valor passado no parâmetro da chamada usar o identificador sem os caracteres '$' '#'. Ex: {valor_variavel_path}
	 * @return
	 */
	String[] pathParameters() default {};
	/**
	 * define parametros QUERY - adiciona valores na query param
	 * @return
	 */
	String[] queryParameters() default {};
		
	/**
	 * 
	 * @return
	 */
	LinkTarget target() default LinkTarget.ENTITY;
	/**
	 *  Classe onde esta o metodo que tera os links clonados
	 * @return
	 */
	Class<?> serviceClass() default void.class;
	/**
	 * Metodo que tera os links clonados
	 * @return
	 */
	String serviceMethodName() default ""; 
		
}

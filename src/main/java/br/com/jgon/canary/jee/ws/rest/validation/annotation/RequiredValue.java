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
package br.com.jgon.canary.jee.ws.rest.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Esta anotação compara os parametros e exige que o mesmo esteja na lista fornecida.
 * 
 * @author Alexandre O. Pereira
 * 
 * @version 1.0
 * 
 * @parameter
 * 		<strong>inStringValues</strong> - Lista de String a ser comparada <br/>
 * 		<strong>inIntValues</strong> - Lista de números int a ser comparados <br/>
 * 		<strong>inLongValues</strong> - Lista de números long a ser comparados <br/>
 * 		<strong>hideListInErrorMessages</strong> - Oculta a lista de valores válidos na mensagem de erro <br/>
 * 		<strong>hideFieldValueInErrorMessage</strong> - Oculta o valor do parâmetro na mensagem de erro <br/> <br/>
 * Uso: <pre> @RequiredValue(inStringValues={"value1","value2","value3"},hideListInErrorMessages=true) @PathParam("foo") String foo</pre>
 * No caso acima, será obrigatório que o valor de "foo" seja um dos listados
 * ("value1", "value2" ou "value3")
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredValue {
	boolean hideListInErrorMessages() default false;
	boolean hideFieldValueInErrorMessage() default false;
	String[] inStringValues() default {};
	int[] inIntValues() default {};
	long[] inLongValues() default {};
}
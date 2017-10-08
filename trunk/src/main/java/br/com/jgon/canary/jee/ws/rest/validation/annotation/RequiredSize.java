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
 * Esta anotação exige que o parametro tenha um tamanho específico (String) ou um range de valor (número)
 * 
 * @author Alexandre O. Pereira
 * 
 * @version 1.0.0
 * 
 * @parameter 
 * 		<strong>minSize</strong> - Valor mínimo <br/>
 * 		<strong>maxSize</strong> - Valor máximo <br/>
 * 		<strong>hideFieldValueInErrorMessage</strong> - Oculta o valor do parâmetro na mensagem de erro <br/> <br/>
 * Uso: <pre>@RequiredSize(minSize=10, maxSize=20) @PathParam("foo") String foo</pre>
 * No caso acima, o parãmetro "foo", deverá ter o tamanho entre 10 e 20 caracteres (se o tipo do parâmetro for numérico, 
 * será exigido o valor entre 10 e 20)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredSize {
	double minSize() default 0;
	double maxSize() default 0;
}
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
package br.com.jgon.canary.ws.rest.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Esta anotação exige que o parâmetro seja válido caso o outro parãmetro citado não seja nulo ou vazio.
 * 
 * @author Alexandre O. Pereira
 * 
 * @version 1.0
 * 
 * <p> String (nome do outro parâmetro a considerar) <br>
 * Uso: <pre>@PathParam("foo") String foo,</pre><br>
 * 		<pre>@RequiredIfNull("foo") @PathParam("bar") String bar</pre>
 * No caso acima, o parâmetro "bar" será obrigatório caso o parâmetro "foo" não for nulo ou vazio
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredIfNotNull {
	String value();
}

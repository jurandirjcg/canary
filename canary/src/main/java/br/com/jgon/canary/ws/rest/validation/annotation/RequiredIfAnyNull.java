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
 * Esta anotação exige que o parâmetro seja válido caso algum dos outros parãmetros citados seja nulo ou vazio.
 * 
 * @author Alexandre O. Pereira
 * 
 * @version 1.0
 * 
 * <p> Array de String (nome dos outros parãmetros a considerar) <br>
 * Uso: <pre>@PathParam("foo") String foo,</pre><br>
 * 		<pre>@PathParam("foo2") String foo2,</pre><br>
 * 		<pre>@RequiredIfNull({"foo", "foo2"}) @PathParam("bar") String bar</pre>
 * No caso acima, o parâmetro "bar" será obrigatório caso o parâmetro "foo" ou o parâmetro "foo2" sejam nulos ou vazios
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredIfAnyNull {
	String[] value();
}

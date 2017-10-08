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
 * Esta anotaçao permite que um (e apenas um) dos parametros seja fornecido
 * 
 * @author Alexandre O. Pereira
 *  
 * @version 1.0
 *
 * <p> Lista de String (nomes dos parametros a ser comparados) <br> <br>
 * Uso: <pre> @RequiredXOR({"paramName1","paramName2","paramName3"}) @PathParam("foo") String foo {@code}</pre>
 * No caso acima, será obrigatório que um parâmetro (e apenas um) seja válido entre 
 * "foo", "paramName1", "paramName2" e "paramName3"
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredXOR {
	String[] value();
}
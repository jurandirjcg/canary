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
 * Esta anotação exige que o parâmetro atenda ao padrão da expressão regular fornecida
 * 
 * @author Alexandre O. Pereira
 * 
 * @version 1.0.0
 * 
 * @parameter String contendo uma expressão regular válida.
 * 		<strong>hideRegExPatternFromErrorMessages</strong> - Oculta a expressão regular de validação na mensagem de erro <br/>
 * 		<strong>hideFieldValueInErrorMessage</strong> - Oculta o valor do parâmetro na mensagem de erro <br/> <br/>
 * Uso: <pre> @RequiredRegExPattern("^([0-9]{3}.){2}([0-9]{3}-)([0-9]{2})$") @PathParam("cpf") String cpf,</pre>
 * No caso acima, o parâmetro "cpf", obrigatoriamente, deve atender ao formato 999.999.999-99 (formato cpf)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredRegExPattern {
	boolean hideRegExPatternFromErrorMessage() default false;
	boolean hideFieldValueInErrorMessage() default false;
	String value();
}

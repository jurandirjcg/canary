package br.com.jgon.canary.jee.ws.rest.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Esta anotação exige que o parâmetro atenda ao padrão da expressão regular fornecida
 * @author alexandre
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

package br.com.jgon.canary.jee.ws.rest.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Esta anotação exige que o parâmetro não nulo ou vazio.
 * @author alexandre
 * Uso: <pre> @Required @PathParam("foo") String foo,</pre><br>
 * No caso acima, o parâmetro "foo" é obrigatório.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Required {
	//Anotation de marcação
}

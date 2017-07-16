package br.com.jgon.canary.jee.ws.rest.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Esta anotação exige que o parâmetro seja válido caso o outro parãmetro citado não seja nulo ou vazio.
 * @author alexandre
 * @parameter String (nome do outro parâmetro a considerar) <br/>
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

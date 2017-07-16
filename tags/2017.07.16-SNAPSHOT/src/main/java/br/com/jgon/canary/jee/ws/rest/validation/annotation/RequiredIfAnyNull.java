package br.com.jgon.canary.jee.ws.rest.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Esta anotação exige que o parâmetro seja válido caso algum dos outros parãmetros citados seja nulo ou vazio.
 * @author alexandre
 * @parameter Array de String (nome dos outros parãmetros a considerar) <br/>
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

package br.com.jgon.canary.ws.rest.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Esta anotação marca o parametro como validável.
 * @author lgustavolima
 * Uso: <pre> @Validatable Object obj,</pre><br>
 * No caso acima, o parâmetro "obj" passará pela validação.
 */
@Target({ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validatable {

}

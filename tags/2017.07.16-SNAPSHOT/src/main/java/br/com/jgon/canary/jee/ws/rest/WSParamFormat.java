package br.com.jgon.canary.jee.ws.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Habilita a conversao e tratamento dos parametros da requicao
 * @author jurandir
 *
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WSParamFormat {
	Class<?> value();
	/**
	 * Forca a inclusao do campo
	 * @return
	 */
	String[] forceFields() default {};
}

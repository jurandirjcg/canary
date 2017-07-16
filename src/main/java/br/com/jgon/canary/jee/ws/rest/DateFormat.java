package br.com.jgon.canary.jee.ws.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define o formato da data vindo na requisicao
 * @author jurandir
 *
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFormat {
	String value() default "yyyy-MM-dd";
}

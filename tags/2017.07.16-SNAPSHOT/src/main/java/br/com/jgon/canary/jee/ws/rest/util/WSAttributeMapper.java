package br.com.jgon.canary.jee.ws.rest.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author jurandir
 *
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.FIELD)
@Inherited
public @interface WSAttributeMapper {
	
	/**
	 * Nome do atributo na base
	 * @return
	 */
	String value() default "";
	/**
	 * Indica se o atributo e enum
	 * @return
	 */
	boolean isEnum() default false;
	
	/**
	 * Utilizado em campos do tipo Collection, pois não é possível realizar reflection 
	 */
	Class<?> valueType() default void.class;
}

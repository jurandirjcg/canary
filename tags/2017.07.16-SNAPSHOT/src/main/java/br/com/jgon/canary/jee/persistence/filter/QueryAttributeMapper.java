package br.com.jgon.canary.jee.persistence.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mapeamento do atributo.
 * Ex: car.color
 * @author jurandir
 *
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.FIELD)
@Inherited
public @interface QueryAttributeMapper {
	
	/**
	 * Nome do atributo na base
	 * @return
	 */
	String value() default "";
	
	/**
	 * Indica se o attributo e do tipo enum
	 * @return
	 */
	boolean isEnum() default false;
	
	/**
	 * Utilizado em campos do tipo Collection, pois não é possível realizar reflection 
	 */
	Class<?> valueType() default void.class;
}

package br.com.jgon.canary.jee.ws.rest.util.link;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permite a adicao de multiplos links e permite tambem a configuracao de links baseadas em outro metodo (evita duplicidade)
 * @author jurandir
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LinkResources {
	/**
	 * Multiplos {@link LinkResource}
	 * @return
	 */
	LinkResource[] value() default {};
	/**
	 * Classe onde esta o metodo que tera os links clonados
	 * @return
	 */
	Class<?> serviceClass() default void.class;
	/**
	 * Metodo que tera os links clonados
	 * @return
	 */
	String serviceMethodName() default ""; 
		
}

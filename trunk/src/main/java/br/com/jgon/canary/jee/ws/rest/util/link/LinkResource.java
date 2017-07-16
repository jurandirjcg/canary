package br.com.jgon.canary.jee.ws.rest.util.link;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permite a definicao do link
 * @author jurandir
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LinkResource {

	String rel();
		
	String type() default "";
		
	String title() default "";
		
	/**
	 * Indica qual sera o path inicial do link, default LinkResouceBasePath.COMPLETE
	 * Ex: /page/recurso ou http://dominio.com/page/recurso
	 * @return
	 */
	LinkResouceBasePath basePath() default LinkResouceBasePath.COMPLETE;
		
	/**
	 * Indica se inclui os query params vindos na requisicao, default false
	 * @return
	 */
	boolean includeRequestQueryParams() default false;
	
	/**
	 * Indica se inclui os path params vindos na requisicao, default false
	 * @return
	 */
	boolean includeRequestPathParams() default false;
		
	/**
	 * define parametros PATH - faz replace na ordem que esta definida na anotation Path.
	 * Ex. path = /system/{id} e pathParameters = /user/${atributo_variavel} apos replace = /system/user/ATRBUTO_DO_USUARIO_RETORNADO
	 * @return
	 */
	String[] pathParameters() default {};
	/**
	 * define parametros QUERY - adiciona valores na query param
	 * @return
	 */
	String[] queryParameters() default {};
		
	/**
	 * 
	 * @return
	 */
	LinkTarget target() default LinkTarget.ENTITY;
	/**
	 * Utilizado somente para controle interno
	 * @return
	 */
	Class<?> serviceClass() default void.class;
	/**
	 * Utilizado somente para controle interno
	 * @return
	 */
	String serviceMethodName() default ""; 
		
}

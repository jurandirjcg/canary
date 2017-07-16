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
public @interface PaginatorTemplate {

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
	 * Indica se inclui os query params vindos na requisicao, default true
	 * @return
	 */
	boolean includeRequestQueryParams() default true;		
	/**
	 * define parametros PATH
	 * @return
	 */
	String[] pathParameters() default {};
	/**
	 * define parametros QUERY
	 * @return
	 */
	String[] queryParameters() default {};
		
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
	
	/**
	 * Nome do parametro que indica a pagina, no template
	 * default: {PAGINATION_PAGE_PARAM}
	 * @return
	 */
	String pageParamName() default "{PAGE_PARAM}";
	
	/**
	 * Nome do parametro que indica o numero de registros paginados, no template
	 * default: {PAGINATION_LIMIT_PARAM}
	 * @return
	 */
	String limitParamName() default "{LIMIT_PARAM}";
		
}

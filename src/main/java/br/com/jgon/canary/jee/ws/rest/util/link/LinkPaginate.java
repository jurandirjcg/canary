package br.com.jgon.canary.jee.ws.rest.util.link;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define como serao gerados os links de paginacao
 * @author jurandir
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LinkPaginate {

	/**
	 * Parametro responsavel por definir a pagina atual
	 * @return
	 */
	String pageParamName();
	/**
	 * Parametro responsavel por definir o limite de registros por pagina
	 * @return
	 */
	String limitParamName();
	/**
	 * Define o nome do atributo que contem a colecao paginada
	 * Default: nome da entidade ou annotation JsonRootName + Items
	 */
	String embeddedCollectionName() default "";
	/**
	 * 
	 * @return
	 */
	LinkTarget target() default LinkTarget.HEADER;
	/**
	 * Define o link SELF
	 * @return
	 */
	LinkResource self() default @LinkResource(rel="self", title="Self", includeRequestQueryParams=true, includeRequestPathParams=true);
	/**
	 * Define o link first
	 * @return
	 */
	LinkResource first() default @LinkResource(rel="first", title="First", includeRequestQueryParams=true, includeRequestPathParams=true);
	/**
	 * Define o link previous
	 * @return
	 */
	LinkResource previous() default @LinkResource(rel="prev", title="Previous", includeRequestQueryParams=true, includeRequestPathParams=true);
	/**
	 * Define o link next
	 * @return
	 */
	LinkResource next() default @LinkResource(rel="next", title="Next", includeRequestQueryParams=true, includeRequestPathParams=true);
	/**
	 * Define o link last
	 * @return
	 */
	LinkResource last() default @LinkResource(rel="last", title="Last", includeRequestQueryParams=true, includeRequestPathParams=true);
	/**
	 * Define o link com o template de navegacao
	 * @return
	 */
	PaginatorTemplate paginationTemplate() default @PaginatorTemplate(rel="paginationTemplate", title="Pagination Template");
	/**
	 * Define os links dos obejtos da lista de paginacao
	 * @return
	 */
	LinkResources collectionLinks() default @LinkResources({});
		
	/**
	 * Indica se utilizara o path completo
	 * @return
	 */
	boolean absolutePath() default true;

	/**
	 * Desativa o link self
	 * @return
	 */
	boolean disableSelf() default false;
	/**
	 * Desativa o link first
	 * @return
	 */
	boolean disableFirst() default false;
	/**
	 * Desativa o link previous
	 * @return
	 */
	boolean disablePrevious() default false;
	/**
	 * Desativa o link next
	 * @return
	 */
	boolean disableNext() default false;
	/**
	 * Desativa o link last
	 * @return
	 */
	boolean disableLast() default false;
	
}

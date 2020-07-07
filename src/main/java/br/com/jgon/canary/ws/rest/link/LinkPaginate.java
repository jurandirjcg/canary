/*
 * Copyright 2017 Jurandir C. Goncalves
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.jgon.canary.ws.rest.link;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define como serao gerados os links de paginacao
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LinkPaginate {

    /**
     * Parametro responsavel por definir a pagina atual
     * 
     * @return
     */
    String pageParamName();

    /**
     * Parametro responsavel por definir o limite de registros por pagina
     * 
     * @return
     */
    String limitParamName();

    /**
     * Define o nome do atributo que contem a colecao paginada.
     * Default: ultimo path param da requisicao
     * Ex: /v1/pacientes : "pacientes"
     */
    String embeddedCollectionName() default "";

    /**
     * Indica qual sera o path inicial do link, default LinkResouceBasePath.COMPLETE
     * Ex: /page/recurso ou http://dominio.com/page/recurso
     * 
     * @return
     */
    LinkResouceBasePath basePath() default LinkResouceBasePath.COMPLETE;

    /**
     * 
     * @return
     */
    LinkTarget target() default LinkTarget.HEADER;

    /**
     * Define o link SELF
     * 
     * @return
     */
    LinkResource self() default @LinkResource(rel = "self", title = "Self", includeRequestQueryParams = true);

    /**
     * Define o link first
     * 
     * @return
     */
    LinkResource first() default @LinkResource(rel = "first", title = "First", includeRequestQueryParams = true);

    /**
     * Define o link previous
     * 
     * @return
     */
    LinkResource previous() default @LinkResource(rel = "prev", title = "Previous", includeRequestQueryParams = true);

    /**
     * Define o link next
     * 
     * @return
     */
    LinkResource next() default @LinkResource(rel = "next", title = "Next", includeRequestQueryParams = true);

    /**
     * Define o link last
     * 
     * @return
     */
    LinkResource last() default @LinkResource(rel = "last", title = "Last", includeRequestQueryParams = true);

    /**
     * Define o link com o template de navegacao
     * 
     * @return
     */
    PaginatorTemplate paginationTemplate() default @PaginatorTemplate(rel = "paginationTemplate", title = "Pagination Template");

    /**
     * Define os links dos obejtos da lista de paginacao
     * 
     * @return
     */
    LinkResources collectionLinks() default @LinkResources({});

    /**
     * Indica se utilizara o path completo
     * 
     * @return
     */
    boolean absolutePath() default true;

    /**
     * Desativa o link self
     * 
     * @return
     */
    boolean disableSelf() default false;

    /**
     * Desativa o link first
     * 
     * @return
     */
    boolean disableFirst() default false;

    /**
     * Desativa o link previous
     * 
     * @return
     */
    boolean disablePrevious() default false;

    /**
     * Desativa o link next
     * 
     * @return
     */
    boolean disableNext() default false;

    /**
     * Desativa o link last
     * 
     * @return
     */
    boolean disableLast() default false;

    /**
     * Desativa o link de template de paginacao
     * 
     * @return
     */
    boolean disablePaginationTemplate() default false;

}

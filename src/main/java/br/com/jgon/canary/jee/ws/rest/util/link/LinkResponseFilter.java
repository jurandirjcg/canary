package br.com.jgon.canary.jee.ws.rest.util.link;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonRootName;
//import com.google.common.base.CaseFormat;

import br.com.jgon.canary.jee.exception.ApplicationException;
import br.com.jgon.canary.jee.util.CollectionUtil;
import br.com.jgon.canary.jee.util.Pagination;
import br.com.jgon.canary.jee.util.ReflectionUtil;
import br.com.jgon.canary.jee.ws.rest.WSFieldsParam;
import br.com.jgon.canary.jee.ws.rest.WSParamFormat;
import br.com.jgon.canary.jee.ws.rest.util.DominiosRest;
import br.com.jgon.canary.jee.ws.rest.util.ResponseError;
import br.com.jgon.canary.jee.ws.rest.util.json.JsonCollectionLinkEntity;
import br.com.jgon.canary.jee.ws.rest.util.json.JsonHALLinkEntity;
import br.com.jgon.canary.jee.ws.rest.util.json.JsonLinkEntity;

/**
 * Provider que habilita a inclusao de links na response
 * @author jurandir
 *
 */
@Provider
public class LinkResponseFilter implements ContainerResponseFilter {

	private static final String REGEX_PATH_PARAMETERS = "(\\#|\\$)\\{[a-z-A-Z\\.]+\\}";
	private static final String REGEX_REPLACE_PARAM = "\\#|\\$|\\{|\\}";
	private static final String REGEX_LINK_TEMPLATE = "(^\\{[a-zA-Z_-]+\\})|([^\\#\\$]\\{[a-zA-Z_-]+\\})";
	public static final String MEDIA_TYPE_APPLICATION_HAL_JSON = "application/hal+json";
	
	private Class<?> serviceClass;
	private Method serviceMethod;
	
	public LinkResponseFilter() {
		
	}
	
	public LinkResponseFilter(Class<?> serviceClass, Method serviceMethod) {
		super();
		this.serviceClass = serviceClass;
		this.serviceMethod = serviceMethod;
	}
	
	/**
	 * Intercepta a response para configrar os links
	 */
	@Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
    	if(responseContext.getEntityAnnotations() != null 
    			&& (responseContext.getEntityClass() == null 
    			|| !responseContext.getEntityClass().equals(ResponseError.class))){
    		
    		try{
    			configLinks(requestContext.getUriInfo(), responseContext);
			} catch (ApplicationException e) {
				IOException ioe = new IOException(e);
				ioe.addSuppressed(e);
				throw ioe;
			}
    	}
    }
    
	/**
	 * Configura os links para a response
	 * @param uriInfo
	 * @param responseContext
	 * @throws ApplicationException
	 */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void configLinks(UriInfo uriInfo, ContainerResponseContext responseContext) throws ApplicationException{
    	LinkResource linkResource = null;
    	LinkResources linkResources = null;
    	LinkPaginate linkPaginate = null;
    	LinkFormat linkFormat = null;
    	
    	boolean isHalLinks = responseContext.getMediaType() != null && responseContext.getMediaType().toString().equals(MEDIA_TYPE_APPLICATION_HAL_JSON);
    	
    	Map<Path, List<LinkResource>> linkPaginateEntity = null;
    	
    	Path path = null;
    	
    	for(Annotation a: responseContext.getEntityAnnotations()){
    		if(a instanceof LinkResource){
    			linkResource = (LinkResource) a;
    		}else if(a instanceof LinkResources){
    			linkResources = (LinkResources) a;
    		}else if(a instanceof LinkPaginate){
    			linkPaginate = (LinkPaginate) a;
    		}else if(a instanceof LinkFormat){
    			linkFormat = (LinkFormat) a; 
    		}else if(a instanceof Path){
    			path = (Path) a;
    		}
    	}
    	
    	List<Link> linksHeader = new ArrayList<Link>();
    	List<Link> linksEntity = new ArrayList<Link>();
    	List<Link> paginateLinks = new ArrayList<Link>();
    	    	
    	if(linkPaginate != null){
    		paginateLinks.addAll(getPaginateLink(responseContext.getEntity(), linkPaginate, uriInfo, path, responseContext.getStringHeaders()));

    		//Adiciona link na entidade somente se requisicao for hal+json
    		if(isHalLinks){
    			linkPaginateEntity = new HashMap<Path, List<LinkResource>>();

    			if(linkPaginate.collectionLinks().value().length > 0){
    				linkPaginateEntity.put(path, CollectionUtil.convertArrayToList(linkPaginate.collectionLinks().value()));
    			}

    			if(!linkPaginate.collectionLinks().serviceClass().equals(void.class) && StringUtils.isNotBlank(linkPaginate.collectionLinks().serviceMethodName())){
    				Class<?> serviceClassPag = linkPaginate.collectionLinks().serviceClass();
    				String serviceMethodNamePag = linkPaginate.collectionLinks().serviceMethodName();
    				Path pathAux = ReflectionUtil.getAnnotation(serviceClassPag, serviceMethodNamePag, Path.class);

    				List<LinkResource> linkResAux = new ArrayList<LinkResource>();

    				LinkResource lrAux = ReflectionUtil.getAnnotation(serviceClassPag,  serviceMethodNamePag, LinkResource.class);
    				if(lrAux != null){    				
    					linkResAux.add(reconfigLink(lrAux, serviceClassPag,  serviceMethodNamePag));
    				}

    				LinkResources lkRes = ReflectionUtil.getAnnotation(serviceClassPag,  serviceMethodNamePag, LinkResources.class);
    				if(lkRes != null){
    					for(LinkResource lr : lkRes.value()){
    						linkResAux.add(reconfigLink(lr, serviceClassPag,  serviceMethodNamePag));
    					}
    				}

    				linkPaginateEntity.put(pathAux, linkResAux);
    			}
    		}
    	}
    	
    	//Adiciona link na entidade somente se requisicao for hal+json
    	//LINKRESOURCE
    	if(isHalLinks && linkResource != null){
    		boolean isCollection = linkPaginate == null && (responseContext.getEntity() instanceof Collection);
    		if(isCollection){
    			List<LinkResource> linkResAux;
    			if(!linkResource.serviceClass().equals(void.class)){
    				Path pathAux = ReflectionUtil.getAnnotation(linkResource.serviceClass(), linkResource.serviceMethodName(), Path.class);
    				linkResAux = new ArrayList<LinkResource>();
    				LinkResource linkResourceAux = ReflectionUtil.getAnnotation(linkResources.serviceClass(), linkResources.serviceMethodName(), LinkResource.class);
    				if(linkResourceAux != null){
    					linkResAux.add(reconfigLink(linkResourceAux, linkResource.serviceClass(), linkResource.serviceMethodName()));
    					linkPaginateEntity.put(pathAux, linkResAux);
    				}
    			}else{
    				linkResAux = new ArrayList<LinkResource>();
    				linkResAux.add(linkResource);
    				linkPaginateEntity.put(path, linkResAux);
    			}
    		}else{
    			Link link = getLink(linkResource, uriInfo, null, responseContext.getEntity(), path, false);
    			if(link != null){
    				if(linkResource.target().equals(LinkTarget.ENTITY)){
    					linksEntity.add(link);
    				}else{
    					linksHeader.add(link);
    				}
    			}
    		}
    	}
    	//Adiciona link na entidade somente se requisicao for hal+json
    	//LINKRESOURCES
    	if(isHalLinks && linkResources != null){
    		boolean isCollection = linkPaginate == null && (responseContext.getEntity() instanceof Collection);
    		if(isCollection){
    			linkPaginateEntity = new HashMap<Path, List<LinkResource>>();
    			List<LinkResource> linkResAux;
    			if(!linkResources.serviceClass().equals(void.class)){
    				Path pathAux = ReflectionUtil.getAnnotation(linkResources.serviceClass(), linkResources.serviceMethodName(), Path.class);
    				linkResAux = new ArrayList<LinkResource>();
    				LinkResources linkResourcesAux = ReflectionUtil.getAnnotation(linkResources.serviceClass(), linkResources.serviceMethodName(), LinkResources.class);
    				if(linkResourcesAux != null && linkResourcesAux.value().length > 0){
    					for(LinkResource lr : linkResourcesAux.value()){
    						linkResAux.add(reconfigLink(lr, linkResources.serviceClass(), linkResources.serviceMethodName()));
    					}
    					linkPaginateEntity.put(pathAux, linkResAux);
    				}
    			}else{
    				linkResAux = new ArrayList<LinkResource>();
    				for(LinkResource lr : linkResources.value()){
    					linkResAux.add(lr);
    				}
    				linkPaginateEntity.put(path, linkResAux);
    			}
    		}else{
    			Link link;
    			for(LinkResource lr : linkResources.value()){
    				link = getLink(lr, uriInfo, null, responseContext.getEntity(), path, false);
    				if(link != null){
    					if(lr.target().equals(LinkTarget.ENTITY)){
    						linksEntity.add(link);
    					}else{
    						linksHeader.add(link);
    					}
    				}
    			}
    			if(!linkResources.serviceClass().equals(void.class)){
	    			Path pathAux = ReflectionUtil.getAnnotation(linkResources.serviceClass(), linkResources.serviceMethodName(), Path.class);
					LinkResources linkResourcesAux = ReflectionUtil.getAnnotation(linkResources.serviceClass(), linkResources.serviceMethodName(), LinkResources.class);
					
					if(linkResourcesAux != null){
						for(LinkResource lr : linkResourcesAux.value()){
							link = getLink(lr, uriInfo, null, responseContext.getEntity(), pathAux, false);
							if(link != null){
								if(lr.target().equals(LinkTarget.ENTITY)){
									linksEntity.add(link);
								}else{
									linksHeader.add(link);
								}
							}
						}
					}
					
					LinkResource linkResourceAux =  ReflectionUtil.getAnnotation(linkResources.serviceClass(), linkResources.serviceMethodName(), LinkResource.class);
					if(linkResourceAux != null){
						link = getLink(linkResourceAux, uriInfo, null, responseContext.getEntity(), pathAux, false);
						if(link != null){
							if(linkResourceAux.target().equals(LinkTarget.ENTITY)){
								linksEntity.add(link);
							}else{
								linksHeader.add(link);
							}
						}
					}
    			}				
    		}
    	}
    	
    	if(!paginateLinks.isEmpty()){
    		if(isHalLinks || linkPaginate.target().equals(LinkTarget.ENTITY)){
				linksEntity.addAll(paginateLinks);
			}else{
				linksHeader.addAll(paginateLinks);
			}
    	}
    	    	
    	Collection<String> attrNotPresentInRequest = getRequiredParamNotPresentInRequest(serviceMethod, uriInfo);
    	
    	if(!linksEntity.isEmpty() || (linkPaginateEntity != null && !linkPaginateEntity.isEmpty())){
    		if(!(responseContext.getEntity() instanceof Collection)){
    			if(isHalLinks || (linkFormat != null && linkFormat.value().equals(LinkFormatType.HAL))){
    				JsonHALLinkEntity json = new JsonHALLinkEntity();
        			json.setEntity(configEntityAttributes(responseContext.getEntity(), attrNotPresentInRequest));
        			json.addAllLink(linksEntity);
        			responseContext.setEntity(json);
    			}else{
	    			JsonLinkEntity json = new JsonLinkEntity();
	    			json.setEntity(configEntityAttributes(responseContext.getEntity(), attrNotPresentInRequest));
	    			json.addAllLink(linksEntity);
	    			responseContext.setEntity(json);
    			}
    		}else if(linkPaginate == null){
    			if(isHalLinks){
    				responseContext.setEntity(getCollectionEntityLink((Collection<Object>) responseContext.getEntity(), isHalLinks, linkPaginateEntity, uriInfo, attrNotPresentInRequest));
    			}
    		}else{
    			String childName;
    			if(linkPaginate != null && StringUtils.isNotBlank(linkPaginate.embeddedCollectionName())){
    				childName = linkPaginate.embeddedCollectionName();
    			}else{
    				childName = getChildClassName((Collection<Object>) responseContext.getEntity());
    				if(StringUtils.isBlank(childName)){
    					childName = "items";
    				}else{
    					childName += "Items";
    				}
    			}
    			    			
    			JsonCollectionLinkEntity json = new JsonCollectionLinkEntity();
    			json.setHalLink(isHalLinks || linkFormat == null || linkFormat.value().equals(LinkFormatType.HAL));
    			if(isHalLinks){
    				json.setEmbedded(getCollectionEntityLink((Collection<Object>) responseContext.getEntity(), isHalLinks, linkPaginateEntity, uriInfo, attrNotPresentInRequest));
    			}else{
    				json.setEmbedded(getCollectionEntity((Collection<Object>) responseContext.getEntity(), attrNotPresentInRequest));
    			}
    		
    			json.addAllLink(linksEntity);
    			json.setItemsName(childName);
    			
    			responseContext.setEntity(json);
    			
    			if(json.isHalLink()){
    				json.setTotal(responseContext.getHeaders().getFirst(DominiosRest.X_PAGINATION_TOTAL_COUNT) == null ? null : Long.valueOf(responseContext.getHeaders().getFirst(DominiosRest.X_PAGINATION_TOTAL_COUNT).toString()));
    				json.setLimit(responseContext.getHeaders().getFirst(DominiosRest.X_PAGINATION_LIMIT) == null ? null : Long.valueOf(responseContext.getHeaders().getFirst(DominiosRest.X_PAGINATION_LIMIT).toString()));
    				json.setPage(responseContext.getHeaders().getFirst(DominiosRest.X_PAGINATION_PAGE) == null ? null : Integer.valueOf(responseContext.getHeaders().getFirst(DominiosRest.X_PAGINATION_PAGE).toString()));
    				
    				responseContext.getHeaders().remove(DominiosRest.X_PAGINATION_TOTAL_COUNT);
    				responseContext.getHeaders().remove(DominiosRest.X_PAGINATION_LIMIT);
    				responseContext.getHeaders().remove(DominiosRest.X_PAGINATION_PAGE);
    			}
    		}
    	}else if(attrNotPresentInRequest != null && !attrNotPresentInRequest.isEmpty()){
    		if(responseContext.getEntity() instanceof Collection){
    			responseContext.setEntity(getCollectionEntity((Collection<Object>) responseContext.getEntity(), attrNotPresentInRequest));
    		}else if(responseContext.getEntity() instanceof Pagination){
    			((Pagination) responseContext.getEntity()).setRegistros(getCollectionEntity(((Pagination) responseContext.getEntity()).getRegistros(), attrNotPresentInRequest)); ;
    		}else{
    			responseContext.setEntity(configEntityAttributes(responseContext.getEntity(), attrNotPresentInRequest));
    		}
    	}
    	
    	if(!linksHeader.isEmpty()){
    		responseContext.getHeaders().add("Link", linksHeader);
    	}
    }
    
    /**
     * Retorno o valor do parametro para configurar o link corretamente
     * @param param
     * @param entity
     * @return
     */
    private String valueFromParameter(String param, Object entity){
    	Pattern pattern = Pattern.compile(REGEX_PATH_PARAMETERS);
    	Matcher m = pattern.matcher(param);
 
    	StringBuffer sb = new StringBuffer();
    	
    	while(m.find()){
    		Object objRet = ReflectionUtil.getAttributteValue(entity, m.group().replaceAll(REGEX_REPLACE_PARAM, ""));
    		if(objRet == null){
    			return null;
    		}
    		m.appendReplacement(sb, objRet.toString());
    	}
    	
    	m.appendTail(sb);
    	
    	return sb.toString();
    }
    
    /**
     * Configura a entidade e seus objetos complexos filhos para null.
     * @param entity
     * @param listAttributes
     * @return
     */
    private Object configEntityAttributes(Object entity, Collection<String> listAttributes){
    	try {
    		if(listAttributes != null && !listAttributes.isEmpty()){
    			ReflectionUtil.setValueToNullCascade(entity, CollectionUtil.convertCollectionToArray(String.class, listAttributes));
    		}
		} catch (Exception e) {
			// Ignora o erro se nao conseguir setar para nulo
		}
    	
    	return entity;
    }
    
    /**
     * Configura os links nos objetos da colecao
     * @param entityList
     * @param isHalLinks
     * @param linkResources
     * @param uriInfo
     * @param attrNotPresentInRequest
     * @return
     * @throws ApplicationException
     */
    private Collection<Object> getCollectionEntityLink(Collection<Object> entityList, boolean isHalLinks, Map<Path, List<LinkResource>> linkResources, UriInfo uriInfo, Collection<String> attrNotPresentInRequest) throws ApplicationException{
    	if(linkResources!=null && !linkResources.isEmpty()){
    		List<Object> entityCollection = new ArrayList<Object>(entityList.size());
    		List<Link> listLink = new ArrayList<Link>(2);
    		if(isHalLinks){
    			JsonHALLinkEntity json;
    			for(Object entity : entityList){
    				listLink.clear();
    				for(Path path : linkResources.keySet()){
    					for(LinkResource lr : linkResources.get(path)){
    						listLink.add(getLink(lr, uriInfo, null, entity, path, false));
    					}
    				}
    				json = new JsonHALLinkEntity();
					json.setEntity(configEntityAttributes(entity, attrNotPresentInRequest));
					json.addAllLink(listLink);

					entityCollection.add(json);
    			}
    		}else{
    			JsonLinkEntity json;
    			for(Object entity : entityList){
    				listLink.clear();
    				for(Path path : linkResources.keySet()){
    					for(LinkResource lr : linkResources.get(path)){
    						listLink.add(getLink(lr, uriInfo, null, entity, path, false));
    					}
    				}
    				json = new JsonLinkEntity();
					json.setEntity(configEntityAttributes(entity, attrNotPresentInRequest));
					json.addAllLink(listLink);

					entityCollection.add(json);
    			}
    		}
    		return entityCollection;
    	}else{
    		return getCollectionEntity(entityList, attrNotPresentInRequest);
    	}
    }
    /**
     * 
     * @param entityList
     * @param attrNotPresentInRequest
     * @return
     * @throws ApplicationException
     */
    private Collection<Object> getCollectionEntity(Collection<Object> entityList, Collection<String> attrNotPresentInRequest) throws ApplicationException{
    	if(attrNotPresentInRequest != null && !attrNotPresentInRequest.isEmpty()){
    		List<Object> entityCollection = new ArrayList<Object>(entityList.size());
    		for(Object entity : entityList){
    			entityCollection.add(configEntityAttributes(entity, attrNotPresentInRequest));
    		}
    		return entityCollection;
    	}else{
    		return entityList;
    	}    	
    }
    /**
     * Configura o link
     * @param linkResource
     * @param uriInfo
     * @param queryParams
     * @param entity
     * @param path
     * @param forceAbsolutePath
     * @return
     * @throws ApplicationException
     */
    private Link getLink(LinkResource linkResource, UriInfo uriInfo, List<SimpleEntry<String, Object>> queryParams, Object entity, Path path, boolean forceAbsolutePath) throws ApplicationException{
    	return getLink(linkResource.pathParameters(), linkResource.queryParameters(), linkResource.serviceClass(), linkResource.serviceMethodName(), linkResource.basePath(), linkResource.includeRequestQueryParams(), linkResource.rel(), linkResource.type(), linkResource.title(), uriInfo, queryParams, entity, path, forceAbsolutePath);
    }
    /**
     * Configura o link
     * @param linkPathParameters
     * @param linkQueryParameter
     * @param linkServiceClass
     * @param linkServiceMethodName
     * @param linkBasePath
     * @param linkIncludeQueryParams
     * @param linkRel
     * @param linkType
     * @param linkTitle
     * @param uriInfo
     * @param queryParams
     * @param entity
     * @param path
     * @param forceAbsolutePath
     * @return
     * @throws ApplicationException
     */
    private Link getLink(String[] linkPathParameters, String[] linkQueryParameter, Class<?> linkServiceClass, String linkServiceMethodName, LinkResouceBasePath linkBasePath, boolean linkIncludeQueryParams, String linkRel, String linkType, String linkTitle, UriInfo uriInfo, List<SimpleEntry<String, Object>> queryParams, Object entity, Path path, boolean forceAbsolutePath) throws ApplicationException{
    	Builder builder;
    	
    	List<Object> values = new LinkedList<Object>();
    	boolean linkTemplate = false;
   
    	//Configura os PathParams da requisicao original
    	for(String pk : uriInfo.getPathParameters().keySet()){
    		if(uriInfo.getPathParameters().get(pk).size() == 1){
    			values.add(uriInfo.getPathParameters().getFirst(pk));
    		}else{
    			values.add(uriInfo.getPathParameters().get(pk));
    		}
    	}
    	//Configura os PathParams adicionados, vinculados ao objeto de retorno
		if(linkPathParameters != null && linkPathParameters.length > 0){
			for(int i = 0; i < linkPathParameters.length; i++){
				if(StringUtils.isBlank(linkPathParameters[i])){
					values.add("");
				}else{
					if(linkPathParameters[i].matches(REGEX_LINK_TEMPLATE)){
			    		linkTemplate = true;
					}
					values.add(valueFromParameter(linkPathParameters[i], entity));
				}
			}
		}
		
		UriBuilder uriBuilder = null;
		
		Class<?> serviceClassAux = linkServiceClass.equals(void.class) ? serviceClass : linkServiceClass;
		String serviceMethodName = StringUtils.isBlank(linkServiceMethodName) ? serviceMethod.getName() : linkServiceMethodName;
		
		if(linkBasePath.equals(LinkResouceBasePath.COMPLETE) || forceAbsolutePath){
			uriBuilder = uriInfo.getBaseUriBuilder().path(serviceClassAux);
			if(StringUtils.isNotBlank(linkServiceMethodName)){
				uriBuilder.path(serviceClassAux, serviceMethodName);
			}else{
				uriBuilder.path(serviceMethod);
			}
		}else if(linkBasePath.equals(LinkResouceBasePath.RESOURCE)){
			uriBuilder = UriBuilder.fromResource(serviceClassAux);
		}else if(linkBasePath.equals(LinkResouceBasePath.METHOD)){
			uriBuilder = UriBuilder.fromMethod(serviceClassAux, serviceMethodName);
		}
		
		if(linkIncludeQueryParams){// || queryParams != null){
			for(String key : uriInfo.getQueryParameters().keySet()){
				uriBuilder.replaceQueryParam(key, uriInfo.getQueryParameters(false).get(key));
			}
		}
		
		Pattern pattern = Pattern.compile(REGEX_LINK_TEMPLATE);
				
		if(queryParams != null){
			for(SimpleEntry<String, Object> qp : queryParams){
				Matcher m = pattern.matcher(qp.getValue().toString());
				
			    StringBuffer sb = new StringBuffer();
			    
			    while(m.find()){
			    	linkTemplate = true;
			    	String val = m.group().indexOf("{") == 1 ? m.group().substring(1) : m.group();
			    	m.appendReplacement(sb, val);
			    }
			    if(linkTemplate){
			    	m.appendTail(sb);
			    	values.add(sb.toString());
			    }
				
				uriBuilder.replaceQueryParam(qp.getKey(), qp.getValue());
			}
		}
		
		if(linkQueryParameter != null && linkQueryParameter.length > 0){
			for(int i = 0; i < linkQueryParameter.length; i++){
				if(StringUtils.isNotBlank(linkQueryParameter[i])){
					String paramName = linkQueryParameter[i].substring(0, linkQueryParameter[i].indexOf("="));
					uriBuilder.replaceQueryParam(paramName, valueFromParameter(linkQueryParameter[i].substring(linkQueryParameter[i].indexOf("=") + 1), entity));
				}
			}
		}
		
		builder = Link.fromUriBuilder(uriBuilder).rel(linkRel);
		
    	if(StringUtils.isNotBlank(linkTitle)){
    		builder = builder.title(linkTitle);
    	}
    	
    	if(StringUtils.isNotBlank(linkType)){
    		builder = builder.title(linkType);
    	}
    	
    	if(linkTemplate){
    		builder.param("templated", "true");
    	}
    	
    	try{
    		if(!values.isEmpty()){
    			Object[] valuesAux = values.toArray(new Object[]{});
    			return builder.build(valuesAux);
    		}else{
    			return builder.build();
    		}
    	}catch(IllegalArgumentException e){
    		throw new ApplicationException("error.link-builder", e);
    	}
    }
    /**
     * Configura o link de paginacao
     * @param entity
     * @param linkPaginate
     * @param uriInfo
     * @param path
     * @param headers
     * @return
     * @throws ApplicationException
     */
    private List<Link> getPaginateLink(Object entity, LinkPaginate linkPaginate, UriInfo uriInfo, Path path, MultivaluedMap<String, String> headers) throws ApplicationException{
    
    	if(StringUtils.isBlank(linkPaginate.pageParamName())){
    		return Collections.emptyList(); 
    	}
    	
    	List<Link> paginationLinks = new ArrayList<Link>(4);
    	
    	int pgAtual = 0;
    	long total = 0L;
    	int limitPg = 0;
    	
    	if(entity != null && entity instanceof Pagination){
    		Pagination<?> pEntity = (Pagination<?>) entity;
    		
    		pgAtual = pEntity.getPaginaAtual();
    		total = pEntity.getQtdeTotalRegistros();
    		limitPg = pEntity.getQtdeRegistrosPagina();
    		
    	}else if(headers.containsKey(DominiosRest.X_PAGINATION_PAGE) 
    				&& headers.containsKey(DominiosRest.X_PAGINATION_TOTAL_COUNT)
    				&& headers.containsKey(DominiosRest.X_PAGINATION_LIMIT)){
    		
    		pgAtual = Integer.parseInt(headers.getFirst(DominiosRest.X_PAGINATION_PAGE));
        	total = Long.parseLong(headers.getFirst(DominiosRest.X_PAGINATION_TOTAL_COUNT));
        	limitPg = Integer.parseInt(headers.getFirst(DominiosRest.X_PAGINATION_LIMIT));
    	}else{
    		//Retorna lista vazia pois não tem parâmetros de paginação
    		return Collections.emptyList();
    	}
    	
    	long maxPgs = total/limitPg;
    	float resMaxPgs = total % limitPg;
    	
    	if(resMaxPgs != 0){
    		maxPgs++;
    	}
    	
    	List<SimpleEntry<String, Object>> queryParams = new ArrayList<SimpleEntry<String, Object>>(2);
    	
    	if(StringUtils.isNotBlank(linkPaginate.limitParamName())){
    		if(uriInfo.getQueryParameters().containsKey(linkPaginate.limitParamName())){
    			int limit = Integer.parseInt(uriInfo.getQueryParameters(false).getFirst(linkPaginate.limitParamName()));
    			if(limit > limitPg){
    				queryParams.add(new SimpleEntry<String, Object>(linkPaginate.limitParamName(), limitPg));
    			}
    		}else{
    			queryParams.add(new SimpleEntry<String, Object>(linkPaginate.limitParamName(), limitPg));
    		}
    	}

    	List<SimpleEntry<String, Object>> queryParamsAux = new ArrayList<SimpleEntry<String, Object>>(2);
    	
    	//SELF
    	if(!linkPaginate.disableSelf()){
    		paginationLinks.add(getLink(linkPaginate.self(), uriInfo,  queryParams, entity, path, linkPaginate.absolutePath()));
    	}
    	
    	//FIRST
    	if(!linkPaginate.disableFirst() && pgAtual > 1){
    		queryParamsAux.clear();
    		queryParamsAux.addAll(queryParams);
    		queryParamsAux.add(new SimpleEntry<String, Object>(linkPaginate.pageParamName(), 1));
    		paginationLinks.add(getLink(linkPaginate.first(), uriInfo, queryParamsAux, entity, path, linkPaginate.absolutePath()));
    	}
    	
    	//PREVIOUS
    	if(!linkPaginate.disablePrevious() && pgAtual > 1){
    		queryParamsAux.clear();
    		queryParamsAux.addAll(queryParams);
    		queryParamsAux.add(new SimpleEntry<String, Object>(linkPaginate.pageParamName(), pgAtual - 1));
    		paginationLinks.add(getLink(linkPaginate.previous(), uriInfo, queryParamsAux, entity, path, linkPaginate.absolutePath()));
    	}
    	
    	//NEXT
    	if(!linkPaginate.disableNext() && maxPgs > pgAtual){
    		queryParamsAux.clear();
    		queryParamsAux.addAll(queryParams);
    		queryParamsAux.add(new SimpleEntry<String, Object>(linkPaginate.pageParamName(), pgAtual + 1));
    		paginationLinks.add(getLink(linkPaginate.next(), uriInfo, queryParamsAux, entity, path, linkPaginate.absolutePath()));
    	}
    	
    	//LAST
    	if(!linkPaginate.disableLast() && maxPgs > pgAtual){
    		queryParamsAux.clear();
    		queryParamsAux.addAll(queryParams);
    		queryParamsAux.add(new SimpleEntry<String, Object>(linkPaginate.pageParamName(), maxPgs));
    		paginationLinks.add(getLink(linkPaginate.last(), uriInfo, queryParamsAux, entity, path, linkPaginate.absolutePath()));
    	}
    	
    	//Pagination Template
    	queryParamsAux.clear();
		queryParamsAux.addAll(queryParams);
		queryParamsAux.add(new SimpleEntry<String, Object>(linkPaginate.pageParamName(), linkPaginate.paginationTemplate().pageParamName()));
		queryParamsAux.add(new SimpleEntry<String, Object>(linkPaginate.limitParamName(), linkPaginate.paginationTemplate().limitParamName()));
    	paginationLinks.add(getLink(linkPaginate.paginationTemplate().pathParameters(), linkPaginate.paginationTemplate().queryParameters(), linkPaginate.paginationTemplate().serviceClass(), linkPaginate.paginationTemplate().serviceMethodName(), linkPaginate.paginationTemplate().basePath(), linkPaginate.paginationTemplate().includeRequestQueryParams(), linkPaginate.paginationTemplate().rel(), linkPaginate.paginationTemplate().type(), linkPaginate.paginationTemplate().title(), uriInfo, queryParamsAux, entity, path, linkPaginate.absolutePath()));
        	
    	return paginationLinks;
    }
    /**
     * @param list
     * @return
     */
	private <T> String getChildClassName(Collection<T> list){
    	if(list != null){
    		for(T obj: list){
    			Class<?> klass = obj.getClass();
    			if(klass.isAnnotationPresent(JsonRootName.class)){
    				return klass.getAnnotation(JsonRootName.class).value();
    			}
    			return obj.getClass().getSimpleName();
    		}
    	}
    	
    	return null;
    }
	/**
	 * Reconfigura a annotation de link
	 * @param link
	 * @param serviceClass
	 * @param serviceMethodName
	 * @return
	 */
    private LinkResource reconfigLink(LinkResource link, Class<?> serviceClass, String serviceMethodName){
    	return new LinkResource() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return link.annotationType();
			}
			@Override
			public String type() {
				return link.type();
			}
			@Override
			public String title() {
				return link.title();
			}
			
			@Override
			public LinkTarget target() {
				return link.target();
			}
			@Override
			public String serviceMethodName() {
				return serviceMethodName;
			}
			@Override
			public Class<?> serviceClass() {
				return serviceClass;
			}
			@Override
			public String rel() {
				return link.rel();
			}
			@Override
			public String[] pathParameters() {
				return link.pathParameters();
			}
			@Override
			public boolean includeRequestQueryParams() {
				return link.includeRequestQueryParams();
			}
			@Override
			public LinkResouceBasePath basePath() {
				return link.basePath();
			}
			@Override
			public String[] queryParameters() {
				return link.queryParameters();
			}
		};
    }
    /**
     * Retorna o parametro obrigatorio para a cricao do link mas que nao esta na lista de parametros solicitados na requisicao,
     * utilizado para remover o parametro da resposta 
     * @param serviceMethod
     * @param uriInfo
     * @return
     * @throws ApplicationException
     */
	private Collection<String> getRequiredParamNotPresentInRequest(Method serviceMethod, UriInfo uriInfo) throws ApplicationException {
		
		Annotation[][] parametrosAnotados = serviceMethod.getParameterAnnotations();
		Class<?>[] parameterTypes = serviceMethod.getParameterTypes();
		
		QueryParam queryParam = null;
		WSParamFormat wsParam = null;
		DefaultValue defValue = null;
		
		for(int i=0; i < parametrosAnotados.length; i++){
			Annotation[] parametroAnotado = parametrosAnotados[i];
			if(parameterTypes[i].equals(WSFieldsParam.class)){
				for(Annotation a : parametroAnotado){
					if(a instanceof QueryParam){
						queryParam = (QueryParam) a;
					}else if( a instanceof WSParamFormat){
						wsParam = (WSParamFormat) a;
					}else if(a instanceof DefaultValue){
						defValue = (DefaultValue) a;
					}
				}
				break;
			}
		}
		
		if(queryParam == null || wsParam == null){
			return null;
		}
		
		List<LinkResource> listResources = new ArrayList<LinkResource>(1);
		listResources.addAll(paramFields(serviceMethod));

		if(!listResources.isEmpty()){
			Pattern pattern = Pattern.compile(REGEX_PATH_PARAMETERS);
			Matcher matcher;
			Set<String> listParamsRemove = new HashSet<String>();
			
			Set<String> listParams = new HashSet<String>();
			if(uriInfo.getQueryParameters().containsKey(queryParam.value())){
				listParams.addAll(uriInfo.getQueryParameters(false).get(queryParam.value()));
			}
			
			//Campos forcados na annotation
			for(String f : wsParam.forceFields()){
				if(!listParams.contains(f)){
					listParams.add(f);
				}
			}
			
			if(listParams.isEmpty() && defValue != null){
				for(String dAux : defValue.value().split(",")){
					listParams.add(dAux);
				}
			}
			
			for(LinkResource linkResource : listResources){
				String mGroup;
				for(String qp : linkResource.queryParameters()){
					matcher = pattern.matcher(qp);
					while(matcher.find()){
						mGroup = matcher.group().replaceAll(REGEX_REPLACE_PARAM, "");
						if(!CollectionUtil.constainsValue(listParams, mGroup)){
							listParamsRemove.add(mGroup);
						}
					}
				}

				for(String qp : linkResource.pathParameters()){
					matcher = pattern.matcher(qp);
					while(matcher.find()){
						mGroup = matcher.group().replaceAll(REGEX_REPLACE_PARAM, "");
						if(!CollectionUtil.constainsValue(listParams, mGroup)){
							listParamsRemove.add(mGroup);
						}
					}
				}
			}
			return listParamsRemove;
		}
		return null;
	}
	
	/**
	 * Parametros exigidos no metedo para criacao do link
	 * @param method
	 * @return
	 */
	private List<LinkResource> paramFields(Method method){
		Annotation[][] parametrosAnotados = method.getParameterAnnotations();
		Class<?>[] parameterTypes = method.getParameterTypes();
		
		WSParamFormat wsAnnotation = null;
		
		for(int i=0; i < parametrosAnotados.length; i++){
			Annotation[] parametroAnotado = parametrosAnotados[i];
			if(parameterTypes[i].equals(WSFieldsParam.class)){
				for(Annotation a : parametroAnotado){
					if(a instanceof WSParamFormat){
						wsAnnotation = (WSParamFormat) a;
					}
				}
				break;
			}
		}
		
		if(wsAnnotation != null){
			List<LinkResource> listResources = new ArrayList<LinkResource>(1);

			LinkResource lr = method.getAnnotation(LinkResource.class);
			LinkResources lrs = method.getAnnotation(LinkResources.class);
			LinkPaginate lp = method.getAnnotation(LinkPaginate.class);

			if(lr != null){
				listResources.add(lr);
			}

			if(lrs != null){
				if(lrs.value().length > 0){
					listResources.addAll(CollectionUtil.convertArrayToList(lrs.value()));
				}
				if(!lrs.serviceClass().equals(void.class)){
					listResources.addAll(paramFields(ReflectionUtil.getMethod(lrs.serviceClass(), lrs.serviceMethodName())));
				}
			}

			if(lp != null){
				if(lp.collectionLinks().value().length > 0){
					listResources.addAll(CollectionUtil.convertArrayToList(lp.collectionLinks().value()));
				}
	
				if(!lp.collectionLinks().serviceClass().equals(void.class)){
					listResources.addAll(paramFields(ReflectionUtil.getMethod(lp.collectionLinks().serviceClass(), lp.collectionLinks().serviceMethodName())));
				}
			}
			
			return listResources;
		}
		return null;
	}
	
	public static String toSnakeCase(String value){
		return  value.replaceAll("([A-Z]+)","\\_$1").toLowerCase(); 
	}	
	
	public static String toCamelCase(String value){
		StringBuilder sb = new StringBuilder();
		for (String s : value.split("_")) {
			if(sb.length() > 1){
				sb.append(Character.toUpperCase(s.charAt(0)));
				if (s.length() > 1) {
					sb.append(s.substring(1, s.length()).toLowerCase());
				}
			}else{
				sb.append(s.toLowerCase());
			}
		}

		return  sb.toString(); 
	}
}
package br.com.jgon.canary.jee.ws.rest.util;

import java.io.IOException;
import java.util.Collections;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.jgon.canary.jee.util.Pagination;

/**
 * Configura os filtros REST
 * @author jurandir
 *
 */
@Provider
public class RestFilter implements ContainerResponseFilter, ContextResolver<ObjectMapper> {

	private final ObjectMapper mapper;
	
	@Context
	private ResourceInfo resourceInfo;
	
	public RestFilter() {
		mapper = new ObjectMapper();
		//mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		//Quem esta chamando o WS deve saber os parametros aceitos
		//mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}


	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}
	
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
    	//responseContext.getHeaders().add("Content-Type", responseContext.getHeaders().get("Content-Type") + ";charset=UFT-8");
    	
    	if(responseContext.getEntity() instanceof Pagination){
    		Pagination<?> pEntity = (Pagination<?>) responseContext.getEntity();
    		if(pEntity.getRegistros() == null){
    			responseContext.setEntity(Collections.EMPTY_LIST);
    		}else{
    			responseContext.setEntity(pEntity.getRegistros());
    		}
    		if(!responseContext.getHeaders().containsKey(DominiosRest.X_PAGINATION_TOTAL_COUNT)){
    			responseContext.getHeaders().add(DominiosRest.X_PAGINATION_TOTAL_COUNT, pEntity.getQtdeTotalRegistros());
    		}
    		if(!responseContext.getHeaders().containsKey(DominiosRest.X_PAGINATION_LIMIT)){
    			responseContext.getHeaders().add(DominiosRest.X_PAGINATION_LIMIT, pEntity.getQtdeRegistrosPagina());
    		}
    		if(!responseContext.getHeaders().containsKey(DominiosRest.X_PAGINATION_PAGE)){
    			responseContext.getHeaders().add(DominiosRest.X_PAGINATION_PAGE, pEntity.getPaginaAtual());
    		}
    	}
    	
    	if(requestContext.getMethod().equals(HttpMethod.POST) && responseContext.getStatusInfo().equals(Status.OK)){
    		responseContext.setStatusInfo(Response.Status.CREATED);
    	}
    }
}
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
package br.com.jgon.canary.ws.rest;

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

import br.com.jgon.canary.util.Pagination;
import br.com.jgon.canary.ws.rest.util.DominiosRest;

/**
 * Configura os filtros REST
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
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
    		if(pEntity.getElements() == null){
    			responseContext.setEntity(Collections.EMPTY_LIST);
    		}else{
    			responseContext.setEntity(pEntity.getElements());
    		}
    		if(!responseContext.getHeaders().containsKey(DominiosRest.X_PAGINATION_TOTAL_ELEMENTS)){
    			responseContext.getHeaders().add(DominiosRest.X_PAGINATION_TOTAL_ELEMENTS, pEntity.getTotalElements());
    		}
    		if(!responseContext.getHeaders().containsKey(DominiosRest.X_PAGINATION_ELEMENTS_PER_PAGE)){
    			responseContext.getHeaders().add(DominiosRest.X_PAGINATION_ELEMENTS_PER_PAGE, pEntity.getElementsPerPage());
    		}
    		if(!responseContext.getHeaders().containsKey(DominiosRest.X_PAGINATION_CURRENT_PAGE)){
    			responseContext.getHeaders().add(DominiosRest.X_PAGINATION_CURRENT_PAGE, pEntity.getCurrentPage());
    		}
    		if(!responseContext.getHeaders().containsKey(DominiosRest.X_PAGINATION_TOTAL_PAGE)){
    			responseContext.getHeaders().add(DominiosRest.X_PAGINATION_TOTAL_PAGE, pEntity.getTotalPages());
    		}
    	}
    	
    	if(requestContext.getMethod().equals(HttpMethod.POST) && responseContext.getStatusInfo().equals(Status.OK)){
    		responseContext.setStatusInfo(Response.Status.CREATED);
    	}
    }
}
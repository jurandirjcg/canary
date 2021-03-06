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
package br.com.jgon.canary.ws.rest.util;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import br.com.jgon.canary.util.Page;

/**
 * Util de WebService
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class WSUtil {
	
	public static String[] convertStringListToArray(List<String> list){
		return list.toArray(new String[list.size()]);
	}
	
	/**
	 * 
	 * @param paginacao
	 * @return
	 */
	public static <T> ResponseBuilder setPaginationToResponse(Page<T> paginacao){
		return Response.ok().entity(paginacao.getElements())
				.header(DominiosRest.X_PAGINATION_TOTAL_ELEMENTS, paginacao.getTotalElements())
				.header(DominiosRest.X_PAGINATION_ELEMENTS_PER_PAGE, paginacao.getElementsPerPage())
				.header(DominiosRest.X_PAGINATION_CURRENT_PAGE, paginacao.getCurrentPage())
				.header(DominiosRest.X_PAGINATION_TOTAL_PAGE, paginacao.getTotalPages());
	}
}

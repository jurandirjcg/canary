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

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.validation.exception.ValidationException;
import br.com.jgon.canary.ws.rest.exception.RestExceptionMapper;
import br.com.jgon.canary.ws.rest.exception.RestValidationExceptionMapper;
import br.com.jgon.canary.ws.rest.link.LinkResponseFeature;
import br.com.jgon.canary.ws.rest.param.DateFormatter;
import br.com.jgon.canary.ws.rest.param.WsFieldsParamFormatter;
import br.com.jgon.canary.ws.rest.param.WsOrderParamFormatter;

/**
 * Mapeamento manual dos resources
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public abstract class CanaryRestResources {
	
	private static Set<Class<?>> resources = new HashSet<Class<?>>();
	
	static{
		resources.add(RestFilter.class);
		resources.add(DateFormatter.class);
		resources.add(WsFieldsParamFormatter.class);
		resources.add(WsOrderParamFormatter.class);
		resources.add(LinkResponseFeature.class);
	}

	/**
	 * Adiciona classes de tratamento de exeção de chamadas REST
	 * @param checkException: adicionar tratamento de excecoes ({@link ApplicationException} {@link ApplicationRuntimeException} {@link ValidationException} {@link WebApplicationException} {@link Exception}
	 * @return
	 */
	public static Set<Class<?>> getClasses(boolean checkException){
		if(checkException){
			resources.add(RestExceptionMapper.class);
			resources.add(RestValidationExceptionMapper.class);
		}
		return resources;
	}
	
	/**
	 * Adiciona classes de tratamento de chamadas REST, adiciona inclusive o tratamento de exceções
	 * @return
	 */
	public static Set<Class<?>> getClasses(){
		return getClasses(true);
	}
}

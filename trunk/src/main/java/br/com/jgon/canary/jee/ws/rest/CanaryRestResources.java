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
package br.com.jgon.canary.jee.ws.rest;

import java.util.HashSet;
import java.util.Set;

import br.com.jgon.canary.jee.exception.ApplicationException;
import br.com.jgon.canary.jee.exception.ApplicationRuntimeException;
import br.com.jgon.canary.jee.validation.exception.ValidationException;
import br.com.jgon.canary.jee.ws.rest.util.RestFilter;
import br.com.jgon.canary.jee.ws.rest.util.link.LinkResponseFeature;
import br.com.jgon.canary.jee.ws.rest.validation.RestValidationParameterFilter;
import br.com.jgon.canary.jee.ws.rest.validation.exception.RestApplicationExceptionMapper;
import br.com.jgon.canary.jee.ws.rest.validation.exception.RestApplicationRuntimeExceptionMapper;
import br.com.jgon.canary.jee.ws.rest.validation.exception.RestValidationExceptionMapper;

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
		resources.add(RestValidationParameterFilter.class);
	}

	/**
	 * Adiciona classes de tratamento de chamadas REST
	 * @param checkException: adicionar tratamento de excecoes ({@link ApplicationException} {@link ApplicationRuntimeException} {@link ValidationException}
	 * @return
	 */
	public static Set<Class<?>> getClasses(boolean checkException){
		if(checkException){
			resources.add(RestApplicationExceptionMapper.class);
			resources.add(RestApplicationRuntimeExceptionMapper.class);
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

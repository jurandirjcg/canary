package br.com.jgon.canary.jee.ws.rest;

import java.util.HashSet;
import java.util.Set;

import br.com.jgon.canary.jee.ws.rest.util.RestFilter;
import br.com.jgon.canary.jee.ws.rest.util.link.LinkResponseFeature;
import br.com.jgon.canary.jee.ws.rest.validation.RestValidationParameterFilter;
import br.com.jgon.canary.jee.ws.rest.validation.exception.RestExceptionMapper;

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
	 * @param checkException: adicionar tratamento de exceções
	 * @return
	 */
	public static Set<Class<?>> getClasses(boolean checkException){
		if(checkException){
			resources.add(RestExceptionMapper.class);
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

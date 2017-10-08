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
package br.com.jgon.canary.jee.ws.rest.validation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import br.com.jgon.canary.jee.exception.MessageSeverity;
import br.com.jgon.canary.jee.util.MessageFactory;
import br.com.jgon.canary.jee.validation.ValidateMessage;
import br.com.jgon.canary.jee.validation.ValidatorFactory;
import br.com.jgon.canary.jee.validation.exception.ValidationException;
import br.com.jgon.canary.jee.ws.rest.validation.annotation.Required;
import br.com.jgon.canary.jee.ws.rest.validation.annotation.RequiredDateGt;
import br.com.jgon.canary.jee.ws.rest.validation.annotation.RequiredDateLt;
import br.com.jgon.canary.jee.ws.rest.validation.annotation.RequiredIfAllNotNull;
import br.com.jgon.canary.jee.ws.rest.validation.annotation.RequiredIfAllNull;
import br.com.jgon.canary.jee.ws.rest.validation.annotation.RequiredIfAnyNotNull;
import br.com.jgon.canary.jee.ws.rest.validation.annotation.RequiredIfAnyNull;
import br.com.jgon.canary.jee.ws.rest.validation.annotation.RequiredIfNotNull;
import br.com.jgon.canary.jee.ws.rest.validation.annotation.RequiredIfNull;
import br.com.jgon.canary.jee.ws.rest.validation.annotation.RequiredRegExPattern;
import br.com.jgon.canary.jee.ws.rest.validation.annotation.RequiredSize;
import br.com.jgon.canary.jee.ws.rest.validation.annotation.RequiredValue;
import br.com.jgon.canary.jee.ws.rest.validation.annotation.RequiredXOR;
import br.com.jgon.canary.jee.ws.rest.validation.enumerator.ParameterTypeEnum;

/**
 * 
 * @author Alexandre O. Pereira
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
@Provider
public class RestValidationParameterFilter implements ContainerRequestFilter{
	private final String VALOR_OCULTO = "[VALOR NAO EXIBIDO]";
	
	private final Logger LOG = Logger.getLogger(RestValidationParameterFilter.class.getName());
	
	@Context
	private ResourceInfo resourceInfo;
		
	@SuppressWarnings("rawtypes")
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		Map<String, RestValidationUnit> parametros = new HashMap<String, RestValidationUnit>();
		
		Method method = resourceInfo.getResourceMethod();

		Annotation[][] parametrosAnotados = method.getParameterAnnotations();
		Class[] parameterTypes = method.getParameterTypes();

		List<Annotation> apiAnnotations = null;
		Annotation wsAnnotation = null;
		boolean possuiApiAnnotation = false;

		int tp = 0;
		for(Annotation[] parametroAnotado : parametrosAnotados){

			Class parameterType = parameterTypes[tp++];

			apiAnnotations = new ArrayList<Annotation>();
			wsAnnotation = null;

			for(Annotation a : parametroAnotado){
				if(a instanceof Required || 
						a instanceof RequiredIfNull || 
						a instanceof RequiredIfNotNull ||
						a instanceof RequiredIfAllNull ||
						a instanceof RequiredIfAnyNull ||
						a instanceof RequiredIfAllNotNull ||
						a instanceof RequiredIfAnyNotNull ||
						a instanceof RequiredXOR ||
						a instanceof RequiredSize ||
						a instanceof RequiredValue ||
						a instanceof RequiredRegExPattern ||
						a instanceof RequiredDateLt ||
						a instanceof RequiredDateGt){
					possuiApiAnnotation = true;
					apiAnnotations.add(a);
				}else if(a instanceof QueryParam || 
						a instanceof PathParam ||
						a instanceof HeaderParam ||
						a instanceof FormParam){
					wsAnnotation = a;
				}
			}

			RestValidationUnit vu = getParameterMap(apiAnnotations, wsAnnotation, parameterType, requestContext);
			if(vu != null){
				parametros.put(vu.getName(), vu);
			}
		}

		if(parametros != null && parametros.size() > 0 && possuiApiAnnotation){
			validaParametros(parametros);
		}
	}
	/**
	 * 
	 * @param apiAnnotations
	 * @param wsAnnotation
	 * @param parameterType
	 * @param requestContext
	 * @return
	 */
	private RestValidationUnit getParameterMap(
			List<Annotation> apiAnnotations, 
			Annotation wsAnnotation, 
			Class<?> parameterType,
			ContainerRequestContext requestContext){
		
		RestValidationUnit retorno = new RestValidationUnit();
		
		try {
			
			if(wsAnnotation != null){
				if(wsAnnotation instanceof QueryParam) {
					
					MultivaluedMap<String, String> queryParams = requestContext.getUriInfo().getQueryParameters();
					
					retorno.setName(((QueryParam) wsAnnotation).value());
					retorno.setType(parameterType);
					
					List<String> v = queryParams.get(((QueryParam) wsAnnotation).value());
					retorno.setValue(v != null && v.size() > 0 ? v.get(0) : null);
					retorno.setApiAnnotations(apiAnnotations);
				}
				
				if(wsAnnotation instanceof PathParam) {
					
					MultivaluedMap<String, String> pathParams = requestContext.getUriInfo().getPathParameters();
					
					retorno.setName(((PathParam) wsAnnotation).value());
					retorno.setType(parameterType);
					
					List<String> v = pathParams.get(((PathParam) wsAnnotation).value());
					retorno.setValue(v != null && v.size() > 0 ? v.get(0) : null);
					retorno.setApiAnnotations(apiAnnotations);
				}
				
				if(wsAnnotation instanceof HeaderParam) {
					
					MultivaluedMap<String, String> headerParams = requestContext.getHeaders();
					
					retorno.setName(((HeaderParam) wsAnnotation).value());
					retorno.setType(parameterType);
	
					List<String> v = headerParams.get(((HeaderParam) wsAnnotation).value());
					retorno.setValue(v != null && v.size() > 0 ? v.get(0) : null);
					retorno.setApiAnnotations(apiAnnotations);
				}
				
				if(wsAnnotation instanceof FormParam) {
					
					MultivaluedMap<String, String> formParams = getFormParams(requestContext);
					
					retorno.setName(((FormParam) wsAnnotation).value());
					retorno.setType(parameterType);
	
					List<String> v = formParams.get(((FormParam) wsAnnotation).value());
					retorno.setValue(v != null && v.size() > 0 ? v.get(0) : null);
					retorno.setApiAnnotations(apiAnnotations);
				}
			}else if(requestContext.getMethod() == HttpMethod.POST){
			
				return null;
				/*TODO: Criar validação para obter objetos no corpo da request (POST)
				 * Para tanto, será necessário controlar anotações internas dos beans
				 * que serão passados como parâmetro.
				 * 
				 * Content-types
				 * MediaType.APPLICATION_JSON;
				 * MediaType.APPLICATION_XML;
				 * MediaType.TEXT_PLAIN;
				 * MediaType.TEXT_XML;*/	
			}else if(requestContext.getMethod() == HttpMethod.PUT){
				return null;
			}
		} catch (Exception ex){
			LOG.severe(ex.getMessage());
		}
		
		return retorno;
	}
	/**
	 * 
	 * @param parametros
	 * @throws ValidationException
	 */
	private void validaParametros(Map<String, RestValidationUnit> parametros) throws ValidationException{
		ValidatorFactory validatorFactory = ValidatorFactory.getInstance();
		
		for(RestValidationUnit v : parametros.values()){
			for(Annotation a : v.getApiAnnotations()){
				if(a instanceof Required){
					validatorFactory.required(v.getValue(), v.getName());
				}else if(a instanceof RequiredIfNull){
					RequiredIfNull rin = (RequiredIfNull) a;
					RestValidationUnit vu = parametros.get(rin.value());
					validatorFactory.requiredIfNull(v.getValue(), vu.getValue(), v.getName(), vu.getName());
				}else if(a instanceof RequiredIfNotNull){
					RequiredIfNotNull rinn = (RequiredIfNotNull) a;
					RestValidationUnit vu = parametros.get(rinn.value());
					validatorFactory.requiredIfNotNull(v.getValue(), vu.getValue(), v.getName(), vu.getName());
				}else if(a instanceof RequiredIfAllNull){
					RequiredIfAllNull rian = (RequiredIfAllNull) a;
					
					String[]  aux = rian.value();
					RestValidationUnit vu = null;
					
					Collection<SimpleEntry<String, Object>> listTest = new ArrayList<SimpleEntry<String, Object>>();
					
					for(int i = 0; i < aux.length; i++){
						vu = parametros.get(aux[i]);
						listTest.add(new SimpleEntry<String, Object>(vu.getName(), vu.getValue()));
					}
					
					validatorFactory.requiredIfAllNull(v.getValue(), v.getName(), listTest);
				}else if(a instanceof RequiredIfAnyNull){
					RequiredIfAnyNull rian = (RequiredIfAnyNull) a;
					
					String[]  aux = rian.value();
					RestValidationUnit vu = null;
					
					Collection<SimpleEntry<String, Object>> listTest = new ArrayList<SimpleEntry<String, Object>>();
					
					for(int i = 0; i < aux.length; i++){
						vu = parametros.get(aux[i]);
						listTest.add(new SimpleEntry<String, Object>(vu.getName(), vu.getValue()));
					}
					
					validatorFactory.requiredIfAnyNull(v.getValue(), v.getName(), listTest);
					
				}else if(a instanceof RequiredIfAllNotNull){
					RequiredIfAllNotNull riann = (RequiredIfAllNotNull) a;
					
					String[]  aux = riann.value();
					RestValidationUnit vu = null;
					
					Collection<SimpleEntry<String, Object>> listTest = new ArrayList<SimpleEntry<String, Object>>();
					
					for(int i = 0; i < aux.length; i++){
						vu = parametros.get(aux[i]);
						listTest.add(new SimpleEntry<String, Object>(vu.getName(), vu.getValue()));
					}
					
					validatorFactory.requiredIfAllNotNull(v.getValue(), v.getName(), listTest);
				}else if(a instanceof RequiredIfAnyNotNull){
					RequiredIfAnyNotNull riann = (RequiredIfAnyNotNull) a;
					
					String[]  aux = riann.value();
					RestValidationUnit vu = null;
					
					Collection<SimpleEntry<String, Object>> listTest = new ArrayList<SimpleEntry<String, Object>>();
					
					for(int i = 0; i < aux.length; i++){
						vu = parametros.get(aux[i]);
						listTest.add(new SimpleEntry<String, Object>(vu.getName(), vu.getValue()));
					}
					
					validatorFactory.requiredIfAnyNotNull(v.getValue(), v.getName(), listTest);
				}else if(a instanceof RequiredXOR){
					RequiredXOR rx = (RequiredXOR) a;
					
					String[]  aux = rx.value();
					RestValidationUnit vu = null;
					
					Collection<SimpleEntry<String, Object>> listTest = new ArrayList<SimpleEntry<String, Object>>();
					
					for(int i = 0; i < aux.length; i++){
						vu = parametros.get(aux[i]);
						listTest.add(new SimpleEntry<String, Object>(vu.getName(), vu.getValue()));
					}
					
					validatorFactory.requiredXOR(v.getValue(), v.getName(), listTest);
					
				}else if(a instanceof RequiredSize){
					RequiredSize rs = (RequiredSize) a;
					
					double minSize = rs.minSize();
					double maxSize = rs.maxSize();
					
					String valor = v.getValue();
					
					if(StringUtils.isNotBlank(valor)){
						if(v.getType() == ParameterTypeEnum.DOUBLE ||
							v.getType() == ParameterTypeEnum.FLOAT ||
							v.getType() == ParameterTypeEnum.INTEGER ||
							v.getType() == ParameterTypeEnum.LONG ||
							v.getType() == ParameterTypeEnum.SHORT){
							
							Double d = Double.valueOf(valor);
						
							if(minSize > 0 && maxSize > 0){
								validatorFactory.lessThanOrGreaterThan(d, minSize, maxSize, v.getName());
							}else if(minSize > 0){
								validatorFactory.lessThan(d, minSize, v.getName());
							}else if(maxSize > 0){
								validatorFactory.greaterThan(d, maxSize, v.getName());
							}
						}else if(v.getType() == ParameterTypeEnum.STRING ||
								v.getType() == ParameterTypeEnum.BYTE ||
								v.getType() == ParameterTypeEnum.BYTEARRAY){
							
							Integer tamanho = null;
							
							if(v.getType() == ParameterTypeEnum.STRING){
								tamanho = v.getValue().length();
							}else{
								byte[] b = v.getValue().getBytes();
								tamanho = b.length;
							}
							
							if(minSize > 0 && maxSize > 0){
								validatorFactory.lessThanOrGreaterThan(tamanho, minSize, maxSize, v.getName());
							}else if(minSize > 0){
								validatorFactory.lessThan(tamanho, minSize, v.getName());
							}else if(maxSize > 0){
								validatorFactory.greaterThan(tamanho, maxSize, v.getName());
							}
						}
					}
				}else if(a instanceof RequiredValue){
					RequiredValue rv = (RequiredValue) a;
					
					boolean 	hideList = rv.hideListInErrorMessages();
					boolean hideValue = rv.hideFieldValueInErrorMessage();
					int[] 		inIntvalues = rv.inIntValues();
					long[] 		inLongValues = rv.inLongValues();
					String[] 	inStringValues = rv.inStringValues();
					
					String valor = v.getValue();
										
					if(StringUtils.isNotBlank(valor)){
						String valorShow = hideValue ? VALOR_OCULTO : valor;
						
						if(v.getType() == ParameterTypeEnum.INTEGER && inIntvalues.length > 0){
							Integer intVal = Integer.valueOf(valor);
							
							List<Integer> lista = new ArrayList<Integer>();
							for(int i : inIntvalues){
								lista.add(i);
							}
							
							if(!lista.contains(intVal)){
								String valorTestShow = hideList ? VALOR_OCULTO : montaListaParametrosIntegerException(lista, "e");
								validatorFactory.add(new ValidateMessage(MessageSeverity.WARN, ValidatorFactory.REQUIRED_VALUE_KEY, valorShow, valorTestShow));
							}
						}else if(v.getType() == ParameterTypeEnum.LONG && inLongValues.length > 0){
							Long longVal = Long.valueOf(valor);
							
							List<Long> lista = new ArrayList<Long>();
							for(long i : inLongValues){
								lista.add(i);
							}
														
							if(!lista.contains(longVal)){
								String valorTestShow = hideList ? VALOR_OCULTO : montaListaParametrosLongException(lista, "e");
								validatorFactory.add(new ValidateMessage(MessageSeverity.WARN, ValidatorFactory.REQUIRED_VALUE_KEY, valorShow, valorTestShow));
							}
						}else if(v.getType() == ParameterTypeEnum.STRING && inStringValues.length > 0){
							
							List<String> lista = new ArrayList<String>(Arrays.asList(inStringValues));
							
							if(!lista.contains(valor)){
								String valorTestShow = hideList ? VALOR_OCULTO : montaListaParametrosStringException(lista, "e");
								validatorFactory.add(new ValidateMessage(MessageSeverity.WARN, ValidatorFactory.REQUIRED_VALUE_KEY, valorShow, valorTestShow));
							}
						}
					}
				}else if(a instanceof RequiredRegExPattern){
					RequiredRegExPattern rep = (RequiredRegExPattern) a;
					
					String regExPattern = rep.value();
					boolean hideRegEx = rep.hideRegExPatternFromErrorMessage();
					boolean hideValue = rep.hideFieldValueInErrorMessage();
					
					String valor = v.getValue();
					
					if(StringUtils.isNotBlank(valor)){
						if(!valor.matches(regExPattern)){
							validatorFactory.add(new ValidateMessage(MessageSeverity.WARN, ValidatorFactory.REQUIRED_REGEX_KEY, v.getName(), hideValue ? VALOR_OCULTO : valor, hideRegEx ? VALOR_OCULTO : regExPattern));
						}
					}
				}else if(a instanceof RequiredDateLt){
					RequiredDateLt rdl = (RequiredDateLt) a;
					RestValidationUnit vu = parametros.get(rdl.value());
					
					if(StringUtils.isNotBlank(vu.getValue()) && StringUtils.isNotBlank(v.getValue())){
						try {
							Date data1 = DateUtils.parseDate(v.getValue(), rdl.pattern());
							Date data2 = DateUtils.parseDate(vu.getValue(), rdl.pattern());
							
							if(data1.after(data2)){
								validatorFactory.lessThan(data1, data2, v.getName());
							}
						} catch (ParseException e) {
							validatorFactory.add(new ValidateMessage(MessageSeverity.WARN, ValidatorFactory.LESS_THAN_KEY, new String[]{v.getName(), vu.getName()}));
						}
					}
				}else if(a instanceof RequiredDateGt){
					RequiredDateGt rdl = (RequiredDateGt) a;
					RestValidationUnit vu = parametros.get(rdl.value());
					
					if(StringUtils.isNotBlank(vu.getValue()) && StringUtils.isNotBlank(v.getValue())){
						try {
							Date data1 = DateUtils.parseDate(v.getValue(), rdl.pattern());
							Date data2 = DateUtils.parseDate(vu.getValue(), rdl.pattern());
							
							if(data1.before(data2)){
								validatorFactory.gt(data1, data2, v.getName());
							}
						} catch (ParseException e) {
							validatorFactory.add(new ValidateMessage(MessageSeverity.WARN, ValidatorFactory.GREATER_THAN_KEY, new String[]{v.getName(), vu.getName()}));
						}
					}
				}
			}
		}
		validatorFactory.check();
	}
	/**
	 * 
	 * @param requestContext
	 * @return
	 */
	private MultivaluedMap<String, String> getFormParams(ContainerRequestContext requestContext){
		MultivaluedMap<String, String> retorno = new MultivaluedMapImpl<String, String>();
		
		String contentType = requestContext.getHeaderString("Content-Type");
		
		if(contentType.contains(MediaType.APPLICATION_FORM_URLENCODED)){
			String body = getEntityBody(requestContext);
			
			if(StringUtils.isNotBlank(body)){
				List<NameValuePair> parametros = URLEncodedUtils.parse(body, getCharset(requestContext));
				
				for(NameValuePair nvp : parametros){
					retorno.add(nvp.getName(), nvp.getValue());
				}
			}
		}else{
			LOG.severe(MessageFactory.getMessage("message.erro.contenttype", new String[]{contentType}));
		}
		
		return retorno;
	}
	
	private Charset getCharset(ContainerRequestContext requestContext){
		
		Charset retorno = null;
		
		String cs = requestContext.getHeaderString("Accept-charset");
		if(requestContext.getMethod().equals(HttpMethod.POST)){
			if(StringUtils.isBlank(cs)){ //Caso o cliente não tenha setado "Accept-charset", tentar obter a partir do header "Content-type"
				try{
					String contentType = requestContext.getHeaderString("Content-Type"); //Pegar o valor após o marcador "charset"
					
					if(contentType.contains("charset=")){
						String[] aux = contentType.split("charset="); //Caso haja mais algum parametro dentro de "Content-type" após o encoding, isola somente o encoding
						if(aux[1].contains(";")){
							cs = aux[1].split(";")[0];
						}else{
							cs = aux[1];
						}
						
						retorno = Charset.forName(cs);
					}
				}finally{
					if(retorno == null){
						retorno = StandardCharsets.UTF_8;
						LOG.warning("Nenhum charset encontrado nos headers da request. Assumindo " + retorno.displayName()); //Adiciona log ao servidor para facilitar a solução de um eventual problema com a codificação
					}
				}
			}else{
				retorno = Charset.forName(cs);
			}
		}
		
		return retorno;
	}
	
	private String getEntityBody(ContainerRequestContext requestContext){
		
		String retorno = null;
		
		try{
			
			Charset charset = getCharset(requestContext);
			
			retorno = IOUtils.toString(requestContext.getEntityStream(), charset.name());
			
			//Revalida a request já que, quando é lida, a request perde os dados
			requestContext.setEntityStream(IOUtils.toInputStream(retorno, charset));
		} catch (IOException ex) {
			LOG.severe(ex.getMessage());
		}
		
		return retorno;
	}
	/**
	 * 
	 * @param parametros
	 * @param ultimoDivisorParametros
	 * @return
	 */
	public static String montaListaParametrosStringException(List<String> parametros, String ultimoDivisorParametros){
		
		StringBuilder retorno = new StringBuilder();
		
		if(parametros != null && parametros.size() > 0){
			
			int tamanho = parametros.size();
			int i = 0;
			
			if(tamanho > 1){
				//Monta ate o penultimo parametro para permitir adicionar o divisor do ultimo parametro ('e', 'ou', etc)
				for(; i < tamanho - 1; i++){
					String aux = parametros.get(i);
					
					if(retorno.length() > 0){
						retorno.append(", ");
					}
					
					retorno.append("'").append(aux).append("'");
				}
				//Adiciona o ultimo divisor de parametros ('e', 'ou', etc)
				retorno.append(" ").append(ultimoDivisorParametros != null ? ultimoDivisorParametros : ", ").append(" ");
			}
			//Adiciona o ultimo parametro (ou único)
			retorno.append("'").append(parametros.get(tamanho - 1)).append("'");
		}
		
		return retorno.toString();
	}
	/**
	 * 
	 * @param parametros
	 * @param ultimoDivisorParametros
	 * @return
	 */
	public static String montaListaParametrosIntegerException(List<Integer> parametros, String ultimoDivisorParametros){
		
		List<String> lista = integerListToSortedStringList(parametros);
		
		return montaListaParametrosStringException(lista, ultimoDivisorParametros);
	}
	/**
	 * 
	 * @param parametros
	 * @param ultimoDivisorParametros
	 * @return
	 */
	public static String montaListaParametrosLongException(List<Long> parametros, String ultimoDivisorParametros){
		
		List<String> lista = longListToSortedStringList(parametros);
		
		return montaListaParametrosStringException(lista, ultimoDivisorParametros);
	}
	/**
	 * 
	 * @param lista
	 * @return
	 */
	public static List<String> integerListToSortedStringList(List<Integer> lista){
		
		List<String> sortedList = null;
		
		if(lista != null && lista.size() > 0){
			sortedList = new ArrayList<String>();
			
			Collections.sort(lista);
			
			for(Integer i : lista){
				if(i != null){
					sortedList.add(i.toString());
				}
			}
		}
		
		return sortedList;
	}
	/**
	 * 
	 * @param lista
	 * @return
	 */
	public static List<String> longListToSortedStringList(List<Long> lista){
		
		List<String> sortedList = null;
		
		if(lista != null && lista.size() > 0){
			sortedList = new ArrayList<String>();
			
			Collections.sort(lista);
			
			for(Long l : lista){
				if(l != null){
					sortedList.add(l.toString());
				}
			}
		}
		
		return sortedList;
	}
}

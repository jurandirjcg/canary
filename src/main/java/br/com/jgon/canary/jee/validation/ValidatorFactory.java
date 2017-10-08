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
package br.com.jgon.canary.jee.validation;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.com.jgon.canary.jee.exception.MessageSeverity;
import br.com.jgon.canary.jee.util.MessageFactory;
import br.com.jgon.canary.jee.validation.exception.ValidationException;

/**
 * Factory de validacoes
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class ValidatorFactory {
	public static final String REQUIRED_KEY = "validation.required-field";
	public static final String REQUIRED_VALUE_KEY = "validation.required-value";
	public static final String REQUIRED_REGEX_KEY = "validation.required-reg-ex";
	public static final String LESS_THAN_KEY = "validation.less-than";
	public static final String GREATER_THAN_KEY = "validation.greater-than";
	public static final String LESS_THAN_OR_GREATER_THAN_KEY = "validation.less-than-or-greater-than";
	public static final String REQUIRED_IF_NULL_KEY = "validation.required-if-null";
	public static final String REQUIRED_IF_ALL_NULL_KEY = "validation.required-if-all-null";
	public static final String REQUIRED_IF_ANY_NULL_KEY = "validation.required-if-any-null";
	public static final String REQUIRED_IF_NOT_NULL_KEY = "validation.required-if-not-null";
	public static final String REQUIRED_IF_ALL_NOT_NULL_KEY = "validation.required-if-all-not-null";
	public static final String REQUIRED_IF_ANY_NOT_NULL_KEY = "validation.required-if-any-not-null";
	public static final String REQUIRED_XOR_KEY = "validation.required-xor";
	
	private ValidationException validatorException = new ValidationException();
	
	private ValidatorFactory() {
		
	}
	/**
	 * 
	 * @return
	 */
	public static ValidatorFactory getInstance(){
		return new ValidatorFactory();
	}
	/**
	 * 
	 * @param validateMessage
	 */
	public void add(ValidateMessage validateMessage){
		validatorException.add(validateMessage);
	}
	/**
	 * 
	 * @param key
	 * @param parametros
	 * @return
	 */
	public static String i18n(String key, Object... parametros) {
		if (parametros == null || parametros.length == 0) {
			return MessageFactory.getMessage(key);
		}
		ArrayList<String> strings = new ArrayList<String>();
		for (Object object : parametros) {
			strings.add(object.toString());
		}
		String[] arrString = new String[strings.size()];
		strings.toArray(arrString);
		return MessageFactory.getMessage(key, arrString);
	}
	/**
	 * 
	 * @param object
	 * @param campoMsg
	 * @return
	 */
	public ValidatorFactory required(Object object, String campoMsg) {
		if (isNull(object)) {
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_KEY, campoMsg));
		}
		return this;
	}
	/**
	 * 
	 * @param object
	 * @param campoMsg
	 * @param regEx
	 * @return
	 */
	public ValidatorFactory requiredRegExPattern(String object, String campoMsg, String regEx) {
		if (isNotNull(object) && !object.matches(regEx)) {
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_REGEX_KEY, campoMsg, regEx));
		}
		return this;
	}
	/**
	 * 
	 * @param object
	 * @param campoMsg
	 * @param value
	 * @return
	 */
	public ValidatorFactory requiredValue(Object object, String campoMsg, Object value) {
		if (object != null && value != null && !object.equals(value)) {
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_VALUE_KEY, campoMsg, value.toString()));
		}
		return this;
	}
	/**
	 * 
	 * @param object
	 * @param campoMsg
	 * @param value
	 * @return
	 */
	public ValidatorFactory requiredValue(Collection<?> object, String campoMsg, Object value) {
		if (object != null && value != null && !object.contains(value)) {
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_VALUE_KEY, campoMsg, value.toString()));
		}
		return this;
	}
	/**
	 * 
	 * @param value
	 * @param testValue
	 * @param valueName
	 * @param testName
	 * @return
	 */
	public ValidatorFactory requiredIfNull(Object value, Object testValue, String valueName, String testName) {
		if (isNull(testValue) && isNull(value)){
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_IF_NULL_KEY, valueName, testName));
		}
		return this;
	}
	/**
	 * 
	 * @param value
	 * @param valueName
	 * @param testValue
	 * @return
	 */
	public ValidatorFactory requiredIfAnyNull(Object value, String valueName, Collection<SimpleEntry<String, Object>> testValue){
		List<String> fieldNames = new ArrayList<String>();
		if (isNull(value) && (isNull(testValue) || testValue.isEmpty())){
			testValue.forEach(item -> {
				fieldNames.add(item.getKey());
			});
		}else if(isNull(value)){
			testValue.forEach(item -> {
				if(isNull(item.getValue())){
					fieldNames.add(item.getKey());
				}
			});
		}
		if(!fieldNames.isEmpty()){
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_IF_ANY_NULL_KEY, valueName, montaListaParametrosStringException(fieldNames, "e")));
		}
		return this;
	}
	/**
	 * 
	 * @param value
	 * @param valueName
	 * @param testValue
	 * @return
	 */
	public ValidatorFactory requiredIfAllNull(Object value, String valueName, Collection<SimpleEntry<String, Object>> testValue){
		List<String> fieldNames = new ArrayList<String>();
		if (isNull(value) && (isNull(testValue) || testValue.isEmpty())){
			testValue.forEach(item -> {
				fieldNames.add(item.getKey());
			});
		}else if(isNull(value)){
			for(SimpleEntry<String, Object> se : testValue){
				fieldNames.add(se.getKey());
				if(isNotNull(se.getValue())){
					fieldNames.clear();
					break;
				}
			}
		}
		if(!fieldNames.isEmpty()){
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_IF_ALL_NULL_KEY, valueName, montaListaParametrosStringException(fieldNames, "e")));
		}
		return this;
	}
	/**
	 * 
	 * @param value
	 * @param testValue
	 * @param valueName
	 * @param testName
	 * @return
	 */
	public ValidatorFactory requiredIfNotNull(Object value, Object testValue, String valueName, String testName) {
		if (isNotNull(testValue) && isNull(value)){
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_IF_NOT_NULL_KEY, valueName, testName));
		}
		return this;
	}
	/**
	 * 
	 * @param value
	 * @param valueName
	 * @param testValue
	 * @return
	 */
	public ValidatorFactory requiredIfAllNotNull(Object value, String valueName, Collection<SimpleEntry<String, Object>> testValue){
		List<String> fieldNames = new ArrayList<String>();
		if (isNull(value) && (isNotNull(testValue) && !testValue.isEmpty())){
			testValue.forEach(item -> {
				fieldNames.add(item.getKey());
			});
		}else if(isNull(value)){
			for(SimpleEntry<String, Object> se : testValue){
				fieldNames.add(se.getKey());
				if(isNull(se.getValue())){
					fieldNames.clear();
					break;
				}
			}
		}
		if(!fieldNames.isEmpty()){
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_IF_ALL_NOT_NULL_KEY, valueName, montaListaParametrosStringException(fieldNames, "e")));
		}
		return this;
	}
	
	/**
	 * 
	 * @param value
	 * @param valueName
	 * @param testValue
	 * @return
	 */
	public ValidatorFactory requiredIfAnyNotNull(Object value, String valueName, Collection<SimpleEntry<String, Object>> testValue){
		List<String> paramNames = new ArrayList<String>();
		List<String> paramNull = new ArrayList<String>();
		List<String> paramNotNull = new ArrayList<String>();
		if (isNull(value) && (isNotNull(testValue) && !testValue.isEmpty())){
			testValue.forEach(item -> {
				paramNull.add(item.getKey());
			});
			
			paramNames.addAll(paramNull);
		}else if(isNull(value)){
			for(SimpleEntry<String, Object> se : testValue){
				paramNames.add(se.getKey());
				if(isNull(se.getValue())){
					paramNull.add(se.getKey());
				}else{
					paramNotNull.add(se.getKey());
				}
			}
		}
		if((paramNull.isEmpty() && paramNotNull.isEmpty()) || paramNotNull.size() > 1){
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_XOR_KEY, valueName, montaListaParametrosStringException(paramNames, "e"), montaListaParametrosStringException(paramNotNull, "e"), montaListaParametrosStringException(paramNull, "e")));
		}
		return this;
	}
	/**
	 * 
	 * @param value
	 * @param valueName
	 * @param testValue
	 * @return
	 */
	public ValidatorFactory requiredXOR(Object value, String valueName, Collection<SimpleEntry<String, Object>> testValue){
		List<String> fieldNames = new ArrayList<String>();
		if (isNull(value) && (isNull(testValue) && testValue.isEmpty())){
			testValue.forEach(item -> {
				fieldNames.add(item.getKey());
			});
		}else if(isNull(value)){
			testValue.forEach(item -> {
				if(isNotNull(item.getValue())){
					fieldNames.add(item.getKey());
				}
			});
		}
		if(!fieldNames.isEmpty()){
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_IF_ANY_NOT_NULL_KEY, valueName, montaListaParametrosStringException(fieldNames, "e")));
		}
		return this;
	}
	/**
	 * 
	 * @param valor
	 * @param minimo
	 * @param campoMsg
	 * @return
	 */
	public ValidatorFactory lessThan(Number valor, Number minimo, String campoMsg){
		if (valor.doubleValue() > minimo.doubleValue()) {
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, LESS_THAN_KEY, campoMsg, minimo.toString()));
		}
		return this;
	}
	/**
	 * 
	 * @param valor
	 * @param minimo
	 * @param campoMsg
	 * @return
	 */
	public ValidatorFactory lessThan(Date valor, Date minimo, String campoMsg){
		if (valor.after(minimo)) {
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, LESS_THAN_KEY, campoMsg, minimo.toString()));
		}
		return this;
	}
	/**
	 * 
	 * @param valor
	 * @param maximo
	 * @param campoMsg
	 * @return
	 */
	public ValidatorFactory greaterThan(Number valor, Number maximo, String campoMsg){
		if (valor.doubleValue() < maximo.doubleValue()) {
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, GREATER_THAN_KEY, campoMsg, maximo.toString()));
		}
		return this;
	}
	/**
	 * 
	 * @param valor
	 * @param minimo
	 * @param campoMsg
	 * @return
	 */
	public ValidatorFactory gt(Date valor, Date minimo, String campoMsg){
		if (valor.before(minimo)) {
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, GREATER_THAN_KEY, campoMsg, minimo.toString()));
		}
		return this;
	}
	/**
	 * 
	 * @param valor
	 * @param min
	 * @param max
	 * @param campoMsg
	 * @return
	 */
	public ValidatorFactory lessThanOrGreaterThan(Number valor, Number min, Number max, String campoMsg){
		if (valor.doubleValue() > min.doubleValue() || valor.doubleValue() < max.doubleValue()) {
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, LESS_THAN_OR_GREATER_THAN_KEY, campoMsg, min.toString(), max.toString()));
		}
		return this;
	}
	/**
	 * 
	 * @param valor
	 * @param campoMsg
	 * @return
	 */
	public ValidatorFactory isBlank(String valor, String campoMsg){
		if (StringUtils.isNotBlank(valor)) {
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_KEY, campoMsg));
		}
		return this;
	}
	/**
	 * 
	 * @param valor
	 * @param campoMsg
	 * @return
	 */
	public ValidatorFactory isNotBlank(String valor, String campoMsg){
		if (StringUtils.isBlank(valor)) {
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_KEY, campoMsg));
		}
		return this;
	}
	/**
	 * 
	 * @param obj
	 * @param campoMsg
	 * @return
	 */
	public ValidatorFactory isNull(Object obj, String campoMsg){
		if (isNotNull(obj)) {
			validatorException.add(new ValidateMessage(MessageSeverity.WARN, REQUIRED_KEY, campoMsg));
		}
		return this;
	}
	/**
	 * 
	 */
	public void clear(){
		validatorException.getListMessage().clear();
	}
	/**
	 * 
	 * @param object
	 * @return
	 */
	private boolean isNull(Object object){
		if(object == null){
			return true;
		}else if(object instanceof String){
			return StringUtils.isBlank((String) object);
		}
		return false;
	}
	/**
	 * 
	 * @param object
	 * @return
	 */
	private boolean isNotNull(Object object){
		return !isNull(object);
	}
	/**
	 * 
	 * @param collection
	 * @return
	 */
	private boolean isNull(Collection<?> collection){
		if(collection == null){
			return true;
		}
		for(Object obj : collection){
			if(isNull(obj)){
				return true;
			}
		}
		return false;
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
	 * @throws ValidationException
	 */
	public void check() throws ValidationException{
		if(!validatorException.getListMessage().isEmpty()){
			throw validatorException;
		}
	}
}

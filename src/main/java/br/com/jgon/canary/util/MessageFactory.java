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
package br.com.jgon.canary.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class MessageFactory {
	
	private static ResourceBundle defaultMessageResource;
	private static ResourceBundle customMessageResource;
	private static String defaultMessage;
	private static final String CUSTOM_MESSAGE_RESOURCE = "ApplicationMessages";
	private static final String DEFAULT_MESSAGE_RESOURCE = "br.com.jgon.jee.messages.ApplicationDefaultMessages";
	private static final String DEFAULT_MESSAGE_KEY = "default.message";
	
	private static Logger logger = LoggerFactory.getLogger(MessageFactory.class);
	
	static {
		try {
			defaultMessageResource = ResourceBundle.getBundle(DEFAULT_MESSAGE_RESOURCE, Locale.getDefault(), MessageFactory.class.getClassLoader());
		} catch (MissingResourceException e) {
			logger.error("[static] - Arquivo ApplicationDefaultMessages não encontrado", e);
			//Nao e necessario lancar execao pois aplicacao sempre apresentara mensagem de erro generica
		}
		try {
			customMessageResource = ResourceBundle.getBundle(CUSTOM_MESSAGE_RESOURCE, Locale.getDefault(), MessageFactory.class.getClassLoader());
		} catch (MissingResourceException e) {
			logger.error("[static] - Arquivo ApplicationMessages não encontrado", e);
			//Nao e necessario lancar execao pois aplicacao sempre apresentara mensagem de erro generica
		}
		
		defaultMessage = getMessage(DEFAULT_MESSAGE_KEY);
		if(defaultMessage == null || "".equals(defaultMessage) ){
			defaultMessage = "Ocorreu um erro inesperado. Contacte o administrador do sistema.";
		}
    }
	/**
	 * 	
	 * @param key - chave da mensagem
	 * @return {@link String}
	 */
	public static String getMessage(String key) {
		String msg = null;
		
		try {
			if(customMessageResource != null){
				if(customMessageResource.containsKey(key)){
					msg = customMessageResource.getString(key);
				}
			}
			
			if(StringUtils.isBlank(msg)){
				msg = defaultMessageResource.getString(key);
			}
		}catch (Exception mre) {
			msg =  defaultMessage;
		}	
		
		return msg == null ? "" : msg ;
	}
	/**
	 * 
	 * @param key - cheve da mensagem
	 * @param params - parametros para serem adicionados na mensagem
	 * @return {@link String}
	 */
	public static String getMessage(String key, String... params) {
		return messageFormat(getMessage(key), params);
	}
	/**
	 * 
	 * @param resourceBundle - arquivo de mensagens
	 * @param key - chave da mensagem
	 * @return {@link String}
	 */
	public static String getMessage(ResourceBundle resourceBundle, String key){
		String msg = null;
		
		try {
			msg = resourceBundle.getString(key);
		}catch (Exception mre) {
			msg =  defaultMessage;
		}
		
		return (msg == null ? "" : msg );
	}
	/**
	 * 
	 * @param resourceBundle - arquivo de mensagens
	 * @param key - chave da mensagem 
	 * @param params - parametros para serem adicionados na mensagem
	 * @return {@link String}
	 */
	public static String getMessage(ResourceBundle resourceBundle, String key, String... params) {
		return messageFormat(getMessage(resourceBundle, key), params);
	}
	/**
	 * 
	 * @param msg - String de mensagem
	 * @param params - parametros para serem adicionados na mensagem
	 * @return {@link String}
	 */
	private static String messageFormat(String msg, String... params){
		if ("".equals(msg)){
			return "";
		}
		
		if(params != null && params.length > 0){
			for(int i = 0; i < params.length; i++){
				if(params[i].length() > 0 && params[i].charAt(0) != '\''){
					params[i] = params[i];//"'" + params[i] + "'";
				}
			}
			
			MessageFormat mf = new MessageFormat(msg);
			msg = mf.format(params);
		}
		
		return msg == null ? "" : msg;
	}
	/**
	 * 
	 * @param resourceBundleLocation - arquivo de mensagens
	 * @param key - chave da mensagem
	 * @return {@link String}
	 */
	public static String getMessageFromResource(String resourceBundleLocation, String key){
		return getMessage(ResourceBundle.getBundle(resourceBundleLocation), key);
	}
	/**
	 * 
	 * @param resourceBundleLocation - arquivo de mensagens
	 * @param key - chave da mensagem
	 * @param params - parametros para serem adicionados na mensagem
	 * @return {@link String}
	 */
	public static String getMessageFromResource(String resourceBundleLocation, String key, String... params){
		return getMessage(ResourceBundle.getBundle(resourceBundleLocation), key, params);
	}
}
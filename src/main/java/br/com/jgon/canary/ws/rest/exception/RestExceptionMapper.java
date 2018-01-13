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
package br.com.jgon.canary.ws.rest.exception;

import java.util.logging.Logger;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.util.MessageFactory;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.validation.exception.ValidationException;
import br.com.jgon.canary.ws.rest.util.ResponseError;

/**
 * Realiza o tratamento de execoes lancadas durante a execucao dos servicos
 *
 * @author Alexandre O. Pereira
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
@Provider
public class RestExceptionMapper implements ExceptionMapper<Exception>{

	private Logger LOG = Logger.getLogger(RestExceptionMapper.class.getName());
	
	@Override
	@Produces(MediaType.APPLICATION_JSON)
	public Response toResponse(Exception exception) {
		
		ResponseError retorno = null;
		if(exception instanceof ValidationException){
			retorno = configValidationException((ValidationException) exception);
		}else if(exception instanceof ApplicationException){
			retorno = configApplicationException((ApplicationException) exception);
		}else if(exception instanceof ApplicationRuntimeException){
			retorno = configApplicationRuntimeException((ApplicationRuntimeException) exception);
		}else if(exception.getCause() instanceof ApplicationRuntimeException){
			retorno = configApplicationRuntimeException((ApplicationRuntimeException) exception.getCause());
		}else if(exception instanceof WebApplicationException){
			return configWebApplicationException((WebApplicationException) exception);
		}else{
			retorno = new ResponseError(Response.Status.INTERNAL_SERVER_ERROR, MessageFactory.getMessage("default.message"), MessageSeverity.ERROR);
			LOG.severe(exception.getMessage());
		}
		return Response.status(retorno.getStatus()).entity(retorno).header("Content-type", "application/json").build();
	}
		
	public ResponseError configApplicationException(ApplicationException exception) {
		ResponseError retorno = null;
		retorno = new ResponseError(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage(), exception.getMessageSeverity());
		if(exception.getMessageSeverity().equals(MessageSeverity.ERROR) || exception.getMessageSeverity().equals(MessageSeverity.FATAL)){
			LOG.severe(exception.getMessage());
		}
		return retorno;
	}
	
	public ResponseError configValidationException(ValidationException exception) {
		return new ResponseError(Response.Status.BAD_REQUEST, ((ValidationException) exception).getMessage(), MessageSeverity.WARN);
	}
	
	public ResponseError configApplicationRuntimeException(ApplicationRuntimeException exception) {
		ResponseError retorno = null;
		retorno = new ResponseError(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage(), exception.getMessageSeverity());
		if(exception.getMessageSeverity().equals(MessageSeverity.ERROR) || exception.getMessageSeverity().equals(MessageSeverity.FATAL)){
			LOG.severe(exception.getMessage());
		}
		return retorno;
	}
	
	public Response configWebApplicationException(WebApplicationException exception) {
		WebApplicationException wa = (WebApplicationException) exception;
		return wa.getResponse();
	}
}
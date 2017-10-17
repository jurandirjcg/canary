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
			retorno = new ResponseError(Response.Status.BAD_REQUEST, ((ValidationException) exception).getMessage(), MessageSeverity.WARN);
		}else if(exception instanceof ApplicationException){
			ApplicationException ae = (ApplicationException) exception;
			retorno = new ResponseError(Response.Status.INTERNAL_SERVER_ERROR, ae.getMessage(), ae.getMessageSeverity());
			if(ae.getMessageSeverity().equals(MessageSeverity.ERROR) || ae.getMessageSeverity().equals(MessageSeverity.FATAL)){
				LOG.severe(exception.getMessage());
			}
		}else if(exception instanceof ApplicationRuntimeException){
			ApplicationRuntimeException ae = (ApplicationRuntimeException) exception;
			retorno = new ResponseError(Response.Status.INTERNAL_SERVER_ERROR, ae.getMessage(), ae.getMessageSeverity());
			if(ae.getMessageSeverity().equals(MessageSeverity.ERROR) || ae.getMessageSeverity().equals(MessageSeverity.FATAL)){
				LOG.severe(exception.getMessage());
			}
		}else if(exception.getCause() instanceof ApplicationRuntimeException){
			ApplicationRuntimeException ae = (ApplicationRuntimeException) exception.getCause();
			retorno = new ResponseError(Response.Status.INTERNAL_SERVER_ERROR, ae.getMessage(), ae.getMessageSeverity());
			if(ae.getMessageSeverity().equals(MessageSeverity.ERROR) || ae.getMessageSeverity().equals(MessageSeverity.FATAL)){
				LOG.severe(exception.getMessage());
			}
		}else if(exception instanceof WebApplicationException){
			WebApplicationException wa = (WebApplicationException) exception;
			return wa.getResponse();
		}else{
			retorno = new ResponseError(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage(), MessageSeverity.ERROR);
			LOG.severe(exception.getMessage());
		}
		return Response.status(retorno.getStatus()).entity(retorno).header("Content-type", "application/json").build();
	}
}
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
package br.com.jgon.canary.ws.rest.validation.exception;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
public class RestValidationExceptionMapper implements ExceptionMapper<ValidationException>{

	@Override
	@Produces(MediaType.APPLICATION_JSON)
	public Response toResponse(ValidationException exception) {
		
		ResponseError retorno = new ResponseError(Response.Status.BAD_REQUEST, ((ValidationException) exception).getMessage(), MessageSeverity.WARN);
		
		return Response.status(retorno.getStatus()).entity(retorno).header("Content-type", "application/json").build();
	}
}

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


import java.io.Serializable;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.ws.rest.util.ResponseError;

/**
 * Interceptor para WebServices Rest - DESATIVADO
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
@RestExceptionHandler
@Interceptor
@Priority(javax.interceptor.Interceptor.Priority.LIBRARY_AFTER)
public class RestExceptionHandlerInterceptor implements Serializable {

	private static final long serialVersionUID = -4500048022362783846L;
	
	/*@Inject
	private Logger LOG;*/

	@AroundInvoke
	public Object handleException(InvocationContext invocationContext) throws Exception {
		//FIXME Atualizar
	/*	final String targetClassName = invocationContext.getTarget().getClass().getName();
		final String methodName = invocationContext.getMethod().getName();*/
		
		try {
			Object retorno = invocationContext.proceed();
			System.out.println("CLASSE DE RETORNO" + retorno.getClass());
			return retorno;
		/*} catch (AcessoNegadoException an) {
			RetornoErro<String> acessoNegado = new RetornoErro<String>();
			acessoNegado.setMensagem(MENSAGEM.ACESSO_NEGADO);
			acessoNegado.setDescricaoMensagem(an.getMessage());
			return Response.status(Status.UNAUTHORIZED).entity(acessoNegado).build();*/
		} catch (ApplicationException appEx) {
			ResponseError erro = new ResponseError(Status.BAD_GATEWAY, appEx.getMessage(), appEx.getMessageSeverity());
			return Response.status(erro.getStatus()).entity(erro).build();
		} catch (Exception ex) {
			//LOG.error("Erro NAO tratado pela aplicacao. Operacao [" + methodName + "] em [" + targetClassName + "] - " + ex.getMessage(), ex);
			ResponseError erro = new ResponseError(Status.INTERNAL_SERVER_ERROR, ex.getMessage());
			return Response.status(erro.getStatus()).entity(erro).build();
		}
	}
}
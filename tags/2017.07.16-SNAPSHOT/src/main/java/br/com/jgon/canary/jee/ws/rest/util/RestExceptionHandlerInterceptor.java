package br.com.jgon.canary.jee.ws.rest.util;


import java.io.Serializable;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.jgon.canary.jee.exception.ApplicationException;

/**
 * Interceptor para WebServices Rest - DESATIVADO
 * @author jurandir
 *
 */
@RestExceptionHandler
@Interceptor
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
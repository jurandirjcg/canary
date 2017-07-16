package br.com.jgon.canary.jee.ws.rest.validation.exception;

import java.util.logging.Logger;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import br.com.jgon.canary.jee.exception.ApplicationException;
import br.com.jgon.canary.jee.exception.MessageSeverity;
import br.com.jgon.canary.jee.validation.exception.ValidationException;
import br.com.jgon.canary.jee.ws.rest.util.ResponseError;

/**
 * Realiza o tratamento de execoes lancadas durante a execucao dos servicos
 * @author jurandir, alexandre
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
		}else if(exception.getSuppressed().length > 0 && exception.getSuppressed()[0] instanceof ApplicationException){
			ApplicationException ae = (ApplicationException) exception.getSuppressed()[0];
			retorno = new ResponseError(Response.Status.INTERNAL_SERVER_ERROR, ae.getMessage(), ae.getMessageSeverity());
			if(ae.getMessageSeverity().equals(MessageSeverity.ERROR) || ae.getMessageSeverity().equals(MessageSeverity.FATAL)){
				LOG.severe(exception.getMessage());
			}
		}else if(exception.getCause() instanceof RuntimeException && exception.getCause().getSuppressed().length > 0 && exception.getCause().getSuppressed()[0] instanceof ApplicationException){
			ApplicationException ae = (ApplicationException) exception.getCause().getSuppressed()[0];
			retorno = new ResponseError(Response.Status.INTERNAL_SERVER_ERROR, ae.getMessage(), ae.getMessageSeverity());
			if(ae.getMessageSeverity().equals(MessageSeverity.ERROR) || ae.getMessageSeverity().equals(MessageSeverity.FATAL)){
				LOG.severe(exception.getMessage());
			}
		}else{
			retorno = new ResponseError(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage(), MessageSeverity.ERROR);
			LOG.severe(exception.getMessage());
		}
		return Response.status(retorno.getStatus()).entity(retorno).header("Content-type", "application/json").build();
	}
}

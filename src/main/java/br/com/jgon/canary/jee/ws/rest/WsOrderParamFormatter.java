package br.com.jgon.canary.jee.ws.rest;

import java.lang.annotation.Annotation;

import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.util.FindAnnotation;

import br.com.jgon.canary.jee.exception.ApplicationException;

/**
 * Intercepta a requisicao para tratamento da ordenacao
 * @author jurandir
 *
 */
public class WsOrderParamFormatter implements StringParameterUnmarshaller<WSSortParam> {
	
	Class<?> returnType;
	
	@Override
	public void setAnnotations(Annotation[] annotations) {
		returnType = FindAnnotation.findAnnotation(annotations, WSParamFormat.class).value();
	}

	@Override
	public WSSortParam fromString(String str) {
		try{
			return new WSSortParam(returnType, str);
		}catch (ApplicationException e){
			throw new RuntimeException(e);
		}
	}
}

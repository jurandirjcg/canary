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
package br.com.jgon.canary.ws.rest.param;

import java.lang.annotation.Annotation;

import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.util.FindAnnotation;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.exception.ApplicationRuntimeException;

/**
 * Intercepta a requisicao para tratamento da ordenacao
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
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
			throw new ApplicationRuntimeException(e);
		}
	}
}

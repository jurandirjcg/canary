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

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;

import org.jboss.resteasy.spi.StringParameterUnmarshaller;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.util.ReflectionUtil;

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

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void setAnnotations(Annotation[] annotations) {
        WSParamFormat wsParamFormat = ReflectionUtil.findAnnotation(annotations, WSParamFormat.class);

        if (wsParamFormat != null) {
            if (wsParamFormat.value() != null) {
                returnType = wsParamFormat.value();
            }
        }
    }

    @Override
    public WSSortParam fromString(String str) {
        try {
            returnType = resourceInfo.getResourceMethod().getReturnType();

            Class<?> auxReturnType = ReflectionUtil.returnParameterType(resourceInfo.getResourceMethod().getGenericReturnType(), 0);
            if (auxReturnType != null) {
                returnType = auxReturnType;
            }
            
            return new WSSortParam(returnType, str);
        } catch (ApplicationException e) {
            throw new ApplicationRuntimeException(e);
        }
    }
}

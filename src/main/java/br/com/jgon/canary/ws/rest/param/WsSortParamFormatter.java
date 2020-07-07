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
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import br.com.jgon.canary.util.ReflectionUtil;

/**
 * Intercepta a requisicao para tratamento da ordenacao
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class WsSortParamFormatter implements ParamConverter<WSSortParam>, ParamConverterProvider {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public WSSortParam fromString(String str) {
        Class<?> returnType = null;
        
        Annotation[] annotations = getParamenterAnnotations(resourceInfo.getResourceMethod());

        WSParamFormat wsParamFormat = ReflectionUtil.findAnnotation(annotations, WSParamFormat.class);
        DefaultValue defaultValue = ReflectionUtil.findAnnotation(annotations, DefaultValue.class);

        if (wsParamFormat != null) {
            if (wsParamFormat.value() != null) {
                returnType = wsParamFormat.value();
            }
        }
        if (returnType == null) {
            returnType = resourceInfo.getResourceMethod().getReturnType();

            Class<?> auxReturnType = ReflectionUtil.returnParameterType(resourceInfo.getResourceMethod().getGenericReturnType(), 0);
            if (auxReturnType != null) {
                returnType = auxReturnType;
            }
        }

        return new WSSortParam(returnType, str, defaultValue == null ? null : defaultValue.value());
    }
    
    private Annotation[] getParamenterAnnotations(Method method) {
        for (Parameter p : method.getParameters()) {
            if (WSFieldParam.class.isAssignableFrom(p.getType())) {
                return p.getAnnotations();
            }
        }
        return null;
    }

    @Override
    public String toString(WSSortParam value) {
        return value.getSortParam();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(WSSortParam.class)) {
            return (ParamConverter<T>) this;
        }
        return null;
    }
}

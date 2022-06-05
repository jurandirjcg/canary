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
package br.com.jgon.canary.ws.rest.util;

import java.util.Collection;
import java.util.List;

import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.util.Page;
import br.com.jgon.canary.util.ReflectionUtil;

/**
 * 
 * @since 17/11/2019
 *
 */
public abstract class ResponseConverterUtil {

    public ResponseConverterUtil() {

    }

    /**
     * 
     * @since 17/11/2019
     *
     * @param <T> generic type
     * @param <O> generic type
     * @param returnType {@link Class}
     * @param obj object
     * @return T
     */
    public static <T extends ResponseConverter<O>, O> T converter(Class<T> returnType, O obj) {
        try {
            T returnAux = ReflectionUtil.getInstance(returnType);
            return returnAux.converter(obj);
        } catch (Exception e) {
            throw new ApplicationRuntimeException(MessageSeverity.ERROR, "error.response-converter", e);
        }
    }

    /**
     * 
     * @since 17/11/2019
     *
     * @param <T> generic type
     * @param <O> generic type
     * @param returnType {@link Class}
     * @param obj {@link Collection}
     * @return {@link List}
     */
    public static <T extends ResponseConverter<O>, O> List<T> converter(Class<T> returnType, Collection<O> obj) {
        try {
            T returnAux = ReflectionUtil.getInstance(returnType);
            return returnAux.converter(obj);
        } catch (Exception e) {
            throw new ApplicationRuntimeException(MessageSeverity.ERROR, "error.response-converter", e);
        }
    }

    /**
     * 
     * @since 17/11/2019
     *
     * @param <T> generic type
     * @param <O> generic type
     * @param returnType {@link Class}
     * @param obj {@link Page}
     * @return {@link Page}
     */
    public static <T extends ResponseConverter<O>, O> Page<T> converter(Class<T> returnType, Page<O> obj) {
        try {
            T returnAux = ReflectionUtil.getInstance(returnType);
            return returnAux.converter(obj);
        } catch (Exception e) {
            throw new ApplicationRuntimeException(MessageSeverity.ERROR, "error.response-converter", e);
        }
    }
}

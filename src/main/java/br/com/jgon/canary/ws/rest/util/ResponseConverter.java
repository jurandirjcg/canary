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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.util.Page;
import br.com.jgon.canary.util.ReflectionUtil;

/**
 * Auxlia na conversao do objeto para o objeto de response
 * 
 * @author jurandir
 *
 * @param <O> - Origem
 */
public abstract class ResponseConverter<O> {

    public ResponseConverter() {

    }

    @SuppressWarnings("unchecked")
    public <N extends ResponseConverter<O>> N converter(O obj) {
        if (obj == null) {
            return null;
        }
        try {
            N ret = (N) ReflectionUtil.getInstance(this.getClass());

            List<Field> thisFields = ReflectionUtil.listAttributes(this.getClass());

            Object objAux;
            for (Field fld : thisFields) {
                objAux = null;
                WSTransient wst = ReflectionUtil.getAnnotation(fld, WSTransient.class);
                if (wst != null) {
                    continue;
                }
                WSAttribute wsa = ReflectionUtil.getAnnotation(fld, WSAttribute.class);
                if (wsa != null && StringUtils.isNotBlank(wsa.value())) {
                    objAux = ReflectionUtil.getAttributteValue(obj, wsa.value());
                } else {
                    objAux = ReflectionUtil.getAttributteValue(obj, fld.getName());
                }

                if (ReflectionUtil.isCollection(fld.getType())) {
                    Class<?> colClass;
                    if (wsa != null && !void.class.equals(wsa.collectionType())) {
                        colClass = wsa.collectionType();
                    } else {
                        colClass = ReflectionUtil.returnParameterType(fld.getGenericType(), 0);
                    }

                    if (ResponseConverter.class.isAssignableFrom(colClass)) {
                        Collection<?> col = checkColResponse((Collection<O>) objAux, colClass);
                        ReflectionUtil.setFieldValue(ret, fld, col);
                    } else {
                        ReflectionUtil.setFieldValue(ret, fld, objAux);
                    }
                } else {
                    if (!ResponseConverter.class.isAssignableFrom(fld.getType())) {
                        ReflectionUtil.setFieldValue(ret, fld, objAux);
                    } else {
                        ResponseConverter<?> respConv = checkResponse(objAux, fld.getType());
                        ReflectionUtil.setFieldValue(ret, fld, respConv);
                    }
                }

            }

            return ret;
        } catch (Exception e) {
            throw new ApplicationRuntimeException(MessageSeverity.ERROR, "error.response-converter", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends ResponseConverter<E>, E> ResponseConverter<E> checkResponse(E value, Class<?> returnClass)
        throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (value == null) {
            return null;
        }

        ResponseConverter<E> ret;
        ret = (ResponseConverter<E>) ReflectionUtil.getInstance(returnClass);
        return ret.converter(value);
    }

    @SuppressWarnings("unchecked")
    private <T extends ResponseConverter<E>, E> List<ResponseConverter<E>> checkColResponse(Collection<E> value, Class<?> returnClass)
        throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (value == null) {
            return null;
        }

        ResponseConverter<E> ret;
        ret = (ResponseConverter<E>) ReflectionUtil.getInstance(returnClass);
        return ret.converter(value);
    }

    /**
     * 
     * @param listObj
     * @return
     */
    public <N extends ResponseConverter<O>> List<N> converter(Collection<O> listObj) {
        if (listObj == null) {
            return null;
        }

        List<N> newList = new ArrayList<N>(listObj.size());
        for (O obj : listObj) {
            newList.add(this.converter(obj));
        }
        return newList;
    }

    /**
     * 
     * @param paginacao
     * @return
     */
    public <N extends ResponseConverter<O>> Page<N> converter(Page<O> paginacao) {
        Page<N> pRetorno = new Page<N>();

        pRetorno.setTotalPages(paginacao.getTotalPages());
        pRetorno.setCurrentPage(paginacao.getCurrentPage());
        pRetorno.setElementsPerPage(paginacao.getElementsPerPage());
        pRetorno.setTotalElements(paginacao.getTotalElements());
        pRetorno.setElements(this.converter(paginacao.getElements()));

        return pRetorno;
    }

    /**
     * 
     * @author Jurandir C. Gonçalves
     * @since 17/11/2019
     *
     * @param <T>
     * @param <O>
     * @param returnType
     * @param obj
     * @return
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
     * @author Jurandir C. Gonçalves
     * @since 23/11/2019
     *
     * @param <T>
     * @param <O>
     * @param returnType
     * @param obj
     * @return
     */
    public static <T extends ResponseConverter<O>, O> Page<T> converter(Class<T> returnType, Page<O> obj) {
        try {
            T returnAux = ReflectionUtil.getInstance(returnType);
            return returnAux.converter(obj);
        } catch (Exception e) {
            throw new ApplicationRuntimeException(MessageSeverity.ERROR, "error.response-converter", e);
        }
    }

    /**
     * 
     * @author Jurandir C. Gonçalves
     * @since 23/11/2019
     *
     * @param <T>
     * @param <O>
     * @param returnType
     * @param list
     * @return
     */
    public static <T extends ResponseConverter<O>, O> Collection<T> converter(Class<T> returnType, Collection<O> list) {
        try {
            T returnAux = ReflectionUtil.getInstance(returnType);
            return returnAux.converter(list);
        } catch (Exception e) {
            throw new ApplicationRuntimeException(MessageSeverity.ERROR, "error.response-converter", e);
        }
    }

}

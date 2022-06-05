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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import br.com.jgon.canary.util.CollectionUtil;
import br.com.jgon.canary.util.ReflectionUtil;
import br.com.jgon.canary.ws.rest.link.LinkPaginate;
import br.com.jgon.canary.ws.rest.link.LinkResource;
import br.com.jgon.canary.ws.rest.link.LinkResources;

/**
 * Intercepta a requisicao para tratamento dos campos
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class WsFieldsParamFormatter implements ParamConverter<WSFieldParam>, ParamConverterProvider {

    private static final String REGEX_PATH_PARAMETERS = "(\\#|\\$)\\{[a-z-A-Z\\.]+\\}";
    private static final String REGEX_REPLACE_PARAM = "\\#|\\$|\\{|\\}";

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public WSFieldParam fromString(String str) {
        Class<?> returnType = null;
        String[] forceFields = {};

        Annotation[] annotations = getParamenterAnnotations(resourceInfo.getResourceMethod());

        WSParamFormat wsParamFormat = ReflectionUtil.findAnnotation(annotations, WSParamFormat.class);
        DefaultValue defaultValue = ReflectionUtil.findAnnotation(annotations, DefaultValue.class);

        if (wsParamFormat != null) {
            if (wsParamFormat.value() != null) {
                returnType = wsParamFormat.value();
            }
            forceFields = wsParamFormat.forceFields();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(str);

        for (String f : forceFields) {
            if (!str.contains(f)) {
                sb.append(",");
                sb.append(f);
            }
        }

        String fieldsReconfig = configRequiredParam(resourceInfo.getResourceMethod(), sb.toString());

        if (returnType == null) {
            returnType = resourceInfo.getResourceMethod().getReturnType();

            Class<?> auxReturnType = ReflectionUtil.returnParameterType(resourceInfo.getResourceMethod().getGenericReturnType(), 0);
            if (auxReturnType != null) {
                returnType = auxReturnType;
            }
        }

        return new WSFieldParam(returnType, fieldsReconfig, defaultValue == null ? null : defaultValue.value());
    }

    private Annotation[] getParamenterAnnotations(Method method) {
        for (Parameter p : method.getParameters()) {
            if (WSFieldParam.class.isAssignableFrom(p.getType())) {
                return p.getAnnotations();
            }
        }
        return null;
    }

    /**
     * 
     * @param serviceMethod {@link Method}
     * @param params {@link String}
     * @return {@link String}
     */
    public String configRequiredParam(Method serviceMethod, String params) {
        List<LinkResource> listResources = new ArrayList<LinkResource>(1);
        listResources.addAll(paramFields(serviceMethod));

        if (!listResources.isEmpty()) {
            Pattern pattern = Pattern.compile(REGEX_PATH_PARAMETERS);
            Matcher matcher;
            Set<String> listFieldParam = new HashSet<String>();

            listFieldParam.add(params);

            for (LinkResource linkResource : listResources) {
                for (String qp : linkResource.queryParameters()) {
                    matcher = pattern.matcher(qp);
                    while (matcher.find()) {
                        listFieldParam.add(matcher.group().replaceAll(REGEX_REPLACE_PARAM, ""));
                    }
                }

                for (String qp : linkResource.pathParameters()) {
                    matcher = pattern.matcher(qp);
                    while (matcher.find()) {
                        listFieldParam.add(matcher.group().replaceAll(REGEX_REPLACE_PARAM, ""));
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            listFieldParam.forEach(item -> {
                if (sb.length() != 0) {
                    sb.append(",");
                }
                sb.append(item);

            });

            return sb.toString();
        }
        return params;
    }

    /**
     * 
     * @param method
     * @return
     */
    private List<LinkResource> paramFields(Method method) {
        List<LinkResource> listResources = new ArrayList<LinkResource>(1);

        LinkResource lr = method.getAnnotation(LinkResource.class);
        LinkResources lrs = method.getAnnotation(LinkResources.class);
        LinkPaginate lp = method.getAnnotation(LinkPaginate.class);

        if (lr != null) {
            listResources.add(lr);
        }

        if (lrs != null) {
            if (lrs.value().length > 0) {
                listResources.addAll(CollectionUtil.convertArrayToList(lrs.value()));
            }

            if (!lrs.serviceClass().equals(void.class)) {
                listResources.addAll(paramFields(ReflectionUtil.getMethod(lrs.serviceClass(), lrs.serviceMethodName())));
            }
        }

        if (lp != null) {
            if (lp.collectionLinks().value().length > 0) {
                listResources.addAll(CollectionUtil.convertArrayToList(lp.collectionLinks().value()));
            }

            if (!lp.collectionLinks().serviceClass().equals(void.class)) {
                listResources.addAll(paramFields(
                    ReflectionUtil.getMethod(lp.collectionLinks().serviceClass(), lp.collectionLinks().serviceMethodName())));
            }
        }

        return listResources;
    }

    @Override
    public String toString(WSFieldParam value) {
        return value.getFieldParam();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(WSFieldParam.class)) {
            return (ParamConverter<T>) this;
        }
        return null;
    }
}

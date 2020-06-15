/*
 * Copyright 2019 Jurandir C. Goncalves
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.bind.annotation.JsonbTransient;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.persistence.DAOUtil;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.util.ReflectionUtil;

/**
 * Auxlia na conversao do objeto para o objeto de request
 * 
 * @author Jurandir C. Gonçalves
 * @since 23/10/2019
 *
 * @param <T>
 */
public abstract class RequestConverter<T> {

    @JsonbTransient
    private Logger log = LoggerFactory.getLogger(RequestConverter.class);

    public RequestConverter() {

    }

    /**
     * 
     * @return Class - classe da entidade
     */
    @SuppressWarnings("unchecked")
    protected Class<T> parameterizedClass() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }

    public T converter() {
        String fldName = "";
        try {
            T ret = getInstance(parameterizedClass());

            List<Field> thisFields = ReflectionUtil.listAttributes(this.getClass());

            Object objAuxValue;
            for (Field fld : thisFields) {
                WSTransient wst = ReflectionUtil.getAnnotation(fld, WSTransient.class);
                if (wst != null) {
                    continue;
                }
                WSAttribute wsa = ReflectionUtil.getAnnotation(fld, WSAttribute.class);
                fldName = fld.getName();
                String recursiveAttribute = null;
                if (wsa != null) {
                    if (StringUtils.isNotBlank(wsa.value())) {
                        fldName = wsa.value();
                    }
                    if (StringUtils.isNotBlank(wsa.recursiveAttribute())) {
                        recursiveAttribute = wsa.recursiveAttribute();
                    }
                }
                objAuxValue = ReflectionUtil.getAttributteValue(this, fld.getName());

                setAttributeValue(ret, fldName, objAuxValue, recursiveAttribute);
            }

            return ret;
        } catch (Exception e) {
            log.error("[converter][class.field]" + this.getClass().getCanonicalName() + "." + fldName, e);
            throw new ApplicationRuntimeException(MessageSeverity.ERROR, "error.request-converter");
        }
    }

    // FIXME mover para ReflectionUtil
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void setAttributeValue(Object obj, String fieldName, Object value, String recursiveAttribute) throws IllegalArgumentException,
        IllegalAccessException, InstantiationException, ApplicationException, InvocationTargetException {
        if (value == null) {
            return;
        }
        if (fieldName.contains(".")) {
            Object objAuxValue;
            if (value instanceof Collection) {
                Collection colFld = (Collection) value;
                objAuxValue = colFld.toArray()[colFld.size() - 1];
            } else {
                Field fld = ReflectionUtil.getAttribute(obj.getClass(), fieldName.substring(0, fieldName.indexOf(".")));
                objAuxValue = ReflectionUtil.getAttributteValue(obj, fld);

                if (objAuxValue == null) {
                    if (ReflectionUtil.isCollection(fld.getType())) {
                        objAuxValue = createCollectionInstance(fld.getType());
                        Object objInCollection = getObjectCollectionInstance(fld);
                        ((Collection) objAuxValue).add(objInCollection);
                    } else {
                        objAuxValue = getInstance(fld.getType());
                    }
                    ReflectionUtil.setFieldValue(obj, fld, objAuxValue);
                }
            }
            setAttributeValue(objAuxValue, fieldName.substring(fieldName.indexOf(".") + 1), value, recursiveAttribute);
        } else if (obj instanceof Collection) {
            Collection col = (Collection) obj;
            Object valueToAdd;
            if (value instanceof Collection) {
                Collection colValue = (Collection) value;
                for (Object objValue : colValue) {
                    if (objValue instanceof RequestConverter) {
                        valueToAdd = checkResponse((RequestConverter) objValue);
                    } else {
                        valueToAdd = objValue;
                    }
                    if (recursiveAttribute != null) {
                        ReflectionUtil.setFieldValue(valueToAdd, recursiveAttribute, obj);
                    }
                    col.add(valueToAdd);
                }
            } else {
                if (value instanceof RequestConverter) {
                    valueToAdd = checkResponse((RequestConverter) value);
                } else {
                    valueToAdd = value;
                }
                if (recursiveAttribute != null) {
                    ReflectionUtil.setFieldValue(valueToAdd, recursiveAttribute, obj);
                }
                col.add(valueToAdd);
            }
            ReflectionUtil.setFieldValue(obj, fieldName, col);

        } else if (value instanceof Collection) {
            Field fld = ReflectionUtil.getAttribute(obj.getClass(), fieldName);

            Collection col = ReflectionUtil.getAttributteValue(obj, fld);

            if (col == null) {
                col = createCollectionInstance(fld.getType());
            }

            Collection colValue = (Collection) value;
            Object valueToAdd;
            for (Object objValue : colValue) {
                if (objValue instanceof RequestConverter) {
                    valueToAdd = checkResponse((RequestConverter) objValue);
                } else {
                    valueToAdd = objValue;
                }
                if (recursiveAttribute != null) {
                    ReflectionUtil.setFieldValue(valueToAdd, recursiveAttribute, obj);
                }
                col.add(valueToAdd);
            }

            ReflectionUtil.setFieldValue(obj, fieldName, col);
        } else {
            Object objAuxValue;
            if (value instanceof RequestConverter) {
                objAuxValue = checkResponse((RequestConverter) value);
            } else {
                objAuxValue = value;
            }

            if (recursiveAttribute != null) {
                ReflectionUtil.setFieldValue(objAuxValue, recursiveAttribute, obj);
            }
            ReflectionUtil.setFieldValue(obj, fieldName, objAuxValue);
        }

    }

    /**
     * 
     * @param klass - tipo de colecao
     * @return {@link Collection}
     * @throws ApplicationException - erro ao instanciar colecao
     */
    private Collection<Object> createCollectionInstance(Class<?> klass) throws ApplicationException {
        if (klass.isAssignableFrom(List.class)) {
            return new ArrayList<Object>();
        } else if (klass.isAssignableFrom(Set.class)) {
            return new HashSet<Object>();
        } else if (klass.isAssignableFrom(Collection.class)) {
            return new HashSet<Object>();
        }
        ApplicationException ae = new ApplicationException(MessageSeverity.ERROR, "error.collection-instance",
            new String[] { klass.getName() });
        throw ae;
    }

    /**
     * 
     * @param fld - campo para analise
     * @return {@link Object}
     * @throws ApplicationException   - objeto nao e colecao
     * @throws InstantiationException - erro ao instanciar
     * @throws IllegalAccessException - erro ao acessar atributo
     */
    private Object getObjectCollectionInstance(Field fld) throws ApplicationException {
        Class<?> classAux = DAOUtil.getCollectionClass(fld);
        if (classAux == null) {
            ApplicationException ae = new ApplicationException(MessageSeverity.ERROR,
                "query-mapper.field-collection-not-definied",
                fld.getDeclaringClass().getName() + "." + fld.getName());
            throw ae;
        } else {
            try {
                return classAux.newInstance();
            } catch (Exception e) {
                throw new ApplicationException(MessageSeverity.ERROR, "error.instantiation", classAux.getName());
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <N extends RequestConverter, E> E checkResponse(N value)
        throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (value == null) {
            return null;
        }
//        RequestConverter<E> ret;
//        ret = (RequestConverter<E>) getInstance(value.getPrimaryClass());
//        return ret.converter(value);
        return (E) value.converter();
    }

    @SuppressWarnings("unchecked")
    private <E> E getInstance(Class<E> klass)
        throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor<?>[] ctors = klass.getDeclaredConstructors();
        Constructor<?> ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getGenericParameterTypes().length == 0) {
                break;
            }
        }

        ctor.setAccessible(true);
        return (E) ctor.newInstance();

    }
}

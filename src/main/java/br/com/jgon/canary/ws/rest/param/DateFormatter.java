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
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.util.DateUtil;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.util.ReflectionUtil;

/**
 * Intercepta requisicao para configuracao do campo data
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class DateFormatter implements /* StringParameterUnmarshaller<Date> */ParamConverter<Date>, ParamConverterProvider {

    private SimpleDateFormat formatter;
    private Annotation[] annotations;

    // @Override
    private void setAnnotations(Annotation[] annotations) {
        DateFormat format = ReflectionUtil.findAnnotation(annotations, DateFormat.class);
        JsonbDateFormat formatAux = ReflectionUtil.findAnnotation(annotations, JsonbDateFormat.class);
        if (format != null) {
            formatter = new SimpleDateFormat(format.value());
        } else if (formatAux != null) {
            formatter = new SimpleDateFormat(formatAux.value());
        }
    }

    @Override
    public Date fromString(String str) {
        setAnnotations(annotations);
        
        try {
            if (formatter != null) {
                return formatter.parse(str);
            } else {
                return DateUtil.parseDate(str);
            }
        } catch (ParseException e) {
            throw new ApplicationRuntimeException(MessageSeverity.ERROR, "error.parse-date", e, str);
        }
    }

    @Override
    public String toString(Date value) {
        if (formatter != null) {
            return formatter.format(value);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(Date.class)) {
            return (ParamConverter<T>) this;
        }
        return null;
    }

}

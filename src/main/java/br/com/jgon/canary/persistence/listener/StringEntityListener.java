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
package br.com.jgon.canary.persistence.listener;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import br.com.jgon.canary.persistence.listener.annotation.LowerCase;
import br.com.jgon.canary.persistence.listener.annotation.OnlyAlpha;
import br.com.jgon.canary.persistence.listener.annotation.OnlyAlphanumeric;
import br.com.jgon.canary.persistence.listener.annotation.OnlyNumeric;
import br.com.jgon.canary.persistence.listener.annotation.ReplaceRegex;
import br.com.jgon.canary.persistence.listener.annotation.UpperCase;
import br.com.jgon.canary.util.ReflectionUtil;

/**
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 * @since 1.0
 *
 */
public class StringEntityListener {
    /**
     * 
     * @param obj
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @PrePersist
    @PreUpdate
    public void processEvent(Object obj) throws IllegalArgumentException, IllegalAccessException {
        List<Field> listField = ReflectionUtil.listAttributesByAnnotation(obj.getClass(), UpperCase.class,
                LowerCase.class, OnlyNumeric.class, OnlyAlpha.class, OnlyAlphanumeric.class);
                
        for (Field fld : listField) {
            if (fld.getType().isAssignableFrom(String.class)) {
                if (fld.isAnnotationPresent(UpperCase.class)) {
                    processUpper(obj, fld);
                } else if (fld.isAnnotationPresent(LowerCase.class)) {
                    processLower(obj, fld);
                }
                if (fld.isAnnotationPresent(ReplaceRegex.class)) {
                    ReplaceRegex[] replaceList = fld.getAnnotationsByType(ReplaceRegex.class);
                    for (ReplaceRegex replace : replaceList) {
                        processReplace(obj, fld, replace.regex(), replace.replacement());
                    }
                }
                if (fld.isAnnotationPresent(OnlyAlpha.class)) {
                    processReplace(obj, fld, "[^a-zA-Záàâãéèêíïóôõöúçñ]", "");
                }

                if (fld.isAnnotationPresent(OnlyAlphanumeric.class)) {
                    processReplace(obj, fld, "[^a-zA-Z0-9áàâãéèêíïóôõöúçñ]", "");
                }

                if (fld.isAnnotationPresent(OnlyNumeric.class)) {
                    processReplace(obj, fld, "[\\D]", "");
                }
            }
        }
    }

    /**
     * 
     * @param obj
     * @param field
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void processUpper(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
        String val = ReflectionUtil.getAttributteValue(obj, field);
        if (StringUtils.isNotBlank(val)) {
            ReflectionUtil.setFieldValue(obj, field, val.toUpperCase());
        }
    }

    /**
     * 
     * @param obj
     * @param field
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void processLower(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
        String val = ReflectionUtil.getAttributteValue(obj, field);
        if (StringUtils.isNotBlank(val)) {
            ReflectionUtil.setFieldValue(obj, field, val.toLowerCase());
        }
    }

    /**
     * 
     * @param obj
     * @param field
     * @param regex
     * @param replacement
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void processReplace(Object obj, Field field, String regex, String replacement)
            throws IllegalArgumentException, IllegalAccessException {
        String val = ReflectionUtil.getAttributteValue(obj, field);
        if (StringUtils.isNotBlank(val)) {
            ReflectionUtil.setFieldValue(obj, field, RegExUtils.replacePattern(val, regex, replacement));
        }
    }
}

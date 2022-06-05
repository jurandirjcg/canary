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

import java.util.List;
import javax.ws.rs.WebApplicationException;
import org.apache.commons.lang3.StringUtils;

import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.util.MessageFactory;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.ws.rest.util.WSMapper;

/**
 * Configura os campos vindos da requisicao Ex: pessoa.nome,
 * pessoa{id,nome,dataNascimento}
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class WSFieldParam {

    private Class<?> returnType;
    private List<String> listField;
    private String fieldParam;
    private String defaultFieldParam;

    /**
     * Compatibilidade com QueryParam
     * 
     * @param fields {@link String}
     */
    public WSFieldParam(String fields) {
        throw new WebApplicationException(new ApplicationRuntimeException(MessageSeverity.ERROR, null,
            MessageFactory.getMessage("message", "Construtor somente para compatibilidade com QueryParam REST")));
    }

    /**
     * 
     * @param returnType {@link Class}
     * @param fields {@link String}
     * @param defaultFieldParam {@link String}
     */
    public WSFieldParam(Class<?> returnType, String fields, String defaultFieldParam) {
        this.returnType = returnType;
        this.fieldParam = fields;
        this.defaultFieldParam = defaultFieldParam;
        config();
    }

    private void config() {
        listField = new WSMapper().getFields(returnType, this.fieldParam);
        if(listField.isEmpty() && StringUtils.isNotBlank(this.defaultFieldParam)) {
            listField = new WSMapper().getFields(returnType, this.defaultFieldParam);
        }
    }

    /**
     * 
     * @return {@link Class}
     */
    public Class<?> getReturnType() {
        return returnType;
    }

    /**
     * 
     * @param returnType {@link Class}
     */
    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    /**
     * 
     * @return {@link String}
     */
    public String getFieldParam() {
        return fieldParam;
    }

    /**
     * 
     * @param fieldParam {@link String}
     */
    public void setFieldParam(String fieldParam) {
        this.fieldParam = fieldParam;
    }

    /**
     * 
     * @return {@link List}
     */
    public List<String> toList() {
        return listField;
    }
}

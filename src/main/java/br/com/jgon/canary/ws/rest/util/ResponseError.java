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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import br.com.jgon.canary.util.MessageSeverity;

/**
 * Padronizacao das mensagens de erro do WebService
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class ResponseError {

    private Integer status;
    private String message;
    private String propertyId;
    private Integer errorCode;
    private MessageSeverity type;
    private String moreInformation;
    private List<ResponseError> errors;

    public ResponseError(Status status, String message) {
        this(status, message, null, null, null);
    }

    public ResponseError(Status status, String message, MessageSeverity type) {
        this(status, message, null, null, type);
    }

    /**
     * 
     * @param status
     * @param message
     * @param errorCode
     * @param moreInformation
     * @param type
     */
    public ResponseError(Status status, String message, Integer errorCode, String moreInformation, MessageSeverity type) {
        super();
        this.status = status != null ? status.getStatusCode() : null;
        this.message = message;
        this.errorCode = errorCode;
        this.moreInformation = moreInformation;
        this.type = type;
    }

    /**
     * 
     * @param status
     * @param type
     */
    public ResponseError(Status status, MessageSeverity type) {
        super();
        this.status = status.getStatusCode();
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public MessageSeverity getType() {
        return type;
    }

    public void setType(MessageSeverity type) {
        this.type = type;
    }

    public String getMoreInformation() {
        return moreInformation;
    }

    public void setMoreInformation(String moreInformation) {
        this.moreInformation = moreInformation;
    }

    public List<ResponseError> getErrors() {
        return errors;
    }

    public void setErrors(List<ResponseError> errors) {
        this.errors = errors;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public boolean addError(ResponseError error) {
        if(this.errors == null) {
            this.errors = new ArrayList<ResponseError>(1);
        }
        return this.errors.add(error);
    }
}

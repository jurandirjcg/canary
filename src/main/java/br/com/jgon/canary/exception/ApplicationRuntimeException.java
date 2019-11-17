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
package br.com.jgon.canary.exception;

import br.com.jgon.canary.util.MessageFactory;
import br.com.jgon.canary.util.MessageSeverity;

/**
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class ApplicationRuntimeException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 5128286607376795028L;
    private MessageSeverity messageSeverity;

//    /**
//     * Registra a mensagem sem buscar no arquivo de mensagens da aplicação
//     * @param severity
//     * @param e
//     * @param message - texto da exceção
//     */
//    public ApplicationRuntimeException(MessageSeverity severity, Exception e, String message) {
//        super(message, e);
//        messageSeverity = severity;
//    }

    /**
     * 
     * @param e
     */
    public ApplicationRuntimeException(ApplicationException e) {
        super(e.getMessage(), e);
        messageSeverity = e.getMessageSeverity();
    }

    /**
     * 
     * @param severity
     * @param key
     */
    public ApplicationRuntimeException(MessageSeverity severity, String key) {
        super(MessageFactory.getMessage(key));
        messageSeverity = severity;
    }

    /**
     * 
     * @param severity
     * @param key
     * @param params
     */
    public ApplicationRuntimeException(MessageSeverity severity, String key, String... params) {
        super(MessageFactory.getMessage(key, params));
        messageSeverity = severity;
    }

    /**
     * 
     * @param key
     * @param e
     */
    public ApplicationRuntimeException(String key, Exception e) {
        super(MessageFactory.getMessage(key), e);
        messageSeverity = MessageSeverity.ERROR;
    }

    /**
     * 
     * @param severity
     * @param key
     * @param e
     */
    public ApplicationRuntimeException(MessageSeverity severity, String key, Exception e) {
        super(MessageFactory.getMessage(key), e);
        messageSeverity = severity;
    }

    /**
     * 
     * @param severity
     * @param key
     * @param e
     * @param params
     */
    public ApplicationRuntimeException(MessageSeverity severity, String key, Exception e, String... params) {
        super(MessageFactory.getMessage(key, params), e);
        messageSeverity = severity;
    }

    public MessageSeverity getMessageSeverity() {
        return messageSeverity;
    }

    public void setMessageSeverity(MessageSeverity messageSeverity) {
        this.messageSeverity = messageSeverity;
    }

}

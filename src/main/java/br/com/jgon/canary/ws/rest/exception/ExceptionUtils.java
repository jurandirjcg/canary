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
package br.com.jgon.canary.ws.rest.exception;

import java.util.stream.StreamSupport;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.util.MessageFactory;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.validation.exception.ValidationException;
import br.com.jgon.canary.ws.rest.util.ResponseError;

/**
 * 
 * @author Jurandir C. Gonçalves
 * @since 24/11/2019
 *
 */
abstract class ExceptionUtils {

    private static Logger LOG = LoggerFactory.getLogger(ExceptionUtils.class);

    public static Response toResponse(Exception exception) {

        ResponseError retorno = null;
        if (exception instanceof ValidationException) {
            retorno = configValidationException((ValidationException) exception);
        } else if (exception instanceof ApplicationException) {
            retorno = configApplicationException((ApplicationException) exception);
        } else if (exception instanceof ApplicationRuntimeException) {
            retorno = configApplicationRuntimeException((ApplicationRuntimeException) exception);
        } else if (exception.getCause() instanceof ApplicationRuntimeException) {
            retorno = configApplicationRuntimeException((ApplicationRuntimeException) exception.getCause());
        } else if (exception instanceof ConstraintViolation) {
            retorno = configConstraintViolationException((ConstraintViolation<?>) exception);
        } else if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        } else if (exception instanceof ConstraintViolationException) {
            retorno = configConstraintViolationException((ConstraintViolationException) exception);
        } else if (exception instanceof javax.validation.ValidationException) {
            retorno = configValidationException((ValidationException) exception);
        } else {
            retorno = new ResponseError(Response.Status.INTERNAL_SERVER_ERROR, MessageFactory.getMessage("default.message"),
                MessageSeverity.ERROR);
            LOG.error("[toResponse]", exception);
        }
        return Response.status(retorno.getStatus()).entity(retorno).header("Content-type", "application/json").build();
    }

    public static ResponseError configValidationException(javax.validation.ValidationException exception) {
        return new ResponseError(Status.BAD_REQUEST, exception.getMessage(), MessageSeverity.WARN);
    }

    public static ResponseError configConstraintViolationException(ConstraintViolation<?> constraintViolation) {
        ResponseError retorno = null;
        retorno = new ResponseError(Status.BAD_REQUEST, constraintViolation.getMessage(), MessageSeverity.WARN);
        return retorno;
    }

    public static ResponseError configConstraintViolationException(ConstraintViolationException constraintViolationException) {
        if (constraintViolationException.getConstraintViolations().size() == 1) {
            return configConstraintViolationException(constraintViolationException.getConstraintViolations().stream().findFirst().get());
        }
        ResponseError retorno = new ResponseError(Status.BAD_REQUEST, "", MessageSeverity.WARN);
        constraintViolationException.getConstraintViolations().forEach(cv -> {
            String nome = StreamSupport.stream(cv.getPropertyPath().spliterator(), false).reduce((first, second) -> second).orElse(null)
                .getName();
            retorno.addError(new ResponseError(null, nome + " " + cv.getMessage()));
        });

        return retorno;
    }

    public static ResponseError configApplicationException(ApplicationException exception) {
        ResponseError retorno = null;

        retorno = new ResponseError(getStatusFromMessageSeverity(exception.getMessageSeverity()), exception.getMessage(),
            exception.getMessageSeverity());
        if (exception.getCause() != null && (exception.getMessageSeverity().equals(MessageSeverity.ERROR)
            || exception.getMessageSeverity().equals(MessageSeverity.FATAL))) {
        }
        return retorno;
    }

    public static ResponseError configValidationException(ValidationException exception) {
        return new ResponseError(Response.Status.BAD_REQUEST, ((ValidationException) exception).getMessage(), MessageSeverity.WARN);
    }

    public static ResponseError configApplicationRuntimeException(ApplicationRuntimeException exception) {
        ResponseError retorno = null;
        retorno = new ResponseError(getStatusFromMessageSeverity(exception.getMessageSeverity()), exception.getMessage(),
            exception.getMessageSeverity());
        if (exception.getCause() != null && (exception.getMessageSeverity().equals(MessageSeverity.ERROR)
            || exception.getMessageSeverity().equals(MessageSeverity.FATAL))) {
        }
        return retorno;
    }

    private static Status getStatusFromMessageSeverity(MessageSeverity messageSeverity) {
        if (messageSeverity != null && messageSeverity.equals(MessageSeverity.WARN)) {
            return Response.Status.BAD_REQUEST;
        } else {
            return Response.Status.INTERNAL_SERVER_ERROR;
        }
    }

}

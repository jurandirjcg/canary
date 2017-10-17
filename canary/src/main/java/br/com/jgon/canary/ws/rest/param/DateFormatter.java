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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.util.FindAnnotation;

import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.exception.MessageSeverity;
import br.com.jgon.canary.util.MessageFactory;
/**
 * Intercepta requisicao para configuracao do campo data
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class DateFormatter implements StringParameterUnmarshaller<Date> {

	private SimpleDateFormat formatter;
	
	@Override
	public void setAnnotations(Annotation[] annotations) {
		 DateFormat format = FindAnnotation.findAnnotation(annotations, DateFormat.class);
		 if(format == null){
			 formatter = new SimpleDateFormat("yyyy-MM-dd");
		 }else{
			 formatter = new SimpleDateFormat(format.value());
		 }
	}

	@Override
	public Date fromString(String str) {
		 try{
            return formatter.parse(str);
         }catch (ParseException e){
        	throw new ApplicationRuntimeException(MessageSeverity.ERROR, e, MessageFactory.getMessage("error.parse-date", str));
         }
	}
}

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
package br.com.jgon.canary.jee.ws.rest.util.json;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

/**
 * Utilizar a annotation {@link JsonFormat} parametro pattern para definir o pattern de retorno - default: yyyy-MM-dd HH:mm:ss
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0.0
 *
 */
public class JsonDateTimeDeserializer extends JsonDeserializer<Date> implements ContextualDeserializer{

	private String pattern = "yyyy-MM-dd HH:mm:ss";
	
	public JsonDateTimeDeserializer() {
		
	}
	
	public JsonDateTimeDeserializer(String pattern){
		this.pattern = pattern;
	}
	

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
		JsonFormat jf;
		if(property != null){
			jf = property.getAnnotation(JsonFormat.class);
			if(jf != null && StringUtils.isNotBlank(jf.pattern())){
				return new JsonDateTimeDeserializer(jf.pattern());
			}
		}
		
		return new JsonDateTimeDeserializer();
	}

	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		try {
			return formatter.parse(p.getValueAsString());
		} catch (ParseException e) {
			throw new IOException(e.getMessage());
		}		
	}
}

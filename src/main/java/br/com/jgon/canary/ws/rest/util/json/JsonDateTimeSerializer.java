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
package br.com.jgon.canary.ws.rest.util.json;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

/**
 * Utilizar a annotation {@link JsonFormat} parametro pattern para definir o pattern de retorno - default: yyyy-MM-dd'T'HH:mm:ssZ
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class JsonDateTimeSerializer extends JsonSerializer<Date> implements ContextualSerializer{

	private String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	public JsonDateTimeSerializer() {
		
	}
	
	public JsonDateTimeSerializer(String pattern){
		this.pattern = pattern;
	}
	
	@Override
	public void serialize(Date date, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		gen.writeString(formatter.format(date));
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
		JsonFormat jf;
		if(property != null){
			jf = property.getAnnotation(JsonFormat.class);
			if(jf != null && StringUtils.isNotBlank(jf.pattern())){
				return new JsonDateTimeSerializer(jf.pattern());
			}
		}
		
		return new JsonDateTimeSerializer();
	}
}

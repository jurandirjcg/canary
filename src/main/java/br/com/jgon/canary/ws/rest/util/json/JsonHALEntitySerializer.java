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

import java.lang.reflect.Field;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import org.apache.commons.lang3.StringUtils;

import br.com.jgon.canary.util.ReflectionUtil;
import br.com.jgon.canary.ws.rest.link.LinkEntity;
/**
 * Serializa links na colecao
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class JsonHALEntitySerializer implements JsonbSerializer<JsonHALLinkEntity>{
	
	public JsonHALEntitySerializer() {

	}
	
	@Override
	public void serialize(JsonHALLinkEntity entity, JsonGenerator gen, SerializationContext ctx) {
		gen.writeStartObject();
		
		List<Field> listField = ReflectionUtil.listAttributes(entity.getEntity());
		for(Field fld: listField) {
			processField(fld, entity.getEntity(), gen, ctx);
		}
		
		gen.writeStartObject("_links");
	
		for(LinkEntity le: entity.getListLink()) {
			String relValue = le.getRel();
			le.setRel(null);
			ctx.serialize(relValue, le, gen);
		}
		
		gen.writeEnd();
		gen.writeEnd();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void processField(Field field, Object obj, JsonGenerator gen, SerializationContext ctx) {
		if(field.isAnnotationPresent(JsonbTransient.class)) {
			return;	
		}
		Object value = ReflectionUtil.getAttributteValue(obj, field);
		String key = field.getName();
		JsonbProperty prop = field.getAnnotation(JsonbProperty.class);
		if(prop != null) {
			if(StringUtils.isNotBlank(prop.value())) {
				key = prop.value();
			}
			if(prop.nillable() && value == null) {
				gen.writeNull(key);
				return;
			}
			if(!prop.nillable() && value == null) {
				return;
			}
		}
		
		if(value == null) {
			return;
		}
		
		JsonbTypeSerializer ser = field.getAnnotation(JsonbTypeSerializer.class);
		if(ser != null) {
			try {
				JsonbSerializer s = ser.value().newInstance();
				if(field.getType().isArray()) {
					gen.writeStartArray(key);
					s.serialize(value, gen, ctx);
					gen.writeEnd();
				}else if(ReflectionUtil.isCollection(field.getType())) {
					gen.writeStartObject(key);
					s.serialize(value, gen, ctx);
					gen.writeEnd();
				}else {
					gen.writeStartObject(key);
					s.serialize(value, gen, ctx);
					gen.writeEnd();
				}
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else {
			if(field.getType().isArray()) {
				gen.writeStartArray(key);
				ctx.serialize(value, gen);
				gen.writeEnd();
			}else if(ReflectionUtil.isCollection(field.getType())) {
				gen.writeStartObject(key);
				ctx.serialize(key, value, gen);
				gen.writeEnd();
			}else {
				ctx.serialize(key, value, gen);
			}
		}
	}
}

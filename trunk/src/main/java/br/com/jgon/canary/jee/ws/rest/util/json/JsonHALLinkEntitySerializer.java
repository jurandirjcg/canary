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
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import br.com.jgon.canary.jee.ws.rest.util.link.LinkEntity;
/**
 * Serializa links na colecao
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0.0
 *
 */
public class JsonHALLinkEntitySerializer extends JsonSerializer<List<LinkEntity>>{

	public JsonHALLinkEntitySerializer() {
		
	}
	
	@Override
	public void serialize(List<LinkEntity> linkEntity, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {		
		gen.writeStartObject();
		for(LinkEntity le : linkEntity){
			String relValue = le.getRel();
			le.setRel(null);
			gen.writeObjectField(relValue, le);
		}
		gen.writeEndObject();
	}
}

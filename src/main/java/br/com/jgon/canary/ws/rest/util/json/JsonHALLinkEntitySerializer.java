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

import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;

import br.com.jgon.canary.ws.rest.link.LinkEntity;
/**
 * Serializa links na colecao
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class JsonHALLinkEntitySerializer implements JsonbSerializer<List<LinkEntity>>{

	private Jsonb builder;
	
	public JsonHALLinkEntitySerializer() {
		builder = JsonbBuilder.create();
	}
	
	@Override
	public void serialize(List<LinkEntity> linkEntity, javax.json.stream.JsonGenerator gen, SerializationContext ctx) {
		gen.writeStartObject();
		for(LinkEntity le : linkEntity){
			String relValue = le.getRel();
			le.setRel(null);
			gen.write(relValue, builder.toJson(le));
		}
		gen.writeEnd();
	}
}

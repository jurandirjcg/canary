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
 * @author jurandir
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

package br.com.jgon.canary.jee.ws.rest.util.json;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import br.com.jgon.canary.jee.ws.rest.util.link.LinkEntity;

/**
 * Serializa os link da colecao
 * @author jurandir
 *
 */
public class JsonCollectionLinkEntitySerializer extends JsonSerializer<JsonCollectionLinkEntity>{

	public JsonCollectionLinkEntitySerializer() {
		
	}
	
	@Override
	public void serialize(JsonCollectionLinkEntity entity, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {		
		gen.writeStartObject();
		if(entity.isHalLink() 
				&& entity.getTotal() != null
				&& entity.getLimit() != null
				&& entity.getPage() != null){
			
			gen.writeObjectField("page", entity.getPage());
			gen.writeObjectField("limit", entity.getLimit());
			gen.writeObjectField("total", entity.getTotal());
		}
		
		if(entity.getEmbedded() instanceof Collection<?>){
			gen.writeObjectFieldStart("_embedded");
			if(!entity.getEmbedded().isEmpty()){
				gen.writeObjectField(entity.getItemsName(), entity.getEmbedded());
			}
			gen.writeEndObject();
		}else{
			throw new IllegalArgumentException("Objeto não é uma coleção");
		}
		if(entity.isHalLink()){
			serializeHalLink(entity.getListLink(), gen);
		}else{
			serializeLink(entity.getListLink(), gen);
		}
		gen.writeEndObject();
	}
	
	private void serializeHalLink(List<LinkEntity> listLink, JsonGenerator gen) throws IOException{
		gen.writeObjectFieldStart("_links");
		for(LinkEntity le : listLink){
			String relValue = le.getRel();
			le.setRel(null);
			gen.writeObjectField(relValue, le);
		}
		gen.writeEndObject();
	}
	
	private void serializeLink(List<LinkEntity> listLink, JsonGenerator gen) throws IOException{
		gen.writeArrayFieldStart("_links");
		for(LinkEntity le : listLink){
			gen.writeObject(le);
		}
		gen.writeEndArray();
	}
}

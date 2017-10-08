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
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0.0
 *
 */
public class JsonCollectionLinkEntitySerializer extends JsonSerializer<JsonCollectionLinkEntity>{

	public JsonCollectionLinkEntitySerializer() {
		
	}
	
	@Override
	public void serialize(JsonCollectionLinkEntity entity, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {		
		gen.writeStartObject();
		if(entity.isHalLink() 
				&& entity.getTotalElements() != null
				&& entity.getElementsPerPage() != null
				&& entity.getCurrentPage() != null
				&& entity.getTotalPages() != null){
			
			gen.writeObjectField("currentPage", entity.getCurrentPage());
			gen.writeObjectField("elementsPerPage", entity.getElementsPerPage());
			gen.writeObjectField("totalElements", entity.getTotalElements());
			gen.writeObjectField("totalPages", entity.getTotalPages());
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
	/**
	 * 
	 * @param listLink
	 * @param gen
	 * @throws IOException
	 */
	private void serializeHalLink(List<LinkEntity> listLink, JsonGenerator gen) throws IOException{
		gen.writeObjectFieldStart("_links");
		for(LinkEntity le : listLink){
			String relValue = le.getRel();
			le.setRel(null);
			gen.writeObjectField(relValue, le);
		}
		gen.writeEndObject();
	}
	/**
	 * 
	 * @param listLink
	 * @param gen
	 * @throws IOException
	 */
	private void serializeLink(List<LinkEntity> listLink, JsonGenerator gen) throws IOException{
		gen.writeArrayFieldStart("_links");
		for(LinkEntity le : listLink){
			gen.writeObject(le);
		}
		gen.writeEndArray();
	}
}

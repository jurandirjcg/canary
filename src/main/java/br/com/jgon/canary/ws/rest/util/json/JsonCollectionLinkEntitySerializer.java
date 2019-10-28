package br.com.jgon.canary.ws.rest.util.json;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import br.com.jgon.canary.ws.rest.link.LinkEntity;

/**
 * Serializa os link da colecao
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class JsonCollectionLinkEntitySerializer implements JsonbSerializer<JsonCollectionLinkEntity>{
	
	public JsonCollectionLinkEntitySerializer() {

	}
	
	/**
	 * 
	 * @param listLink
	 * @param gen
	 * @throws IOException
	 */
	private void serializeHalLink(List<LinkEntity> listLink, JsonGenerator gen, SerializationContext ctx){
		gen.writeStartObject("_links");
		for(LinkEntity le : listLink){
			String relValue = le.getRel();
			le.setRel(null);
			ctx.serialize(relValue, le, gen);
		}
		gen.writeEnd();
	}
	/**
	 * 
	 * @param listLink
	 * @param gen
	 * @throws IOException
	 */
	private void serializeLink(List<LinkEntity> listLink, JsonGenerator gen, SerializationContext ctx){
		gen.writeStartArray("_links");
		for(LinkEntity le : listLink){
			ctx.serialize(le, gen);
		}
		gen.writeEnd();
	}

	@Override
	public void serialize(JsonCollectionLinkEntity entity, JsonGenerator gen, SerializationContext ctx) {
		gen.writeStartObject();
		if(entity.isHalLink() 
				&& entity.getTotalElements() != null
				&& entity.getElementsPerPage() != null
				&& entity.getCurrentPage() != null
				&& entity.getTotalPages() != null){
			
			gen.write("currentPage", entity.getCurrentPage());
			gen.write("elementsPerPage", entity.getElementsPerPage());
			gen.write("totalElements", entity.getTotalElements());
			gen.write("totalPages", entity.getTotalPages());
		}
		if(entity.getEmbedded() instanceof Collection<?>){
			gen.writeStartObject("_embedded");
			if(!entity.getEmbedded().isEmpty()){
				ctx.serialize(entity.getItemsName(), entity.getEmbedded(), gen);
			}
			gen.writeEnd();
		}else{
			throw new IllegalArgumentException("Objeto não é uma coleção");
		}
		if(entity.isHalLink()){
			serializeHalLink(entity.getListLink(), gen, ctx);
		}else{
			serializeLink(entity.getListLink(), gen, ctx);
		}
		gen.writeEnd();
	}
}

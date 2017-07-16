package br.com.jgon.canary.jee.ws.rest.util.json;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.jgon.canary.jee.ws.rest.util.link.LinkEntity;
/**
 * Prepara entidade para serializacao de link tipo HAL+JSON
 * @author jurandir
 *
 */
public class JsonHALLinkEntity {
	
	@JsonUnwrapped
	private Object entity;
	@JsonProperty("_links")
	@JsonSerialize(using=JsonHALLinkEntitySerializer.class)
	private List<LinkEntity> listLink = new ArrayList<LinkEntity>();
	
	public JsonHALLinkEntity(){
		
	}

	public JsonHALLinkEntity(Object entity, List<LinkEntity> listLink) {
		super();
		this.entity = entity;
		this.listLink = listLink;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}

	public List<LinkEntity> getListLink() {
		return listLink;
	}

	public void setListLink(List<LinkEntity> listLink) {
		this.listLink = listLink;
	}
	
	/**
	 * 
	 * @param link
	 * @return
	 */
	public boolean addLink(Link link){
		Boolean templated = link.getParams().containsKey("templated");
		LinkEntity linkEntity = new LinkEntity(link.getUri().toString(), link.getRel(), link.getTitle(), link.getType(), templated ? "true" : null);
		return this.listLink.add(linkEntity);
	}
	/**
	 * 
	 * @param links
	 */
	public void addAllLink(List<Link> links){
		for(Link lnk: links){
			addLink(lnk);
		}
	}	
}

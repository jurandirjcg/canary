package br.com.jgon.canary.jee.ws.rest.util.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Link;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.jgon.canary.jee.ws.rest.util.link.LinkEntity;

/**
 * Prepara a colecao para serializacao
 * @author jurandir
 *
 */
@JsonSerialize(using=JsonCollectionLinkEntitySerializer.class)
public class JsonCollectionLinkEntity {

	private boolean isHalLink;
	private String itemsName;
	private Collection<Object> embedded;
	private List<LinkEntity> listLink = new ArrayList<LinkEntity>();
	private Long total;
	private Long limit;
	private Integer page;
	
	public JsonCollectionLinkEntity(){
		
	}

	public JsonCollectionLinkEntity(Collection<Object> embedded, List<LinkEntity> listLink) {
		super();
		this.embedded = embedded;
		this.listLink = listLink;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public String getItemsName() {
		return itemsName;
	}

	public void setItemsName(String itemsName) {
		this.itemsName = itemsName;
	}

	public boolean isHalLink() {
		return isHalLink;
	}

	public void setHalLink(boolean isHalLink) {
		this.isHalLink = isHalLink;
	}

	public Collection<Object> getEmbedded() {
		return embedded;
	}

	public void setEmbedded(Collection<Object> embedded) {
		this.embedded = embedded;
	}

	public List<LinkEntity> getListLink() {
		return listLink;
	}

	public void setListLink(List<LinkEntity> listLink) {
		this.listLink = listLink;
	}
	
	public boolean addLink(Link link){
		String templated = null;
		if(link.getParams().containsKey("templated")){
			templated = "true";
		}
		LinkEntity linkEntity = new LinkEntity(link.getUri().toString(), link.getRel(), link.getTitle(), link.getType(), templated);
		return this.listLink.add(linkEntity);
	}
	
	public void addAllLink(List<Link> links){
		for(Link lnk: links){
			addLink(lnk);
		}
	}	
}

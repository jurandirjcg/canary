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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.ws.rs.core.Link;

import br.com.jgon.canary.ws.rest.link.LinkEntity;

/**
 * Prepara a colecao para serializacao
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
@JsonbTypeSerializer(JsonCollectionLinkEntitySerializer.class)
public class JsonCollectionLinkEntity {

	private boolean isHalLink;
	private String itemsName;
	private Collection<Object> embedded;
	private List<LinkEntity> listLink = new ArrayList<LinkEntity>();
	private Long totalElements;
	private Integer elementsPerPage;
	private Integer currentPage;
	private Integer totalPages;
	
	public JsonCollectionLinkEntity(){
		
	}

	public JsonCollectionLinkEntity(Collection<Object> embedded, List<LinkEntity> listLink) {
		super();
		this.embedded = embedded;
		this.listLink = listLink;
	}

	public Long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}

	public Integer getElementsPerPage() {
		return elementsPerPage;
	}

	public void setElementsPerPage(Integer elementsPerPage) {
		this.elementsPerPage = elementsPerPage;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
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
	/**
	 * 
	 * @param link
	 * @return
	 */
	public boolean addLink(Link link){
		String templated = null;
		if(link.getParams().containsKey("templated")){
			templated = "true";
		}
		LinkEntity linkEntity = new LinkEntity(link.getUri().toString(), link.getRel(), link.getTitle(), link.getType(), templated);
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

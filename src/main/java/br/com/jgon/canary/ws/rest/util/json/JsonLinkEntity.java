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
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.ws.rs.core.Link;

import br.com.jgon.canary.ws.rest.link.LinkEntity;

/**
 * Trata as respostas rest para adicionar links de acesso na entidade
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class JsonLinkEntity {
	
	//@JsonUnwrapped
	private Object entity;
	@JsonbProperty("_links")
	private List<LinkEntity> listLink = new ArrayList<LinkEntity>();
	
	public JsonLinkEntity(){
		
	}

	/**
	 * 
	 * @param entity
	 * @param listLink
	 */
	public JsonLinkEntity(Object entity, List<LinkEntity> listLink) {
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

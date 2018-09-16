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
package br.com.jgon.canary.ws.rest.link;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Configura o link para serializacao
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
@JsonInclude(Include.NON_EMPTY)
public class LinkEntity {
	
	private String rel;
	private String href;
	private String title;
	private String type;
	private String templated;
	
	public LinkEntity(){
		
	}
	
	public LinkEntity(String href, String rel, String title, String type, String templated) {
		super();
		this.rel = rel;
		this.href = href;
		this.title = title;
		this.type = type;
		this.templated = templated;
	}
	
	public LinkEntity(String href, String rel) {
		super();
		this.href = href;
		this.rel = rel;
	}

	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getTemplated() {
		return templated;
	}

	public void setTemplated(String templated) {
		this.templated = templated;
	}
	
}

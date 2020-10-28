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
package br.com.jgon.canary.ws.rest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.ws.rs.core.Link;

/**
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 * @param <T> Tipo
 */
public class ResponseList<T> {

	private Collection<T> data;
	@JsonbProperty("_link")
	private List<Link> link = new ArrayList<Link>(0);

	public ResponseList(){

	}

	public ResponseList(Collection<T> data){
		this.data = data;
	}

	public Collection<T> getData() {
		return data;
	}
	public void setData(Collection<T> data) {
		this.data = data;
	}
	public List<Link> getLink() {
		return link;
	}
	public void setLink(List<Link> link) {
		this.link = link;
	}

}

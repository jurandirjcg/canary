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

import javax.persistence.criteria.JoinType;
/**
 * Configura relacao entre objetos.
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class WSAttributeJoin{

	private String attribute;
	private JoinType joinType;
	private boolean fetch;

	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public JoinType getJoinType() {
		return joinType;
	}
	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}
	public boolean isFetch() {
		return fetch;
	}
	public void setFetch(boolean fetch) {
		this.fetch = fetch;
	}
}
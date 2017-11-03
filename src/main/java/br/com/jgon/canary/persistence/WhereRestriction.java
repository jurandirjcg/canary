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
package br.com.jgon.canary.persistence;

import java.util.AbstractMap.SimpleEntry;

import br.com.jgon.canary.persistence.CriteriaFilterImpl.Where;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
class WhereRestriction {

	private Map<String, List<SimpleEntry<Where, ?>>> listRestriction;

	public WhereRestriction(){
		listRestriction = new LinkedHashMap<String, List<SimpleEntry<Where,?>>>();
	}
	/**
	 * 
	 * @param fieldName
	 * @param where
	 * @param value
	 */
	public <E> void add(String fieldName, Where where, E value){
		if(listRestriction.containsKey(fieldName)){
			listRestriction.get(fieldName).add(new SimpleEntry<Where, E>(where, value));
		}else{
			List<SimpleEntry<Where, ?>> listAux = new LinkedList<SimpleEntry<Where, ?>>();
			listAux.add(new SimpleEntry<Where, E>(where, value));
			listRestriction.put(fieldName, listAux);
		}
	}
	/**
	 * 
	 * @param fieldName
	 * @param listRestriction
	 */
	public <E> void addAll(String fieldName, List<SimpleEntry<Where, ?>> listRestriction){
		if(this.listRestriction.containsKey(fieldName)){
			this.listRestriction.get(fieldName).addAll(listRestriction);
		}else{
			this.listRestriction.put(fieldName, listRestriction);
		}
	}
	/**
	 * 
	 * @param fieldName
	 * @param where
	 */
	public void remove(String fieldName, Where where){
		if(listRestriction.containsKey(fieldName)){
			for(Iterator<SimpleEntry<Where, ?>> it = listRestriction.get(fieldName).iterator(); it.hasNext(); ){
				SimpleEntry<Where, ?> itSe = it.next();
				if(itSe.getKey().equals(where)){
					it.remove();
				}
			}
			
			if(listRestriction.get(fieldName).isEmpty()){
				listRestriction.remove(fieldName);
			}
		}
	}
	/**
	 * 
	 * @param fieldName
	 */
	public void remove(String fieldName){
		if(listRestriction.containsKey(fieldName)){
			listRestriction.remove(fieldName);
		}
	}
	/**
	 * 
	 * @param fieldName
	 * @return
	 */
	public List<SimpleEntry<Where, ?>> getRestrictions(String fieldName){
		return listRestriction.get(fieldName);
	}
	/**
	 * 
	 * @return
	 */
	public Map<String, List<SimpleEntry<Where, ?>>> getRestrictions(){
		return listRestriction;
	}
}

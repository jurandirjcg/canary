package br.com.jgon.canary.jee.persistence;

import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.com.jgon.canary.jee.persistence.CriteriaFilterImpl.Where;

class WhereRestriction {

	private Map<String, List<SimpleEntry<Where, ?>>> listRestriction;

	public WhereRestriction(){
		listRestriction = new LinkedHashMap<String, List<SimpleEntry<Where,?>>>();
	}
	
	public <E> void add(String fieldName, Where where, E value){
		if(listRestriction.containsKey(fieldName)){
			listRestriction.get(fieldName).add(new SimpleEntry<Where, E>(where, value));
		}else{
			List<SimpleEntry<Where, ?>> listAux = new LinkedList<SimpleEntry<Where, ?>>();
			listAux.add(new SimpleEntry<Where, E>(where, value));
			listRestriction.put(fieldName, listAux);
		}
	}
	
	public <E> void addAll(String fieldName, List<SimpleEntry<Where, ?>> listRestriction){
		if(this.listRestriction.containsKey(fieldName)){
			this.listRestriction.get(fieldName).addAll(listRestriction);
		}else{
			this.listRestriction.put(fieldName, listRestriction);
		}
	}
	
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
	
	public void remove(String fieldName){
		if(listRestriction.containsKey(fieldName)){
			listRestriction.remove(fieldName);
		}
	}
	
	public List<SimpleEntry<Where, ?>> getRestrictions(String fieldName){
		return listRestriction.get(fieldName);
	}
	
	public Map<String, List<SimpleEntry<Where, ?>>> getRestrictions(){
		return listRestriction;
	}
}

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
package br.com.jgon.canary.persistence.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.metamodel.Attribute;

/**
 *  
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class ComplexAttribute {

	List<Attribute<?, ?>> listAttribute = new LinkedList<Attribute<?, ?>>();
	
	public ComplexAttribute() {
		
	}
	
	public ComplexAttribute(Attribute<?, ?>... attributes) {
		listAttribute.addAll(new ArrayList<Attribute<?, ?>>(Arrays.asList(attributes)));
	}
		
	public String getName(){
		StringBuilder f = new StringBuilder();
		for(int i=0; i < listAttribute.size(); i++){
			if(i > 0){
				f.append(".");
			}
			f.append(listAttribute.get(i).getName());
		}
		return f.toString();
	}
	
	public static ComplexAttribute instance(Attribute<?, ?>... attributes){
		return new ComplexAttribute(attributes);
	}
	/**
	 * Retorna o tipo do ultimo atributo adicionado
	 * @return Class
	 */
	public Class<?> getFieldType(){
		return listAttribute.get(listAttribute.size() - 1).getJavaType();
	}

    /**
     * 
     * @param attribute
     * @return
     */
    public boolean addAtribute(Attribute<?, ?> attribute) {
        return this.listAttribute.add(attribute);
    }
}

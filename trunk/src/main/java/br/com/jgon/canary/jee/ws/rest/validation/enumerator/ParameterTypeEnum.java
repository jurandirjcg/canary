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
package br.com.jgon.canary.jee.ws.rest.validation.enumerator;

/**
 * 
 * @author Alexandre O. Pereira
 * 
 * @version 1.0.0
 *
 */
public enum ParameterTypeEnum {

	BOOLEAN (Boolean.class),
	BYTE (Byte.class),
	BYTEARRAY (Byte[].class),
	CHAR (Character.class),
	DOUBLE (Double.class),
	FLOAT (Float.class),
	INTEGER (Integer.class),
	LONG (Long.class),
	SHORT (Short.class),
	STRING (String.class);
	
	public Class<?> value;
	
	ParameterTypeEnum(Class<?> value) {
		this.value = value;
	}
	
	public static ParameterTypeEnum valueOf(Class<?> value){
		for(ParameterTypeEnum obj : ParameterTypeEnum.values()){
			if(obj.value.equals(value) ){
				return obj;
			}
		}
		
		return null;
	}
}

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
package br.com.jgon.canary.persistence.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
@Converter
public class SituacaoAtivoInativoConverter implements AttributeConverter<SituacaoAtivoInativo, Integer>{

	@Override
	public Integer convertToDatabaseColumn(SituacaoAtivoInativo arg0) {
		if(arg0 == null){
			return null;
		}
		return arg0.getChave();
	}

	@Override
	public SituacaoAtivoInativo convertToEntityAttribute(Integer arg0) {
		if(arg0 == null){
			return null;
		}
		return SituacaoAtivoInativo.valueOf(arg0);
	}
}
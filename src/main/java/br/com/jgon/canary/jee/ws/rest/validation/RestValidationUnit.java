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
package br.com.jgon.canary.jee.ws.rest.validation;

import java.lang.annotation.Annotation;
import java.util.List;

import br.com.jgon.canary.jee.ws.rest.validation.enumerator.ParameterTypeEnum;

/**
 * Unidade de validacao
 *
 * @author Alexandre O. Pereira
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0.0
 *
 */
public class RestValidationUnit {

	private ParameterTypeEnum type;
	private String name;
	private String value;
	private List<Annotation> apiAnnotations = null;

	public ParameterTypeEnum getType() {
		return type;
	}

	public void setType(ParameterTypeEnum type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public List<Annotation> getApiAnnotations() {
		return apiAnnotations;
	}

	public void setApiAnnotations(List<Annotation> apiAnnotations) {
		this.apiAnnotations = apiAnnotations;
	}

	public void setType(Class<?> type) {
		this.type = ParameterTypeEnum.valueOf(type);
	}
}

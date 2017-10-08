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
package br.com.jgon.canary.jee.persistence;

import java.util.AbstractMap.SimpleEntry;

import br.com.jgon.canary.jee.exception.ApplicationException;

import java.util.List;

/**
 * Ajusta a ordenacao dos campos
 * Ex: pessoa.nome:asc ou +pessoa.nome -> JPA: ORDER BY pessoa.nome ASC 
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0.0
 *
 */
class OrderMapper extends QueryMapper {

	private static final String expOrder = "[a-zA-Z]+\\{(([a-zA-Z\\.]+:(asc|desc)),*)+\\}";
	private String orderFields;
	
	/**
	 * 
	 * @param responseClass
	 * @param orderFields
	 */
	public OrderMapper(Class<?> responseClass, String orderFields) {
		super(responseClass);
		this.orderFields = orderFields;
	}
	
	/**
	 * Retorna os campos ajustados para ordenacao
	 * @return
	 * @throws ApplicationException
	 */
	public List<SimpleEntry<String, String>> getOrder() throws ApplicationException{
		return getCamposAjustados(orderFields, expOrder);
	}
	
}

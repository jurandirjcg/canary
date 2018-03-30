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

import br.com.jgon.canary.util.Pagination;

/**
 * Auxlia na conversao do objeto para o objeto de response
 * @author jurandir
 *
 * @param <O> - Origem
 * @param <N> - Destino
 */
public abstract class ResponseConverter<O> {

	public abstract <N extends ResponseConverter<O>> N converter(O obj);
	/**
	 * 
	 * @param listObj
	 * @return
	 */
	public <N extends ResponseConverter<O>> List<N> converter(Collection<O> listObj){
		if(listObj == null){
			return null;
		}
		
		List<N> newList = new ArrayList<N>(listObj.size());
		for(O obj: listObj){
			newList.add(this.converter(obj));
		}
		return newList;
	}
	/**
	 * 
	 * @param paginacao
	 * @return
	 */
	public <N extends ResponseConverter<O>> Pagination<N> converter(Pagination<O> paginacao){
		Pagination<N> pRetorno = new Pagination<N>();
		
		pRetorno.setTotalPages(paginacao.getTotalPages());
		pRetorno.setCurrentPage(paginacao.getCurrentPage());
		pRetorno.setElementsPerPage(paginacao.getElementsPerPage());
		pRetorno.setTotalElements(paginacao.getTotalElements());
		pRetorno.setElements(this.converter(paginacao.getElements()));
		
		return pRetorno;
	}
}

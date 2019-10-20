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
package br.com.jgon.canary.util;

import java.util.Collection;

/**
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 * @param <T> - tipo
 */
public class Pagination<T> {
	private Long totalElements;
	private Integer elementsPerPage;
	private Integer currentPage;
	private Long totalPages;
	private Collection<T> elements;
	
	public Pagination(){
		totalElements = 0L;
	}
	
	public Pagination(Collection<T> elements){
		super();
		this.elements = elements;
	}
	
	public Pagination(Integer currentPage, Integer elementsPerPage){
		super();
		this.currentPage = currentPage;
		this.elementsPerPage = elementsPerPage;
	}
	
	public Pagination(Collection<T> elements, Pagination<T> pagination) {
		super();
		this.elements = elements;
		this.totalElements = pagination.getTotalElements();
		this.elementsPerPage = pagination.getElementsPerPage();
		this.currentPage = pagination.getCurrentPage();
		this.totalPages = pagination.getTotalPages();
	}
	
	public Pagination(Long totalElementes, Integer elementsPerPage, Integer currentPage) {
		super();
		this.totalElements = totalElementes;
		this.elementsPerPage = elementsPerPage;
		this.currentPage = currentPage;
	}

	public Long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}

	public Integer getElementsPerPage() {
		return elementsPerPage;
	}

	public void setElementsPerPage(Integer elementsPerPage) {
		this.elementsPerPage = elementsPerPage;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Collection<T> getElements() {
		return elements;
	}

	public void setElements(Collection<T> elements) {
		this.elements = elements;
	}

	public Long getTotalPages() {
		if(totalPages == null && elementsPerPage != null && totalElements != null && elementsPerPage > 0){
			Long parteInteira = totalElements / elementsPerPage;
			double parteFracionada = totalElements % elementsPerPage;
			totalPages = Long.valueOf(parteInteira + (parteFracionada == 0 ? 0 : 1));
		}
		return totalPages;
	}
	
	public void setTotalPages(Long totalPages) {
		this.totalPages = totalPages;
	}

}

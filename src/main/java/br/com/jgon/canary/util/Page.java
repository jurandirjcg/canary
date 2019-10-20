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
 * @param <T> - Tipo do objeto
 */
public class Page<T> {
	private Long totalElements;
	private Integer elementsPerPage;
	private Integer currentPage;
	private Long totalPages;
	private Collection<T> elements;
	
	public Page(){
		totalElements = 0L;
	}
	
	public Page(Collection<T> elements){
		super();
		this.elements = elements;
	}
	
	public Page(Integer currentPage, Integer elementsPerPage){
		super();
		this.currentPage = currentPage;
		this.elementsPerPage = elementsPerPage;
	}
	
	public Page(Collection<T> elements, Page<T> page) {
		super();
		this.elements = elements;
		this.totalElements = page.getTotalElements();
		this.elementsPerPage = page.getElementsPerPage();
		this.currentPage = page.getCurrentPage();
		this.totalPages = page.getTotalPages();
	}
	
	public Page(Long totalElementes, Integer elementsPerPage, Integer currentPage) {
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

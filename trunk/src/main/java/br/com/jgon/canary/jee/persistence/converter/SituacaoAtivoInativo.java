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
package br.com.jgon.canary.jee.persistence.converter;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public enum SituacaoAtivoInativo{
	ATIVO(1, "ATIVO"),
	INATIVO(0, "INATIVO");

	private Integer chave;
	private String descricao;
	
	SituacaoAtivoInativo(Integer chave, String descricao) {
		this.chave = chave;
		this.descricao = descricao;
	}

	public Integer getChave() {
		return chave;
	}

	public void setChave(Integer chave) {
		this.chave = chave;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public static List<SituacaoAtivoInativo> list() {
		return Arrays.asList(values());
	}

	public static SituacaoAtivoInativo valueOf(Integer chave) {
		for(SituacaoAtivoInativo tp: values()){
			if(tp.getChave() == chave.shortValue()){
				return tp;
			}
		}
		return null;
	}

}

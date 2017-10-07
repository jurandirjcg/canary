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
public enum Sexo{
	MASCULINO(1, "MASCULINO", "M"),
	FEMININO(2 , "FEMININO", "F");

	private Integer chave;
	private String descricao;
	private String sigla;
	
	Sexo(Integer chave, String descricao, String sigla) {
		this.chave = chave;
		this.descricao = descricao;
		this.sigla = sigla;
	}

	public Integer getChave() {
		return chave;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public String getSigla() {
		return sigla;
	}

	/**
	 * 
	 * @return
	 */
	public static List<Sexo> list() {
		return Arrays.asList(values());
	}

	/**
	 * 
	 * @param chave
	 * @return
	 */
	public static Sexo valueOf(Integer chave) {
		for(Sexo tp: values()){
			if(tp.getChave().equals(chave)){
				return tp;
			}
		}
		return null;
	}
}

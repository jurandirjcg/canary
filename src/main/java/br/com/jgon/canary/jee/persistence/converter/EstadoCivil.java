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
public enum EstadoCivil{
	SOLTEIRO(1, "SOLTEIRO"),
	CASASO(2, "CASADO"),
	DIVORCIADO(3, "DIVORCIADO"),
	UNIDAO_ESTAVEL(4, "UNIÃO ESTÁVEL");

	private Integer chave;
	private String descricao;
	
	EstadoCivil(Integer chave, String descricao) {
		this.chave = chave;
		this.descricao = descricao;
	}

	public Integer getChave() {
		return chave;
	}

	public String getDescricao() {
		return descricao;
	}

	public static List<EstadoCivil> list() {
		return Arrays.asList(values());
	}

	public static EstadoCivil valueOf(Integer chave) {
		for(EstadoCivil tp: values()){
			if(tp.getChave().equals(chave)){
				return tp;
			}
		}
		return null;
	}

}

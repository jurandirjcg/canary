package br.com.jgon.canary.jee.persistence.converter;

import java.util.Arrays;
import java.util.List;
/**
 * 
 * @author jurandir
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

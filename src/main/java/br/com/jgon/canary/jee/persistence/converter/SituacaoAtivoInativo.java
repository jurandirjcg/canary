package br.com.jgon.canary.jee.persistence.converter;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author jurandir
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

	public static List<SituacaoAtivoInativo> listaStatus() {
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

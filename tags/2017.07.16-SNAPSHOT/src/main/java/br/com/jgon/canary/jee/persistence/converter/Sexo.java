package br.com.jgon.canary.jee.persistence.converter;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author jurandir
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

	public static List<Sexo> list() {
		return Arrays.asList(values());
	}

	public static Sexo valueOf(Integer chave) {
		for(Sexo tp: values()){
			if(tp.getChave().equals(chave)){
				return tp;
			}
		}
		return null;
	}
}

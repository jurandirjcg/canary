package br.com.jgon.canary.jee.util;

import java.util.Collection;
/**
 * 
 * @author jurandir
 *
 * @param <T>
 */
public class Pagination<T> {
	private Long totalRegistros;
	private Integer qtdeRegistrosPagina;
	private Integer paginaAtual;
	private Integer numeroTotalPaginas;
	private Collection<T> registros;
	
	public Pagination(){
		totalRegistros = 0L;
	}
	
	public Pagination(Collection<T> registros){
		super();
		this.registros = registros;
	}
	
	public Pagination(Integer paginaAtual, Integer qtdeRegistrosPagina){
		super();
		this.paginaAtual = paginaAtual;
		this.qtdeRegistrosPagina = qtdeRegistrosPagina;
	}
	
	public Pagination(Collection<T> registros, Pagination<?> paginacao) {
		super();
		this.registros = registros;
		this.totalRegistros = paginacao.getQtdeTotalRegistros();
		this.qtdeRegistrosPagina = paginacao.getQtdeRegistrosPagina();
		this.paginaAtual = paginacao.getPaginaAtual();
		this.numeroTotalPaginas = paginacao.getNumeroTotalPaginas();
	}
	
	

	public Pagination(Long totalRegistros, Integer qtdeRegistrosPagina, Integer paginaAtual) {
		super();
		this.totalRegistros = totalRegistros;
		this.qtdeRegistrosPagina = qtdeRegistrosPagina;
		this.paginaAtual = paginaAtual;
	}

	public Collection<T> getRegistros() {
		return registros;
	}

	public void setRegistros(Collection<T> registros) {
		this.registros = registros;
	}

	public Long getQtdeTotalRegistros() {
		return totalRegistros;
	}

	public void setTotalRegistros(Long totalRegistros) {
		this.totalRegistros = totalRegistros;
	}

	public Integer getPaginaAtual() {
		return paginaAtual;
	}

	public void setPaginaAtual(Integer paginaAtual) {
		this.paginaAtual = paginaAtual;
	}

	public Integer getQtdeRegistrosPagina() {
		return qtdeRegistrosPagina;
	}

	public void setQtdeRegistrosPagina(Integer qtdeRegistrosPagina) {
		this.qtdeRegistrosPagina = qtdeRegistrosPagina;
	}

	public Integer getNumeroTotalPaginas() {
		if(numeroTotalPaginas == null && qtdeRegistrosPagina != null && totalRegistros != null && qtdeRegistrosPagina > 0){
			Long parteInteira = totalRegistros / qtdeRegistrosPagina;
			double parteFracionada = totalRegistros % qtdeRegistrosPagina;
			numeroTotalPaginas = Long.valueOf(parteInteira + (parteFracionada == 0 ? 0 : 1)).intValue();
		}
		return numeroTotalPaginas;
	}

	public void setNumeroTotalPaginas(Integer numeroTotalPaginas) {
		this.numeroTotalPaginas = numeroTotalPaginas;
	}
}

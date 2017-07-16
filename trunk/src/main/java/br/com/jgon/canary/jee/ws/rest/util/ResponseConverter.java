package br.com.jgon.canary.jee.ws.rest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.jgon.canary.jee.util.Pagination;
/**
 * Auxlia na conversao do objeto para o objeto de response
 * @author jurandir
 *
 * @param <O> - Origem
 * @param <N> - Destino
 */
public abstract class ResponseConverter<O, N> {

	public abstract N converter(O obj);
	/**
	 * 
	 * @param listObj
	 * @return
	 */
	public List<N> converterList(Collection<O> listObj){
		if(listObj == null){
			return null;
		}
		
		List<N> newList = new ArrayList<N>(listObj.size());
		for(O obj: listObj){
			newList.add(this.converter(obj));
		}
		return newList;
	}
	
	public Pagination<N> converterPagination(Pagination<O> paginacao){
		Pagination<N> pRetorno = new Pagination<N>();
		
		pRetorno.setNumeroTotalPaginas(paginacao.getNumeroTotalPaginas());
		pRetorno.setPaginaAtual(paginacao.getPaginaAtual());
		pRetorno.setQtdeRegistrosPagina(paginacao.getQtdeRegistrosPagina());
		pRetorno.setTotalRegistros(paginacao.getQtdeTotalRegistros());
		pRetorno.setRegistros(this.converterList(paginacao.getRegistros()));
		
		return pRetorno;
	}
}

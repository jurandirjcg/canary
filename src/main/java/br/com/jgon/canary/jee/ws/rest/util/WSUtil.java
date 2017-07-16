package br.com.jgon.canary.jee.ws.rest.util;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import br.com.jgon.canary.jee.util.Pagination;

/**
 * Util de WebService
 * @author jurandir
 *
 */
public class WSUtil {
	
	public static String[] convertStringListToArray(List<String> list){
		return list.toArray(new String[list.size()]);
	}
		
	/**
	 * Converte os objetos de paginacao
	 * @param paginacao
	 * @param converter
	 * @return
	 *//*
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <E, T> Pagination<T> paginationConverter(Pagination<E> paginacao, ResponseConverter converter){
		Pagination<T> pRetorno = new Pagination<T>();
		
		pRetorno.setNumeroTotalPaginas(paginacao.getNumeroTotalPaginas());
		pRetorno.setPaginaAtual(paginacao.getPaginaAtual());
		pRetorno.setQtdeRegistrosPagina(paginacao.getQtdeRegistrosPagina());
		pRetorno.setTotalRegistros(paginacao.getQtdeTotalRegistros());
		pRetorno.setRegistros(converter.converterList(paginacao.getRegistros()));
		
		return pRetorno;
	}*/
	/**
	 * 
	 * @param paginacao
	 * @return
	 */
	public static <T> ResponseBuilder setPaginationToResponse(Pagination<T> paginacao){
		//ResponseList<T> res = new ResponseList<T>(paginacao.getRegistros());
		//Link lnk = Link.from Resource(ServiceFaturamento.class).link(Link.fromMethod(ServiceFaturamento.class, "list").rel("next").build()).build();
		
		return Response.ok().entity(paginacao.getRegistros())
				.header(DominiosRest.X_PAGINATION_TOTAL_COUNT, paginacao.getQtdeTotalRegistros())
				.header(DominiosRest.X_PAGINATION_LIMIT, paginacao.getQtdeRegistrosPagina())
				.header(DominiosRest.X_PAGINATION_PAGE, paginacao.getPaginaAtual());
	}
}

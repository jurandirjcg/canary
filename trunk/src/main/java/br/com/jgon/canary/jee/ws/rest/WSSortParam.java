package br.com.jgon.canary.jee.ws.rest;

import java.util.List;

import br.com.jgon.canary.jee.exception.ApplicationException;
import br.com.jgon.canary.jee.ws.rest.util.WSMapper;
/**
 * Configura os atributos de ordenacao vindos na requisicao
 * Ex: pessoa.nome:desc, -pessoa.nome ou pessoa{+id,-nome}
 * @author jurandir
 *
 */
public class WSSortParam{

	private Class<?> returnType;
	private List<String> listSort;
	private String sort;
	
	/**
	 * Compatibilidade com QueryParam
	 * @param fields
	 */
	public WSSortParam(String fields){
		throw new RuntimeException("Construtor somente para compatibilidade com QueryParam REST");
	}
	
	public WSSortParam(Class<?> returnType, String fields) throws ApplicationException{
		this.returnType = returnType;
		this.sort = fields;
		config();
	}
	
	private void config() throws ApplicationException{
		listSort = new WSMapper().getSort(returnType, this.sort);
	}
		
	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	public List<String> getListSort() {
		return listSort;
	}

	public void setListSort(List<String> listSort) {
		this.listSort = listSort;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

}

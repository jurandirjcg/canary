package br.com.jgon.canary.jee.persistence;

import java.util.AbstractMap.SimpleEntry;

import br.com.jgon.canary.jee.exception.ApplicationException;

import java.util.List;

/**
 * Ajusta a ordenacao dos campos
 * Ex: pessoa.nome:asc ou +pessoa.nome -> JPA: ORDER BY pessoa.nome ASC 
 * @author jurandir
 *
 */
class OrderMapper extends QueryMapper {

	private static final String expOrder = "[a-zA-Z]+\\{(([a-zA-Z\\.]+:(asc|desc)),*)+\\}";
	private String orderFields;
	
	/**
	 * 
	 * @param responseClass
	 * @param orderFields
	 */
	public OrderMapper(Class<?> responseClass, String orderFields) {
		super(responseClass);
		this.orderFields = orderFields;
	}
	
	/**
	 * Retorna os campos ajustados para ordenacao
	 * @return
	 * @throws ApplicationException
	 */
	public List<SimpleEntry<String, String>> getOrder() throws ApplicationException{
		return getCamposAjustados(orderFields, expOrder);
	}
	
}

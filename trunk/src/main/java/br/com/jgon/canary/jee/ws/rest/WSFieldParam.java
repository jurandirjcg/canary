package br.com.jgon.canary.jee.ws.rest;

import java.util.List;

import br.com.jgon.canary.jee.exception.ApplicationException;
import br.com.jgon.canary.jee.ws.rest.util.WSMapper;
/**
 * Configura os campos vindos da requisicao
 * Ex: pessoa.nome, pessoa{id,nome,dataNascimento}
 * @author jurandir
 *
 */
public class WSFieldParam{

	private Class<?> returnType;
	private List<String> listFields;
	private String fields;
	
	/**
	 * Compatibilidade com QueryParam
	 * @param fields
	 */
	public WSFieldParam(String fields){
		throw new RuntimeException("Construtor somente para compatibilidade com QueryParam REST");
	}
	
	public WSFieldParam(Class<?> returnType, String fields) throws ApplicationException{
		this.returnType = returnType;
		this.fields = fields;
		config();
	}
	
	private void config() throws ApplicationException{
		listFields = new WSMapper().getFields(returnType, this.fields);
	}
		
	public Class<?> getReturnType() {
		return returnType;
	}

	public List<String> getListField() {
		return listFields;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	public void setListFields(List<String> listFields) {
		this.listFields = listFields;
	}		
}

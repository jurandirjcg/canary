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
package br.com.jgon.canary.ws.rest.param;

import java.util.List;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.exception.MessageSeverity;
import br.com.jgon.canary.util.MessageFactory;
import br.com.jgon.canary.ws.rest.util.WSMapper;
/**
 * Configura os campos vindos da requisicao
 * Ex: pessoa.nome, pessoa{id,nome,dataNascimento}
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
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
		throw new ApplicationRuntimeException(MessageSeverity.ERROR, null, MessageFactory.getMessage("message","Construtor somente para compatibilidade com QueryParam REST"));
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

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
package br.com.jgon.canary.jee.validation.exception;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ApplicationException;

import br.com.jgon.canary.jee.validation.ValidateMessage;

/**
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
@ApplicationException(rollback=true)
public class ValidationException extends RuntimeException{

	private static final long serialVersionUID = -3033543720897816964L;
	
	private List<ValidateMessage> listMessage = new ArrayList<ValidateMessage>(0);
		
	public ValidationException() {
		
	}	
	/**
	 * 
	 * @param validateMessage
	 */
	public void add(ValidateMessage validateMessage){
		listMessage.add(validateMessage);
	}
	/**
	 * 
	 * @return
	 */
	public List<ValidateMessage> getListMessage() {
		return listMessage;
	}
	/**
	 * Retorna as mensagens da lista separadas por "-"
	 */
	@Override
	public String getMessage() {
		StringBuffer b = new StringBuffer();
		
		boolean separador = false;
		for(ValidateMessage m : listMessage){
			if(separador){
				b.append(" - ");
			}else{
				separador = true;
			}
			b.append(m.getMensagem());
		}
		return b.toString();
	}	 
}

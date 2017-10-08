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
package br.com.jgon.canary.jee.validation;

import br.com.jgon.canary.jee.exception.MessageSeverity;
import br.com.jgon.canary.jee.util.MessageFactory;

/**
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class ValidateMessage {

	private String mensagem;
	private MessageSeverity tipo;
	
	public ValidateMessage(MessageSeverity tipo, String key, String... params){
		this.mensagem = MessageFactory.getMessage(key, params);
		this.tipo = tipo;
	}
	
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public MessageSeverity getTipo() {
		return tipo;
	}
	public void setTipo(MessageSeverity tipo) {
		this.tipo = tipo;
	}
	
}

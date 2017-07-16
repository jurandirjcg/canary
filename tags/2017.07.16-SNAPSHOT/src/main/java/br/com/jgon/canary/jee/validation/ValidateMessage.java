package br.com.jgon.canary.jee.validation;

import br.com.jgon.canary.jee.exception.MessageSeverity;
import br.com.jgon.canary.jee.util.MessageFactory;

/**
 * 
 * @author jurandir
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

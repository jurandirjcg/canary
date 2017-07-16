package br.com.jgon.canary.jee.validation.exception;

import java.util.ArrayList;
import java.util.List;

import br.com.jgon.canary.jee.validation.ValidateMessage;
/**
 * 
 * @author jurandir
 *
 */
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

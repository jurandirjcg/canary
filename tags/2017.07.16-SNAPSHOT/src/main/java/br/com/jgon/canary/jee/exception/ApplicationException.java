package br.com.jgon.canary.jee.exception;

import br.com.jgon.canary.jee.util.MessageFactory;

/**
 * Controla as excecoes lancadas pela aplicacao
 * @author jurandir
 *
 */
public class ApplicationException extends Exception {
		
	private static final long serialVersionUID = -8779781457082467689L;
	private MessageSeverity messageSeverity;

	public ApplicationException(MessageSeverity severity, String key){
		super(MessageFactory.getMessage(key));
		messageSeverity = severity;
	}
	
	public ApplicationException(MessageSeverity severity, String key, String... params){
		super(MessageFactory.getMessage(key, params));
		messageSeverity = severity;
	}
	
	public ApplicationException(String key, Exception e){
		super(MessageFactory.getMessage(key), e);
		messageSeverity = MessageSeverity.ERROR;
	}
	
	public ApplicationException(MessageSeverity severity, String key, Exception e){
		super(MessageFactory.getMessage(key), e);
		messageSeverity = severity;
	}
	
	public ApplicationException(MessageSeverity severity, String key, Exception e, String... params){
		super(MessageFactory.getMessage(key, params), e);
		messageSeverity = severity;
	}

	public MessageSeverity getMessageSeverity() {
		return messageSeverity;
	}

	public void setMessageSeverity(MessageSeverity messageSeverity) {
		this.messageSeverity = messageSeverity;
	}
	
}

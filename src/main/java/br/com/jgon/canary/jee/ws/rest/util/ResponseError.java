package br.com.jgon.canary.jee.ws.rest.util;

import javax.ws.rs.core.Response.Status;

import br.com.jgon.canary.jee.exception.MessageSeverity;

/**
 * Padronizacao das mensagens de erro do WebService
 * @author jurandir
 *
 */
public class ResponseError{
	
	private Integer status;
	private String message;
	private Integer errorCode;
	private MessageSeverity type;
	private String moreInformation;
	
	public ResponseError(Status status, String message) {
		this(status, message, null, null, null);
	}
	
	public ResponseError(Status status, String message, MessageSeverity type) {
		this(status, message, null, null, type);
	}
	/**
	 * 
	 * @param status
	 * @param message
	 * @param errorCode
	 * @param moreInformation
	 * @param type
	 */
	public ResponseError(Status status, String message, Integer errorCode, String moreInformation, MessageSeverity type) {
		super();
		this.status = status.getStatusCode();
		this.message = message;
		this.errorCode = errorCode;
		this.moreInformation = moreInformation;
		this.type = type;
	}
	/**
	 * 
	 * @param status
	 * @param type
	 */
	public ResponseError(Status status, MessageSeverity type) {
		super();
		this.status = status.getStatusCode();
		this.type = type;
	}
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
	public MessageSeverity getType() {
		return type;
	}
	public void setType(MessageSeverity type) {
		this.type = type;
	}
	public String getMoreInformation() {
		return moreInformation;
	}
	public void setMoreInformation(String moreInformation) {
		this.moreInformation = moreInformation;
	}

}

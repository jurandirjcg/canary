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
package br.com.jgon.canary.exception;

import br.com.jgon.canary.util.MessageFactory;

/**
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
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

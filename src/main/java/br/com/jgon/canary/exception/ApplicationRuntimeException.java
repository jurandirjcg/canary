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

import br.com.jgon.canary.util.MessageSeverity;

/**
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class ApplicationRuntimeException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5128286607376795028L;
	private MessageSeverity messageSeverity;
	
	public ApplicationRuntimeException(MessageSeverity severity, Exception e, String message){
		super(message, e);
		messageSeverity = severity;
	}
	
	public ApplicationRuntimeException(ApplicationException e){
		super(e.getMessage(), e);
		messageSeverity = e.getMessageSeverity();
	}

	public MessageSeverity getMessageSeverity() {
		return messageSeverity;
	}
	
}

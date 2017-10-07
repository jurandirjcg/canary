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
package br.com.jgon.canary.jee.exception;

/**
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
@javax.ejb.ApplicationException(rollback=true)
public class SaveEntityException extends ApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8785226925929014724L;

	public SaveEntityException(Exception e, Class<?> entityClass) {
		super(MessageSeverity.ERROR, "error.save", e, entityClass.getSimpleName());
	}

}

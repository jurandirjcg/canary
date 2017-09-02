package br.com.jgon.canary.jee.exception;

public class SaveEntityException extends ApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8785226925929014724L;

	public SaveEntityException(Exception e, Class<?> entityClass) {
		super(MessageSeverity.ERROR, "error.save", e, entityClass.getSimpleName());
	}

}

package br.com.jgon.canary.jee.exception;

@javax.ejb.ApplicationException(rollback=true)
public class RemoveEntityException extends ApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6362902586242197678L;

	public RemoveEntityException(Exception e, Class<?> entityClass) {
		super(MessageSeverity.ERROR, "error.remove", e, entityClass.getSimpleName());
	}

}

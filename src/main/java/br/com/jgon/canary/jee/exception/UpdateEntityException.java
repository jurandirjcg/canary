package br.com.jgon.canary.jee.exception;

@javax.ejb.ApplicationException(rollback=true)
public class UpdateEntityException extends ApplicationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3181380870254612845L;

	public UpdateEntityException(Exception e, Class<?> entityClass) {
		super(MessageSeverity.ERROR, "error.update", e, entityClass.getSimpleName());
	}

}

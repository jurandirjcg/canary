package br.com.jgon.canary.persistence;

import javax.persistence.criteria.JoinType;

/**
 * 
 * @since 24/06/2019
 * @author Jurandir C. Gonçalves
 *
 */
public class JoinMapper {

	private JoinType joinType;
	private Boolean fetch;
	private Boolean force;
	
	public JoinMapper() {
	
	}

	public JoinMapper(JoinType joinType, Boolean fetch, Boolean force) {
		super();
		this.joinType = joinType;
		this.fetch = fetch;
		this.force = force;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}

	public Boolean getFetch() {
		return fetch;
	}

	public void setFetch(Boolean fetch) {
		this.fetch = fetch;
	}

	public Boolean getForce() {
		return force;
	}

	public void setForce(Boolean force) {
		this.force = force;
	}

}

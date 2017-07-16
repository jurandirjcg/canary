package br.com.jgon.canary.jee.ws.rest.util;

import javax.persistence.criteria.JoinType;
/**
 * Configura relacao entre objetos.
 * @author jurandir
 *
 */
public class WSAttributeJoin{
	
		private String attribute;
		private JoinType joinType;
		private boolean fetch;
		
		public String getAttribute() {
			return attribute;
		}
		public void setAttribute(String attribute) {
			this.attribute = attribute;
		}
		public JoinType getJoinType() {
			return joinType;
		}
		public void setJoinType(JoinType joinType) {
			this.joinType = joinType;
		}
		public boolean isFetch() {
			return fetch;
		}
		public void setFetch(boolean fetch) {
			this.fetch = fetch;
		}
	
	}
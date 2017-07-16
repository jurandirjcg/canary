package br.com.jgon.canary.tree;

import java.io.Serializable;

/**
 * Classe abastrata para utilização no conteúdo dos nodes da arvore {@link GenericTree}
 * @author jurandir
 * 
 * @version 1.0 - 31/07/2011
 */
public abstract class GenericConteudoTree implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Identificacao do conteúdo
	 */
	@FieldGenericTree(mapearAtributo=false)
	private String label;

	public GenericConteudoTree(String label){
		this.label = label;
	}
	
	public GenericConteudoTree(){
		
	}
	
	/**
	 * Identificacao do conteúdo
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Identificacao do conteúdo
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericConteudoTree other = (GenericConteudoTree) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}	

}

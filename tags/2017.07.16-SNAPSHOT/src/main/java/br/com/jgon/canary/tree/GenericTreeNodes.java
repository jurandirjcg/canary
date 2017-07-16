package br.com.jgon.canary.tree;

import java.io.Serializable;

public class GenericTreeNodes implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2314819646438900384L;
	private String node;
	private String[] nodesCaseNull;
	
	public GenericTreeNodes(String node) {
		super();
		this.node = node;
	}
	
	public GenericTreeNodes(String node, String[] nodesCaseNull) {
		super();
		this.node = node;
		this.nodesCaseNull = nodesCaseNull;
	}

	public String[] getNodesCaseNull() {
		return nodesCaseNull;
	}

	public void setNodesCaseNull(String[] nodesCaseNull) {
		this.nodesCaseNull = nodesCaseNull;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}
}

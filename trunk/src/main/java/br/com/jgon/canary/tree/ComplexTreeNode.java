package br.com.jgon.canary.tree;

public class ComplexTreeNode extends GenericTreeNodes {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5825508692113888313L;
	private String[] nodesOperation;
	
	public ComplexTreeNode(String node, String[] nodesOperation) {
		super(node, nodesOperation);
	}
	
	public ComplexTreeNode(String node, String[] nodesCaseNull, String[] nodesOperation) {
		super(node, nodesCaseNull);
		this.nodesOperation = nodesOperation;
	}

	public String[] getNodesOperation() {
		return nodesOperation;
	}

	public void setNodesOperation(String[] nodesOperation) {
		this.nodesOperation = nodesOperation;
	}
	
}

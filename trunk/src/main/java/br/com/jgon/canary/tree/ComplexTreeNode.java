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
package br.com.jgon.canary.tree;

/**
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
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

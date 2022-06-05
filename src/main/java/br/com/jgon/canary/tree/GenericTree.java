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

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.util.ReflectionUtil;


/**
 * Árvore de conteúdos
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 * 
 * @param <T> - Tipo do conteúdo da árvore
 */
public class GenericTree<T extends GenericConteudoTree> implements Serializable{

	private static final long serialVersionUID = -6825312504752843113L;
	private static final String SEPARATOR_NODE = "_";
	private static final String SEPARATOR_NODE_NULL = "_";
	
	private Logger logger = LoggerFactory.getLogger(GenericTree.class);
	/**
	 * Nome do node
	 */
	private String nodeName;
	/**
	 * Node pai
	 */
	private GenericTree<T> parent;
	/**
	 * Nodes filhos
	 */
	private LinkedList<GenericTree<T>> nodesFilhos = new LinkedList<GenericTree<T>>();
	/**
	 * Conteudo do node
	 */
	private T conteudo;
	
	/**
	 * @param nodeName - nome do node atual
	 * @param conteudo - conteudo do node
	 * @param parent - node pai
	 */
	public GenericTree(String nodeName, T conteudo, GenericTree<T> parent){
		this.nodeName = nodeName;
		this.nodesFilhos = new LinkedList<GenericTree<T>>();
		this.conteudo = conteudo;
		this.parent = parent;
	}
	
	private String configNodesConteudo(T conteudo, String[] nodes) throws ApplicationException{
		String nodeResAux = "";

		if(nodes == null)
			return nodeResAux;

		try{
			for(int k = 0;  k < nodes.length; k++){
				Object resAux = ReflectionUtil.executeGet(conteudo, nodes[k]);
				if(resAux != null)
					nodeResAux += (k == 0 ? "" : SEPARATOR_NODE_NULL) + String.valueOf(resAux);
			}
		}catch (Exception e) {
			logger.error("[configNodesConteudo]", e);
			throw new ApplicationException(MessageSeverity.ERROR, "error.tree.node", e);
		}
		//Remove Formacacao
		return StringUtils.stripAccents(RegExUtils.replacePattern(nodeResAux, "[.-\\s/\\]", ""));
	}
	
	/**
	 * Instancia um objeto GenericTree basedo na lista de dados
	 * @param rootNode - nome do node raiz
	 * @param subNodes - lista de nodes que serao desmembrados da lista de dados
	 * @param dados - lista de dados
	 * @param alteraConteudo - Indica se forca a alteracao de conteudo caso ja exista em um node
	 */
	public GenericTree(String rootNode, LinkedList<GenericTreeNodes> subNodes, List<T> dados, boolean alteraConteudo){
		
		this.nodesFilhos = new LinkedList<GenericTree<T>>();
		this.nodeName = rootNode;

		if(dados.size() > 0){
			//Testa se existem nodes, caso nao add no raiz
			if(subNodes.isEmpty()){
				for(T dd : dados){
					setConteudo(dd);
				}
			}
			
			//Iteracao pelos subNodes
			for(int i=0; i < subNodes.size(); i++){
				GenericTreeNodes genTreeNodeAux = subNodes.get(i);
				//Inclui o conteudo somente se for o ultimo node mapeado
				boolean includeContent = i == subNodes.size() - 1;
				//Iteracao pelos dados
				for(T dd: dados){
					String nodeAtual = rootNode;
					String nodeParent = rootNode;
					try {
						/*
						 * Pega todos os nodes que ja foram percorridos para obter o nome do parent node 
						 * 
						 * Iteracao nos subNodes que ja passaram
						 */
						for(int j= 0; j <= i; j++){
							Object res = null;
							/* 
							 * Obtem valor inicial do node mapeado
							 * Verifica se eh uma instancia de ComplexTreeNode
							 * e se existe o atributo no conteudo.Caso seja instancia e exista o atributo utiliza o valor deste conteudo
							 * caso não exista o atributo itera pelos nodes setados no atributo nodesCaseNull.
							 * Caso nao seja uma instancia de ComplexTreeNode utiliza o atributo setado como padrao 
							 * 
							 */
							if(subNodes.get(j) instanceof ComplexTreeNode){
								if(ReflectionUtil.existAttribute(dd.getClass(), subNodes.get(j).getNode()))
										res = ReflectionUtil.executeGet(dd, subNodes.get(j).getNode());
							}else
								res = ReflectionUtil.executeGet(dd, subNodes.get(j).getNode());
							
							//Percorre os nodes setados em nodesCaseNull
							if(res == null){
								String nodeResAux = this.configNodesConteudo(dd, subNodes.get(j).getNodesCaseNull());
								res = StringUtils.isEmpty(nodeResAux) ? null : nodeResAux;
							}
							
							//Remove Formatacao
							nodeAtual += RegExUtils.replacePattern((res != null ? SEPARATOR_NODE + String.valueOf(res) : ""), "[.-\\s/\\]", "");
							
							if(j < i)
								nodeParent = nodeAtual;
						}
						
						/* 
						 * Verifica se eh instancia de ComplexTreeNode
						 * para localizar o node que deverá ser inserido o conteudo
						 */
						if(genTreeNodeAux instanceof ComplexTreeNode){
							//Monta o novo node que vai ser inserido
							String nodeOperAux = this.configNodesConteudo(dd, ((ComplexTreeNode) genTreeNodeAux).getNodesOperation());							
							nodeOperAux = RegExUtils.replacePattern(nodeOperAux, "[.-\\s/\\]", "");
							
							//Verifica se existe o node da consulta
							if(existNode(nodeAtual))
								this.setConteudo(nodeAtual, nodeAtual + SEPARATOR_NODE + nodeOperAux,  (includeContent ? dd : null), alteraConteudo);
							else{
								/* 
								 * Nao existe o node, monta o node com nome minimo (nodesCaseNull) e localiza em qualquer ponto
								 * da arvore retornando null caso nao encontre e o node caso contrario
								 */
								
								String nodeAux;
								String nodeResAux = this.configNodesConteudo(dd, genTreeNodeAux.getNodesCaseNull());
								Object res = StringUtils.isEmpty(nodeResAux) ? null : RegExUtils.replacePattern(nodeResAux, "[.-\\s/\\]", "");
								nodeAux = String.valueOf(res);
								GenericTree<T> gtAux = getNode(this, nodeAux, false);
								/*
								 * Se node != null seta o novo node e o conteudo, se for o caso (includeContent) 
								 */
								if(gtAux != null){
									setConteudo(gtAux.getNodeName(), gtAux.getNodeName() + SEPARATOR_NODE + nodeOperAux,  (includeContent ? dd : null), alteraConteudo);
								}else{
									/*
									 * Nao localizou adiciona o node parent, sem conteudo
									 * e adiciona o node (nodesOperation) ao node parent recem criado
									 */
									this.addNode(nodeParent, nodeAtual, null);
									this.setConteudo(nodeAtual, nodeAtual + SEPARATOR_NODE + nodeOperAux,  (includeContent ? dd : null), alteraConteudo);
								}
							}
						/*
						 * Nao eh instancia ComplexTreeNode, tenta inserir o conteudo ao node parent caso nao consiga
						 * forca a inclusao em um novo node
						 */
							 
						}else if(!this.setConteudo(nodeParent, nodeAtual, (includeContent ? dd : null), alteraConteudo))
							this.addNode(nodeParent, nodeAtual,  (includeContent ? dd : null));

					} catch (Exception e) {
						//FIXME ADD LOGGER
						//AndorinhaLogManager.loggerAdd(GenericTree.class, LogType.WARN, e.getMessage(), e);
					}
				}
			}

		}
	}
	
	/**
	 * Retorna o node pai
	 * @return {@link GenericTree}
	 */
	public GenericTree<T> getParent() {
		return parent;
	}

	/**
	 * Seta um node pai
	 * @param parent - node pai
	 */
	public void setParent(GenericTree<T> parent) {
		this.parent = parent;
	}

	/**
	 * Retorna o nome do node
	 * @return {@link String}
	 */
	public String getNodeName() {
		return this.getNodeName(false);
	}

	/**
	 * Retorna o ultimo valor adicionado ao node
	 * Ex: root_node1_node2_node3 - retorna o node3
	 * @param ultimoNodeName - nome do último node
	 * @return {@link String}
	 */
	public String getNodeName(boolean ultimoNodeName) {
		if(ultimoNodeName){
			String[] node = nodeName.split(SEPARATOR_NODE);
			return node[node.length - 1];
		}			
		return nodeName;
	}


	/**
	 * Seta o nome do node
	 * @param nodeName - nome do node
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * Retorna os nodes filhos
	 * @return {@link List}
	 */
	public List<GenericTree<T>> getNodesFilhos() {
		return nodesFilhos;
	}

	/**
	 * Seta os nodes filhos
	 * @param nodesFilhos - lista de nodes filhos
	 */
	public void setNodesFilhos(LinkedList<GenericTree<T>> nodesFilhos) {
		this.nodesFilhos = nodesFilhos;
	}

	/**
	 * Retorna o conteúdo do node
	 * @return T
	 */
	public T getConteudo() {
		return conteudo;
	}

	/**
	 * Seta o conteudo do node
	 * @param conteudo - conteúdo
	 */
	public void setConteudo(T conteudo) {
		this.conteudo = conteudo;
	}

	/**
	 * Seta conteudo no node informado, caso nao exista cria um nodeFilho no nodeParent
	 * @param nodeParent - node pai
	 * @param node - node atual
	 * @param conteudo - conteúdo do node
	 * @param alteraConteudo - true para sobrescrever se o conteudo nao for nulo
	 * @return - true se adicionou e false caso contrario
	 */
	public boolean setConteudo(String nodeParent, String node, T conteudo, boolean alteraConteudo){
		
		boolean adicionou = false;
		
		GenericTree<T> gt = getNode(node);
		if(gt != null){
			if(alteraConteudo || nodeParent.equals(node))
				gt.setConteudo(conteudo);
			
			adicionou = true;
		}else{
			gt = getNode(nodeParent);
			if(gt != null){		
				if(gt.addNodeFilho(node, conteudo))
					adicionou = true;
			}
		}		
		
		return adicionou;
	}
	
	/**
	 * Adiciona um novo node com conteudo
	 * @param node - nome do node
	 * @param conteudo - conteúdo do node
	 * @return {@link Boolean}
	 */
	public boolean addNodeFilho(String node, T conteudo){
		return nodesFilhos.add(new GenericTree<T>(node, conteudo, this));
	}
	
	/**
	 * Adiciona um novo node sem conteúdo
	 * @param node - nome do node
	 * @return {@link Boolean}
	 */
	public boolean addNodeFilho(String node){
		return addNodeFilho(node, null);
	}
	
	/**
	 * Verifica se existe o node
	 * @param nodeName - nome do node a ser pesquisado
	 * @return {@link Boolean}
	 */
	public boolean existNode(String nodeName){
		if(getNode(nodeName) != null)
			return true;
		else
			return false;
	}
	/**
	 * Retorna true se node foi adicionado ou ja existe e false se nao foi adicionado
	 * @param nodeParent - nome do node pai
	 * @param node - nome do node atual
     * @param conteudo content
	 * @return {@link Boolean}
	 */
	public boolean addNode(String nodeParent, String node, T conteudo){
		boolean adicionou = false;
		
		if(!existNode(node)){
			GenericTree<T> gt = getNode(nodeParent);
			if(gt != null){
				gt.addNodeFilho(node, conteudo);
				adicionou = true;
			}	
		}else
			return true;
				
		return adicionou;
	}
	/**
	 * Remove um node filho pelo nome
	 * @param nodeName - nome do node
	 * @return {@link Boolean}
	 */
	public boolean removeNodeFilho(String nodeName){
		for(Iterator<GenericTree<T>> itGt = nodesFilhos.iterator(); itGt.hasNext(); ){
			GenericTree<T> gt = itGt.next();
			if(gt.getNodeName().equals(nodeName)){
				itGt.remove();
				return true;
			}
		}
		
		return false;
	}
	/**
	 * Retorna o node atraves do nome, null se nao encontrar, deve ser dependente do node principal
	 * @param nodeName - nome do node
	 * @return {@link GenericTree}
	 */
	public GenericTree<T> getNode(String nodeName){
		return getNode(this, nodeName, true);
	}
	
	/**
	 * Retorna um node da arvore
	 * @param genericTree - árvore a ser pesquisada
	 * @param nodeName - nome do node
	 * @param fullName - Pesquisa por nome completo do node.
	 * @return {@link GenericTree}
	 */
	private GenericTree<T> getNode(GenericTree<T> genericTree, String nodeName, boolean fullName){
		if(fullName && genericTree.getNodeName().equals(nodeName))
			return genericTree;
		else if(genericTree.getNodeName().contains(nodeName))
			return genericTree;
			
		
		GenericTree<T> gtAux = null;
		for(GenericTree<T> gt: genericTree.getNodesFilhos()){
			gtAux = getNode(gt, nodeName, fullName);
			if(gtAux != null)
				break;
		}
		
		return gtAux;
	}
}

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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Responsavel por indicar o local dos atributos da arvore, se posicaoNode possuir valor o atributo sera mapeado para raiz
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0.0
 *
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.FIELD)
@Inherited
public @interface FieldGenericTree {

	/**
	 * Posicao do atributo linha ou coluna ou nao mapear
	 * @author jurandirjcg
	 * 
	 *  04/01/2012
	 * @version 1.0.0
	 */
	public enum PositionTree{
		ROW,
		COL,
		NONE
	}
	/**
	 * Posicao do node
	 * @return
	 */
	int posicaoNode() default -1;
	
	/**
	 * Indica se o atributo será mapeado
	 * @return
	 */
	boolean mapearAtributo() default true; 
	
	/**
	 * Localizacao do atributo - Linha ou Coluna
	 * @return
	 */
	PositionTree posicaoTree() default PositionTree.NONE;
}

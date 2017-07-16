package br.com.jgon.canary.tree;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Responsavel por indicar o local dos atributos da arvore, se posicaoNode possuir valor o atributo sera mapeado para raiz
 * @author jurandir
 * 
 * @version 1.0 - 31/07/2011
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.FIELD)
@Inherited
public @interface FieldGenericTree {

	/**
	 * Posicao do atributo linha ou coluna ou nao mapear
	 * @author jurandirjcg
	 * 
	 * @since 04/01/2012
	 * @version 1.0
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

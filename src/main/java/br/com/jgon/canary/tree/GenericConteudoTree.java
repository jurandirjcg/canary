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

/**
 * Classe abastrata para utilização no conteúdo dos nodes da arvore
 * {@link GenericTree}
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public abstract class GenericConteudoTree implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Identificacao do conteúdo
     */
    @FieldGenericTree(mapearAtributo = false)
    private String label;

    public GenericConteudoTree(String label) {
        this.label = label;
    }

    public GenericConteudoTree() {

    }

    /**
     * Identificacao do conteúdo
     * 
     * @return {@link String}
     */
    public String getLabel() {
        return label;
    }

    /**
     * Identificacao do conteúdo
     * 
     * @param label - identificação
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

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
package br.com.jgon.canary.ws.rest.param;

import java.util.List;

import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.util.MessageFactory;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.ws.rest.util.WSMapper;

/**
 * Configura os atributos de ordenacao vindos na requisicao Ex:
 * pessoa.nome:desc, -pessoa.nome ou pessoa{+id,-nome}
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class WSSortParam {

    private Class<?> returnType;
    private List<String> listSort;
    private String sortParam;

    /**
     * Compatibilidade com QueryParam
     * 
     * @param fields
     */
    public WSSortParam(String fields) {
        throw new ApplicationRuntimeException(MessageSeverity.ERROR, null,
            MessageFactory.getMessage("message", "Construtor somente para compatibilidade com QueryParam REST"));
    }

    public WSSortParam(Class<?> returnType, String fields) {
        this.returnType = returnType;
        this.sortParam = fields;
        config();
    }

    private void config() {
        listSort = new WSMapper().getSort(returnType, this.sortParam);
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public List<String> getListSort() {
        return listSort;
    }

    public void setListSort(List<String> listSort) {
        this.listSort = listSort;
    }

    public String getSortParam() {
        return sortParam;
    }

    public void setSortParam(String sortParam) {
        this.sortParam = sortParam;
    }

}

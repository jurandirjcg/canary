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
package br.com.jgon.canary.persistence.filter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import br.com.jgon.canary.exception.ApplicationRuntimeException;

/**
 * Define os filtros da consulta
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 * @param <T>
 */
public interface CriteriaWhere<T> {

    /**
     * Regex de consulta
     * 
     * @author Jurandir C. Goncalves
     * 
     * @version 1.0
     * 
     */
    public enum RegexWhere {
        /**
         * Ex: =10
         */
        EQUAL,
        /**
         * Ex: (10 btwn 20)
         */
        BETWEEN,
        /**
         * Ex: &lt;10
         */
        LESS_THAN,
        /**
         * Ex: &lt;=10
         */
        LESS_THAN_OR_EQUAL_TO,
        /**
         * Ex: &gt;10
         */
        GREATER_THAN,
        /**
         * Ex: &gt;=10
         */
        GREATER_THAN_OR_EQUAL_TO,
        /**
         * Ex: !=10
         */
        NOT_EQUAL,
        /**
         * Ex: (10,15,20)
         */
        IN,
        /**
         * Ex: !(10,12,15,20)
         */
        NOT_IN,
        /**
         * Ex: =%nome
         */
        LIKE_EXACT,
        /**
         * Ex: !%nome
         */
        LIKE_NOT_EXACT,
        /**
         * Ex: %nome%
         */
        LIKE_MATCH_ANYWHERE,
        /**
         * Ex: !%nome!%
         */
        LIKE_NOT_MATCH_ANYWHERE,
        /**
         * Ex: %nome
         */
        LIKE_MATCH_END,
        /**
         * Ex: !%nome
         */
        LIKE_NOT_MATCH_END,
        /**
         * Ex: nome%
         */
        LIKE_MATCH_START,
        /**
         * Ex: nome!%
         */
        LIKE_NOT_MATCH_START,
        /**
         * Ex: =*nome
         */
        ILIKE_EXACT,
        /**
         * Ex: !*nome
         */
        ILIKE_NOT_EXACT,
        /**
         * Ex: *nome*
         */
        ILIKE_MATCH_ANYWHERE,
        /**
         * Ex: !*nome!*
         */
        ILIKE_NOT_MATCH_ANYWHERE,
        /**
         * Ex: *nome
         */
        ILIKE_MATCH_END,
        /**
         * Ex: !*nome
         */
        ILIKE_NOT_MATCH_END,
        /**
         * Ex: nome*
         */
        ILIKE_MATCH_START,
        /**
         * Ex: nome!*
         */
        ILIKE_NOT_MATCH_START,
        /**
         * Ex: null
         */
        IS_NULL,
        /**
         * Ex: not null
         */
        IS_NOT_NULL,
        /**
         * Ex: &lt;=100;&gt;10;!=50
         */
        MULTI
    }

    /**
     * 
     * @author Jurandir C. Goncalves
     * 
     * @version 1.0
     *
     */
    public enum MatchMode {
        ANYWHERE,
        EXACT,
        START,
        END
    }

    /**
     * 
     * @return
     */
    public T getObjBase();

    /**
     * 
     * @param field
     * @return
     */
    public CriteriaWhere<T> addWhereEqual(String field);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaWhere<T> addWhereIn(String field, E... values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    public <E> CriteriaWhere<T> addWhereIn(String field, List<E> values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaWhere<T> addWhereNotIn(String field, E... values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    public <E> CriteriaWhere<T> addWhereNotIn(String field, List<E> values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    public <E> CriteriaWhere<T> addWhereEqual(String field, List<E> values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    public <E> CriteriaWhere<T> addWhereNotEqual(String field, List<E> values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaWhere<T> addWhereEqual(String field, E... values);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public <E> CriteriaWhere<T> addWhereEqual(String field, E value);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaWhere<T> addWhereNotEqual(String field, E... values);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public <E> CriteriaWhere<T> addWhereNotEqual(String field, E value);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhere<T> addWhereBetween(String field, Integer startValue, Integer endValue);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhere<T> addWhereBetween(String field, Short startValue, Short endValue);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhere<T> addWhereBetween(String field, Long startValue, Long endValue);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    public CriteriaWhere<T> addWhereLessThanField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    public CriteriaWhere<T> addWhereGreaterThanField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    public CriteriaWhere<T> addWhereLessThanOrEqualToField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    public CriteriaWhere<T> addWhereGreaterThanOrEqualToField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    public CriteriaWhere<T> addWhereEqualField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    public CriteriaWhere<T> addWhereNotEqualField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhere<T> addWhereBetween(String field, Date startValue, Date endValue);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhere<T> addWhereBetween(String field, LocalDate startValue, LocalDate endValue);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhere<T> addWhereBetween(String field, LocalDateTime startValue, LocalDateTime endValue);

    /**
     * 
     * @param field
     * @return
     */
    public CriteriaWhere<T> addWhereGreaterThan(String field);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public <E> CriteriaWhere<T> addWhereGreaterThan(String field, Date value);

    /**
     * 
     * @param <E>
     * @param field
     * @param value
     * @return
     */
    public <E> CriteriaWhere<T> addWhereGreaterThan(String field, LocalDate value);

    /**
     * 
     * @param <E>
     * @param field
     * @param value
     * @return
     */
    public <E> CriteriaWhere<T> addWhereGreaterThan(String field, LocalDateTime value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public <E> CriteriaWhere<T> addWhereGreaterThan(String field, Number value);

    /**
     * 
     * @param field
     * @return
     */
    public CriteriaWhere<T> addWhereGreaterThanOrEqualTo(String field);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhereGreaterThanOrEqualTo(String field, Date value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhereGreaterThanOrEqualTo(String field, LocalDate value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhereGreaterThanOrEqualTo(String field, LocalDateTime value);

    /**
     *
     * @param field
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhereGreaterThanOrEqualTo(String field, Number value);

    /**
     * 
     * @param field
     * @return
     */
    public CriteriaWhere<T> addWhereIn(String field);

    /**
     * 
     * @param field
     * @return
     */
    public CriteriaWhere<T> addWhereIsNotNull(String field);

    /**
     * 
     * @param field
     * @return
     */
    public CriteriaWhere<T> addWhereIsNull(String field);

    /**
     * 
     * @param field
     * @return
     */
    public CriteriaWhere<T> addWhereLessThan(String field);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhereLessThan(String field, Date value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhereLessThan(String field, LocalDate value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhereLessThan(String field, LocalDateTime value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhereLessThan(String field, Number value);

    /**
     * 
     * @param field
     * @return
     */
    public CriteriaWhere<T> addWhereLessThanOrEqualTo(String field);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhereLessThanOrEqualTo(String field, Date value);
    
    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhereLessThanOrEqualTo(String field, LocalDate value);
    
    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhereLessThanOrEqualTo(String field, LocalDateTime value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhereLessThanOrEqualTo(String field, Number value);

    /**
     * @param field
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhere<T> addWhereLike(String field, MatchMode matchMode);

    /**
     * @param field
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhere<T> addWhereNotLike(String field, MatchMode matchMode);

    /**
     * @param field
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhere<T> addWhereNotILike(String field, MatchMode matchMode);

    /**
     * @param field
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhere<T> addWhereLike(String field, String value, MatchMode matchMode);

    /**
     * @param field
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhere<T> addWhereNotLike(String field, String value, MatchMode matchMode);

    /**
     * @param field
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhere<T> addWhereNotILike(String field, String value, MatchMode matchMode);

    /**
     * @param field
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhere<T> addWhereILike(String field, MatchMode matchMode);

    /**
     * @param field
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhere<T> addWhereILike(String field, String value, MatchMode matchMode);

    /**
     * 
     * @param field
     * @return
     */
    public CriteriaWhere<T> addWhereNotEqual(String field);

    /**
     * 
     * @param field
     * @return
     */
    public CriteriaWhere<T> addWhereNotIn(String field);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhere<T> addWhereBetween(String field, Double startValue, Double endValue);

    /**
     * Verifica a condicao recebida junto com o valor
     * 
     * Ex: <b>equal</b> =10<br>
     * <b>not equal</b> !=10<br>
     * <b>less than</b> &lt;10<br>
     * <b>less than or equal</b> &lt;=10<br>
     * <b>greater than</b> &gt;10<br>
     * <b>greater than or equal to</b> &gt;=10<br>
     * <b>in</b> (10,15,20)<br>
     * <b>not in</b> !(10,15,20)<br>
     * <b>is null</b> null<br>
     * <b>not equal</b> not null<br>
     * <b>between</b> (10 &amp; 20)<br>
     * <b>multi</b> &lt;=100;&gt;10;!=50<br>
     * <b>like</b> =%nome<br>
     * <b>not like</b> !%nome <b>like after</b> nome%<br>
     * <b>like before</b> %nome<br>
     * <b>like both</b> %nome%<br>
     * <b>ilike</b> =*nome<br>
     * <b>not ilike</b> !*nome <b>ilike after</b> nome*<br>
     * <b>ilike before</b> *nome<br>
     * <b>ilike both</b> *nome*<br>
     * 
     * Obs: com exececao das regex de like e ilike as demais instrucoes aceitam
     * valores com formato data/hora. Ex: &lt;=2000-10-20
     * 
     * @param field
     * @param fieldType
     * @param value             regex com valor. Ex: &gt;10
     * @param regexToAnalyse    condicoes (Where) para analisar para analisar, se
     *                          null verifica todas.
     * @param defaultIfNotMatch padrao caso nao encontre referencia
     * @return
     * @throws ApplicationRuntimeException
     */
    public CriteriaWhere<T> addWhereRegex(
        String field,
        Class<?> fieldType,
        String value,
        RegexWhere[] regexToAnalyse,
        RegexWhere defaultIfNotMatch) throws ApplicationRuntimeException;
}

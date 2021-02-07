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
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute;

import br.com.jgon.canary.exception.ApplicationRuntimeException;

/**
 * Define os filtros da consulta
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 * @param <T> Entity
 */
public interface CriteriaFilterMetamodel<T> extends CriteriaFilter<T> {

    /**
     * 
     * @param returnType
     * @return
     * @throws ApplicationRuntimeException
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelect(Class<?> returnType) throws ApplicationRuntimeException;

    /**
     * 
     * @param returnType
     * @param fields
     * @return
     * @throws ApplicationRuntimeException
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelect(Class<?> returnType, List<String> fields) throws ApplicationRuntimeException;

    /**
     * 
     * @param returnType
     * @param fields
     * @return
     * @throws ApplicationRuntimeException
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelect(Class<?> returnType, String... fields) throws ApplicationRuntimeException;

    /**
     * 
     * @param field
     * @param alias
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelect(String field, String alias);

    /**
     * 
     * @param fieldAlias
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelect(Map<String, String> fieldAlias);

    /**
     * 
     * @param field
     * @param alias
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelectCount(String field, String alias);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelectCount(String field);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelectUpper(String field);

    /**
     * 
     * @param field
     * @param alias
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelectUpper(String field, String alias);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelectLower(String field);

    /**
     * 
     * @param field
     * @param alias
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelectLower(String field, String alias);

    /**
     * 
     * @param field
     * @param alias
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelectMax(String field, String alias);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelectMax(String field);

    /**
     * 
     * @param field
     * @param alias
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelectMin(String field, String alias);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelectMin(String field);

    /**
     * 
     * @param field
     * @param alias
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelectSum(String field, String alias);

    /**
     * 
     * @param field
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectSum(String field);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelect(String field);

    /**
     * 
     * @param fields
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelect(String[] fields);

    /**
     * 
     * @param fields
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addSelect(List<String> fields);

    /**
     * 
     * @param returnType
     * @param order
     * @return
     * @throws ApplicationRuntimeException
     */
    @Override
    public CriteriaFilterMetamodel<T> addOrder(Class<?> returnType, String... order) throws ApplicationRuntimeException;

    /**
     * 
     * @param returnType
     * @param order
     * @return
     * @throws ApplicationRuntimeException
     */
    @Override
    public CriteriaFilterMetamodel<T> addOrder(Class<?> returnType, List<String> order) throws ApplicationRuntimeException;

    /**
     *
     * @param order
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addOrder(List<String> order);

    /**
     * 
     * @param order
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addOrder(String... order);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addOrderAsc(String field);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addOrderDesc(String field);

    /**
     * 
     * @param fields
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addOrderAsc(List<String> fields);

    /**
     * 
     * @param fields
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addOrderDesc(List<String> fields);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereEqual(String field);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereIn(String field, E... values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereIn(String field, List<E> values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereNotIn(String field, E... values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereNotIn(String field, List<E> values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(String field, List<E> values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(String field, List<E> values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(String field, E... values);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(String field, E value);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(String field, E... values);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(String field, E value);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(String field, Integer startValue, Integer endValue);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(String field, Short startValue, Short endValue);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(String field, Long startValue, Long endValue);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThanField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereEqualField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereNotEqualField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(String field, Date startValue, Date endValue);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(String field, LocalDate startValue, LocalDate endValue);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(String field, LocalDateTime startValue, LocalDateTime endValue);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(String field);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereGreaterThan(String field, Date value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereGreaterThan(String field, Number value);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(String field);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(String field, Date value);

    /**
     *
     * @param field
     * @param value
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(String field, Number value);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereIn(String field);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereIsNotNull(String field);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereIsNull(String field);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(String field);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(String field, Date value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(String field, Number value);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(String field);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(String field, Date value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(String field, Number value);

    /**
     * @param field
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereLike(String field, MatchMode matchMode);

    /**
     * @param field
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereNotLike(String field, MatchMode matchMode);

    /**
     * @param field
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereNotILike(String field, MatchMode matchMode);

    /**
     * @param field
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereLike(String field, String value, MatchMode matchMode);

    /**
     * @param field
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereNotLike(String field, String value, MatchMode matchMode);

    /**
     * @param field
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereNotILike(String field, String value, MatchMode matchMode);

    /**
     * @param field
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereILike(String field, MatchMode matchMode);

    /**
     * @param field
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereILike(String field, String value, MatchMode matchMode);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereNotEqual(String field);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereNotIn(String field);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(String field, Double startValue, Double endValue);

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
    @Override
    public CriteriaFilterMetamodel<T> addWhereRegex(
        String field,
        Class<?> fieldType,
        String value,
        RegexWhere[] regexToAnalyse,
        RegexWhere defaultIfNotMatch) throws ApplicationRuntimeException;

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addGroupBy(String field);

    /**
     * 
     * @param fields
     * @return
     */
    @Override
    public CriteriaFilter<T> addGroupBy(List<String> fields);

    /**
     * 
     * @param field
     * @param joinType
     * @param fetch
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addJoin(String field, JoinType joinType, boolean fetch);

    /**
     * fetch: false
     * 
     * @param field
     * @param joinType
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addJoin(String field, JoinType joinType);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterMetamodel<T> addJoin(String field);

    /**
     * 
     */
    public CriteriaFilter<T> addJoin(String field, JoinType joinType, boolean fetch, boolean force);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereILike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereILike(ComplexAttribute attribute, String value, MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereILike(Attribute<T, String> attribute, MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereILike(ComplexAttribute attribute, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotILike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotILike(ComplexAttribute attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotLike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotLike(ComplexAttribute attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLike(ComplexAttribute attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotILike(Attribute<T, String> attribute, MatchMode matchMode);

    /**
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotILike(ComplexAttribute attribute, MatchMode matchMode);

    /**
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotLike(Attribute<T, String> attribute, MatchMode matchMode);

    /**
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotLike(ComplexAttribute attribute, MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLike(Attribute<T, String> attribute, MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLike(ComplexAttribute attribute, MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelect(Attribute<T, ?> attribute, String alias);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelect(ComplexAttribute attribute, String alias);

    /**
     * 
     * @param attributes
     * @return
     */
    @SuppressWarnings("unchecked")
    public CriteriaFilterMetamodel<T> addSelect(Attribute<T, ?>... attributes);

    /**
     * 
     * @param attributes
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelect(ComplexAttribute... attributes);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectCount(Attribute<T, ?> attribute, String alias);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectCount(ComplexAttribute attribute, String alias);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectCount(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectCount(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectUpper(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectUpper(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectUpper(Attribute<T, ?> attribute, String alias);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectUpper(ComplexAttribute attribute, String alias);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectLower(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectLower(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectLower(Attribute<T, ?> attribute, String alias);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectLower(ComplexAttribute attribute, String alias);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectMax(Attribute<T, ?> attribute, String alias);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectMax(ComplexAttribute attribute, String alias);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectMax(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectMax(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectMin(Attribute<T, ?> attribute, String alias);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectMin(ComplexAttribute attribute, String alias);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectMin(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectMin(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectSum(Attribute<T, ?> attribute, String alias);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectSum(ComplexAttribute attribute, String alias);

    /**
     *
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectSum(Attribute<T, ?> attribute);

    /**
     *
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectSum(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelect(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelect(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addOrderAsc(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addOrderAsc(ComplexAttribute attribute);

    /**
     * 
     * @param fields
     * @return
     */
    @SuppressWarnings("unchecked")
    public CriteriaFilterMetamodel<T> addOrderAsc(Attribute<T, ?>... fields);

    /**
     * 
     * @param fields
     * @return
     */
    @SuppressWarnings("unchecked")
    public CriteriaFilterMetamodel<T> addOrderDesc(Attribute<T, ?>... fields);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addOrderDesc(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addOrderDesc(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereEqual(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereIn(Attribute<T, E> attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereIn(ComplexAttribute attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereIn(Attribute<T, E> attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereIn(ComplexAttribute attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereNotIn(Attribute<T, E> attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereNotIn(ComplexAttribute attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereNotIn(Attribute<T, E> attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereNotIn(ComplexAttribute attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(Attribute<T, E> attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(Attribute<T, E> attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<T, E> attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<T, E> attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<T, Integer> attribute, Integer startValue, Integer endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Integer startValue, Integer endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<T, Short> attribute, Short startValue, Short endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Short startValue, Short endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<T, Long> attribute, Long startValue, Long endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Long startValue, Long endValue);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereLessThanField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThanField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThanField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereGreaterThanField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThanField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThanField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereEqualField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereEqualField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereEqualField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqualField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotEqualField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotEqualField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<T, Date> attribute, Date startValue, Date endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Date startValue, Date endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<T, LocalDate> attribute, LocalDate startValue, LocalDate endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, LocalDate startValue, LocalDate endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereBetween(
        Attribute<T, LocalDateTime> attribute,
        LocalDateTime startValue,
        LocalDateTime endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, LocalDateTime startValue, LocalDateTime endValue);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereIsNotNull(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereIsNotNull(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereIsNull(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereIsNull(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereIn(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereIn(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThan(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotIn(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotIn(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addGroupBy(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addGroupBy(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param joinType
     * @param fetch
     * @return
     */
    public CriteriaFilterMetamodel<T> addJoin(Attribute<T, ?> attribute, JoinType joinType, boolean fetch);

    /**
     * 
     * @param attribute
     * @param joinType
     * @param fetch
     * @return
     */
    public CriteriaFilterMetamodel<T> addJoin(ComplexAttribute attribute, JoinType joinType, boolean fetch);

    /**
     * 
     * @param attribute
     * @param joinType
     * @return
     */
    public CriteriaFilterMetamodel<T> addJoin(Attribute<T, ?> attribute, JoinType joinType);

    /**
     * 
     * @param attribute
     * @param joinType
     * @return
     */
    public CriteriaFilterMetamodel<T> addJoin(ComplexAttribute attribute, JoinType joinType, boolean fetch, boolean force);

    /**
     * 
     * @param attribute
     * @param joinType
     * @param fetch
     * @param force
     * @return
     */
    public CriteriaFilter<T> addJoin(Attribute<T, ?> attribute, JoinType joinType, boolean fetch, boolean force);

    /**
     * 
     * @param attribute
     * @param joinType
     * @return
     */
    public CriteriaFilterMetamodel<T> addJoin(ComplexAttribute attribute, JoinType joinType);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addJoin(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addJoin(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(Attribute<T, E> attribute, E value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute, E value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<T, E> attribute, E value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, E value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<T, Date> attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<T, Number> attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<T, Date> attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<T, Number> attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<T, Date> attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThan(ComplexAttribute attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<T, Number> attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThan(ComplexAttribute attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<T, Date> attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<T, Number> attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Number value);

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
     * @param attribute
     * @param value             regex com valor. Ex: &gt;10
     * @param regexToAnalyse    condicoes (Where) para analisar para analisar, se
     *                          null verifica todas.
     * @param defaultIfNotMatch padrao caso nao encontre referencia
     * @return
     * @throws ApplicationRuntimeException
     */
    public CriteriaFilterMetamodel<T> addWhereRegex(
        Attribute<T, ?> attribute,
        String value,
        RegexWhere[] regexToAnalyse,
        RegexWhere defaultIfNotMatch) throws ApplicationRuntimeException;

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
     * @param attribute
     * @param value             regex com valor. Ex: &gt;10
     * @param regexToAnalyse    condicoes (Where) para analisar para analisar, se
     *                          null verifica todas.
     * @param defaultIfNotMatch padrao caso nao encontre referencia
     * @return
     * @throws ApplicationRuntimeException
     */
    public CriteriaFilterMetamodel<T> addWhereRegex(
        ComplexAttribute attribute,
        String value,
        RegexWhere[] regexToAnalyse,
        RegexWhere defaultIfNotMatch) throws ApplicationRuntimeException;

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereNotEqualField(Attribute<T, ?> attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereEqualField(Attribute<T, ?> attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @author Jurandir C. Gonçalves
     * @since 19/06/2020
     *
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<T, LocalTime> attribute, LocalTime startValue, LocalTime endValue);

}

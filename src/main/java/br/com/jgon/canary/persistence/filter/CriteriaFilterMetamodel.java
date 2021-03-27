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

import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
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
public interface CriteriaFilterMetamodel<T> extends CriteriaFilter<T>, CriteriaWhereMetamodel<T> {


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
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectAvg(Attribute<T, ?> attribute, String alias);

    /**
     * 
     * @param attribute
     * @param alias
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectAvg(ComplexAttribute attribute, String alias);

    /**
     *
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectAvg(Attribute<T, ?> attribute);

    /**
     *
     * @param attribute
     * @return
     */
    public CriteriaFilterMetamodel<T> addSelectAvg(ComplexAttribute attribute);

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
    public CriteriaFilterMetamodel<T> addJoin(Attribute<T, ?> attribute, JoinType joinType, boolean fetch, boolean force);

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

    @Override
    public CriteriaFilterMetamodel<T> addSelect(Class<?> returnType) throws ApplicationRuntimeException;

    @Override
    public CriteriaFilterMetamodel<T> addSelect(Class<?> returnType, List<String> fields) throws ApplicationRuntimeException;

    @Override
    public CriteriaFilterMetamodel<T> addSelect(Class<?> returnType, String... fields) throws ApplicationRuntimeException;

    @Override
    public CriteriaFilterMetamodel<T> addSelect(String field, String alias);

    @Override
    public CriteriaFilterMetamodel<T> addSelect(Map<String, String> fieldAlias);

    @Override
    public CriteriaFilterMetamodel<T> addSelectCount(String field, String alias);

    @Override
    public CriteriaFilterMetamodel<T> addSelectCount(String field);

    @Override
    public CriteriaFilterMetamodel<T> addSelectUpper(String field);

    @Override
    public CriteriaFilterMetamodel<T> addSelectUpper(String field, String alias);

    @Override
    public CriteriaFilterMetamodel<T> addSelectLower(String field);

    @Override
    public CriteriaFilterMetamodel<T> addSelectLower(String field, String alias);

    @Override
    public CriteriaFilterMetamodel<T> addSelectMax(String field, String alias);

    @Override
    public CriteriaFilterMetamodel<T> addSelectMax(String field);

    @Override
    public CriteriaFilterMetamodel<T> addSelectMin(String field, String alias);

    @Override
    public CriteriaFilterMetamodel<T> addSelectMin(String field);

    @Override
    public CriteriaFilterMetamodel<T> addSelectSum(String field, String alias);

    @Override
    public CriteriaFilterMetamodel<T> addSelectSum(String field);

    @Override
    public CriteriaFilterMetamodel<T> addSelectAvg(String field, String alias);

    @Override
    public CriteriaFilterMetamodel<T> addSelectAvg(String field);

    @Override
    public CriteriaFilterMetamodel<T> addSelect(String field);

    @Override
    public CriteriaFilterMetamodel<T> addSelect(String[] fields);

    @Override
    public CriteriaFilterMetamodel<T> addSelect(List<String> fields);

    @Override
    public CriteriaFilterMetamodel<T> addOrder(Class<?> returnType, String... order) throws ApplicationRuntimeException;

    @Override
    public CriteriaFilterMetamodel<T> addOrder(Class<?> returnType, List<String> order) throws ApplicationRuntimeException;

    @Override
    public CriteriaFilterMetamodel<T> addOrder(List<String> order);

    @Override
    public CriteriaFilterMetamodel<T> addOrder(String... order);

    @Override
    public CriteriaFilterMetamodel<T> addOrderAsc(String field);

    @Override
    public CriteriaFilterMetamodel<T> addOrderDesc(String field);

    @Override
    public CriteriaFilterMetamodel<T> addOrderAsc(List<String> fields);

    @Override
    public CriteriaFilterMetamodel<T> addOrderDesc(List<String> fields);

    @Override
    public CriteriaFilterMetamodel<T> addGroupBy(String field);

    @Override
    public CriteriaFilterMetamodel<T> addGroupBy(List<String> fields);

    @Override
    public CriteriaFilterMetamodel<T> addJoin(String field, JoinType joinType, boolean fetch);

    @Override
    public CriteriaFilterMetamodel<T> addJoin(String field, JoinType joinType);

    @Override
    public CriteriaFilterMetamodel<T> addJoin(String field);

    @Override
    public CriteriaFilterMetamodel<T> addJoin(String field, JoinType joinType, boolean fetch, boolean force);

    @Override
    public CriteriaFilterMetamodel<T> addWhereEqual(String field);

    @Override
    public CriteriaFilterMetamodel<T> addWhere(String field, Predicate value);

    @Override
    public CriteriaFilterMetamodel<T> addWhere(Attribute<T, ?> attribute, Predicate value);

    @Override
    public CriteriaFilterMetamodel<T> addWhere(ComplexAttribute attribute, Predicate value);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereIn(String field, E... values);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereIn(String field, List<E> values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereNotIn(String field, E... values);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereNotIn(String field, List<E> values);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(String field, List<E> values);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(String field, List<E> values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(String field, E... values);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(String field, E value);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(String field, E... values);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(String field, E value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(String field, Integer startValue, Integer endValue);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(String field, Short startValue, Short endValue);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(String field, Long startValue, Long endValue);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanField(String field, String anotherField);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanField(String field, String anotherField);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(String field, String anotherField);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(String field, String anotherField);

    @Override
    public CriteriaFilterMetamodel<T> addWhereEqualField(String field, String anotherField);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotEqualField(String field, String anotherField);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(String field, Date startValue, Date endValue);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(String field, Temporal startValue, Temporal endValue);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(String field);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereGreaterThan(String field, Date value);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereGreaterThan(String field, Temporal value);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereGreaterThan(String field, Number value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(String field);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(String field, Date value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(String field, Temporal value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(String field, Number value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereIn(String field);

    @Override
    public CriteriaFilterMetamodel<T> addWhereIsNotNull(String field);

    @Override
    public CriteriaFilterMetamodel<T> addWhereIsNull(String field);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(String field);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(String field, Date value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(String field, Temporal value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(String field, Number value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(String field);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(String field, Date value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(String field, Temporal value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(String field, Number value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLike(String field, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotLike(String field, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotILike(String field, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLike(String field, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotLike(String field, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotILike(String field, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereILike(String field, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereILike(String field, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotEqual(String field);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotIn(String field);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(String field, Double startValue, Double endValue);

    @Override
    public CriteriaFilterMetamodel<T> addWhereRegex(
        String field,
        Class<?> fieldType,
        String value,
        RegexWhere[] regexToAnalyse,
        RegexWhere defaultIfNotMatch) throws ApplicationRuntimeException;

    @Override
    public CriteriaFilterMetamodel<T> addWhereILike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereILike(ComplexAttribute attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereILike(Attribute<T, String> attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereILike(ComplexAttribute attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotILike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotILike(ComplexAttribute attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotLike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotLike(ComplexAttribute attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLike(ComplexAttribute attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotILike(Attribute<T, String> attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotILike(ComplexAttribute attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotLike(Attribute<T, String> attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotLike(ComplexAttribute attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLike(Attribute<T, String> attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLike(ComplexAttribute attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterMetamodel<T> addWhereEqual(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereIn(Attribute<T, E> attribute, List<E> values);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereIn(ComplexAttribute attribute, List<E> values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereIn(Attribute<T, E> attribute, E... values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereIn(ComplexAttribute attribute, E... values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereNotIn(Attribute<T, E> attribute, E... values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereNotIn(ComplexAttribute attribute, E... values);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereNotIn(Attribute<T, E> attribute, List<E> values);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereNotIn(ComplexAttribute attribute, List<E> values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(Attribute<T, E> attribute, E... values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute, E... values);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(Attribute<T, E> attribute, List<E> values);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute, List<E> values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<T, E> attribute, E... values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, E... values);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<T, E> attribute, List<E> values);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, List<E> values);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<T, Integer> attribute, Integer startValue, Integer endValue);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Integer startValue, Integer endValue);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<T, Short> attribute, Short startValue, Short endValue);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Short startValue, Short endValue);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<T, Long> attribute, Long startValue, Long endValue);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Long startValue, Long endValue);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereLessThanField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereGreaterThanField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereEqualField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereEqualField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereEqualField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqualField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotEqualField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotEqualField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<T, Date> attribute, Date startValue, Date endValue);

    @Override
    public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Date startValue, Date endValue);

    @Override
    public <E extends Temporal> CriteriaFilterMetamodel<T> addWhereBetween(Attribute<T, E> attribute, Temporal startValue, Temporal endValue);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute, Temporal value);

    @Override
    public <E extends Temporal> CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<T, E> attribute, Temporal value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute, Temporal value);

    @Override
    public <E extends Temporal> CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<T, E> attribute, Temporal value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereIsNotNull(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereIsNotNull(ComplexAttribute attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereIsNull(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereIsNull(ComplexAttribute attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereIn(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereIn(ComplexAttribute attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(ComplexAttribute attribute);

    @Override
    public <E extends Temporal> CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<T, E> attribute, Temporal value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(ComplexAttribute attribute, Temporal value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<T, ?> attribute);

    @Override
    public <E extends Temporal> CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<T, E> attribute, Temporal value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Temporal value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotIn(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereNotIn(ComplexAttribute attribute);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(Attribute<T, E> attribute, E value);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute, E value);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<T, E> attribute, E value);

    @Override
    public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, E value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<T, Date> attribute, Date value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute, Date value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<T, Number> attribute, Number value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute, Number value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<T, Date> attribute, Date value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute, Date value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<T, Number> attribute, Number value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute, Number value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<T, Date> attribute, Date value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(ComplexAttribute attribute, Date value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<T, Number> attribute, Number value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThan(ComplexAttribute attribute, Number value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<T, Date> attribute, Date value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Date value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<T, Number> attribute, Number value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Number value);

    @Override
    public CriteriaFilterMetamodel<T> addWhereRegex(
        Attribute<T, ?> attribute,
        String value,
        RegexWhere[] regexToAnalyse,
        RegexWhere defaultIfNotMatch) throws ApplicationRuntimeException;

        @Override
    public CriteriaFilterMetamodel<T> addWhereRegex(
        ComplexAttribute attribute,
        String value,
        RegexWhere[] regexToAnalyse,
        RegexWhere defaultIfNotMatch) throws ApplicationRuntimeException;

        @Override
    public CriteriaFilterMetamodel<T> addWhereNotEqualField(Attribute<T, ?> attribute, ComplexAttribute anotherAttribute);

    @Override
    public CriteriaFilterMetamodel<T> addWhereEqualField(Attribute<T, ?> attribute, ComplexAttribute anotherAttribute);

}

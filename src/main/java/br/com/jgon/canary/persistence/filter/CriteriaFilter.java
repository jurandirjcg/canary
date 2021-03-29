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
public interface CriteriaFilter<T> extends CriteriaWhere<T> {

    /**
     * 
     * @param returnType type
     * @return CriteriaFilter
     * @throws ApplicationRuntimeException
     */
    public CriteriaFilter<T> addSelect(Class<?> returnType) throws ApplicationRuntimeException;

    /**
     * 
     * @param returnType type
     * @param fields {@link List}
     * @return {@link CriteriaFilter}
     * @throws ApplicationRuntimeException
     */
    public CriteriaFilter<T> addSelect(Class<?> returnType, List<String> fields) throws ApplicationRuntimeException;

    /**
     * 
     * @param returnType type
     * @param fields fields
     * @return {@link CriteriaFilter}
     * @throws ApplicationRuntimeException
     */
    public CriteriaFilter<T> addSelect(Class<?> returnType, String... fields) throws ApplicationRuntimeException;

    /**
     * 
     * @param field
     * @param alias
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelect(String field, String alias);

    /**
     * 
     * @param fieldAlias
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelect(Map<String, String> fieldAlias);

    /**
     * 
     * @param field
     * @param alias
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectCount(String field, String alias);

    /**
     * 
     * @param field
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectCount(String field);

    /**
     * 
     * @param field
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectUpper(String field);

    /**
     * 
     * @param field
     * @param alias
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectUpper(String field, String alias);

    /**
     * 
     * @param field
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectLower(String field);

    /**
     * 
     * @param field
     * @param alias
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectLower(String field, String alias);

    /**
     * 
     * @param field
     * @param alias
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectMax(String field, String alias);

    /**
     * 
     * @param field
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectMax(String field);

    /**
     * 
     * @param field
     * @param alias
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectMin(String field, String alias);

    /**
     * 
     * @param field
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectMin(String field);

    /**
     * 
     * @param field
     * @param alias
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectSum(String field, String alias);

    /**
     * 
     * @param field
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectSum(String field);

    /**
     * 
     * @param field
     * @param alias
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectAvg(String field, String alias);

    /**
     * 
     * @param field
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelectAvg(String field);

    /**
     * 
     * @param field
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelect(String field);

    /**
     * 
     * @param fields
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelect(String[] fields);

    /**
     * 
     * @param fields
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addSelect(List<String> fields);

    /**
     * 
     * @param returnType
     * @param order
     * @return {@link CriteriaFilter}
     * @throws ApplicationRuntimeException
     */
    public CriteriaFilter<T> addOrder(Class<?> returnType, String... order) throws ApplicationRuntimeException;

    /**
     * 
     * @param returnType
     * @param order
     * @return {@link CriteriaFilter}
     * @throws ApplicationRuntimeException
     */
    public CriteriaFilter<T> addOrder(Class<?> returnType, List<String> order) throws ApplicationRuntimeException;

    /**
     * 
     * @param order
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addOrder(List<String> order);

    /**
     * 
     * @param order
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addOrder(String... order);

    /**
     * 
     * @param field
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addOrderAsc(String field);

    /**
     * 
     * @param field
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addOrderDesc(String field);

    /**
     * Ex: id:asc or id:desc - default :asc
     * 
     * @param fields
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addOrderAsc(List<String> fields);

    /**
     * Ex: id:asc or id:desc - default :asc
     * 
     * @param fields
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addOrderDesc(List<String> fields);

    /**
     * 
     * @param field
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addGroupBy(String field);

    /**
     * 
     * @param fields
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addGroupBy(List<String> fields);

    /**
     * 
     * @param field
     * @param joinType
     * @param fetch
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addJoin(String field, JoinType joinType, boolean fetch);

    /**
     * fetch: false
     * 
     * @param field
     * @param joinType
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addJoin(String field, JoinType joinType);

    /**
     * 
     * @param field
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addJoin(String field);

    /**
     * 
     * @since 24/06/2019
     * @param field
     * @param joinType
     * @param fetch
     * @param force
     * @return {@link CriteriaFilter}
     */
    public CriteriaFilter<T> addJoin(String field, JoinType joinType, boolean fetch, boolean force);

    @Override
    public CriteriaFilter<T> addWhere(String field, Predicate value);

    @Override
    public CriteriaFilter<T> addWhereEqual(String field);

    @SuppressWarnings("unchecked")
    @Override
    public <E> CriteriaFilter<T> addWhereIn(String field, E... values);

    @Override
    public <E> CriteriaFilter<T> addWhereIn(String field, List<E> values);

    @SuppressWarnings("unchecked")
    @Override
    public <E> CriteriaFilter<T> addWhereNotIn(String field, E... values);

    @Override
    public <E> CriteriaFilter<T> addWhereNotIn(String field, List<E> values);

    @Override
    public <E> CriteriaFilter<T> addWhereEqual(String field, List<E> values);

    @Override
    public <E> CriteriaFilter<T> addWhereNotEqual(String field, List<E> values);

    @SuppressWarnings("unchecked")
    @Override
    public <E> CriteriaFilter<T> addWhereEqual(String field, E... values);

    @Override
    public <E> CriteriaFilter<T> addWhereEqual(String field, E value);

    @SuppressWarnings("unchecked")
    @Override
    public <E> CriteriaFilter<T> addWhereNotEqual(String field, E... values);

    @Override
    public <E> CriteriaFilter<T> addWhereNotEqual(String field, E value);

    @Override
    public CriteriaFilter<T> addWhereBetween(String field, Integer startValue, Integer endValue);

    @Override
    public CriteriaFilter<T> addWhereBetween(String field, Short startValue, Short endValue);

    @Override
    public CriteriaFilter<T> addWhereBetween(String field, Long startValue, Long endValue);

    @Override
    public CriteriaFilter<T> addWhereLessThanField(String field, String anotherField);

    @Override
    public CriteriaFilter<T> addWhereGreaterThanField(String field, String anotherField);

    @Override
    public CriteriaFilter<T> addWhereLessThanOrEqualToField(String field, String anotherField);

    @Override
    public CriteriaFilter<T> addWhereGreaterThanOrEqualToField(String field, String anotherField);

    @Override
    public CriteriaFilter<T> addWhereEqualField(String field, String anotherField);

    @Override
    public CriteriaFilter<T> addWhereNotEqualField(String field, String anotherField);

    @Override
    public CriteriaFilter<T> addWhereBetween(String field, Date startValue, Date endValue);

    @Override
    public CriteriaFilter<T> addWhereBetween(String field, Temporal startValue, Temporal endValue);

    @Override
    public CriteriaFilter<T> addWhereGreaterThan(String field);

    @Override
    public <E> CriteriaFilter<T> addWhereGreaterThan(String field, Date value);

    @Override
    public <E> CriteriaFilter<T> addWhereGreaterThan(String field, Number value);

    @Override
    public CriteriaFilter<T> addWhereGreaterThanOrEqualTo(String field);

    @Override
    public CriteriaFilter<T> addWhereGreaterThanOrEqualTo(String field, Date value);
    
    @Override
    public CriteriaFilter<T> addWhereGreaterThanOrEqualTo(String field, Number value);

    @Override
    public CriteriaFilter<T> addWhereIn(String field);
    
    @Override
    public CriteriaFilter<T> addWhereIsNotNull(String field);

    @Override
    public CriteriaFilter<T> addWhereIsNull(String field);

    @Override
    public CriteriaFilter<T> addWhereLessThan(String field);

    @Override
    public CriteriaFilter<T> addWhereLessThan(String field, Date value);

    @Override
    public CriteriaFilter<T> addWhereLessThan(String field, Number value);

    @Override
    public CriteriaFilter<T> addWhereLessThanOrEqualTo(String field);

    @Override
    public CriteriaFilter<T> addWhereLessThanOrEqualTo(String field, Date value);

    @Override
    public CriteriaFilter<T> addWhereLessThanOrEqualTo(String field, Number value);

    @Override
    public CriteriaFilter<T> addWhereLike(String field, MatchMode matchMode);

    @Override
    public CriteriaFilter<T> addWhereNotLike(String field, MatchMode matchMode);

    @Override
    public CriteriaFilter<T> addWhereNotILike(String field, MatchMode matchMode);

    @Override
    public CriteriaFilter<T> addWhereLike(String field, String value, MatchMode matchMode);

    @Override
    public CriteriaFilter<T> addWhereNotLike(String field, String value, MatchMode matchMode);

    @Override
    public CriteriaFilter<T> addWhereNotILike(String field, String value, MatchMode matchMode);

    @Override
    public CriteriaFilter<T> addWhereILike(String field, MatchMode matchMode);

    @Override
    public CriteriaFilter<T> addWhereILike(String field, String value, MatchMode matchMode);

    @Override
    public CriteriaFilter<T> addWhereNotEqual(String field);

    @Override
    public CriteriaFilter<T> addWhereNotIn(String field);

    @Override
    public CriteriaFilter<T> addWhereBetween(String field, Double startValue, Double endValue);

    @Override
    public CriteriaFilter<T> addWhereRegex(
        String field,
        Class<?> fieldType,
        String value,
        RegexWhere[] regexToAnalyse,
        RegexWhere defaultIfNotMatch) throws ApplicationRuntimeException;

}

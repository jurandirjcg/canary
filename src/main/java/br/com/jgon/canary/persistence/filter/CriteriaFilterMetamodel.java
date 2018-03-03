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
import java.util.Map;

import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute;

import br.com.jgon.canary.exception.ApplicationException;

/**
 * Define os filtros da consulta
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 * @param <T>
 */
public interface CriteriaFilterMetamodel<T> extends CriteriaFilter<T>{
	/**
	 * 
	 * @return
	 */
	public T getObjBase();
	
	/**
	 * 
	 * @param returnType
	 * @return
	 * @throws ApplicationException 
	 */
	@Override
	public CriteriaFilterMetamodel<T> addSelect(Class<?> returnType) throws ApplicationException;
	/**
	 * 
	 * @param returnType
	 * @param fields
	 * @return
	 * @throws ApplicationException 
	 */
	@Override
	public CriteriaFilterMetamodel<T> addSelect(Class<?> returnType, List<String> fields) throws ApplicationException;
	
	/**
	 * 
	 * @param returnType
	 * @param fields
	 * @return
	 * @throws ApplicationException 
	 */
	@Override
	public CriteriaFilterMetamodel<T> addSelect(Class<?> returnType, String... fields) throws ApplicationException;
		
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
	 * @throws ApplicationException 
	 */
	@Override
	public CriteriaFilterMetamodel<T> addOrder(Class<?> returnType, String... order) throws ApplicationException;
	/**
	 * 
	 * @param returnType
	 * @param order
	 * @return
	 * @throws ApplicationException 
	 */
	@Override
	public CriteriaFilterMetamodel<T> addOrder(Class<?> returnType, List<String> order) throws ApplicationException;
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
	public CriteriaFilterMetamodel<T> addOrderDesc(String field);
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
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	@Override
	public CriteriaFilterMetamodel<T> addWhereLike(String field, String value, MatchMode matchMode);
	/**
	 * @param field
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotLike(String field, String value, MatchMode matchMode);
	/**
	 * @param field
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotILike(String field, String value, MatchMode matchMode);
	/**
	 * @param field
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	@Override
	public CriteriaFilterMetamodel<T> addWhereILike(String field, MatchMode matchMode);
	/**
	 * @param field
	 * @param value
	 * @param matchMode  {@link MatchMode}
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
	 * <b>not like</b> !%nome
	 * <b>like after</b> nome%<br>
	 * <b>like before</b> %nome<br>
	 * <b>like both</b> %nome%<br>
	 * <b>ilike</b> =*nome<br>
	 * <b>not ilike</b> !*nome
	 * <b>ilike after</b> nome*<br>
	 * <b>ilike before</b> *nome<br>
	 * <b>ilike both</b> *nome*<br>
	 * 
	 * Obs: com exececao das regex de like e ilike as demais instrucoes aceitam valores com formato data/hora. Ex: &lt;=2000-10-20
	 * 
	 * @param field
	 * @param fieldType
	 * @param value regex com valor. Ex: &gt;10 
	 * @param regexToAnalyse condicoes (Where) para analisar  para analisar, se null verifica todas.
	 * @param defaultIfNotMatch padrao caso nao encontre referencia
	 * @return
	 * @throws ApplicationException
	 */
	@Override
	public CriteriaFilterMetamodel<T> addWhereRegex(String field, Class<?> fieldType, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException;
	/**
	 * 
	 * @param field
	 * @return
	 */
	@Override
	public CriteriaFilterMetamodel<T> addGroupBy(String field);
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
	 * @param attribute
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereILike(Attribute<?, ?> attribute, String value, MatchMode matchMode);
	/**
	 * @param attribute
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereILike(ComplexAttribute attribute, String value, MatchMode matchMode);
	/**
	 * 
	 * @param attribute
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereILike(Attribute<?, ?> attribute, MatchMode matchMode);
	/**
	 * 
	 * @param attribute
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereILike(ComplexAttribute attribute, MatchMode matchMode);
	/**
	 * @param attribute
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereNotILike(Attribute<?, ?> attribute, String value, MatchMode matchMode);
	/**
	 * @param attribute
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereNotILike(ComplexAttribute attribute, String value, MatchMode matchMode);
	/**
	 * @param attribute
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereNotLike(Attribute<?, ?> attribute, String value, MatchMode matchMode);
	/**
	 * @param attribute
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereNotLike(ComplexAttribute attribute, String value, MatchMode matchMode);
	/**
	 * @param attribute
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereLike(Attribute<?, ?> attribute, String value, MatchMode matchMode);
	/**
	 * @param attribute
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereLike(ComplexAttribute attribute, String value, MatchMode matchMode);
	/**
	 * @param attribute
	 * @param matchMode {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereNotILike(Attribute<?, ?> attribute, MatchMode matchMode);
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
	public CriteriaFilterMetamodel<T> addWhereNotLike(Attribute<?, ?> attribute, MatchMode matchMode);
	/**
	 * @param attribute
	 * @param matchMode {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereNotLike(ComplexAttribute attribute, MatchMode matchMode);
	/**
	 * 
	 * @param attribute
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereLike(Attribute<?, ?> attribute, MatchMode matchMode);
	/**
	 * 
	 * @param attribute
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereLike(ComplexAttribute attribute, MatchMode matchMode);
	/**
	 * 
	 * @param attribute
	 * @param alias
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addSelect(Attribute<?, ?> attribute, String alias);
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
	public CriteriaFilterMetamodel<T> addSelect(Attribute<?, ?>... attributes);
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
	public CriteriaFilterMetamodel<T> addSelectCount(Attribute<?, ?> attribute, String alias);
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
	public CriteriaFilterMetamodel<T> addSelectCount(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addSelectUpper(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addSelectUpper(Attribute<?, ?> attribute, String alias);
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
	public CriteriaFilterMetamodel<T> addSelectLower(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addSelectLower(Attribute<?, ?> attribute, String alias);
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
	public CriteriaFilterMetamodel<T> addSelectMax(Attribute<?, ?> attribute, String alias);
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
	public CriteriaFilterMetamodel<T> addSelectMax(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addSelectMin(Attribute<?, ?> attribute, String alias);
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
	public CriteriaFilterMetamodel<T> addSelectMin(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addSelectSum(Attribute<?, ?> attribute, String alias);
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
	public CriteriaFilterMetamodel<T> addSelectSum(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addSelect(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addOrderAsc(Attribute<?, ?> attribute);
	/**
	 * 
	 * @param attribute
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addOrderAsc(ComplexAttribute attribute);
	/**
	 * 
	 * @param attribute
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addOrderDesc(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addWhereEqual(Attribute<?, ?> attribute);
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
	public <E> CriteriaFilterMetamodel<T> addWhereIn(Attribute<?, ?> attribute, List<E> values);
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
	public <E> CriteriaFilterMetamodel<T> addWhereIn(Attribute<?, ?> attribute, E... values);
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
	public <E> CriteriaFilterMetamodel<T> addWhereNotIn(Attribute<?, ?> attribute, E... values);
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
	public <E> CriteriaFilterMetamodel<T> addWhereNotIn(Attribute<?, ?> attribute, List<E> values);
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
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(Attribute<?, ?> attribute, E... values);
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
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(Attribute<?, ?> attribute, List<E> values);
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
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<?, ?> attribute, E... values);
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
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<?, ?> attribute, List<E> values);
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
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, Integer startValue, Integer endValue);
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
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, Short startValue, Short endValue);
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
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, Long startValue, Long endValue);
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
	public CriteriaFilterMetamodel<T> addWhereLessThanField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute);
	/**
	 * 
	 * @param attribute
	 * @param anotherAttribute
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereLessThanField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute);
	/**
	 * 
	 * @param attribute
	 * @param anotherAttribute
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereGreaterThanField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute);
	/**
	 * 
	 * @param attribute
	 * @param anotherAttribute
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereGreaterThanField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute);
	/**
	 * 
	 * @param attribute
	 * @param anotherAttribute
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(Attribute<?, ?> attribute, 	Attribute<?, ?> anotherAttribute);
	/**
	 * 
	 * @param attribute
	 * @param anotherAttribute
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute, 	Attribute<?, ?> anotherAttribute);
	/**
	 * 
	 * @param attribute
	 * @param anotherAttribute
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute);
	/**
	 * 
	 * @param attribute
	 * @param anotherAttribute
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute);
	/**
	 * 
	 * @param attribute
	 * @param anotherAttribute
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereEqualField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute);
	/**
	 * 
	 * @param attribute
	 * @param anotherAttribute
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereEqualField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute);
	/**
	 * 
	 * @param attribute
	 * @param anotherAttribute
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereNotEqualField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute);
	/**
	 * 
	 * @param attribute
	 * @param anotherAttribute
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereNotEqualField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute);
	/**
	 * 
	 * @param attribute
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, Date startValue, Date endValue);
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
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, LocalDate startValue, LocalDate endValue);
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
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, LocalDateTime startValue, LocalDateTime endValue);
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
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addWhereIsNotNull(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addWhereIsNull(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addWhereIn(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addWhereNotIn(Attribute<?, ?> attribute);
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
	public CriteriaFilterMetamodel<T> addGroupBy(Attribute<?, ?> attribute);

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
	public CriteriaFilterMetamodel<T> addJoin(Attribute<?, ?> attribute, JoinType joinType, boolean fetch);
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
	public CriteriaFilterMetamodel<T> addJoin(Attribute<?, ?> attribute, JoinType joinType);
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
	public CriteriaFilterMetamodel<T> addJoin(Attribute<?, ?> attribute);
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
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(Attribute<?, ?> attribute, E value);
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
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<?, ?> attribute, E value);
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
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<?, ?> attribute, Date value);
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
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<?, ?> attribute, Number value);
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
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<?, ?> attribute,	Date value);
	/**
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute,	Date value);
	/**
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<?, ?> attribute,	Number value);
	/**
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute,	Number value);
	/**
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<?, ?> attribute, Date value);
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
	public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<?, ?> attribute, Number value);
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
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<?, ?> attribute,	Date value);
	/**
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute,	Date value);
	/**
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<?, ?> attribute,	Number value);
	/**
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute,	Number value);
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
	 * <b>not like</b> !%nome
	 * <b>like after</b> nome%<br>
	 * <b>like before</b> %nome<br>
	 * <b>like both</b> %nome%<br>
	 * <b>ilike</b> =*nome<br>
	 * <b>not ilike</b> !*nome
	 * <b>ilike after</b> nome*<br>
	 * <b>ilike before</b> *nome<br>
	 * <b>ilike both</b> *nome*<br>
	 * 
	 * Obs: com exececao das regex de like e ilike as demais instrucoes aceitam valores com formato data/hora. Ex: &lt;=2000-10-20
	 * 
	 * @param attribute
	 * @param value regex com valor. Ex: &gt;10 
	 * @param regexToAnalyse condicoes (Where) para analisar  para analisar, se null verifica todas.
	 * @param defaultIfNotMatch padrao caso nao encontre referencia
	 * @return
	 * @throws ApplicationException
	 */
	public CriteriaFilterMetamodel<T> addWhereRegex(Attribute<?, ?> attribute, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException;
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
	 * <b>not like</b> !%nome
	 * <b>like after</b> nome%<br>
	 * <b>like before</b> %nome<br>
	 * <b>like both</b> %nome%<br>
	 * <b>ilike</b> =*nome<br>
	 * <b>not ilike</b> !*nome
	 * <b>ilike after</b> nome*<br>
	 * <b>ilike before</b> *nome<br>
	 * <b>ilike both</b> *nome*<br>
	 * 
	 * Obs: com exececao das regex de like e ilike as demais instrucoes aceitam valores com formato data/hora. Ex: &lt;=2000-10-20
	 * 
	 * @param attribute
	 * @param value regex com valor. Ex: &gt;10 
	 * @param regexToAnalyse condicoes (Where) para analisar  para analisar, se null verifica todas.
	 * @param defaultIfNotMatch padrao caso nao encontre referencia
	 * @return
	 * @throws ApplicationException
	 */
	public CriteriaFilterMetamodel<T> addWhereRegex(ComplexAttribute attribute, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException;
	
}

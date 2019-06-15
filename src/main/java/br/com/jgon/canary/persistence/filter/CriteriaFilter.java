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
public interface CriteriaFilter<T> extends CriteriaWhere<T>{
	
	/**
	 * 
	 * @param returnType
	 * @return
	 * @throws ApplicationException 
	 */
	public CriteriaFilter<T> addSelect(Class<?> returnType) throws ApplicationException;
	/**
	 * 
	 * @param returnType
	 * @param fields
	 * @return
	 * @throws ApplicationException 
	 */
	public CriteriaFilter<T> addSelect(Class<?> returnType, List<String> fields) throws ApplicationException;
	
	/**
	 * 
	 * @param returnType
	 * @param fields
	 * @return
	 * @throws ApplicationException 
	 */
	public CriteriaFilter<T> addSelect(Class<?> returnType, String... fields) throws ApplicationException;
		
	/**
	 * 
	 * @param field
	 * @param alias
	 * @return
	 */
	public CriteriaFilter<T> addSelect(String field, String alias);
	/**
	 * 
	 * @param fieldAlias
	 * @return
	 */
	public CriteriaFilter<T> addSelect(Map<String, String> fieldAlias);
	
	/**
	 * 
	 * @param field
	 * @param alias
	 * @return
	 */
	public CriteriaFilter<T> addSelectCount(String field, String alias);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addSelectCount(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addSelectUpper(String field);
	/**
	 * 
	 * @param field
	 * @param alias
	 * @return
	 */
	public CriteriaFilter<T> addSelectUpper(String field, String alias);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addSelectLower(String field);
	/**
	 * 
	 * @param field
	 * @param alias
	 * @return
	 */
	public CriteriaFilter<T> addSelectLower(String field, String alias);
	/**
	 * 
	 * @param field
	 * @param alias
	 * @return
	 */
	public CriteriaFilter<T> addSelectMax(String field, String alias);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addSelectMax(String field);
	/**
	 * 
	 * @param field
	 * @param alias
	 * @return
	 */
	public CriteriaFilter<T> addSelectMin(String field, String alias);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addSelectMin(String field);
	/**
	 * 
	 * @param field
	 * @param alias
	 * @return
	 */
	public CriteriaFilter<T> addSelectSum(String field, String alias);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addSelectSum(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addSelect(String field);
	/**
	 * 
	 * @param fields
	 * @return
	 */
	public CriteriaFilter<T> addSelect(String[] fields);
	
	/**
	 * 
	 * @param fields
	 * @return
	 */
	public CriteriaFilter<T> addSelect(List<String> fields);
	
	/**
	 * 
	 * @param returnType
	 * @param order
	 * @return
	 * @throws ApplicationException 
	 */
	public CriteriaFilter<T> addOrder(Class<?> returnType, String... order) throws ApplicationException;
	
	/**
	 * 
	 * @param returnType
	 * @param order
	 * @return
	 * @throws ApplicationException 
	 */
	public CriteriaFilter<T> addOrder(Class<?> returnType, List<String> order) throws ApplicationException;
	
	/**
	 * 
	 * @param order
	 * @return
	 */
	public CriteriaFilter<T> addOrder(List<String> order);
	
	/**
	 * 
	 * @param order
	 * @return
	 */
	public CriteriaFilter<T> addOrder(String... order);
	
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addOrderAsc(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addOrderDesc(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereEqual(String field);
	
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilter<T> addWhereIn(String field, E... values);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	public <E> CriteriaFilter<T> addWhereIn(String field, List<E> values);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilter<T> addWhereNotIn(String field, E... values);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	public <E> CriteriaFilter<T> addWhereNotIn(String field, List<E> values);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	public <E> CriteriaFilter<T> addWhereEqual(String field, List<E> values);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	public <E> CriteriaFilter<T> addWhereNotEqual(String field, List<E> values);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilter<T> addWhereEqual(String field, E... values);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public <E> CriteriaFilter<T> addWhereEqual(String field, E value);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilter<T> addWhereNotEqual(String field, E... values);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public <E> CriteriaFilter<T> addWhereNotEqual(String field, E value);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaFilter<T> addWhereBetween(String field, Integer startValue, Integer endValue);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaFilter<T> addWhereBetween(String field, Short startValue, Short endValue);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaFilter<T> addWhereBetween(String field, Long startValue, Long endValue);
	/**
	 * 
	 * @param field
	 * @param anotherField
	 * @return
	 */
	public CriteriaFilter<T> addWhereLessThanField(String field, String anotherField);
	/**
	 * 
	 * @param field
	 * @param anotherField
	 * @return
	 */
	public CriteriaFilter<T> addWhereGreaterThanField(String field, String anotherField);
	/**
	 * 
	 * @param field
	 * @param anotherField
	 * @return
	 */
	public CriteriaFilter<T> addWhereLessThanOrEqualToField(String field, String anotherField);
	/**
	 * 
	 * @param field
	 * @param anotherField
	 * @return
	 */
	public CriteriaFilter<T> addWhereGreaterThanOrEqualToField(String field, String anotherField);
	/**
	 * 
	 * @param field
	 * @param anotherField
	 * @return
	 */
	public CriteriaFilter<T> addWhereEqualField(String field, String anotherField);
	/**
	 * 
	 * @param field
	 * @param anotherField
	 * @return
	 */
	public CriteriaFilter<T> addWhereNotEqualField(String field, String anotherField);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaFilter<T> addWhereBetween(String field, Date startValue, Date endValue);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaFilter<T> addWhereBetween(String field, LocalDate startValue, LocalDate endValue);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaFilter<T> addWhereBetween(String field, LocalDateTime startValue, LocalDateTime endValue);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereGreaterThan(String field);	
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public <E> CriteriaFilter<T> addWhereGreaterThan(String field, Date value);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public <E> CriteriaFilter<T> addWhereGreaterThan(String field, Number value);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereGreaterThanOrEqualTo(String field);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public CriteriaFilter<T> addWhereGreaterThanOrEqualTo(String field, Date value);
	/**
	 *
	 * @param field
	 * @param value
	 * @return
	 */
	public CriteriaFilter<T> addWhereGreaterThanOrEqualTo(String field, Number value);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereIn(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereIsNotNull(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereIsNull(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereLessThan(String field);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public CriteriaFilter<T> addWhereLessThan(String field, Date value);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public CriteriaFilter<T> addWhereLessThan(String field, Number value);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereLessThanOrEqualTo(String field);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public CriteriaFilter<T> addWhereLessThanOrEqualTo(String field, Date value);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public CriteriaFilter<T> addWhereLessThanOrEqualTo(String field, Number value);
	/**
	 * @param field
	 * @param matchMode {@link MatchMode}
	 * @return
	 */
	public CriteriaFilter<T> addWhereLike(String field, MatchMode matchMode);
	/**
	 * @param field
	 * @param matchMode {@link MatchMode}
	 * @return
	 */
	public CriteriaFilter<T> addWhereNotLike(String field, MatchMode matchMode);
	/**
	 * @param field
	 * @param matchMode {@link MatchMode}
	 * @return
	 */
	public CriteriaFilter<T> addWhereNotILike(String field, MatchMode matchMode);
	/**
	 * @param field
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilter<T> addWhereLike(String field, String value, MatchMode matchMode);
	/**
	 * @param field
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilter<T> addWhereNotLike(String field, String value, MatchMode matchMode);
	/**
	 * @param field
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilter<T> addWhereNotILike(String field, String value, MatchMode matchMode);
	/**
	 * @param field
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilter<T> addWhereILike(String field, MatchMode matchMode);
	/**
	 * @param field
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaFilter<T> addWhereILike(String field, String value, MatchMode matchMode);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereNotEqual(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereNotIn(String field);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaFilter<T> addWhereBetween(String field, Double startValue, Double endValue);
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
	public CriteriaFilter<T> addWhereRegex(String field, Class<?> fieldType, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException;
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addGroupBy(String field);
	/**
	 * 
	 * @param field
	 * @param joinType
	 * @param fetch
	 * @return
	 */
	public CriteriaFilter<T> addJoin(String field, JoinType joinType, boolean fetch);
	
	/**
	 * fetch: false
	 * @param field
	 * @param joinType
	 * @return
	 */
	public CriteriaFilter<T> addJoin(String field, JoinType joinType);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addJoin(String field);
	
}

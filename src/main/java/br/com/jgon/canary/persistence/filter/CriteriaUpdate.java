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
public interface CriteriaUpdate<T> extends CriteriaWhere<T>{
	
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public <E> CriteriaFilter<T> addUpdate(String field, E value);
	/**
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public <E> CriteriaFilterMetamodel<T> addUpdate(Attribute<?, ?> attribute, E value);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaUpdate<T> addWhereEqual(String field);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> CriteriaUpdate<T> addWhereIn(String field, E... values);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	public <E> CriteriaUpdate<T> addWhereIn(String field, List<E> values);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> CriteriaUpdate<T> addWhereNotIn(String field, E... values);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	public <E> CriteriaUpdate<T> addWhereNotIn(String field, List<E> values);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	public <E> CriteriaUpdate<T> addWhereEqual(String field, List<E> values);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	public <E> CriteriaUpdate<T> addWhereNotEqual(String field, List<E> values);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> CriteriaUpdate<T> addWhereEqual(String field, E... values);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public <E> CriteriaUpdate<T> addWhereEqual(String field, E value);
	/**
	 * 
	 * @param field
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> CriteriaUpdate<T> addWhereNotEqual(String field, E... values);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public <E> CriteriaUpdate<T> addWhereNotEqual(String field, E value);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaUpdate<T> addWhereBetween(String field, Integer startValue, Integer endValue);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaUpdate<T> addWhereBetween(String field, Short startValue, Short endValue);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaUpdate<T> addWhereBetween(String field, Long startValue, Long endValue);
	/**
	 * 
	 * @param field
	 * @param anotherField
	 * @return
	 */
	public CriteriaUpdate<T> addWhereLessThanField(String field, String anotherField);
	/**
	 * 
	 * @param field
	 * @param anotherField
	 * @return
	 */
	public CriteriaUpdate<T> addWhereGreaterThanField(String field, String anotherField);
	/**
	 * 
	 * @param field
	 * @param anotherField
	 * @return
	 */
	public CriteriaUpdate<T> addWhereLessThanOrEqualToField(String field, String anotherField);
	/**
	 * 
	 * @param field
	 * @param anotherField
	 * @return
	 */
	public CriteriaUpdate<T> addWhereGreaterThanOrEqualToField(String field, String anotherField);
	/**
	 * 
	 * @param field
	 * @param anotherField
	 * @return
	 */
	public CriteriaUpdate<T> addWhereEqualField(String field, String anotherField);
	/**
	 * 
	 * @param field
	 * @param anotherField
	 * @return
	 */
	public CriteriaUpdate<T> addWhereNotEqualField(String field, String anotherField);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaUpdate<T> addWhereBetween(String field, Date startValue, Date endValue);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaUpdate<T> addWhereBetween(String field, LocalDate startValue, LocalDate endValue);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaUpdate<T> addWhereBetween(String field, LocalDateTime startValue, LocalDateTime endValue);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaUpdate<T> addWhereGreaterThan(String field);	
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public <E> CriteriaUpdate<T> addWhereGreaterThan(String field, Date value);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public <E> CriteriaUpdate<T> addWhereGreaterThan(String field, Number value);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaUpdate<T> addWhereGreaterThanOrEqualTo(String field);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public CriteriaUpdate<T> addWhereGreaterThanOrEqualTo(String field, Date value);
	/**
	 *
	 * @param field
	 * @param value
	 * @return
	 */
	public CriteriaUpdate<T> addWhereGreaterThanOrEqualTo(String field, Number value);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaUpdate<T> addWhereIn(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaUpdate<T> addWhereIsNotNull(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaUpdate<T> addWhereIsNull(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaUpdate<T> addWhereLessThan(String field);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public CriteriaUpdate<T> addWhereLessThan(String field, Date value);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public CriteriaUpdate<T> addWhereLessThan(String field, Number value);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaUpdate<T> addWhereLessThanOrEqualTo(String field);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public CriteriaUpdate<T> addWhereLessThanOrEqualTo(String field, Date value);
	/**
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public CriteriaUpdate<T> addWhereLessThanOrEqualTo(String field, Number value);
	/**
	 * @param field
	 * @param matchMode {@link MatchMode}
	 * @return
	 */
	public CriteriaUpdate<T> addWhereLike(String field, MatchMode matchMode);
	/**
	 * @param field
	 * @param matchMode {@link MatchMode}
	 * @return
	 */
	public CriteriaUpdate<T> addWhereNotLike(String field, MatchMode matchMode);
	/**
	 * @param field
	 * @param matchMode {@link MatchMode}
	 * @return
	 */
	public CriteriaUpdate<T> addWhereNotILike(String field, MatchMode matchMode);
	/**
	 * @param field
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaUpdate<T> addWhereLike(String field, String value, MatchMode matchMode);
	/**
	 * @param field
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaUpdate<T> addWhereNotLike(String field, String value, MatchMode matchMode);
	/**
	 * @param field
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaUpdate<T> addWhereNotILike(String field, String value, MatchMode matchMode);
	/**
	 * @param field
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaUpdate<T> addWhereILike(String field, MatchMode matchMode);
	/**
	 * @param field
	 * @param value
	 * @param matchMode  {@link MatchMode}
	 * @return
	 */
	public CriteriaUpdate<T> addWhereILike(String field, String value, MatchMode matchMode);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaUpdate<T> addWhereNotEqual(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaUpdate<T> addWhereNotIn(String field);
	/**
	 * 
	 * @param field
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public CriteriaUpdate<T> addWhereBetween(String field, Double startValue, Double endValue);
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
	public CriteriaUpdate<T> addWhereRegex(String field, Class<?> fieldType, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException;
}

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
public interface CriteriaFilterDelete<T> extends CriteriaWhereMetamodel<T> {

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereEqual(String field);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterDelete<T> addWhereIn(String field, E... values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereIn(String field, List<E> values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterDelete<T> addWhereNotIn(String field, E... values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    public <E> CriteriaFilterDelete<T> addWhereNotIn(String field, List<E> values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    public <E> CriteriaFilterDelete<T> addWhereEqual(String field, List<E> values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    public <E> CriteriaFilterDelete<T> addWhereNotEqual(String field, List<E> values);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterDelete<T> addWhereEqual(String field, E... values);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereEqual(String field, E value);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterDelete<T> addWhereNotEqual(String field, E... values);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereNotEqual(String field, E value);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereBetween(String field, Integer startValue, Integer endValue);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereBetween(String field, Short startValue, Short endValue);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereBetween(String field, Long startValue, Long endValue);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereLessThanField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThanField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereLessThanOrEqualToField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereGreaterThanOrEqualToField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereEqualField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param anotherField
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereNotEqualField(String field, String anotherField);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereBetween(String field, Date startValue, Date endValue);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereBetween(String field, LocalDate startValue, LocalDate endValue);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereBetween(String field, LocalDateTime startValue, LocalDateTime endValue);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereGreaterThan(String field);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public <E> CriteriaFilterDelete<T> addWhereGreaterThan(String field, Date value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public <E> CriteriaFilterDelete<T> addWhereGreaterThan(String field, Number value);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereGreaterThanOrEqualTo(String field);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereGreaterThanOrEqualTo(String field, Date value);

    /**
     *
     * @param field
     * @param value
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereGreaterThanOrEqualTo(String field, Number value);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereIn(String field);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereIsNotNull(String field);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereIsNull(String field);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereLessThan(String field);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereLessThan(String field, Date value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereLessThan(String field, Number value);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereLessThanOrEqualTo(String field);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereLessThanOrEqualTo(String field, Date value);

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereLessThanOrEqualTo(String field, Number value);

    /**
     * @param field
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereLike(String field, MatchMode matchMode);

    /**
     * @param field
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereNotLike(String field, MatchMode matchMode);

    /**
     * @param field
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereNotILike(String field, MatchMode matchMode);

    /**
     * @param field
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereLike(String field, String value, MatchMode matchMode);

    /**
     * @param field
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereNotLike(String field, String value, MatchMode matchMode);

    /**
     * @param field
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereNotILike(String field, String value, MatchMode matchMode);

    /**
     * @param field
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereILike(String field, MatchMode matchMode);

    /**
     * @param field
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereILike(String field, String value, MatchMode matchMode);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereNotEqual(String field);

    /**
     * 
     * @param field
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereNotIn(String field);

    /**
     * 
     * @param field
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public CriteriaFilterDelete<T> addWhereBetween(String field, Double startValue, Double endValue);

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
    public CriteriaFilterDelete<T> addWhereRegex(
        String field,
        Class<?> fieldType,
        String value,
        RegexWhere[] regexToAnalyse,
        RegexWhere defaultIfNotMatch) throws ApplicationRuntimeException;

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereILike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereILike(ComplexAttribute attribute, String value, MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereILike(Attribute<T, String> attribute, MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereILike(ComplexAttribute attribute, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotILike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotILike(ComplexAttribute attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotLike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotLike(ComplexAttribute attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLike(ComplexAttribute attribute, String value, MatchMode matchMode);

    /**
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotILike(Attribute<T, String> attribute, MatchMode matchMode);

    /**
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotILike(ComplexAttribute attribute, MatchMode matchMode);

    /**
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotLike(Attribute<T, String> attribute, MatchMode matchMode);

    /**
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotLike(ComplexAttribute attribute, MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLike(Attribute<T, String> attribute, MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLike(ComplexAttribute attribute, MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereEqual(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereEqual(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereIn(Attribute<T, E> attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereIn(ComplexAttribute attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterDelete<T> addWhereIn(Attribute<T, E> attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterDelete<T> addWhereIn(ComplexAttribute attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterDelete<T> addWhereNotIn(Attribute<T, E> attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterDelete<T> addWhereNotIn(ComplexAttribute attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereNotIn(Attribute<T, E> attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereNotIn(ComplexAttribute attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterDelete<T> addWhereEqual(Attribute<T, E> attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterDelete<T> addWhereEqual(ComplexAttribute attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereEqual(Attribute<T, E> attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereEqual(ComplexAttribute attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterDelete<T> addWhereNotEqual(Attribute<T, E> attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterDelete<T> addWhereNotEqual(ComplexAttribute attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereNotEqual(Attribute<T, E> attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereNotEqual(ComplexAttribute attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterDelete<T> addWhereBetween(Attribute<T, Integer> attribute, Integer startValue, Integer endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterDelete<T> addWhereBetween(ComplexAttribute attribute, Integer startValue, Integer endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterDelete<T> addWhereBetween(Attribute<T, Short> attribute, Short startValue, Short endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterDelete<T> addWhereBetween(ComplexAttribute attribute, Short startValue, Short endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterDelete<T> addWhereBetween(Attribute<T, Long> attribute, Long startValue, Long endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterDelete<T> addWhereBetween(ComplexAttribute attribute, Long startValue, Long endValue);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereLessThanField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThanField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThanField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereGreaterThanField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThanField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThanField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereLessThanOrEqualToField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereGreaterThanOrEqualToField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereEqualField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereEqualField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereEqualField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereNotEqualField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotEqualField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotEqualField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterDelete<T> addWhereBetween(Attribute<T, Date> attribute, Date startValue, Date endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterDelete<T> addWhereBetween(ComplexAttribute attribute, Date startValue, Date endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterDelete<T> addWhereBetween(Attribute<T, LocalDate> attribute, LocalDate startValue, LocalDate endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterDelete<T> addWhereBetween(ComplexAttribute attribute, LocalDate startValue, LocalDate endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterDelete<T> addWhereBetween(Attribute<T, LocalDateTime> attribute, LocalDateTime startValue, LocalDateTime endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaFilterDelete<T> addWhereBetween(ComplexAttribute attribute, LocalDateTime startValue, LocalDateTime endValue);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThan(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThan(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThanOrEqualTo(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereIsNotNull(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereIsNotNull(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereIsNull(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereIsNull(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereIn(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereIn(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThan(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThan(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThanOrEqualTo(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotEqual(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotEqual(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotIn(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereNotIn(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereEqual(Attribute<T, E> attribute, E value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereEqual(ComplexAttribute attribute, E value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereNotEqual(Attribute<T, E> attribute, E value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E> CriteriaFilterDelete<T> addWhereNotEqual(ComplexAttribute attribute, E value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThan(Attribute<T, Date> attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThan(ComplexAttribute attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThan(Attribute<T, Number> attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThan(ComplexAttribute attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThanOrEqualTo(Attribute<T, Date> attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThanOrEqualTo(Attribute<T, Number> attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThan(Attribute<T, Date> attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThan(ComplexAttribute attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThan(Attribute<T, Number> attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThan(ComplexAttribute attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThanOrEqualTo(Attribute<T, Date> attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThanOrEqualTo(Attribute<T, Number> attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaFilterDelete<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Number value);

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
    public CriteriaFilterDelete<T> addWhereRegex(
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
    public CriteriaFilterDelete<T> addWhereRegex(
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
    public CriteriaFilterDelete<T> addWhereNotEqualField(Attribute<T, ?> attribute, ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaFilterDelete<T> addWhereEqualField(Attribute<T, ?> attribute, ComplexAttribute anotherAttribute);

}

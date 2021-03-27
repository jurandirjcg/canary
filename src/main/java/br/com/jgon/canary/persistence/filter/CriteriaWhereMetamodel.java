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
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;
import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.persistence.filter.CriteriaWhere.MatchMode;
import br.com.jgon.canary.persistence.filter.CriteriaWhere.RegexWhere;

/**
 * Define os filtros da consulta
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 * @param <T> Entity
 */
public interface CriteriaWhereMetamodel<T> {

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhere(Attribute<T, ?> attribute, Predicate value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhere<T> addWhere(ComplexAttribute attribute, Predicate value);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereEqual(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereEqual(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaWhereMetamodel<T> addWhereEqual(Attribute<T, E> attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaWhereMetamodel<T> addWhereEqual(ComplexAttribute attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereEqual(Attribute<T, E> attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereEqual(ComplexAttribute attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereEqual(Attribute<T, E> attribute, E value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereEqual(ComplexAttribute attribute, E value);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaWhereMetamodel<T> addWhereNotEqual(Attribute<T, E> attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaWhereMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereNotEqual(Attribute<T, E> attribute,
            List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereNotEqual(ComplexAttribute attribute,
            List<E> values);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotEqual(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotEqual(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereNotEqual(Attribute<T, E> attribute, E value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, E value);

    /**
     * 
     * @param field
     * @param values
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereIn(String field, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereIn(Attribute<T, E> attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereIn(ComplexAttribute attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaWhereMetamodel<T> addWhereIn(Attribute<T, E> attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaWhereMetamodel<T> addWhereIn(ComplexAttribute attribute, E... values);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereIn(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereIn(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaWhereMetamodel<T> addWhereNotIn(Attribute<T, E> attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> CriteriaWhereMetamodel<T> addWhereNotIn(ComplexAttribute attribute, E... values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereNotIn(Attribute<T, E> attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @param values
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereNotIn(ComplexAttribute attribute, List<E> values);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotIn(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotIn(ComplexAttribute attribute);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereILike(Attribute<T, String> attribute, String value,
            MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereILike(ComplexAttribute attribute, String value,
            MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereILike(Attribute<T, String> attribute,
            MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereILike(ComplexAttribute attribute, MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotILike(Attribute<T, String> attribute, String value,
            MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotILike(ComplexAttribute attribute, String value,
            MatchMode matchMode);

    /**
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotILike(Attribute<T, String> attribute,
            MatchMode matchMode);

    /**
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotILike(ComplexAttribute attribute,
            MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLike(Attribute<T, String> attribute, String value,
            MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLike(ComplexAttribute attribute, String value,
            MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @param matchMode
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLike(ComplexAttribute attribute, MatchMode matchMode);

    /**
     * 
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLike(Attribute<T, String> attribute,
            MatchMode matchMode);

    /**
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotLike(Attribute<T, String> attribute,
            MatchMode matchMode);

    /**
     * @param attribute
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotLike(ComplexAttribute attribute,
            MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotLike(Attribute<T, String> attribute, String value,
            MatchMode matchMode);

    /**
     * @param attribute
     * @param value
     * @param matchMode {@link MatchMode}
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotLike(ComplexAttribute attribute, String value,
            MatchMode matchMode);



    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereBetween(Attribute<T, Integer> attribute,
            Integer startValue, Integer endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereBetween(ComplexAttribute attribute, Integer startValue,
            Integer endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereBetween(Attribute<T, Short> attribute,
            Short startValue, Short endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereBetween(ComplexAttribute attribute, Short startValue,
            Short endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereBetween(Attribute<T, Long> attribute, Long startValue,
            Long endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereBetween(ComplexAttribute attribute, Long startValue,
            Long endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereBetween(Attribute<T, Date> attribute, Date startValue,
            Date endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereBetween(ComplexAttribute attribute, Date startValue,
            Date endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public <E extends Temporal> CriteriaWhereMetamodel<T> addWhereBetween(Attribute<T, E> attribute,
            Temporal startValue, Temporal endValue);

    /**
     * 
     * @param attribute
     * @param startValue
     * @param endValue
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereBetween(ComplexAttribute attribute,
            Temporal startValue, Temporal endValue);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereLessThanField(Attribute<T, E> attribute,
            Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThanField(ComplexAttribute attribute,
            Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThanField(ComplexAttribute attribute,
            ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereGreaterThanField(Attribute<T, E> attribute,
            Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThanField(ComplexAttribute attribute,
            Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThanField(ComplexAttribute attribute,
            ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereLessThanOrEqualToField(Attribute<T, E> attribute,
            Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute,
            Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute,
            ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereGreaterThanOrEqualToField(
            Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute,
            Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute,
            ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereEqualField(Attribute<T, E> attribute,
            Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereEqualField(ComplexAttribute attribute,
            Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereEqualField(ComplexAttribute attribute,
            ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereEqualField(Attribute<T, ?> attribute,
            ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public <E> CriteriaWhereMetamodel<T> addWhereNotEqualField(Attribute<T, E> attribute,
            Attribute<T, E> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotEqualField(ComplexAttribute attribute,
            Attribute<T, ?> anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotEqualField(ComplexAttribute attribute,
            ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @param anotherAttribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereNotEqualField(Attribute<T, ?> attribute,
            ComplexAttribute anotherAttribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThan(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThan(Attribute<T, Date> attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E extends Temporal> CriteriaWhereMetamodel<T> addWhereGreaterThan(
            Attribute<T, E> attribute, Temporal value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute,
            Temporal value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThan(Attribute<T, Number> attribute,
            Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute, Number value);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute);


    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<T, Date> attribute,
            Date value);

    /**
     *
     * @param attribute
     * @param value
     * @return
     */
    public <E extends Temporal> CriteriaWhereMetamodel<T> addWhereGreaterThanOrEqualTo(
            Attribute<T, E> attribute, Temporal value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute,
            Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute,
            Temporal value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<T, Number> attribute,
            Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute,
            Number value);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereIsNotNull(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereIsNotNull(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereIsNull(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereIsNull(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThan(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThan(ComplexAttribute attribute);


    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThan(Attribute<T, Date> attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E extends Temporal> CriteriaWhereMetamodel<T> addWhereLessThan(
            Attribute<T, E> attribute, Temporal value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThan(ComplexAttribute attribute, Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThan(ComplexAttribute attribute, Temporal value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThan(Attribute<T, Number> attribute, Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThan(ComplexAttribute attribute, Number value);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThanOrEqualTo(Attribute<T, ?> attribute);

    /**
     * 
     * @param attribute
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThanOrEqualTo(Attribute<T, Date> attribute,
            Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E extends Temporal> CriteriaWhereMetamodel<T> addWhereLessThanOrEqualTo(
            Attribute<T, E> attribute, Temporal value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute,
            Date value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute,
            Temporal value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThanOrEqualTo(Attribute<T, Number> attribute,
            Number value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public CriteriaWhereMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute,
            Number value);

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
     * Obs: com exececao das regex de like e ilike as demais instrucoes aceitam valores com formato
     * data/hora. Ex: &lt;=2000-10-20
     * 
     * @param attribute
     * @param value             regex com valor. Ex: &gt;10
     * @param regexToAnalyse    condicoes (Where) para analisar para analisar, se null verifica
     *                          todas.
     * @param defaultIfNotMatch padrao caso nao encontre referencia
     * @return
     * @throws ApplicationRuntimeException
     */
    public CriteriaWhereMetamodel<T> addWhereRegex(Attribute<T, ?> attribute, String value,
            RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch)
            throws ApplicationRuntimeException;

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
     * Obs: com exececao das regex de like e ilike as demais instrucoes aceitam valores com formato
     * data/hora. Ex: &lt;=2000-10-20
     * 
     * @param attribute
     * @param value             regex com valor. Ex: &gt;10
     * @param regexToAnalyse    condicoes (Where) para analisar para analisar, se null verifica
     *                          todas.
     * @param defaultIfNotMatch padrao caso nao encontre referencia
     * @return
     * @throws ApplicationRuntimeException
     */
    public CriteriaWhereMetamodel<T> addWhereRegex(ComplexAttribute attribute, String value,
            RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch)
            throws ApplicationRuntimeException;



}

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
 * @param <T> Entity
 */
public interface CriteriaFilterUpdate<T> extends CriteriaWhereMetamodel<T> {

    /**
     * 
     * @param field
     * @param value
     * @return
     */
    public <E> CriteriaFilterUpdate<T> addUpdate(String field, E value);

    /**
     * 
     * @param attribute
     * @param value
     * @return
     */
    public <E> CriteriaFilterUpdate<T> addUpdate(Attribute<T, ?> attribute, E value);

    @Override
    public CriteriaFilterUpdate<T> addWhereEqual(String field);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterUpdate<T> addWhereIn(String field, E... values);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereIn(String field, List<E> values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterUpdate<T> addWhereNotIn(String field, E... values);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereNotIn(String field, List<E> values);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereEqual(String field, List<E> values);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereNotEqual(String field, List<E> values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterUpdate<T> addWhereEqual(String field, E... values);

    public <E> CriteriaFilterUpdate<T> addWhereEqual(String field, E value);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterUpdate<T> addWhereNotEqual(String field, E... values);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereNotEqual(String field, E value);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(String field, Integer startValue, Integer endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(String field, Short startValue, Short endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(String field, Long startValue, Long endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanField(String field, String anotherField);

    public CriteriaFilterUpdate<T> addWhereGreaterThanField(String field, String anotherField);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualToField(String field, String anotherField);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualToField(String field, String anotherField);

    @Override
    public CriteriaFilterUpdate<T> addWhereEqualField(String field, String anotherField);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotEqualField(String field, String anotherField);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(String field, Date startValue, Date endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(String field, LocalDate startValue, LocalDate endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(String field, LocalDateTime startValue, LocalDateTime endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThan(String field);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereGreaterThan(String field, Date value);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereGreaterThan(String field, LocalDate value);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereGreaterThan(String field, LocalDateTime value);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereGreaterThan(String field, Number value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualTo(String field);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualTo(String field, Date value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualTo(String field, LocalDate value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualTo(String field, LocalDateTime value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualTo(String field, Number value);

    @Override
    public CriteriaFilterUpdate<T> addWhereIn(String field);

    @Override
    public CriteriaFilterUpdate<T> addWhereIsNotNull(String field);

    @Override
    public CriteriaFilterUpdate<T> addWhereIsNull(String field);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThan(String field);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThan(String field, Date value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThan(String field, LocalDate value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThan(String field, LocalDateTime value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThan(String field, Number value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualTo(String field);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualTo(String field, Date value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualTo(String field, LocalDate value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualTo(String field, LocalDateTime value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualTo(String field, Number value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLike(String field, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotLike(String field, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotILike(String field, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereLike(String field, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotLike(String field, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotILike(String field, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereILike(String field, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereILike(String field, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotEqual(String field);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotIn(String field);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(String field, Double startValue, Double endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereRegex(
        String field,
        Class<?> fieldType,
        String value,
        RegexWhere[] regexToAnalyse,
        RegexWhere defaultIfNotMatch) throws ApplicationRuntimeException;

    @Override
    public CriteriaFilterUpdate<T> addWhereILike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereILike(ComplexAttribute attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereILike(Attribute<T, String> attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereILike(ComplexAttribute attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotILike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotILike(ComplexAttribute attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotLike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotLike(ComplexAttribute attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereLike(Attribute<T, String> attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereLike(ComplexAttribute attribute, String value, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotILike(Attribute<T, String> attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotILike(ComplexAttribute attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotLike(Attribute<T, String> attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotLike(ComplexAttribute attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereLike(Attribute<T, String> attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereLike(ComplexAttribute attribute, MatchMode matchMode);

    @Override
    public CriteriaFilterUpdate<T> addWhereEqual(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereEqual(ComplexAttribute attribute);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereIn(Attribute<T, E> attribute, List<E> values);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereIn(ComplexAttribute attribute, List<E> values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterUpdate<T> addWhereIn(Attribute<T, E> attribute, E... values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterUpdate<T> addWhereIn(ComplexAttribute attribute, E... values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterUpdate<T> addWhereNotIn(Attribute<T, E> attribute, E... values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterUpdate<T> addWhereNotIn(ComplexAttribute attribute, E... values);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereNotIn(Attribute<T, E> attribute, List<E> values);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereNotIn(ComplexAttribute attribute, List<E> values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterUpdate<T> addWhereEqual(Attribute<T, E> attribute, E... values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterUpdate<T> addWhereEqual(ComplexAttribute attribute, E... values);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereEqual(Attribute<T, E> attribute, List<E> values);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereEqual(ComplexAttribute attribute, List<E> values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterUpdate<T> addWhereNotEqual(Attribute<T, E> attribute, E... values);

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterUpdate<T> addWhereNotEqual(ComplexAttribute attribute, E... values);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereNotEqual(Attribute<T, E> attribute, List<E> values);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereNotEqual(ComplexAttribute attribute, List<E> values);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(Attribute<T, Integer> attribute, Integer startValue, Integer endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(ComplexAttribute attribute, Integer startValue, Integer endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(Attribute<T, Short> attribute, Short startValue, Short endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(ComplexAttribute attribute, Short startValue, Short endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(Attribute<T, Long> attribute, Long startValue, Long endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(ComplexAttribute attribute, Long startValue, Long endValue);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereLessThanField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereGreaterThanField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereLessThanOrEqualToField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualToField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereEqualField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereEqualField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereEqualField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereNotEqualField(Attribute<T, E> attribute, Attribute<T, E> anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotEqualField(ComplexAttribute attribute, Attribute<T, ?> anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotEqualField(ComplexAttribute attribute, ComplexAttribute anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(Attribute<T, Date> attribute, Date startValue, Date endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(ComplexAttribute attribute, Date startValue, Date endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(Attribute<T, LocalDate> attribute, LocalDate startValue, LocalDate endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(ComplexAttribute attribute, LocalDate startValue, LocalDate endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(Attribute<T, LocalDateTime> attribute, LocalDateTime startValue, LocalDateTime endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereBetween(ComplexAttribute attribute, LocalDateTime startValue, LocalDateTime endValue);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThan(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThan(ComplexAttribute attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualTo(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereIsNotNull(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereIsNotNull(ComplexAttribute attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereIsNull(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereIsNull(ComplexAttribute attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereIn(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereIn(ComplexAttribute attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThan(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThan(ComplexAttribute attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualTo(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotEqual(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotEqual(ComplexAttribute attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotIn(Attribute<T, ?> attribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereNotIn(ComplexAttribute attribute);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereEqual(Attribute<T, E> attribute, E value);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereEqual(ComplexAttribute attribute, E value);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereNotEqual(Attribute<T, E> attribute, E value);

    @Override
    public <E> CriteriaFilterUpdate<T> addWhereNotEqual(ComplexAttribute attribute, E value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThan(Attribute<T, Date> attribute, Date value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThan(Attribute<T, LocalDate> attribute, LocalDate value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThan(Attribute<T, LocalDateTime> attribute, LocalDateTime value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThan(ComplexAttribute attribute, Date value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThan(Attribute<T, Number> attribute, Number value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThan(ComplexAttribute attribute, Number value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualTo(Attribute<T, Date> attribute, Date value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualTo(Attribute<T, LocalDate> attribute, LocalDate value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualTo(Attribute<T, LocalDateTime> attribute, LocalDateTime value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute, Date value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualTo(Attribute<T, Number> attribute, Number value);

    @Override
    public CriteriaFilterUpdate<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute, Number value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThan(Attribute<T, Date> attribute, Date value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThan(Attribute<T, LocalDate> attribute, LocalDate value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThan(Attribute<T, LocalDateTime> attribute, LocalDateTime value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThan(ComplexAttribute attribute, Date value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThan(Attribute<T, Number> attribute, Number value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThan(ComplexAttribute attribute, Number value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualTo(Attribute<T, Date> attribute, Date value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualTo(Attribute<T, LocalDate> attribute, LocalDate value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualTo(Attribute<T, LocalDateTime> attribute, LocalDateTime value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Date value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualTo(Attribute<T, Number> attribute, Number value);

    @Override
    public CriteriaFilterUpdate<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Number value);

    @Override
    public CriteriaFilterUpdate<T> addWhereRegex(
        Attribute<T, ?> attribute,
        String value,
        RegexWhere[] regexToAnalyse,
        RegexWhere defaultIfNotMatch) throws ApplicationRuntimeException;

    @Override
    public CriteriaFilterUpdate<T> addWhereRegex(
        ComplexAttribute attribute,
        String value,
        RegexWhere[] regexToAnalyse,
        RegexWhere defaultIfNotMatch) throws ApplicationRuntimeException;

    @Override
    public CriteriaFilterUpdate<T> addWhereNotEqualField(Attribute<T, ?> attribute, ComplexAttribute anotherAttribute);

    @Override
    public CriteriaFilterUpdate<T> addWhereEqualField(Attribute<T, ?> attribute, ComplexAttribute anotherAttribute);

}

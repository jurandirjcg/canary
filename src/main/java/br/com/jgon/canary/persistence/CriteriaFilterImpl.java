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
package br.com.jgon.canary.persistence;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.persistence.filter.ComplexAttribute;
import br.com.jgon.canary.persistence.filter.CriteriaFilterDelete;
import br.com.jgon.canary.persistence.filter.CriteriaFilterMetamodel;
import br.com.jgon.canary.persistence.filter.CriteriaFilterUpdate;
import br.com.jgon.canary.util.DateUtil;
import br.com.jgon.canary.util.MessageSeverity;

/**
 * Define os filtros que serao utilizados para construir a criteria
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 * @param <T>
 */
class CriteriaFilterImpl<T>
        implements CriteriaFilterMetamodel<T>, CriteriaFilterUpdate<T>, CriteriaFilterDelete<T> {

    private static final String regexPatternAlpha = "[a-zA-Z0-9\u00C0-\u00FF\\s_-]+";
    private static final String regexPatternDate =
            "(((0?[1-9]|[12][0-9]|3[01])[/-](0[1-9]|1[0-2])[/-]((19|20)\\d\\d))|((19|20)\\d\\d[-/](0[1-9]|1[012])[-/](0[1-9]|[12][0-9]|3[01])))";
    private static final String regexTime =
            "((0\\d|1\\d|2[0-3]):[0-5]\\d)?(:[0-5]\\d)?(.\\d\\d\\d)?(Z)?(\\+[0-2][0-4]:[0-5]\\d)?";
    // OLD private static final String regexPatternDateTime = regexPatternDate
    // +"(([\\s]?(0\\d|1\\d|2[0-3]):[0-5]\\d)?(:[0-5]\\d)?)?";
    private static final String regexPatternDateTime =
            regexPatternDate + "(([\\s]|T|'T')?" + regexTime + ")?";
    private static final String regexPatternDateTimeOrNumber =
            "((" + regexPatternDateTime + ")|[0-9]+)";
    private static final String regexPatternMultiDateTimeOrNumber =
            "(([a-zA-Z0-9,\\s_-\u00C0-\u00FF]+)|[" + regexPatternDateTime + ",]+)";

    private Logger logger = LoggerFactory.getLogger(CriteriaFilterImpl.class);

    /**
     * Filtro de restricao
     *
     * @author Jurandir C. Goncalves
     * 
     * @version 1.0
     *
     */
    enum Where {
        IGNORE(null, null), EQUAL(RegexWhere.EQUAL,
                "(?<=^\\=)" + regexPatternAlpha + "$"), LESS_THAN(RegexWhere.LESS_THAN,
                        "(?<=^\\<)" + regexPatternDateTimeOrNumber + "$"), LESS_THAN_OR_EQUAL_TO(
                                RegexWhere.LESS_THAN_OR_EQUAL_TO,
                                "(?<=^\\<\\=)" + regexPatternDateTimeOrNumber + "$"), GREATER_THAN(
                                        RegexWhere.GREATER_THAN,
                                        "(?<=^\\>)" + regexPatternDateTimeOrNumber
                                                + "$"), GREATER_THAN_OR_EQUAL_TO(
                                                        RegexWhere.GREATER_THAN_OR_EQUAL_TO,
                                                        "(?<=^\\>\\=)"
                                                                + regexPatternDateTimeOrNumber
                                                                + "$"), NOT_EQUAL(
                                                                        RegexWhere.NOT_EQUAL,
                                                                        "(?<=^\\!\\=)"
                                                                                + regexPatternAlpha
                                                                                + "$"), IN(
                                                                                        RegexWhere.IN,
                                                                                        "(?<=^\\()"
                                                                                                + regexPatternMultiDateTimeOrNumber
                                                                                                + "(?=\\)$)"), NOT_IN(
                                                                                                        RegexWhere.NOT_IN,
                                                                                                        "(?<=^!\\()"
                                                                                                                + regexPatternMultiDateTimeOrNumber
                                                                                                                + "(?=\\)$)"), LIKE_EXACT(
                                                                                                                        RegexWhere.LIKE_EXACT,
                                                                                                                        "(?<=^\\=\\%)"
                                                                                                                                + regexPatternAlpha
                                                                                                                                + "(?!\\%$)"), LIKE_NOT_EXACT(
                                                                                                                                        RegexWhere.LIKE_NOT_EXACT,
                                                                                                                                        "(?<=^\\!\\=\\%)"
                                                                                                                                                + regexPatternAlpha
                                                                                                                                                + "(?!\\%$)"), LIKE_MATCH_ANYWHERE(
                                                                                                                                                        RegexWhere.LIKE_MATCH_ANYWHERE,
                                                                                                                                                        "(?<=^\\%)"
                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                + "(?=(\\!)?\\%$)"), LIKE_MATCH_END(
                                                                                                                                                                        RegexWhere.LIKE_MATCH_END,
                                                                                                                                                                        "(?<=^\\%)"
                                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                                + "(?!\\%$)"), LIKE_MATCH_START(
                                                                                                                                                                                        RegexWhere.LIKE_MATCH_START,
                                                                                                                                                                                        "(?<!^\\%)"
                                                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                                                + "(?=\\%$)"), LIKE_NOT_MATCH_ANYWHERE(
                                                                                                                                                                                                        RegexWhere.LIKE_NOT_MATCH_ANYWHERE,
                                                                                                                                                                                                        "(?<=^\\!\\%)"
                                                                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                                                                + "(?=\\!\\%$)"), LIKE_NOT_MATCH_END(
                                                                                                                                                                                                                        RegexWhere.LIKE_NOT_MATCH_END,
                                                                                                                                                                                                                        "(?<=^\\!\\%)"
                                                                                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                                                                                + "(?!\\%$)"), LIKE_NOT_MATCH_START(
                                                                                                                                                                                                                                        RegexWhere.LIKE_NOT_MATCH_START,
                                                                                                                                                                                                                                        "(?<!^\\%)"
                                                                                                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                                                                                                + "(?=\\!\\%$)"), ILIKE_EXACT(
                                                                                                                                                                                                                                                        RegexWhere.ILIKE_EXACT,
                                                                                                                                                                                                                                                        "(?<=^\\=\\*)"
                                                                                                                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                                                                                                                + "(?!\\*$)"), ILIKE_NOT_EXACT(
                                                                                                                                                                                                                                                                        RegexWhere.ILIKE_NOT_EXACT,
                                                                                                                                                                                                                                                                        "(?<=^\\!\\=\\*)"
                                                                                                                                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                                                                                                                                + "(?!\\*$)"), ILIKE_MATCH_ANYWHERE(
                                                                                                                                                                                                                                                                                        RegexWhere.ILIKE_MATCH_ANYWHERE,
                                                                                                                                                                                                                                                                                        "(?<=^\\*)"
                                                                                                                                                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                                                                                                                                                + "(?=\\*$)"), ILIKE_MATCH_END(
                                                                                                                                                                                                                                                                                                        RegexWhere.ILIKE_MATCH_END,
                                                                                                                                                                                                                                                                                                        "(?<=^\\*)"
                                                                                                                                                                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                                                                                                                                                                + "(?!\\*$)"), ILIKE_MATCH_START(
                                                                                                                                                                                                                                                                                                                        RegexWhere.ILIKE_MATCH_START,
                                                                                                                                                                                                                                                                                                                        "(?<!^\\*)"
                                                                                                                                                                                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                                                                                                                                                                                + "(?=\\*$)"), ILIKE_NOT_MATCH_ANYWHERE(
                                                                                                                                                                                                                                                                                                                                        RegexWhere.ILIKE_NOT_MATCH_ANYWHERE,
                                                                                                                                                                                                                                                                                                                                        "(?<=^\\!\\*)"
                                                                                                                                                                                                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                                                                                                                                                                                                + "(?=\\!\\*$)"), ILIKE_NOT_MATCH_END(
                                                                                                                                                                                                                                                                                                                                                        RegexWhere.ILIKE_NOT_MATCH_END,
                                                                                                                                                                                                                                                                                                                                                        "(?<=^\\!\\*)"
                                                                                                                                                                                                                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                                                                                                                                                                                                                + "(?!\\*$)"), ILIKE_NOT_MATCH_START(
                                                                                                                                                                                                                                                                                                                                                                        RegexWhere.ILIKE_NOT_MATCH_START,
                                                                                                                                                                                                                                                                                                                                                                        "(?<!^\\*)"
                                                                                                                                                                                                                                                                                                                                                                                + regexPatternAlpha
                                                                                                                                                                                                                                                                                                                                                                                + "(?=\\!\\*$)"), IS_NULL(
                                                                                                                                                                                                                                                                                                                                                                                        RegexWhere.IS_NULL,
                                                                                                                                                                                                                                                                                                                                                                                        "^null$"), IS_NOT_NULL(
                                                                                                                                                                                                                                                                                                                                                                                                RegexWhere.IS_NOT_NULL,
                                                                                                                                                                                                                                                                                                                                                                                                "^not null$"), BETWEEN(
                                                                                                                                                                                                                                                                                                                                                                                                        RegexWhere.BETWEEN,
                                                                                                                                                                                                                                                                                                                                                                                                        "(?<=^)" + regexPatternDateTimeOrNumber
                                                                                                                                                                                                                                                                                                                                                                                                                + "(\\s(btwn|between)\\s)"
                                                                                                                                                                                                                                                                                                                                                                                                                + regexPatternDateTimeOrNumber
                                                                                                                                                                                                                                                                                                                                                                                                                + "(?=$)"), EQUAL_OTHER_FIELD(
                                                                                                                                                                                                                                                                                                                                                                                                                        null,
                                                                                                                                                                                                                                                                                                                                                                                                                        null), LESS_THAN_OTHER_FIELD(
                                                                                                                                                                                                                                                                                                                                                                                                                                null,
                                                                                                                                                                                                                                                                                                                                                                                                                                null), GREATER_THAN_OTHER_FIELD(
                                                                                                                                                                                                                                                                                                                                                                                                                                        null,
                                                                                                                                                                                                                                                                                                                                                                                                                                        null), LESS_THAN_OR_EQUAL_TO_OTHER_FIELD(
                                                                                                                                                                                                                                                                                                                                                                                                                                                null,
                                                                                                                                                                                                                                                                                                                                                                                                                                                null), GREATER_THAN_OR_EQUAL_TO_OTHER_FIELD(
                                                                                                                                                                                                                                                                                                                                                                                                                                                        null,
                                                                                                                                                                                                                                                                                                                                                                                                                                                        null), NOT_EQUAL_OTHER_FIELD(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                null,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                null);

        public String exp;
        public RegexWhere regexWhere;

        private Where(RegexWhere regexWhere, String exp) {
            this.exp = exp;
            this.regexWhere = regexWhere;
        }
    }

    /**
     * Filtro de ordenacao
     *
     */
    enum Order {
        ASC, DESC
    }

    /**
     * Filtro de selecao
     * 
     */
    enum SelectAggregate {
        FIELD, COUNT, MAX, MIN, SUM, UPPER, LOWER
    }

    private boolean collectionSelectionControl = true;

    private Map<String, Where> listWhere = new LinkedHashMap<String, Where>(0);
    private WhereRestriction whereRestriction = new WhereRestriction();
    private Map<String, SimpleEntry<SelectAggregate, String>> listSelection =
            new LinkedHashMap<String, SimpleEntry<SelectAggregate, String>>(0);
    private Map<Class<?>, Map<String, SimpleEntry<SelectAggregate, String>>> collectionSelection =
            new LinkedHashMap<Class<?>, Map<String, SimpleEntry<SelectAggregate, String>>>();
    private Map<String, Order> listOrder = new LinkedHashMap<String, Order>(0);
    private Set<String> listGroupBy = new LinkedHashSet<String>();
    private Map<String, JoinMapper> listJoin = new LinkedHashMap<String, JoinMapper>();
    private Map<String, Object> listUpdate = new LinkedHashMap<String, Object>();
    private T objBase;
    private Class<T> objClass;

    /**
     * 
     * @param objBase
     */
    public CriteriaFilterImpl(T objBase, Class<T> objClass) {
        this.objBase = objBase;
        this.objClass = objClass;
    }

    /**
     * 
     * @param objClass
     */
    public CriteriaFilterImpl(Class<T> objClass) {
        this.objClass = objClass;
    }

    /**
     * 
     * @return
     */
    public boolean isCollectionSelectionControl() {
        return collectionSelectionControl;
    }

    /**
     * 
     * @param collectionSelectionControl
     */
    public void setCollectionSelectionControl(boolean collectionSelectionControl) {
        this.collectionSelectionControl = collectionSelectionControl;
    }

    /**
     * 
     * @return
     */
    @Override
    public T getObjBase() {
        return this.objBase;
    }

    /**
     * 
     * @param objBase
     */
    public void setObjBase(T objBase) {
        this.objBase = objBase;
    }

    /**
     * 
     * @param field
     * @return
     */
    public Where getWhere(String field) {
        return listWhere.get(field);
    }

    /**
     * 
     * @return
     */
    public Map<String, Where> getListWhere() {
        return listWhere;
    }

    /**
     * 
     * @return
     */
    public Map<String, Object> getListUpdate() {
        return listUpdate;
    }

    /**
     * 
     * @param fieldName
     * @return
     */
    public List<SimpleEntry<Where, ?>> getWhereRestriction(String fieldName) {
        return this.whereRestriction.getRestrictions(fieldName);
    }

    /**
     * 
     * @return
     */
    public WhereRestriction getWhereRestriction() {
        return this.whereRestriction;
    }

    /**
     * 
     * @return
     */
    public Map<String, Order> getListOrder() {
        return listOrder;
    }

    /**
     * 
     * @return
     */
    public Set<String> getListGroupBy() {
        return this.listGroupBy;
    }

    /**
     * 
     * @return
     */
    public Map<String, JoinMapper> getListJoin() {
        return this.listJoin;
    }

    /**
     * 
     * @return
     */
    public Map<String, SimpleEntry<SelectAggregate, String>> getListSelection() {
        return this.listSelection;
    }

    /**
     * 
     * @param field
     * @param selectFunction
     * @param alias
     * @return
     */
    private CriteriaFilterImpl<T> addSelect(String field, SelectAggregate selectFunction,
            String alias) {
        this.listSelection.put(field,
                new SimpleEntry<CriteriaFilterImpl.SelectAggregate, String>(selectFunction, alias));
        return this;
    }

    /**
     * 
     * @param returnType
     * @return
     * @throws ApplicationRuntimeException
     */
    public CriteriaFilterImpl<T> addSelect(Class<?> returnType) throws ApplicationRuntimeException {
        return addSelect(returnType, (String[]) null);
    }

    @Override
    public CriteriaFilterImpl<T> addSelect(Class<?> returnType, List<String> fields) {
        Class<?> returnTypeAux = returnType == null ? this.objClass : returnType;
        // TODO Verificar se funciona corretamente
        /*
         * if(returnType.equals(this.objClass)){ addSelect(fields); }else{
         */
        StringBuilder fieldAux = new StringBuilder();
        if (fields != null) {
            for (String f : fields) {
                if (fieldAux.length() > 0) {
                    fieldAux.append(",");
                }
                fieldAux.append(f);
            }
        }
        List<SimpleEntry<String, String>> listaCampos =
                new SelectMapper(returnTypeAux, fieldAux.toString()).getFields();

        for (SimpleEntry<String, String> se : listaCampos) {
            addSelect(se.getKey(), se.getValue());
        }
        // }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addSelect(Class<?> returnType, String... fields)
            throws ApplicationRuntimeException {
        return addSelect(returnType, fields == null ? null : Arrays.asList(fields));
    }

    @Override
    public CriteriaFilterImpl<T> addSelect(String field, String alias) {
        return addSelect(field, SelectAggregate.FIELD, alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelect(Attribute<T, ?> attribute, String alias) {
        return addSelect(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelect(String[] fields) {
        if (fields != null) {
            for (String fld : fields) {
                addSelect(fld);
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CriteriaFilterImpl<T> addSelect(Attribute<T, ?>... attributes) {
        if (attributes != null) {
            for (Attribute<T, ?> fld : attributes) {
                addSelect(fld);
            }
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addSelect(List<String> fields) {
        if (fields != null) {
            fields.forEach(item -> {
                addSelect(item);
            });
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addSelect(Map<String, String> fieldAlias) {
        if (fieldAlias != null) {
            for (String k : fieldAlias.keySet()) {
                addSelect(k, fieldAlias.get(k));
            }
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addSelectCount(String field, String alias) {
        return addSelect(field, SelectAggregate.COUNT, alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectCount(Attribute<T, ?> attribute, String alias) {
        return addSelectCount(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectCount(String field) {
        return addSelect(field, SelectAggregate.COUNT, field);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectCount(Attribute<T, ?> attribute) {
        return addSelectCount(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addSelectUpper(String field) {
        return addSelect(field, SelectAggregate.UPPER, field);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectUpper(Attribute<T, ?> attribute) {
        return addSelectUpper(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addSelectUpper(String field, String alias) {
        return addSelect(field, SelectAggregate.UPPER, alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectUpper(Attribute<T, ?> attribute, String alias) {
        return addSelectUpper(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectLower(String field) {
        return addSelect(field, SelectAggregate.LOWER, field);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectLower(Attribute<T, ?> attribute) {
        return addSelectLower(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addSelectLower(String field, String alias) {
        return addSelect(field, SelectAggregate.LOWER, alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectLower(Attribute<T, ?> attribute, String alias) {
        return addSelectLower(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectMax(String field, String alias) {
        return addSelect(field, SelectAggregate.MAX, alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectMax(Attribute<T, ?> attribute, String alias) {
        return addSelectMax(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectMax(String field) {
        return addSelect(field, SelectAggregate.MAX, field);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectMax(Attribute<T, ?> attribute) {
        return addSelectMax(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addSelectMin(String field, String alias) {
        return addSelect(field, SelectAggregate.MIN, alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectMin(Attribute<T, ?> attribute, String alias) {
        return addSelectMin(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectMin(String field) {
        return addSelect(field, SelectAggregate.MIN, field);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectMin(Attribute<T, ?> attribute) {
        return addSelectMin(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addSelectSum(String field, String alias) {
        return addSelect(field, SelectAggregate.SUM, alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectSum(Attribute<T, ?> attribute, String alias) {
        return addSelectSum(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectSum(String field) {
        return addSelect(field, SelectAggregate.SUM, field);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectSum(Attribute<T, ?> attribute) {
        return addSelectSum(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addSelect(String field) {
        return addSelect(field, SelectAggregate.FIELD, field);
    }

    @Override
    public CriteriaFilterImpl<T> addSelect(Attribute<T, ?> attribute) {
        return addSelect(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addOrder(Class<?> returnType, String... order)
            throws ApplicationRuntimeException {
        return addOrder(returnType, Arrays.asList(order));
    }

    @Override
    public CriteriaFilterImpl<T> addOrder(Class<?> returnType, List<String> order)
            throws ApplicationRuntimeException {
        StringBuilder orderAux = new StringBuilder();
        if (order != null) {
            for (String f : order) {
                if (orderAux.length() > 0) {
                    orderAux.append(",");
                }
                orderAux.append(f);
            }

            List<SimpleEntry<String, String>> listOrder =
                    new OrderMapper(returnType, orderAux.toString()).getOrder();

            for (SimpleEntry<String, String> se : listOrder) {
                if (se.getValue().equals("asc")) {
                    addOrderAsc(se.getKey());
                } else if (se.getValue().equals("desc")) {
                    addOrderDesc(se.getKey());
                }
            }
        }

        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addOrder(List<String> orderList) {
        if (orderList != null) {
            for (String o : orderList) {
                int aux;
                if ((aux = o.indexOf(":asc")) > 0) {
                    addOrderAsc(o.substring(0, aux));
                } else if ((aux = o.indexOf(":desc")) > 0) {
                    addOrderDesc(o.substring(0, aux));
                } else {
                    addOrderAsc(o);
                }
            }
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addOrder(String... order) {
        return addOrder(Arrays.asList(order));
    }

    @Override
    public CriteriaFilterImpl<T> addOrderAsc(String field) {
        this.listOrder.put(field, Order.ASC);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addOrderAsc(Attribute<T, ?> attribute) {
        return addOrderAsc(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addOrderDesc(String field) {
        this.listOrder.put(field, Order.DESC);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addOrderDesc(Attribute<T, ?> attribute) {
        return addOrderDesc(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addOrderAsc(List<String> fields) {
        if (fields != null) {
            for (String o : fields) {
                addOrderAsc(o);
            }
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addOrderDesc(List<String> fields) {
        if (fields != null) {
            for (String o : fields) {
                addOrderDesc(o);
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CriteriaFilterImpl<T> addOrderAsc(Attribute<T, ?>... fields) {
        if (fields != null) {
            for (Attribute<T, ?> o : fields) {
                addOrderAsc(o);
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CriteriaFilterImpl<T> addOrderDesc(Attribute<T, ?>... fields) {
        if (fields != null) {
            for (Attribute<T, ?> o : fields) {
                addOrderDesc(o);
            }
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereEqual(String field) {
        this.listWhere.put(field, Where.EQUAL);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereEqual(Attribute<T, ?> attribute) {
        return addWhereEqual(attribute.getName());
    }

    /**
     * 
     * @param field
     * @param where
     * @param values
     * @return
     */
    private <E> CriteriaFilterImpl<T> addWhereListValues(String field, Where where,
            List<E> values) {
        if (values != null) {
            List<E> listAux = new ArrayList<E>(values);
            listAux.remove(null);
            if (!listAux.isEmpty()) {
                this.whereRestriction.add(field, where, values);
            }
        }

        return this;
    }

    /**
     * 
     * @param field
     * @param where
     * @param values
     * @return
     */
    private <E> CriteriaFilterImpl<T> addWhereListValues(String field, Where where, E[] values) {
        if (values != null) {
            return addWhereListValues(field, where, Arrays.asList(values));
        }
        return this;
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereIn(String field, List<E> values) {
        return addWhereListValues(field, Where.IN, values);
    }

    /**
     * 
     * @param regexToAnalyse
     * @param search
     * @return
     */
    private boolean containsRegex(RegexWhere[] regexToAnalyse, RegexWhere rgxSearch) {
        for (RegexWhere rgx : regexToAnalyse) {
            if (rgx.equals(rgxSearch)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereRegex(String field, Class<?> fieldType, String value,
            RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch)
            throws ApplicationRuntimeException {
        if (StringUtils.isBlank(value)) {
            return this;
        }
        boolean added =
                configWhereRegex(field, fieldType, value, regexToAnalyse, defaultIfNotMatch);
        if (!added && defaultIfNotMatch != null) {
            ApplicationRuntimeException ae = new ApplicationRuntimeException(MessageSeverity.ERROR,
                    "error.regex-config", value, field);
            logger.error("addWhereRegex]", ae);
            throw ae;
        }
        return this;
    }

    /**
     * 
     * @author Jurandir C. Gonçalves <jurandir>
     * @since 19/06/2020
     *
     * @param type
     * @param date
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E extends Temporal> E toTemporal(final Class<E> type, final Date date) {
        if (LocalDate.class.isAssignableFrom(type)) {
            return (E) date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (LocalDateTime.class.isAssignableFrom(type)) {
            return (E) date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (LocalTime.class.isAssignableFrom(type)) {
            return (E) date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        }

        return null;
    }

    /**
     * 
     * @author Jurandir C. Gonçalves <jurandir>
     * @since 19/06/2020
     *
     * @param type
     * @param date
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E extends Temporal> E[] toTemporal(final Class<E> type, final Date[] date) {
        E[] tempAux = (E[]) Array.newInstance(type, date.length);
        for (int i = 0; i < tempAux.length; i++) {
            tempAux[i] = toTemporal(type, date[i]);
        }

        return tempAux;
    }

    /**
     * 
     * @author Jurandir C. Gonçalves <jurandir>
     * @since 19/06/2020
     *
     * @param type
     * @param field
     * @param startValue
     * @param endValue
     */
    private void addWhereBetweenDateOrTemporal(Class<?> type, String field, Date startValue,
            Date endValue) {
        if (Date.class.isAssignableFrom(type)) {
            addWhereBetween(field, startValue, endValue);
        } else if (LocalTime.class.isAssignableFrom(type)) {
            addWhereBetween(field, toTemporal(LocalTime.class, startValue),
                    toTemporal(LocalTime.class, endValue));
        } else if (LocalDate.class.isAssignableFrom(type)) {
            addWhereBetween(field, toTemporal(LocalDate.class, startValue),
                    toTemporal(LocalDate.class, endValue));
        } else if (LocalDateTime.class.isAssignableFrom(type)) {
            addWhereBetween(field, toTemporal(LocalDateTime.class, startValue),
                    toTemporal(LocalDateTime.class, endValue));
        }
    }

    /**
     * 
     * @author Jurandir C. Gonçalves <jurandir>
     * @since 20/06/2020
     *
     * @param type
     * @param field
     * @param dates
     */
    private void addWhereDateOrTemporal(Class<?> type, Where where, String field, Date[] dates) {
        if (Date.class.isAssignableFrom(type)) {
            addWhereListValues(field, where, dates);
        } else if (LocalTime.class.isAssignableFrom(type)) {
            addWhereListValues(field, where, toTemporal(LocalTime.class, dates));
        } else if (LocalDate.class.isAssignableFrom(type)) {
            addWhereListValues(field, where, toTemporal(LocalDate.class, dates));
        } else if (LocalDateTime.class.isAssignableFrom(type)) {
            addWhereListValues(field, where, toTemporal(LocalDateTime.class, dates));
        }
    }

    /**
     * 
     * @author Jurandir C. Gonçalves <jurandir>
     * @since 20/06/2020
     *
     * @param type
     * @param where
     * @param field
     * @param date
     */
    private void addWhereDateOrTemporal(Class<?> type, Where where, String field, Date date) {
        if (Date.class.isAssignableFrom(type)) {
            this.whereRestriction.add(field, where, date);
        } else if (LocalTime.class.isAssignableFrom(type)) {
            this.whereRestriction.add(field, where, toTemporal(LocalTime.class, date));
        } else if (LocalDate.class.isAssignableFrom(type)) {
            this.whereRestriction.add(field, where, toTemporal(LocalDate.class, date));
        } else if (LocalDateTime.class.isAssignableFrom(type)) {
            this.whereRestriction.add(field, where, toTemporal(LocalDateTime.class, date));
        }
    }

    /**
     * 
     * @param field
     * @param fieldType
     * @param value
     * @param regexToAnalyse
     * @param defaultIfNotMatch
     * @return
     * @throws ApplicationRuntimeException
     */
    private boolean configWhereRegex(String field, Class<?> fieldType, String value,
            RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch)
            throws ApplicationRuntimeException {
        Where where = null;
        for (Where wh : Where.values()) {
            if (wh.exp != null
                    && (regexToAnalyse == null || containsRegex(regexToAnalyse, wh.regexWhere))
                    && checkRegex(value, wh.exp)) {
                where = wh;
                break;
            }
        }
        if (where != null) {
            Pattern p = Pattern.compile(where.exp);
            Matcher m = p.matcher(value);

            if (m.find()) {
                if (where.equals(Where.IS_NULL)) {
                    this.addWhereIsNull(field);
                } else if (where.equals(Where.IS_NOT_NULL)) {
                    this.addWhereIsNotNull(field);
                } else if (where.equals(Where.BETWEEN)) {
                    String[] val = m.group().split("\\s(btwn|between)\\s");
                    if (NumberUtils.isCreatable(val[0])) {
                        this.whereRestriction.add(field, Where.BETWEEN,
                                new Number[] {NumberUtils.createNumber(val[0]),
                                        NumberUtils.createNumber(val[1])});
                        return true;
                    } else {
                        Date dt1, dt2;
                        dt1 = parseDate(val[0]);
                        dt2 = parseDate(val[1]);
                        if (val[1].matches(regexPatternDate)) {
                            dt2 = DateUtils.setHours(dt2, 23);
                            dt2 = DateUtils.setMinutes(dt2, 59);
                            dt2 = DateUtils.setSeconds(dt2, 59);
                            dt2 = DateUtils.setMilliseconds(dt2, 999);
                        }
                        addWhereBetweenDateOrTemporal(fieldType, field, dt1, dt2);
                        return true;
                    }
                } else if (where.equals(Where.IN) || where.equals(Where.NOT_IN)) {
                    String[] val = m.group().replace(" ", "").split("\\,");

                    if (val[0] != null
                            && (Date.class.isAssignableFrom(fieldType)
                                    || Calendar.class.isAssignableFrom(fieldType))
                            || Temporal.class.isAssignableFrom(fieldType)) { // ||
                                                                             // val[0].matches(regexPatternDateTime))){

                        Date[] dates = new Date[val.length];
                        for (int i = 0; i < val.length; i++) {
                            dates[i] = parseDate(val[i]);
                        }

                        addWhereDateOrTemporal(fieldType, where, field, dates);
                        return true;
                    } else {
                        if (where.equals(Where.IN)) {
                            addWhereIn(field, val);
                            return true;
                        } else {
                            addWhereNotIn(field, val);
                            return true;
                        }
                    }
                } else {
                    String val = m.group();
                    if (Date.class.isAssignableFrom(fieldType)
                            || Calendar.class.isAssignableFrom(fieldType)
                            || Temporal.class.isAssignableFrom(fieldType)) {// val.matches(regexPatternDateTime)){
                        Date dt = parseDate(m.group());
                        if (val.matches(regexPatternDate) && (where.equals(Where.LESS_THAN)
                                || where.equals(Where.LESS_THAN_OR_EQUAL_TO))) {
                            DateUtils.setHours(dt, 23);
                            DateUtils.setMinutes(dt, 59);
                            DateUtils.setSeconds(dt, 59);
                            DateUtils.setMilliseconds(dt, 999);
                        }
                        addWhereDateOrTemporal(fieldType, where, field, dt);
                        return true;
                    } else if (Number.class.isAssignableFrom(fieldType)) {
                        this.whereRestriction.add(field, where, NumberUtils.createNumber(val));
                        return true;
                    } else {
                        this.whereRestriction.add(field, where, val);
                        return true;
                    }
                }
            }
        } else {
            boolean found = false;
            if (ArrayUtils.contains(regexToAnalyse, RegexWhere.MULTI)) {
                final String multiWhere = "^(<|<=|=|!=|>=|>|)" + regexPatternDateTimeOrNumber
                        + "(\\s?&\\s?(<|<=|=|!=|>=|>|)" + regexPatternDateTimeOrNumber + "){1,}$";
                Pattern p = Pattern.compile(multiWhere);
                Matcher m = p.matcher(value);

                if (m.find()) {
                    found = true;
                    String[] val = m.group().split(";");

                    for (String v : val) {
                        boolean add = configWhereRegex(field, fieldType, v,
                                new RegexWhere[] {RegexWhere.LESS_THAN,
                                        RegexWhere.LESS_THAN_OR_EQUAL_TO, RegexWhere.EQUAL,
                                        RegexWhere.NOT_EQUAL, RegexWhere.GREATER_THAN,
                                        RegexWhere.GREATER_THAN_OR_EQUAL_TO},
                                defaultIfNotMatch);

                        if (!add) {
                            return false;
                        }
                    }
                    return true;
                }
            }

            if (!found && defaultIfNotMatch != null) {
                if (defaultIfNotMatch.equals(RegexWhere.EQUAL)
                        && value.matches("^" + regexPatternDateTime + "$")) {
                    this.whereRestriction.add(field, Where.EQUAL, parseDate(value));
                    return true;
                } else if (defaultIfNotMatch.equals(RegexWhere.EQUAL)
                        && value.matches("^[a-zA-Z0-9]" + regexPatternAlpha + "$")) {
                    this.whereRestriction.add(field, Where.EQUAL, value);
                    return true;
                } else {
                    boolean ret = configWhereRegex(field, fieldType, value,
                            new RegexWhere[] {defaultIfNotMatch}, null);
                    if (!ret) {
                        Where whereAux = getWhereFromRegexWhere(defaultIfNotMatch);
                        if (whereAux != null) {
                            this.whereRestriction.add(field, whereAux, value);
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 
     * @param dateValue
     * @return
     */
    private static Date parseDate(String dateValue) {
        try {
            return DateUtil.parseDate(dateValue);
        } catch (ParseException e) {
            throw new ApplicationRuntimeException(MessageSeverity.ERROR, "error.parse-date", e,
                    dateValue);
        }
    }

    /**
     * 
     * @autor jurandirjcg
     * @param regexWhr
     * @return
     */
    private Where getWhereFromRegexWhere(RegexWhere regexWhr) {
        for (Where whr : Where.values()) {
            if (whr.regexWhere != null && whr.regexWhere.equals(regexWhr)) {
                return whr;
            }
        }
        return null;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereRegex(Attribute<T, ?> attribute, String value,
            RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch)
            throws ApplicationRuntimeException {
        if (StringUtils.isBlank(value)) {
            return this;
        }

        boolean added = configWhereRegex(attribute.getName(), attribute.getJavaType(), value,
                regexToAnalyse, defaultIfNotMatch);
        if (!added && defaultIfNotMatch != null) {
            ApplicationRuntimeException ae = new ApplicationRuntimeException(MessageSeverity.ERROR,
                    "error.regex-config", value, attribute.getName());
            logger.error("[addWhereRegex]", ae.getMessage());
            throw ae;
        }
        return this;
    }

    /**
     * 
     * @param value
     * @param regex
     * @return
     */
    private boolean checkRegex(String value, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(value);
        return m.find();
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereIn(Attribute<T, E> attribute, List<E> values) {
        return addWhereListValues(attribute.getName(), Where.IN, values);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterImpl<T> addWhereIn(String field, E... values) {
        return addWhereListValues(field, Where.IN, values);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterImpl<T> addWhereIn(Attribute<T, E> attribute, E... values) {
        return addWhereListValues(attribute.getName(), Where.IN, values);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterImpl<T> addWhereNotIn(String field, E... values) {
        return addWhereListValues(field, Where.NOT_IN, values);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> CriteriaFilterImpl<T> addWhereNotIn(Attribute<T, E> attribute, E... values) {
        return addWhereListValues(attribute.getName(), Where.NOT_IN, values);
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereNotIn(String field, List<E> values) {
        return addWhereListValues(field, Where.NOT_IN, values);
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereNotIn(Attribute<T, E> attribute, List<E> values) {
        return addWhereListValues(attribute.getName(), Where.NOT_IN, values);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterImpl<T> addWhereEqual(String field, E... values) {
        return addWhereListValues(field, Where.EQUAL, values);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterImpl<T> addWhereEqual(Attribute<T, E> attribute, E... values) {
        return addWhereListValues(attribute.getName(), Where.EQUAL, values);
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereEqual(String field, List<E> values) {
        return addWhereListValues(field, Where.EQUAL, values);
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereEqual(Attribute<T, E> attribute, List<E> values) {
        return addWhereListValues(attribute.getName(), Where.EQUAL, values);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterImpl<T> addWhereNotEqual(String field, E... values) {
        return addWhereListValues(field, Where.NOT_EQUAL, values);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> CriteriaFilterImpl<T> addWhereNotEqual(Attribute<T, E> attribute, E... values) {
        return addWhereListValues(attribute.getName(), Where.NOT_EQUAL, values);
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereNotEqual(String field, List<E> values) {
        return addWhereListValues(field, Where.NOT_EQUAL, values);
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereNotEqual(Attribute<T, E> attribute, List<E> values) {
        return addWhereListValues(attribute.getName(), Where.NOT_EQUAL, values);
    }

    /**
     * 
     * @param listWhere
     * @return
     */
    public CriteriaFilterImpl<T> addAllWhere(Map<String, Where> listWhere) {
        this.listWhere.putAll(listWhere);
        return this;
    }

    /**
     * 
     * @param listComplexWhere
     * @return
     */
    public CriteriaFilterImpl<T> addAllWhereComplex(
            Map<String, List<SimpleEntry<Where, ?>>> listComplexWhere) {
        this.whereRestriction.getRestrictions().putAll(listComplexWhere);
        return this;
    }

    /**
     * 
     * @param listJoin
     * @return
     */
    public CriteriaFilterImpl<T> addAllJoin(Map<String, JoinMapper> listJoin) {
        this.listJoin.putAll(listJoin);
        return this;
    }

    /**
     * 
     * @param listSelection
     * @return
     */
    public CriteriaFilterImpl<T> addAllSelection(
            Map<String, SimpleEntry<SelectAggregate, String>> listSelection) {
        this.listSelection.putAll(listSelection);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(String field, Integer startValue,
            Integer endValue) {
        this.whereRestriction.add(field, Where.BETWEEN, new Integer[] {startValue, endValue});
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(String field, Double startValue, Double endValue) {
        this.whereRestriction.add(field, Where.BETWEEN, new Double[] {startValue, endValue});
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(Attribute<T, Integer> attribute,
            Integer startValue, Integer endValue) {
        return addWhereBetween(attribute.getName(), startValue, endValue);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(String field, Short startValue, Short endValue) {
        this.whereRestriction.add(field, Where.BETWEEN, new Short[] {startValue, endValue});
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(Attribute<T, Short> attribute, Short startValue,
            Short endValue) {
        return addWhereBetween(attribute.getName(), startValue, endValue);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(String field, Long startValue, Long endValue) {
        this.whereRestriction.add(field, Where.BETWEEN, new Long[] {startValue, endValue});
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(Attribute<T, Long> attribute, Long startValue,
            Long endValue) {
        return addWhereBetween(attribute.getName(), startValue, endValue);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanField(String field, String anotherField) {
        this.whereRestriction.add(field, Where.LESS_THAN_OTHER_FIELD, anotherField);
        return this;
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereLessThanField(Attribute<T, E> attribute,
            Attribute<T, E> anotherAttribute) {
        return addWhereLessThanField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanField(String field, String anotherField) {
        this.whereRestriction.add(field, Where.GREATER_THAN_OTHER_FIELD, anotherField);
        return this;
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereGreaterThanField(Attribute<T, E> attribute,
            Attribute<T, E> anotherAttribute) {
        return addWhereGreaterThanField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualToField(String field, String anotherField) {
        this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO_OTHER_FIELD, anotherField);
        return this;
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereLessThanOrEqualToField(Attribute<T, E> attribute,
            Attribute<T, E> anotherAttribute) {
        return addWhereLessThanOrEqualToField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualToField(String field,
            String anotherField) {
        this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO_OTHER_FIELD, anotherField);
        return this;
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereGreaterThanOrEqualToField(Attribute<T, E> attribute,
            Attribute<T, E> anotherAttribute) {
        return addWhereGreaterThanOrEqualToField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereEqualField(String field, String anotherField) {
        this.whereRestriction.add(field, Where.EQUAL_OTHER_FIELD, anotherField);
        return this;
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereEqualField(Attribute<T, E> attribute,
            Attribute<T, E> anotherAttribute) {
        return addWhereEqualField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereEqualField(Attribute<T, ?> attribute,
            ComplexAttribute anotherAttribute) {
        return addWhereEqualField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotEqualField(String field, String anotherField) {
        this.whereRestriction.add(field, Where.NOT_EQUAL_OTHER_FIELD, anotherField);
        return this;
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereNotEqualField(Attribute<T, E> attribute,
            Attribute<T, E> anotherAttribute) {
        return addWhereNotEqualField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotEqualField(Attribute<T, ?> attribute,
            ComplexAttribute anotherAttribute) {
        return addWhereNotEqualField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(String field, Date startValue, Date endValue) {
        this.whereRestriction.add(field, Where.BETWEEN, new Date[] {startValue, endValue});
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(Attribute<T, Date> attribute, Date startValue,
            Date endValue) {
        return addWhereBetween(attribute.getName(), startValue, endValue);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(String field, LocalDate startValue,
            LocalDate endValue) {
        this.whereRestriction.add(field, Where.BETWEEN, new LocalDate[] {startValue, endValue});
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(Attribute<T, LocalDate> attribute,
            LocalDate startValue, LocalDate endValue) {
        return addWhereBetween(attribute.getName(), startValue, endValue);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(String field, LocalDateTime startValue,
            LocalDateTime endValue) {
        this.whereRestriction.add(field, Where.BETWEEN, new LocalDateTime[] {startValue, endValue});
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(Attribute<T, LocalDateTime> attribute,
            LocalDateTime startValue, LocalDateTime endValue) {
        return addWhereBetween(attribute.getName(), startValue, endValue);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(String field, LocalTime startValue,
            LocalTime endValue) {
        this.whereRestriction.add(field, Where.BETWEEN, new LocalTime[] {startValue, endValue});
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(Attribute<T, LocalTime> attribute,
            LocalTime startValue, LocalTime endValue) {
        return addWhereBetween(attribute.getName(), startValue, endValue);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThan(String field) {
        this.listWhere.put(field, Where.GREATER_THAN);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThan(Attribute<T, ?> attribute) {
        return addWhereGreaterThan(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(String field) {
        this.listWhere.put(field, Where.GREATER_THAN_OR_EQUAL_TO);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(Attribute<T, ?> attribute) {
        return addWhereGreaterThanOrEqualTo(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereIn(String field) {
        this.listWhere.put(field, Where.IN);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereIn(Attribute<T, ?> attribute) {
        return addWhereIn(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereIsNotNull(String field) {
        this.whereRestriction.add(field, Where.IS_NOT_NULL, null);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereIsNotNull(Attribute<T, ?> attribute) {
        return addWhereIsNotNull(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereIsNull(String field) {
        this.whereRestriction.add(field, Where.IS_NULL, null);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereIsNull(Attribute<T, ?> attribute) {
        return addWhereIsNull(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(String field) {
        this.listWhere.put(field, Where.LESS_THAN);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(Attribute<T, ?> attribute) {
        return addWhereLessThan(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(String field) {
        this.listWhere.put(field, Where.LESS_THAN_OR_EQUAL_TO);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(Attribute<T, ?> attribute) {
        return addWhereLessThanOrEqualTo(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLike(String field, MatchMode matchMode) {
        switch (matchMode) {
            case ANYWHERE:
                this.listWhere.put(field, Where.LIKE_MATCH_ANYWHERE);
                break;
            case START:
                this.listWhere.put(field, Where.LIKE_MATCH_START);
                break;
            case END:
                this.listWhere.put(field, Where.LIKE_MATCH_END);
                break;
            case EXACT:
            default:
                this.listWhere.put(field, Where.LIKE_EXACT);
                break;
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotLike(String field, MatchMode matchMode) {
        switch (matchMode) {
            case ANYWHERE:
                this.listWhere.put(field, Where.LIKE_NOT_MATCH_ANYWHERE);
                break;
            case START:
                this.listWhere.put(field, Where.LIKE_NOT_MATCH_START);
                break;
            case END:
                this.listWhere.put(field, Where.LIKE_NOT_MATCH_END);
                break;
            case EXACT:
            default:
                this.listWhere.put(field, Where.LIKE_NOT_EXACT);
                break;
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotLike(Attribute<T, String> attribute,
            MatchMode matchMode) {
        return addWhereNotLike(attribute.getName(), matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLike(Attribute<T, String> attribute, MatchMode matchMode) {
        return addWhereLike(attribute.getName(), matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereILike(String field, MatchMode matchMode) {
        switch (matchMode) {
            case ANYWHERE:
                this.listWhere.put(field, Where.ILIKE_MATCH_ANYWHERE);
                break;
            case START:
                this.listWhere.put(field, Where.ILIKE_MATCH_START);
                break;
            case END:
                this.listWhere.put(field, Where.ILIKE_MATCH_END);
                break;
            case EXACT:
            default:
                this.listWhere.put(field, Where.ILIKE_EXACT);
                break;
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotILike(String field, MatchMode matchMode) {
        switch (matchMode) {
            case ANYWHERE:
                this.listWhere.put(field, Where.ILIKE_NOT_MATCH_ANYWHERE);
                break;
            case START:
                this.listWhere.put(field, Where.ILIKE_NOT_MATCH_START);
                break;
            case END:
                this.listWhere.put(field, Where.ILIKE_NOT_MATCH_END);
                break;
            case EXACT:
            default:
                this.listWhere.put(field, Where.ILIKE_NOT_EXACT);
                break;
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotILike(Attribute<T, String> attribute,
            MatchMode matchMode) {
        return addWhereNotILike(attribute.getName(), matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereILike(Attribute<T, String> attribute,
            MatchMode matchMode) {
        return addWhereILike(attribute.getName(), matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotEqual(String field) {
        this.listWhere.put(field, Where.NOT_EQUAL);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotEqual(Attribute<T, ?> attribute) {
        return addWhereNotEqual(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotIn(String field) {
        this.listWhere.put(field, Where.NOT_IN);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotIn(Attribute<T, ?> attribute) {
        return addWhereNotIn(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addGroupBy(String field) {
        this.listGroupBy.add(field);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addGroupBy(Attribute<T, ?> attribute) {
        return addGroupBy(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addJoin(String field, JoinType joinType, boolean fetch) {
        this.listJoin.put(field, new JoinMapper(joinType, fetch, false));
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addJoin(Attribute<T, ?> attribute, JoinType joinType,
            boolean fetch) {
        this.listJoin.put(attribute.getName(), new JoinMapper(joinType, fetch, false));
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addJoin(String field, JoinType joinType) {
        return addJoin(field, joinType, false);
    }

    @Override
    public CriteriaFilterImpl<T> addJoin(Attribute<T, ?> attribute, JoinType joinType) {
        return addJoin(attribute, joinType, false);
    }

    @Override
    public CriteriaFilterImpl<T> addJoin(String field) {
        return addJoin(field, JoinType.INNER, false);
    }

    @Override
    public CriteriaFilterImpl<T> addJoin(Attribute<T, ?> attribute) {
        return addJoin(attribute, JoinType.INNER, false);
    }

    public Map<Class<?>, Map<String, SimpleEntry<SelectAggregate, String>>> getCollectionSelection() {
        return collectionSelection;
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereEqual(String field, E value) {
        this.whereRestriction.add(field, Where.EQUAL, value);
        return this;
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereEqual(Attribute<T, E> attribute, E value) {
        return addWhereEqual(attribute.getName(), value);
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereNotEqual(String field, E value) {
        this.whereRestriction.add(field, Where.NOT_EQUAL, value);
        return this;
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereNotEqual(Attribute<T, E> attribute, E value) {
        return addWhereNotEqual(attribute.getName(), value);
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereGreaterThan(String field, Date value) {
        this.whereRestriction.add(field, Where.GREATER_THAN, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThan(Attribute<T, Date> attribute, Date value) {
        return addWhereGreaterThan(attribute.getName(), value);
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereGreaterThan(String field, Number value) {
        this.whereRestriction.add(field, Where.GREATER_THAN, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThan(Attribute<T, Number> attribute, Number value) {
        return addWhereGreaterThan(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(String field, Date value) {
        this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(Attribute<T, Date> attribute,
            Date value) {
        return addWhereGreaterThanOrEqualTo(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(String field, Number value) {
        this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(Attribute<T, Number> attribute,
            Number value) {
        return addWhereGreaterThanOrEqualTo(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(String field, Date value) {
        this.whereRestriction.add(field, Where.LESS_THAN, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(Attribute<T, Date> attribute, Date value) {
        return addWhereLessThan(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(String field, Number value) {
        this.whereRestriction.add(field, Where.LESS_THAN, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(Attribute<T, Number> attribute, Number value) {
        return addWhereLessThan(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(String field, Date value) {
        this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(Attribute<T, Date> attribute,
            Date value) {
        return addWhereLessThanOrEqualTo(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(String field, Number value) {
        this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(Attribute<T, Number> attribute,
            Number value) {
        return addWhereLessThanOrEqualTo(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLike(String field, String value, MatchMode matchMode) {
        switch (matchMode) {
            case ANYWHERE:
                this.whereRestriction.add(field, Where.LIKE_MATCH_ANYWHERE, value);
                break;
            case START:
                this.whereRestriction.add(field, Where.LIKE_MATCH_START, value);
                break;
            case END:
                this.whereRestriction.add(field, Where.LIKE_MATCH_END, value);
                break;
            case EXACT:
            default:
                this.whereRestriction.add(field, Where.LIKE_EXACT, value);
                break;
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotLike(String field, String value, MatchMode matchMode) {
        switch (matchMode) {
            case ANYWHERE:
                this.whereRestriction.add(field, Where.LIKE_NOT_MATCH_ANYWHERE, value);
                break;
            case START:
                this.whereRestriction.add(field, Where.LIKE_NOT_MATCH_START, value);
                break;
            case END:
                this.whereRestriction.add(field, Where.LIKE_NOT_MATCH_END, value);
                break;
            case EXACT:
            default:
                this.whereRestriction.add(field, Where.LIKE_NOT_EXACT, value);
                break;
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereILike(String field, String value, MatchMode matchMode) {
        switch (matchMode) {
            case ANYWHERE:
                this.whereRestriction.add(field, Where.ILIKE_MATCH_ANYWHERE, value);
                break;
            case START:
                this.whereRestriction.add(field, Where.ILIKE_MATCH_START, value);
                break;
            case END:
                this.whereRestriction.add(field, Where.ILIKE_MATCH_END, value);
                break;
            case EXACT:
            default:
                this.whereRestriction.add(field, Where.ILIKE_EXACT, value);
                break;
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotILike(String field, String value, MatchMode matchMode) {
        switch (matchMode) {
            case ANYWHERE:
                this.whereRestriction.add(field, Where.ILIKE_NOT_MATCH_ANYWHERE, value);
                break;
            case START:
                this.whereRestriction.add(field, Where.ILIKE_NOT_MATCH_START, value);
                break;
            case END:
                this.whereRestriction.add(field, Where.ILIKE_NOT_MATCH_END, value);
                break;
            case EXACT:
            default:
                this.whereRestriction.add(field, Where.ILIKE_NOT_EXACT, value);
                break;
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotLike(Attribute<T, String> attribute, String value,
            MatchMode matchMode) {
        return addWhereNotLike(attribute.getName(), value, matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLike(Attribute<T, String> attribute, String value,
            MatchMode matchMode) {
        return addWhereLike(attribute.getName(), value, matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereILike(Attribute<T, String> attribute, String value,
            MatchMode matchMode) {
        return addWhereILike(attribute.getName(), value, matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotILike(Attribute<T, String> attribute, String value,
            MatchMode matchMode) {
        return addWhereNotILike(attribute.getName(), value, matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereILike(ComplexAttribute attribute, String value,
            MatchMode matchMode) {
        return addWhereILike(attribute.getName(), value, matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereILike(ComplexAttribute attribute, MatchMode matchMode) {
        return addWhereILike(attribute.getName(), matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotILike(ComplexAttribute attribute, String value,
            MatchMode matchMode) {
        return addWhereNotILike(attribute.getName(), value, matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotLike(ComplexAttribute attribute, String value,
            MatchMode matchMode) {
        return addWhereNotLike(attribute.getName(), value, matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLike(ComplexAttribute attribute, String value,
            MatchMode matchMode) {
        return addWhereLike(attribute.getName(), value, matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotILike(ComplexAttribute attribute, MatchMode matchMode) {
        return addWhereNotILike(attribute.getName(), matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotLike(ComplexAttribute attribute, MatchMode matchMode) {
        return addWhereNotLike(attribute.getName(), matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLike(ComplexAttribute attribute, MatchMode matchMode) {
        return addWhereLike(attribute.getName(), matchMode);
    }

    @Override
    public CriteriaFilterImpl<T> addSelect(ComplexAttribute attribute, String alias) {
        return addSelect(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelect(ComplexAttribute... attributes) {
        for (ComplexAttribute ca : attributes) {
            this.addSelect(ca.getName());
        }
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addSelectCount(ComplexAttribute attribute, String alias) {
        return addSelectCount(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectCount(ComplexAttribute attribute) {
        return addSelectCount(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addSelectUpper(ComplexAttribute attribute) {
        return addSelectUpper(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addSelectUpper(ComplexAttribute attribute, String alias) {
        return addSelectUpper(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectLower(ComplexAttribute attribute) {
        return addSelectLower(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addSelectLower(ComplexAttribute attribute, String alias) {
        return addSelectLower(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectMax(ComplexAttribute attribute, String alias) {
        return addSelectMax(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectMax(ComplexAttribute attribute) {
        return addSelectMax(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addSelectMin(ComplexAttribute attribute, String alias) {
        return addSelectMin(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectMin(ComplexAttribute attribute) {
        return addSelectMin(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addSelectSum(ComplexAttribute attribute, String alias) {
        return addSelectSum(attribute.getName(), alias);
    }

    @Override
    public CriteriaFilterImpl<T> addSelectSum(ComplexAttribute attribute) {
        return addSelectSum(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addSelect(ComplexAttribute attribute) {
        return addSelect(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addOrderAsc(ComplexAttribute attribute) {
        return addOrderAsc(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addOrderDesc(ComplexAttribute attribute) {
        return addOrderDesc(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereEqual(ComplexAttribute attribute) {
        return addWhereEqual(attribute.getName());
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereIn(ComplexAttribute attribute, List<E> values) {
        return addWhereIn(attribute.getName(), values);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> CriteriaFilterImpl<T> addWhereIn(ComplexAttribute attribute, E... values) {
        return addWhereIn(attribute.getName(), values);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> CriteriaFilterImpl<T> addWhereNotIn(ComplexAttribute attribute, E... values) {
        return addWhereNotIn(attribute.getName(), values);
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereNotIn(ComplexAttribute attribute, List<E> values) {
        return addWhereNotIn(attribute.getName(), values);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> CriteriaFilterImpl<T> addWhereEqual(ComplexAttribute attribute, E... values) {
        return addWhereEqual(attribute.getName(), values);
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereEqual(ComplexAttribute attribute, List<E> values) {
        return addWhereEqual(attribute.getName(), values);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> CriteriaFilterImpl<T> addWhereNotEqual(ComplexAttribute attribute, E... values) {
        return addWhereNotEqual(attribute.getName(), values);
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereNotEqual(ComplexAttribute attribute, List<E> values) {
        return addWhereNotEqual(attribute.getName(), values);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(ComplexAttribute attribute, Integer startValue,
            Integer endValue) {
        return addWhereBetween(attribute.getName(), startValue, endValue);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(ComplexAttribute attribute, Short startValue,
            Short endValue) {
        return addWhereBetween(attribute.getName(), startValue, endValue);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(ComplexAttribute attribute, Long startValue,
            Long endValue) {
        return addWhereBetween(attribute.getName(), startValue, endValue);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanField(ComplexAttribute attribute,
            Attribute<T, ?> anotherAttribute) {
        return addWhereLessThanField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanField(ComplexAttribute attribute,
            Attribute<T, ?> anotherAttribute) {
        return addWhereGreaterThanField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute,
            Attribute<T, ?> anotherAttribute) {
        return addWhereLessThanOrEqualToField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute,
            Attribute<T, ?> anotherAttribute) {
        return addWhereGreaterThanOrEqualToField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereEqualField(ComplexAttribute attribute,
            Attribute<T, ?> anotherAttribute) {
        return addWhereEqualField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotEqualField(ComplexAttribute attribute,
            Attribute<T, ?> anotherAttribute) {
        return addWhereNotEqualField(attribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(ComplexAttribute attribute, Date startValue,
            Date endValue) {
        return addWhereBetween(attribute.getName(), startValue, endValue);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(ComplexAttribute attribute, LocalDate startValue,
            LocalDate endValue) {
        return addWhereBetween(attribute.getName(), startValue, endValue);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereBetween(ComplexAttribute attribute,
            LocalDateTime startValue, LocalDateTime endValue) {
        return addWhereBetween(attribute.getName(), startValue, endValue);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThan(ComplexAttribute attribute) {
        return addWhereGreaterThan(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute) {
        return addWhereGreaterThanOrEqualTo(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereIsNotNull(ComplexAttribute attribute) {
        return addWhereIsNotNull(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereIsNull(ComplexAttribute attribute) {
        return addWhereIsNull(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereIn(ComplexAttribute attribute) {
        return addWhereIn(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(ComplexAttribute attribute) {
        return addWhereLessThan(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute) {
        return addWhereLessThanOrEqualTo(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotEqual(ComplexAttribute attribute) {
        return addWhereNotEqual(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotIn(ComplexAttribute attribute) {
        return addWhereNotIn(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addGroupBy(ComplexAttribute attribute) {
        return addGroupBy(attribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addJoin(ComplexAttribute attribute, JoinType joinType,
            boolean fetch) {
        return addJoin(attribute.getName(), joinType, fetch);
    }

    @Override
    public CriteriaFilterImpl<T> addJoin(ComplexAttribute attribute, JoinType joinType) {
        return addJoin(attribute.getName(), joinType);
    }

    @Override
    public CriteriaFilterImpl<T> addJoin(ComplexAttribute attribute) {
        return addJoin(attribute.getName());
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereEqual(ComplexAttribute attribute, E value) {
        return addWhereEqual(attribute.getName(), value);
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereNotEqual(ComplexAttribute attribute, E value) {
        return addWhereNotEqual(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThan(ComplexAttribute attribute, Date value) {
        return addWhereGreaterThan(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThan(ComplexAttribute attribute, Number value) {
        return addWhereGreaterThan(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute,
            Date value) {
        return addWhereGreaterThanOrEqualTo(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute,
            Number value) {
        return addWhereGreaterThanOrEqualTo(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(ComplexAttribute attribute, Date value) {
        return addWhereLessThan(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(ComplexAttribute attribute, Number value) {
        return addWhereLessThan(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Date value) {
        return addWhereLessThanOrEqualTo(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute,
            Number value) {
        return addWhereLessThanOrEqualTo(attribute.getName(), value);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereRegex(ComplexAttribute attribute, String value,
            RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch)
            throws ApplicationRuntimeException {
        return addWhereRegex(attribute.getName(), attribute.getFieldType(), value, regexToAnalyse,
                defaultIfNotMatch);
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanField(ComplexAttribute attribute,
            ComplexAttribute anotherAttribute) {
        return addWhereLessThanField(anotherAttribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanField(ComplexAttribute attribute,
            ComplexAttribute anotherAttribute) {
        return addWhereGreaterThanField(anotherAttribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute,
            ComplexAttribute anotherAttribute) {
        return addWhereLessThanOrEqualToField(anotherAttribute.getName(),
                anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute,
            ComplexAttribute anotherAttribute) {
        return addWhereGreaterThanOrEqualToField(anotherAttribute.getName(),
                anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereEqualField(ComplexAttribute attribute,
            ComplexAttribute anotherAttribute) {
        return addWhereEqualField(anotherAttribute.getName(), anotherAttribute.getName());
    }

    @Override
    public CriteriaFilterImpl<T> addWhereNotEqualField(ComplexAttribute attribute,
            ComplexAttribute anotherAttribute) {
        return addWhereNotEqualField(anotherAttribute.getName(), anotherAttribute.getName());
    }

    @Override
    public <E> CriteriaFilterImpl<T> addUpdate(String field, E value) {
        this.listUpdate.put(field, value);
        return this;
    }

    @Override
    public <E> CriteriaFilterImpl<T> addUpdate(Attribute<T, ?> attribute, E value) {
        this.listUpdate.put(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addJoin(String field, JoinType joinType, boolean fetch,
            boolean force) {
        this.listJoin.put(field, new JoinMapper(joinType, fetch, force));
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addJoin(ComplexAttribute attribute, JoinType joinType,
            boolean fetch, boolean force) {
        this.addJoin(attribute.getName(), joinType, fetch, force);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addJoin(Attribute<T, ?> attribute, JoinType joinType,
            boolean fetch, boolean force) {
        this.addJoin(attribute.getName(), joinType, fetch, force);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThan(ComplexAttribute attribute, LocalDate value) {
        this.addWhereGreaterThan(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThan(ComplexAttribute attribute,
            LocalDateTime value) {
        this.addWhereGreaterThan(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute,
            LocalDate value) {
        this.addWhereGreaterThanOrEqualTo(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute,
            LocalDateTime value) {
        this.addWhereGreaterThanOrEqualTo(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(ComplexAttribute attribute, LocalDate value) {
        this.addWhereLessThan(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(ComplexAttribute attribute, LocalDateTime value) {
        this.addWhereLessThan(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute,
            LocalDate value) {
        this.addWhereLessThanOrEqualTo(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute,
            LocalDateTime value) {
        this.addWhereLessThanOrEqualTo(attribute.getName(), value);
        return this;
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereGreaterThan(String field, LocalDate value) {
        this.whereRestriction.add(field, Where.GREATER_THAN, value);
        return this;
    }

    @Override
    public <E> CriteriaFilterImpl<T> addWhereGreaterThan(String field, LocalDateTime value) {
        this.whereRestriction.add(field, Where.GREATER_THAN, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(String field, LocalDate value) {
        this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(String field, LocalDateTime value) {
        this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(String field, LocalDate value) {
        this.whereRestriction.add(field, Where.LESS_THAN, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(String field, LocalDateTime value) {
        this.whereRestriction.add(field, Where.LESS_THAN, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(String field, LocalDate value) {
        this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(String field, LocalDateTime value) {
        this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO, value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThan(Attribute<T, LocalDate> attribute,
            LocalDate value) {
        this.addWhereGreaterThan(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThan(Attribute<T, LocalDateTime> attribute,
            LocalDateTime value) {
        this.addWhereGreaterThan(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(Attribute<T, LocalDate> attribute,
            LocalDate value) {
        this.addWhereGreaterThanOrEqualTo(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(Attribute<T, LocalDateTime> attribute,
            LocalDateTime value) {
        this.addWhereGreaterThanOrEqualTo(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(Attribute<T, LocalDate> attribute,
            LocalDate value) {
        this.addWhereLessThan(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThan(Attribute<T, LocalDateTime> attribute,
            LocalDateTime value) {
        this.addWhereLessThan(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(Attribute<T, LocalDate> attribute,
            LocalDate value) {
        this.addWhereLessThanOrEqualTo(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(Attribute<T, LocalDateTime> attribute,
            LocalDateTime value) {
        this.addWhereLessThanOrEqualTo(attribute.getName(), value);
        return this;
    }

    @Override
    public CriteriaFilterImpl<T> addGroupBy(List<String> fields) {
        if (fields != null) {
            fields.forEach(item -> {
                addGroupBy(item);
            });
        }
        return this;
    }
}

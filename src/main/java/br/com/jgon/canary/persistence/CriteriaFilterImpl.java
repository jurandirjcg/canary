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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
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

import javax.inject.Inject;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.persistence.filter.ComplexAttribute;
import br.com.jgon.canary.persistence.filter.CriteriaFilterMetamodel;
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
class CriteriaFilterImpl<T> implements CriteriaFilterMetamodel<T> {
	
	private static final String regexPatternAlpha = "[a-zA-Z0-9\u00C0-\u00FF\\s_-]+";
	private static final String regexPatternDate = "(((0?[1-9]|[12][0-9]|3[01])[/-](0[1-9]|1[0-2])[/-]((19|20)\\d\\d))|((19|20)\\d\\d[-/](0[1-9]|1[012])[-/](0[1-9]|[12][0-9]|3[01])))";
	private static final String regexTime = "((0\\d|1\\d|2[0-3]):[0-5]\\d)?(:[0-5]\\d)?(.\\d\\d\\d)?(Z)?(\\+[0-2][0-4]:[0-5]\\d)?";
	//OLD private static final String regexPatternDateTime =  regexPatternDate +"(([\\s]?(0\\d|1\\d|2[0-3]):[0-5]\\d)?(:[0-5]\\d)?)?";
	private static final String regexPatternDateTime =  regexPatternDate +"(([\\s]|T|'T')?" + regexTime + ")?";
	private static final String regexPatternDateTimeOrNumber = "((" + regexPatternDateTime + ")|[0-9]+)";
	private static final String regexPatternMultiDateTimeOrNumber = "(([a-zA-Z0-9,\\s_-\u00C0-\u00FF]+)|[" + regexPatternDateTime + ",]+)";
	
	@Inject
	private Logger logger;
	/**
	 * Filtro de restricao
	 *
	 * @author Jurandir C. Goncalves
	 * 
	 * @version 1.0
	 *
	 */
	enum Where{
		IGNORE (null, null),
		EQUAL (						RegexWhere.EQUAL, 					"(?<=^\\=)"			+ regexPatternAlpha 				+ "$"),
		LESS_THAN (					RegexWhere.LESS_THAN, 				"(?<=^\\<)"			+ regexPatternDateTimeOrNumber 		+ "$"),
		LESS_THAN_OR_EQUAL_TO (		RegexWhere.LESS_THAN_OR_EQUAL_TO, 	"(?<=^\\<\\=)" 		+ regexPatternDateTimeOrNumber 		+ "$"),
		GREATER_THAN (				RegexWhere.GREATER_THAN,		 	"(?<=^\\>)"			+ regexPatternDateTimeOrNumber 		+ "$"),
		GREATER_THAN_OR_EQUAL_TO (	RegexWhere.GREATER_THAN_OR_EQUAL_TO,"(?<=^\\>\\=)" 		+ regexPatternDateTimeOrNumber 		+ "$"),
		NOT_EQUAL (					RegexWhere.NOT_EQUAL, 				"(?<=^\\!\\=)" 		+ regexPatternAlpha 				+ "$"),
		IN (						RegexWhere.IN, 						"(?<=^\\()"			+ regexPatternMultiDateTimeOrNumber + "(?=\\)$)"),
		NOT_IN (					RegexWhere.NOT_IN, 					"(?<=^!\\()"		+ regexPatternMultiDateTimeOrNumber	+ "(?=\\)$)"),
		LIKE_EXACT (				RegexWhere.LIKE_EXACT, 				"(?<=^\\=\\%)" 		+ regexPatternAlpha 				+ "(?!\\%$)"),
		LIKE_NOT_EXACT (			RegexWhere.LIKE_NOT_EXACT, 			"(?<=^\\!\\=\\%)" 	+ regexPatternAlpha 				+ "(?!\\%$)"),
		LIKE_MATCH_ANYWHERE (		RegexWhere.LIKE_MATCH_ANYWHERE, 	"(?<=^\\%)" 		+ regexPatternAlpha					+ "(?=(\\!)?\\%$)"),
		LIKE_MATCH_END (			RegexWhere.LIKE_MATCH_END, 			"(?<=^\\%)" 		+ regexPatternAlpha					+ "(?!\\%$)"),
		LIKE_MATCH_START (			RegexWhere.LIKE_MATCH_START, 		"(?<!^\\%)" 		+ regexPatternAlpha					+ "(?=\\%$)"),
		LIKE_NOT_MATCH_ANYWHERE (	RegexWhere.LIKE_NOT_MATCH_ANYWHERE, "(?<=^\\!\\%)" 		+ regexPatternAlpha					+ "(?=\\!\\%$)"),
		LIKE_NOT_MATCH_END (		RegexWhere.LIKE_NOT_MATCH_END, 		"(?<=^\\!\\%)" 		+ regexPatternAlpha 				+ "(?!\\%$)"),
		LIKE_NOT_MATCH_START (		RegexWhere.LIKE_NOT_MATCH_START,	"(?<!^\\%)" 		+ regexPatternAlpha 				+ "(?=\\!\\%$)"),
		ILIKE_EXACT (				RegexWhere.ILIKE_EXACT, 			"(?<=^\\=\\*)" 		+ regexPatternAlpha 				+ "(?!\\*$)"),
		ILIKE_NOT_EXACT (			RegexWhere.ILIKE_NOT_EXACT, 		"(?<=^\\!\\=\\*)" 	+ regexPatternAlpha 				+ "(?!\\*$)"),
		ILIKE_MATCH_ANYWHERE (		RegexWhere.ILIKE_MATCH_ANYWHERE, 	"(?<=^\\*)" 		+ regexPatternAlpha 				+ "(?=\\*$)"),
		ILIKE_MATCH_END (			RegexWhere.ILIKE_MATCH_END, 		"(?<=^\\*)" 		+ regexPatternAlpha 				+ "(?!\\*$)"),
		ILIKE_MATCH_START (			RegexWhere.ILIKE_MATCH_START, 		"(?<!^\\*)" 		+ regexPatternAlpha					+ "(?=\\*$)"),
		ILIKE_NOT_MATCH_ANYWHERE (	RegexWhere.ILIKE_NOT_MATCH_ANYWHERE,"(?<=^\\!\\*)" 		+ regexPatternAlpha 				+ "(?=\\!\\*$)"),
		ILIKE_NOT_MATCH_END (		RegexWhere.ILIKE_NOT_MATCH_END, 	"(?<=^\\!\\*)" 		+ regexPatternAlpha					+ "(?!\\*$)"),
		ILIKE_NOT_MATCH_START (		RegexWhere.ILIKE_NOT_MATCH_START, 	"(?<!^\\*)" 		+ regexPatternAlpha 				+ "(?=\\!\\*$)"),
		IS_NULL (					RegexWhere.IS_NULL, 				"^null$"),
		IS_NOT_NULL (				RegexWhere.IS_NOT_NULL,	 			"^not null$"),
		BETWEEN (					RegexWhere.BETWEEN,					"(?<=^)" 			+ regexPatternDateTimeOrNumber 		+ "(\\s(btwn|between)\\s)" + regexPatternDateTimeOrNumber + "(?=$)"),
		EQUAL_OTHER_FIELD (null, null),
		LESS_THAN_OTHER_FIELD (null, null),
		GREATER_THAN_OTHER_FIELD (null ,null),
		LESS_THAN_OR_EQUAL_TO_OTHER_FIELD (null, null),
		GREATER_THAN_OR_EQUAL_TO_OTHER_FIELD (null, null),
		NOT_EQUAL_OTHER_FIELD (null, null);
		
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
	enum Order{
		ASC,
		DESC
	}
	/**
	 * Filtro de selecao
	 * 
	 */
	enum SelectAggregate{
		FIELD,
		COUNT,
		MAX,
		MIN,
		SUM,
		UPPER,
		LOWER
	}	
			
	private boolean collectionSelectionControl = true;
	
	private Map<String, Where> listWhere = new LinkedHashMap<String, Where>(0);
	private WhereRestriction whereRestriction = new WhereRestriction();
	private Map<String, SimpleEntry<SelectAggregate, String>> listSelection = new LinkedHashMap<String, SimpleEntry<SelectAggregate, String>>(0);
	private Map<Class<?>, Map<String, SimpleEntry<SelectAggregate, String>>> collectionSelection = new LinkedHashMap<Class<?>, Map<String, SimpleEntry<SelectAggregate, String>>>();
	private Map<String, Order> listOrder = new LinkedHashMap<String, Order>(0);
	private Set<String> listGroupBy = new LinkedHashSet<String>();
	private Map<String, SimpleEntry<JoinType, Boolean>> listJoin = new LinkedHashMap<String, SimpleEntry<JoinType, Boolean>>();
	private Map<String, Object> listUpdate = new LinkedHashMap<String, Object>();
	private T objBase;
	private Class<T> objClass;
	
	/**
	 * 
	 * @param objBase
	 */
	public CriteriaFilterImpl(T objBase, Class<T> objClass){
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
	public T getObjBase(){
		return this.objBase;
	}
	/**
	 * 
	 * @param objBase
	 */
	public void setObjBase(T objBase){
		this.objBase = objBase;
	}
	/**
	 * 
	 * @param field
	 * @return
	 */
	public Where getWhere(String field){
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
	public Map<String, Object> getListUpdate(){
		return listUpdate;
	}
	/**
	 * 
	 * @param fieldName
	 * @return
	 */
	public List<SimpleEntry<Where, ?>> getWhereRestriction(String fieldName){
		return this.whereRestriction.getRestrictions(fieldName);
	}
	/**
	 * 
	 * @return
	 */
	public WhereRestriction getWhereRestriction(){
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
	public Set<String> getListGroupBy(){
		return this.listGroupBy;
	}
	/**
	 * 
	 * @return
	 */
	public Map<String, SimpleEntry<JoinType, Boolean>> getListJoin(){
		return this.listJoin;
	}
	/**
	 * 
	 * @return
	 */
	public Map<String, SimpleEntry<SelectAggregate, String>> getListSelection(){
		return this.listSelection;
	}
	/**
	 * 
	 * @param field
	 * @param selectFunction
	 * @param alias
	 * @return
	 */
	private CriteriaFilterMetamodel<T> addSelect(String field, SelectAggregate selectFunction, String alias){
		this.listSelection.put(field, new SimpleEntry<CriteriaFilterImpl.SelectAggregate, String>(selectFunction, alias));
		return this;
	}
	/**
	 * 
	 * @param returnType
	 * @return
	 * @throws ApplicationException 
	 */
	public CriteriaFilterMetamodel<T> addSelect(Class<?> returnType) throws ApplicationException{
		return addSelect(returnType, (String[]) null);
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addSelect(Class<?> returnType, List<String> fields) throws ApplicationException{
		Class<?> returnTypeAux = returnType == null ? this.objClass : returnType;
		//TODO Verificar se funciona corretamente
	/*	if(returnType.equals(this.objClass)){
			addSelect(fields);
		}else{*/
			StringBuilder fieldAux = new StringBuilder();
			if(fields != null){
				for(String f : fields){
					if(fieldAux.length() > 0){
						fieldAux.append(",");
					}
					fieldAux.append(f);
				}
			}
			List<SimpleEntry<String, String>> listaCampos = new SelectMapper(returnTypeAux, fieldAux.toString()).getFields();

			for(SimpleEntry<String, String> se : listaCampos){
				addSelect(se.getKey(), se.getValue());
			}
	//	}
		return this;
	}	
	@Override
	public CriteriaFilterMetamodel<T> addSelect(Class<?> returnType, String... fields) throws ApplicationException{
		return addSelect(returnType, fields == null ? null : Arrays.asList(fields));
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(String field, String alias){
		return addSelect(field, SelectAggregate.FIELD, alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(Attribute<?, ?> attribute, String alias){
		return addSelect(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(String[] fields){
		if(fields != null) {
			for(String fld : fields){
				addSelect(fld);
			}
		}
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(Attribute<?, ?>... attributes){
		if(attributes != null) {
			for(Attribute<?, ?> fld : attributes){
				addSelect(fld);
			}
		}
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(List<String> fields) {
		if(fields != null) {
			fields.forEach( item -> {
				addSelect(item);
			});
		}
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(Map<String, String> fieldAlias){
		if(fieldAlias != null) {
			for(String k : fieldAlias.keySet()){
				addSelect(k, fieldAlias.get(k));
			}
		}
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectCount(String field, String alias){
		return addSelect(field, SelectAggregate.COUNT, alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectCount(Attribute<?, ?> attribute, String alias){
		return addSelectCount(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectCount(String field){
		return addSelect(field, SelectAggregate.COUNT, field);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectCount(Attribute<?, ?> attribute){
		return addSelectCount(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectUpper(String field){
		return addSelect(field, SelectAggregate.UPPER, field);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectUpper(Attribute<?, ?> attribute){
		return addSelectUpper(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectUpper(String field, String alias){
		return addSelect(field, SelectAggregate.UPPER, alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectUpper(Attribute<?, ?> attribute, String alias){
		return addSelectUpper(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectLower(String field){
		return addSelect(field, SelectAggregate.LOWER, field);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectLower(Attribute<?, ?> attribute){
		return addSelectLower(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectLower(String field, String alias){
		return addSelect(field, SelectAggregate.LOWER, alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectLower(Attribute<?, ?> attribute, String alias){
		return addSelectLower(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMax(String field, String alias){
		return addSelect(field, SelectAggregate.MAX, alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMax(Attribute<?, ?> attribute, String alias){
		return addSelectMax(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMax(String field){
		return addSelect(field, SelectAggregate.MAX, field);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMax(Attribute<?, ?> attribute){
		return addSelectMax(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMin(String field, String alias){
		return addSelect(field, SelectAggregate.MIN, alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMin(Attribute<?, ?> attribute, String alias){
		return addSelectMin(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMin(String field){
		return addSelect(field, SelectAggregate.MIN, field);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMin(Attribute<?, ?> attribute){
		return addSelectMin(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectSum(String field, String alias){
		return addSelect(field, SelectAggregate.SUM, alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectSum(Attribute<?, ?> attribute, String alias){
		return addSelectSum(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectSum(String field){
		return addSelect(field, SelectAggregate.SUM, field);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectSum(Attribute<?, ?> attribute){
		return addSelectSum(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(String field){
		return addSelect(field, SelectAggregate.FIELD, field);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(Attribute<?, ?> attribute){
		return addSelect(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addOrder(Class<?> returnType, String... order) throws ApplicationException{
		return addOrder(returnType, Arrays.asList(order));
	}	
	@Override
	public CriteriaFilterMetamodel<T> addOrder(Class<?> returnType, List<String> order) throws ApplicationException{
		StringBuilder orderAux = new StringBuilder();
		if(order != null){
			for(String f : order){
				if(orderAux.length() > 0){
					orderAux.append(",");
				}
				orderAux.append(f);
			}

			List<SimpleEntry<String, String>> listOrder = new OrderMapper(returnType, orderAux.toString()).getOrder();

			for(SimpleEntry<String, String> se : listOrder){
				if(se.getValue().equals("asc")){
					addOrderAsc(se.getKey());
				}else if(se.getValue().equals("desc")){
					addOrderDesc(se.getKey());
				}
			}
		}

		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addOrder(List<String> orderList){
		if(orderList != null){
			for(String o : orderList){
				int aux;
				if((aux = o.indexOf(":asc")) > 0){
					addOrderAsc(o.substring(0, aux));
				}else if((aux = o.indexOf(":desc")) > 0){
					addOrderDesc(o.substring(0, aux));
				}
			}		
		}
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addOrder(String... order){
		return addOrder(Arrays.asList(order));
	}
	@Override
	public CriteriaFilterMetamodel<T> addOrderAsc(String field){
		this.listOrder.put(field, Order.ASC);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addOrderAsc(Attribute<?, ?> attribute){
		return addOrderAsc(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addOrderDesc(String field){
		this.listOrder.put(field, Order.DESC);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addOrderDesc(Attribute<?, ?> attribute){
		return addOrderDesc(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereEqual(String field){
		this.listWhere.put(field, Where.EQUAL);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereEqual(Attribute<?, ?> attribute){
		return addWhereEqual(attribute.getName());
	}
	/**
	 * 
	 * @param field
	 * @param where
	 * @param values
	 * @return
	 */
	private <E> CriteriaFilterMetamodel<T> addWhereListValues(String field, Where where, List<E> values){
		if(values != null && !values.isEmpty()){
			this.whereRestriction.add(field, where, values);
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
	private <E> CriteriaFilterMetamodel<T> addWhereListValues(String field, Where where, E[] values){
		if(values != null){
			return addWhereListValues(field, where, Arrays.asList(values));
		}
		return this;
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereIn(String field, List<E> values){
		return addWhereListValues(field, Where.IN, values);
	}
	
	/**
	 * 
	 * @param regexToAnalyse
	 * @param search
	 * @return
	 */
	private boolean containsRegex(RegexWhere[] regexToAnalyse, RegexWhere rgxSearch){
		for(RegexWhere rgx : regexToAnalyse){
			if(rgx.equals(rgxSearch)){
				return true;
			}
		}
		return false;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereRegex(String field, Class<?> fieldType, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException{
		boolean added = configWhereRegex(field, fieldType, value, regexToAnalyse, defaultIfNotMatch);
		if(!added && defaultIfNotMatch != null){
			ApplicationException ae = new ApplicationException(MessageSeverity.ERROR, "error.regex-config", value, field);
			logger.error("addWhereRegex]", ae);
			throw ae;
		}
		return this;
	}
	/**
	 * 
	 * @param field
	 * @param fieldType
	 * @param value
	 * @param regexToAnalyse
	 * @param defaultIfNotMatch
	 * @return
	 * @throws ApplicationException
	 */
	private boolean configWhereRegex(String field, Class<?> fieldType, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException{
		Where where = null;
		for(Where wh : Where.values()){
			if(wh.exp != null && (regexToAnalyse == null || containsRegex(regexToAnalyse, wh.regexWhere)) && checkRegex(value, wh.exp)){
				where = wh;
				break;
			}
		}
		if(where != null){
			Pattern p = Pattern.compile(where.exp);
			Matcher m = p.matcher(value);
			
			if(m.find()){
				if(where.equals(Where.IS_NULL)){
					this.addWhereIsNull(field);
				}else if(where.equals(Where.IS_NOT_NULL)){
					this.addWhereIsNotNull(field);
				}else if(where.equals(Where.BETWEEN)){
					String[] val = m.group().split("\\s(btwn|between)\\s");
					if(NumberUtils.isCreatable(val[0])){
						this.whereRestriction.add(field, Where.BETWEEN, new Number[] {NumberUtils.createNumber(val[0]), NumberUtils.createNumber(val[1])});
						return true;
					}else{
						Date dt1, dt2;
						dt1 = DateUtil.parseDate(val[0]);
						dt2 = DateUtil.parseDate(val[1]);
						if(val[1].matches(regexPatternDate)){
								dt2 = DateUtils.setHours(dt2, 23);
								dt2 = DateUtils.setMinutes(dt2, 59);
								dt2 = DateUtils.setSeconds(dt2, 59);
								dt2 = DateUtils.setMilliseconds(dt2, 999);
						}
						addWhereBetween(field, dt1, dt2);
						return true;
					}
				}else if(where.equals(Where.IN) || where.equals(Where.NOT_IN)){
					String[] val = m.group().replace(" ", "").split("\\,");
					if(val[0] != null && (Date.class.isAssignableFrom(fieldType) || Calendar.class.isAssignableFrom(fieldType))){// || fieldType.equals(Temporal.class) || val[0].matches(regexPatternDateTime))){
						Date[] dates = new Date[val.length];
						for(int i=0; i < val.length; i++){
							dates[i] = DateUtil.parseDate(val[i]);
						}
						if(where.equals(Where.IN)){
							addWhereIn(field, dates);
							return true;
						}else{
							addWhereNotIn(field, dates);
							return true;
						}
					}else{
						if(where.equals(Where.IN)){
							addWhereIn(field, val);
							return true;
						}else{
							addWhereNotIn(field, val);
							return true;
						}
					}
				}else{
					String val = m.group();
					if(Date.class.isAssignableFrom(fieldType) || Calendar.class.isAssignableFrom(fieldType)){// val.matches(regexPatternDateTime)){
						Date dt = DateUtil.parseDate(m.group());
						if(val.matches(regexPatternDate) && (where.equals(Where.LESS_THAN) || where.equals(Where.LESS_THAN_OR_EQUAL_TO))){
							DateUtils.setHours(dt, 23);
							DateUtils.setMinutes(dt, 59);
							DateUtils.setSeconds(dt, 59);
							DateUtils.setMilliseconds(dt, 999);
						}
						this.whereRestriction.add(field, where, dt);
						return true;
					}else if (Number.class.isAssignableFrom(fieldType)){
						this.whereRestriction.add(field, where, NumberUtils.createNumber(val));
						return true;
					}else{
						this.whereRestriction.add(field, where, val);
						return true;
					}
				}
			}
		}else{
			boolean found = false;
			if(ArrayUtils.contains(regexToAnalyse, RegexWhere.MULTI)){
				final String multiWhere = "^(<|<=|=|!=|>=|>|)" + regexPatternDateTimeOrNumber +  "(\\s?&\\s?(<|<=|=|!=|>=|>|)" + regexPatternDateTimeOrNumber + "){1,}$";
				Pattern p = Pattern.compile(multiWhere);
				Matcher m = p.matcher(value);

				if(m.find()){
					found= true;
					String[] val = m.group().split(";");

					for(String v: val){
						boolean add = configWhereRegex(field, fieldType, v, new RegexWhere[] {
								RegexWhere.LESS_THAN, 
								RegexWhere.LESS_THAN_OR_EQUAL_TO, 
								RegexWhere.EQUAL, 
								RegexWhere.NOT_EQUAL,
								RegexWhere.GREATER_THAN,
								RegexWhere.GREATER_THAN_OR_EQUAL_TO}, defaultIfNotMatch);
						
						if(!add){
							return false;
						}
					}
					return true;
				}
			}

			if(!found && defaultIfNotMatch != null){
				if(defaultIfNotMatch.equals(RegexWhere.EQUAL) && value.matches("^" + regexPatternDateTime + "$")){
					this.whereRestriction.add(field, Where.EQUAL, DateUtil.parseDate(value));
					return true;
				}else if(defaultIfNotMatch.equals(RegexWhere.EQUAL) && value.matches("^[a-zA-Z0-9]" + regexPatternAlpha + "$")){
					this.whereRestriction.add(field, Where.EQUAL, value);
					return true;
				}else{
					boolean ret = configWhereRegex(field, fieldType, value, new RegexWhere[] {defaultIfNotMatch}, null);
					if(!ret){
						Where whereAux = getWhereFromRegexWhere(defaultIfNotMatch);
						if(whereAux != null) {
							this.whereRestriction.add(field, whereAux, value);
							return true;
						}else {
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
	 * @autor jurandirjcg
	 * @param regexWhr
	 * @return
	 */
	private Where getWhereFromRegexWhere(RegexWhere regexWhr) {
		for(Where whr : Where.values()) {
			if(whr.regexWhere != null && whr.regexWhere.equals(regexWhr)) {
				return whr;
			}
		}
		return null;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereRegex(Attribute<?, ?> attribute, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException{
		boolean added = configWhereRegex(attribute.getName(), attribute.getJavaType(), value, regexToAnalyse, defaultIfNotMatch);
		if(!added && defaultIfNotMatch != null){
			ApplicationException ae = new ApplicationException(MessageSeverity.ERROR, "error.regex-config", value, attribute.getName());
			logger.error("[addWhereRegex]", ae.getMessage());
			throw new ApplicationException(MessageSeverity.ERROR, "error.regex-config", value, attribute.getName());
		}
		return this;
	}
	/**
	 * 
	 * @param value
	 * @param regex
	 * @return
	 */
	private boolean checkRegex(String value, String regex){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);
		return m.find();
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereIn(Attribute<?, ?> attribute, List<E> values){
		return addWhereListValues(attribute.getName(), Where.IN, values);
	}
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilterMetamodel<T> addWhereIn(String field, E... values){
		return addWhereListValues(field, Where.IN, values);
	}
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilterMetamodel<T> addWhereIn(Attribute<?, ?> attribute, E... values){
		return addWhereListValues(attribute.getName(), Where.IN, values);
	}
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilterMetamodel<T> addWhereNotIn(String field, E... values){
		return addWhereListValues(field, Where.NOT_IN, values);
	}
	@SuppressWarnings("unchecked")
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotIn(Attribute<?, ?> attribute, E... values){
		return addWhereListValues(attribute.getName(), Where.NOT_IN, values);
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotIn(String field, List<E> values){
		return addWhereListValues(field, Where.NOT_IN, values);
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotIn(Attribute<?, ?> attribute, List<E> values){
		return addWhereListValues(attribute.getName(), Where.NOT_IN, values);
	}
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(String field, E... values){
		return addWhereListValues(field, Where.EQUAL, values);
	}
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(Attribute<?, ?> attribute, E... values){
		return addWhereListValues(attribute.getName(), Where.EQUAL, values);
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(String field, List<E> values){
		return addWhereListValues(field, Where.EQUAL, values);
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(Attribute<?, ?> attribute, List<E> values){
		return addWhereListValues(attribute.getName(), Where.EQUAL, values);
	}
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(String field, E... values){
		return addWhereListValues(field, Where.NOT_EQUAL, values);
	}
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<?, ?> attribute, E... values){
		return addWhereListValues(attribute.getName(), Where.NOT_EQUAL, values);
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(String field, List<E> values){
		return addWhereListValues(field, Where.NOT_EQUAL, values);
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<?, ?> attribute, List<E> values){
		return addWhereListValues(attribute.getName(), Where.NOT_EQUAL, values);
	}
	/**
	 * 
	 * @param listWhere
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addAllWhere(Map<String, Where> listWhere){
		this.listWhere.putAll(listWhere);
		return this;
	}
	/**
	 * 
	 * @param listComplexWhere
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addAllWhereComplex(Map<String, List<SimpleEntry<Where, ?>>> listComplexWhere){
		this.whereRestriction.getRestrictions().putAll(listComplexWhere);
		return this;
	}
	/**
	 * 
	 * @param listJoin
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addAllJoin(Map<String, SimpleEntry<JoinType, Boolean>> listJoin){
		this.listJoin.putAll(listJoin);
		return this;
	}
	/**
	 * 
	 * @param listSelection
	 * @return
	 */
	public CriteriaFilterMetamodel<T> addAllSelection(Map<String, SimpleEntry<SelectAggregate, String>> listSelection){
		this.listSelection.putAll(listSelection);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(String field, Integer startValue, Integer endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new Integer[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(String field, Double startValue, Double endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new Double[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, Integer startValue, Integer endValue){
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(String field, Short startValue, Short endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new Short[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, Short startValue, Short endValue){
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(String field, Long startValue, Long endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new Long[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, Long startValue, Long endValue){
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanField(String field, String anotherField){
		this.whereRestriction.add(field, Where.LESS_THAN_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		return addWhereLessThanField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanField(String field, String anotherField){
		this.whereRestriction.add(field, Where.GREATER_THAN_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		return addWhereGreaterThanField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(String field, String anotherField){
		this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		return addWhereLessThanOrEqualToField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(String field, String anotherField){
		this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		return addWhereGreaterThanOrEqualToField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereEqualField(String field, String anotherField){
		this.whereRestriction.add(field, Where.EQUAL_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereEqualField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		return addWhereEqualField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereEqualField(Attribute<?, ?> attribute, ComplexAttribute anotherAttribute){
		return addWhereEqualField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqualField(String field, String anotherField){
		this.whereRestriction.add(field, Where.NOT_EQUAL_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqualField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		return addWhereNotEqualField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqualField(Attribute<?, ?> attribute, ComplexAttribute anotherAttribute){
		return addWhereNotEqualField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(String field, Date startValue, Date endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new Date[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, Date startValue, Date endValue){
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(String field, LocalDate startValue, LocalDate endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new LocalDate[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, LocalDate startValue, LocalDate endValue){
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(String field, LocalDateTime startValue, LocalDateTime endValue){	
		this.whereRestriction.add(field, Where.BETWEEN, new LocalDateTime[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, LocalDateTime startValue, LocalDateTime endValue){
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(String field){
		this.listWhere.put(field, Where.GREATER_THAN);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<?, ?> attribute){
		return addWhereGreaterThan(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(String field){
		this.listWhere.put(field, Where.GREATER_THAN_OR_EQUAL_TO);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<?, ?> attribute){
		return addWhereGreaterThanOrEqualTo(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereIn(String field){
		this.listWhere.put(field, Where.IN);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereIn(Attribute<?, ?> attribute){
		return addWhereIn(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereIsNotNull(String field){
		this.whereRestriction.add(field, Where.IS_NOT_NULL, null);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereIsNotNull(Attribute<?, ?> attribute){
		return addWhereIsNotNull(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereIsNull(String field){
		this.whereRestriction.add(field, Where.IS_NULL, null);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereIsNull(Attribute<?, ?> attribute){
		return addWhereIsNull(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(String field){
		this.listWhere.put(field, Where.LESS_THAN);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<?, ?> attribute){
		return addWhereLessThan(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(String field){
		this.listWhere.put(field, Where.LESS_THAN_OR_EQUAL_TO);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<?, ?> attribute){
		return addWhereLessThanOrEqualTo(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLike(String field, MatchMode matchMode){
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
	public CriteriaFilterMetamodel<T> addWhereNotLike(String field, MatchMode matchMode){
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
	public CriteriaFilterMetamodel<T> addWhereNotLike(Attribute<?, ?> attribute, MatchMode matchMode){
		return addWhereNotLike(attribute.getName(), matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLike(Attribute<?, ?> attribute, MatchMode matchMode){
		return addWhereLike(attribute.getName(), matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereILike(String field, MatchMode matchMode){
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
	public CriteriaFilterMetamodel<T> addWhereNotILike(String field, MatchMode matchMode){
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
	public CriteriaFilterMetamodel<T> addWhereNotILike(Attribute<?, ?> attribute, MatchMode matchMode){
		return addWhereNotILike(attribute.getName(), matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereILike(Attribute<?, ?> attribute, MatchMode matchMode){
		return addWhereILike(attribute.getName(), matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqual(String field){
		this.listWhere.put(field, Where.NOT_EQUAL);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<?, ?> attribute){
		return addWhereNotEqual(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotIn(String field){
		this.listWhere.put(field, Where.NOT_IN);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotIn(Attribute<?, ?> attribute){
		return addWhereNotIn(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addGroupBy(String field){
		this.listGroupBy.add(field);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addGroupBy(Attribute<?, ?> attribute){
		return addGroupBy(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addJoin(String field, JoinType joinType, boolean fetch){
		this.listJoin.put(field, new SimpleEntry<JoinType, Boolean>(joinType, fetch));
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addJoin(Attribute<?, ?> attribute, JoinType joinType, boolean fetch){
		this.listJoin.put(attribute.getName(), new SimpleEntry<JoinType, Boolean>(joinType, fetch));
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addJoin(String field, JoinType joinType){
		return addJoin(field, joinType, false);
	}
	@Override
	public CriteriaFilterMetamodel<T> addJoin(Attribute<?, ?> attribute, JoinType joinType){
		return addJoin(attribute, joinType, false);
	}
	@Override
	public CriteriaFilterMetamodel<T> addJoin(String field){
		return addJoin(field, JoinType.INNER, false);
	}
	@Override
	public CriteriaFilterMetamodel<T> addJoin(Attribute<?, ?> attribute){
		return addJoin(attribute, JoinType.INNER, false);
	}
	public Map<Class<?>, Map<String, SimpleEntry<SelectAggregate, String>>> getCollectionSelection() {
		return collectionSelection;
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(String field, E value) {
		this.whereRestriction.add(field, Where.EQUAL, value);
		return this;
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(Attribute<?, ?> attribute, E value) {
		return addWhereEqual(attribute.getName(), value);
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(String field, E value) {
		this.whereRestriction.add(field, Where.NOT_EQUAL, value);
		return this;
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<?, ?> attribute, E value) {
		return addWhereNotEqual(attribute.getName(), value);
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereGreaterThan(String field, Date value) {
		this.whereRestriction.add(field, Where.GREATER_THAN, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<?, ?> attribute, Date value) {
		return addWhereGreaterThan(attribute.getName(), value);
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereGreaterThan(String field, Number value) {
		this.whereRestriction.add(field, Where.GREATER_THAN, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<?, ?> attribute, Number value) {
		return addWhereGreaterThan(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(String field, Date value) {
		this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<?, ?> attribute, Date value) {
		return addWhereGreaterThanOrEqualTo(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(String field, Number value) {
		this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<?, ?> attribute, Number value) {
		return addWhereGreaterThanOrEqualTo(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(String field, Date value) {
		this.whereRestriction.add(field, Where.LESS_THAN, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<?, ?> attribute, Date value) {
		return addWhereLessThan(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(String field, Number value) {
		this.whereRestriction.add(field, Where.LESS_THAN, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<?, ?> attribute, Number value) {
		return addWhereLessThan(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(String field, Date value) {
		this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<?, ?> attribute, Date value) {
		return addWhereLessThanOrEqualTo(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(String field, Number value) {
		this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<?, ?> attribute, Number value) {
		return addWhereLessThanOrEqualTo(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLike(String field, String value, MatchMode matchMode) {
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
	public CriteriaFilterMetamodel<T> addWhereNotLike(String field, String value, MatchMode matchMode) {
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
	public CriteriaFilterMetamodel<T> addWhereILike(String field, String value, MatchMode matchMode) {
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
	public CriteriaFilterMetamodel<T> addWhereNotILike(String field, String value, MatchMode matchMode) {
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
	public CriteriaFilterMetamodel<T> addWhereNotLike(Attribute<?, ?> attribute, String value, MatchMode matchMode) {
		return addWhereNotLike(attribute.getName(), value, matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLike(Attribute<?, ?> attribute, String value, MatchMode matchMode) {
		return addWhereLike(attribute.getName(), value, matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereILike(Attribute<?, ?> attribute, String value, MatchMode matchMode) {
		return addWhereILike(attribute.getName(), value, matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotILike(Attribute<?, ?> attribute, String value, MatchMode matchMode) {
		return addWhereNotILike(attribute.getName(), value, matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereILike(ComplexAttribute attribute, String value, MatchMode matchMode) {
		return addWhereILike(attribute.getName(), value, matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereILike(ComplexAttribute attribute, MatchMode matchMode) {
		return addWhereILike(attribute.getName(), matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotILike(ComplexAttribute attribute, String value, MatchMode matchMode) {
		return addWhereNotILike(attribute.getName(), value, matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotLike(ComplexAttribute attribute, String value, MatchMode matchMode) {
		return addWhereNotLike(attribute.getName(), value, matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLike(ComplexAttribute attribute,	String value, MatchMode matchMode) {
		return addWhereLike(attribute.getName(), value, matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotILike(ComplexAttribute attribute, MatchMode matchMode) {
		return addWhereNotILike(attribute.getName(), matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotLike(ComplexAttribute attribute, MatchMode matchMode) {
		return addWhereNotLike(attribute.getName(), matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLike(ComplexAttribute attribute, MatchMode matchMode) {
		return addWhereLike(attribute.getName(), matchMode);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(ComplexAttribute attribute, String alias) {
		return addSelect(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(ComplexAttribute... attributes) {
		for(ComplexAttribute ca : attributes) {
			this.addSelect(ca.getName());
		}
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectCount(ComplexAttribute attribute, String alias) {
		return addSelectCount(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectCount(ComplexAttribute attribute) {
		return addSelectCount(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectUpper(ComplexAttribute attribute) {
		return addSelectUpper(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectUpper(ComplexAttribute attribute, String alias) {
		return addSelectUpper(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectLower(ComplexAttribute attribute) {
		return addSelectLower(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectLower(ComplexAttribute attribute, String alias) {
		return addSelectLower(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMax(ComplexAttribute attribute, String alias) {
		return addSelectMax(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMax(ComplexAttribute attribute) {
		return addSelectMax(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMin(ComplexAttribute attribute, String alias) {
		return addSelectMin(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMin(ComplexAttribute attribute) {
		return addSelectMin(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectSum(ComplexAttribute attribute, String alias) {
		return addSelectSum(attribute.getName(), alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectSum(ComplexAttribute attribute) {
		return addSelectSum(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(ComplexAttribute attribute) {
		return addSelect(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addOrderAsc(ComplexAttribute attribute) {
		return addOrderAsc(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addOrderDesc(ComplexAttribute attribute) {
		return addOrderDesc(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereEqual(	ComplexAttribute attribute) {
		return addWhereEqual(attribute.getName());
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereIn(ComplexAttribute attribute, List<E> values) {
		return addWhereIn(attribute.getName(), values);
	}
	@SuppressWarnings("unchecked")
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereIn(ComplexAttribute attribute, E... values) {
		return addWhereIn(attribute.getName(), values);
	}
	@SuppressWarnings("unchecked")
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotIn(ComplexAttribute attribute, E... values) {
		return addWhereNotIn(attribute.getName(), values);
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotIn(ComplexAttribute attribute, List<E> values) {
		return addWhereNotIn(attribute.getName(), values);
	}
	@SuppressWarnings("unchecked")
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute, E... values) {
		return addWhereEqual(attribute.getName(), values);
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute, List<E> values) {
		return addWhereEqual(attribute.getName(), values);
	}
	@SuppressWarnings("unchecked")
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, E... values) {
		return addWhereNotEqual(attribute.getName(), values);
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, List<E> values) {
		return addWhereNotEqual(attribute.getName(), values);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Integer startValue, Integer endValue) {
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Short startValue, Short endValue) {
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Long startValue, Long endValue) {
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute) {
		return addWhereLessThanField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute) {
		return addWhereGreaterThanField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute) {
		return addWhereLessThanOrEqualToField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute) {
		return addWhereGreaterThanOrEqualToField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereEqualField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute) {
		return addWhereEqualField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqualField(ComplexAttribute attribute, 	Attribute<?, ?> anotherAttribute) {
		return addWhereNotEqualField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Date startValue, Date endValue) {
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, LocalDate startValue, LocalDate endValue) {
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, LocalDateTime startValue, LocalDateTime endValue) {
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute) {
		return addWhereGreaterThan(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute) {
		return addWhereGreaterThanOrEqualTo(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereIsNotNull(ComplexAttribute attribute) {
		return addWhereIsNotNull(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereIsNull(ComplexAttribute attribute) {
		return addWhereIsNull(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereIn(ComplexAttribute attribute) {
		return addWhereIn(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(ComplexAttribute attribute) {
		return addWhereLessThan(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute) {
		return addWhereLessThanOrEqualTo(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqual(	ComplexAttribute attribute) {
		return addWhereNotEqual(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotIn(ComplexAttribute attribute) {
		return addWhereNotIn(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addGroupBy(ComplexAttribute attribute) {
		return addGroupBy(attribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addJoin(ComplexAttribute attribute, JoinType joinType, boolean fetch) {
		return addJoin(attribute.getName(), joinType, fetch);
	}
	@Override
	public CriteriaFilterMetamodel<T> addJoin(ComplexAttribute attribute, JoinType joinType) {
		return addJoin(attribute.getName(), joinType);
	}
	@Override
	public CriteriaFilterMetamodel<T> addJoin(ComplexAttribute attribute) {
		return addJoin(attribute.getName());
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute, E value) {
		return addWhereEqual(attribute.getName(), value);
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, E value) {
		return addWhereNotEqual(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute, Date value) {
		return addWhereGreaterThan(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute, Number value) {
		return addWhereGreaterThan(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute, Date value) {
		return addWhereGreaterThanOrEqualTo(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute, Number value) {
		return addWhereGreaterThanOrEqualTo(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(ComplexAttribute attribute, Date value) {
		return addWhereLessThan(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(ComplexAttribute attribute, Number value) {
		return addWhereLessThan(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Date value) {
		return addWhereLessThanOrEqualTo(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Number value) {
		return addWhereLessThanOrEqualTo(attribute.getName(), value);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereRegex(ComplexAttribute attribute, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException {
		return addWhereRegex(attribute.getName(), attribute.getFieldType(), value, regexToAnalyse, defaultIfNotMatch);
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanField(ComplexAttribute attribute, ComplexAttribute anotherAttribute) {
		return addWhereLessThanField(anotherAttribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanField(ComplexAttribute attribute, ComplexAttribute anotherAttribute) {
		return addWhereGreaterThanField(anotherAttribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute, ComplexAttribute anotherAttribute) {
		return addWhereLessThanOrEqualToField(anotherAttribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute, ComplexAttribute anotherAttribute) {
		return addWhereGreaterThanOrEqualToField(anotherAttribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereEqualField(ComplexAttribute attribute, ComplexAttribute anotherAttribute) {
		return addWhereEqualField(anotherAttribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqualField(ComplexAttribute attribute, ComplexAttribute anotherAttribute) {
		return addWhereNotEqualField(anotherAttribute.getName(), anotherAttribute.getName());
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addUpdate(String field, E value){
		this.listUpdate.put(field, value);
		return this;
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addUpdate(Attribute<?, ?> attribute, E value){
		this.listUpdate.put(attribute.getName(), value);
		return this;
	}
}

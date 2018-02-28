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

import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

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
	private static final String regexPatternDateTime =  regexPatternDate +"(([\\s]?(0\\d|1\\d|2[0-3]):[0-5]\\d)?(:[0-5]\\d)?)?";
	private static final String regexPatternDateTimeOrNumber = "((" + regexPatternDateTime + ")|[0-9]+)";
	private static final String regexPatternMultiDateTimeOrNumber = "(([a-zA-Z0-9,\\s_-\u00C0-\u00FF]+)|[" + regexPatternDateTime + ",]+)";
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
		EQUAL (RegexWhere.EQUAL, "(?<=^\\=)" + regexPatternAlpha + "$"),
		LESS_THAN (RegexWhere.LESS_THAN, "(?<=^\\<)" + regexPatternDateTimeOrNumber + "$"),
		LESS_THAN_OR_EQUAL_TO (RegexWhere.LESS_THAN_OR_EQUAL_TO, "(?<=^\\<\\=)" + regexPatternDateTimeOrNumber + "$"),
		GREATER_THAN (RegexWhere.GREATER_THAN, "(?<=^\\>)" + regexPatternDateTimeOrNumber + "$"),
		GREATER_THAN_OR_EQUAL_TO (RegexWhere.GREATER_THAN_OR_EQUAL_TO, "(?<=^\\>\\=)" + regexPatternDateTimeOrNumber + "$"),
		NOT_EQUAL (RegexWhere.NOT_EQUAL, "(?<=^\\!\\=)" + regexPatternAlpha + "$"),
		IN (RegexWhere.IN, "(?<=^\\()" + regexPatternMultiDateTimeOrNumber + "(?=\\)$)"),
		NOT_IN (RegexWhere.NOT_IN, "(?<=^!\\()" + regexPatternMultiDateTimeOrNumber + "(?=\\)$)"),
		LIKE_EXACT (RegexWhere.LIKE_EXACT, "(?<=^\\=\\%)" + regexPatternAlpha + "(?!\\%$)"),
		LIKE_NOT_EXACT (RegexWhere.LIKE_NOT_EXACT, "(?<=^\\!\\=\\%)" + regexPatternAlpha + "(?!\\%$)"),
		LIKE_MATCH_ANYWHERE (RegexWhere.LIKE_MATCH_ANYWHERE, "(?<=^\\%)" + regexPatternAlpha + "(?=(\\!)?\\%$)"),
		LIKE_MATCH_END (RegexWhere.LIKE_MATCH_END, "(?<=^\\%)" +  regexPatternAlpha + "(?!\\%$)"),
		LIKE_MATCH_START (RegexWhere.LIKE_MATCH_START, "(?<!^\\%)" +  regexPatternAlpha + "(?=\\%$)"),
		LIKE_NOT_MATCH_ANYWHERE (RegexWhere.LIKE_NOT_MATCH_ANYWHERE, "(?<=^\\!\\%)" + regexPatternAlpha + "(?=\\!\\%$)"),
		LIKE_NOT_MATCH_END (RegexWhere.LIKE_NOT_MATCH_END, "(?<=^\\!\\%)" +  regexPatternAlpha + "(?!\\%$)"),
		LIKE_NOT_MATCH_START (RegexWhere.LIKE_NOT_MATCH_START, "(?<!^\\%)" +  regexPatternAlpha + "(?=\\!\\%$)"),
		ILIKE_EXACT (RegexWhere.ILIKE_EXACT, "(?<=^\\=\\*)" + regexPatternAlpha + "(?!\\*$)"),
		ILIKE_NOT_EXACT (RegexWhere.ILIKE_NOT_EXACT, "(?<=^\\!\\=\\*)" + regexPatternAlpha + "(?!\\*$)"),
		ILIKE_MATCH_ANYWHERE (RegexWhere.ILIKE_MATCH_ANYWHERE, "(?<=^\\*)" +  regexPatternAlpha + "(?=\\*$)"),
		ILIKE_MATCH_END (RegexWhere.ILIKE_MATCH_END, "(?<=^\\*)" +  regexPatternAlpha + "(?!\\*$)"),
		ILIKE_MATCH_START (RegexWhere.ILIKE_MATCH_START, "(?<!^\\*)" +  regexPatternAlpha + "(?=\\*$)"),
		ILIKE_NOT_MATCH_ANYWHERE (RegexWhere.ILIKE_NOT_MATCH_ANYWHERE, "(?<=^\\!\\*)" +  regexPatternAlpha + "(?=\\!\\*$)"),
		ILIKE_NOT_MATCH_END (RegexWhere.ILIKE_NOT_MATCH_END, "(?<=^\\!\\*)" +  regexPatternAlpha + "(?!\\*$)"),
		ILIKE_NOT_MATCH_START (RegexWhere.ILIKE_NOT_MATCH_START, "(?<!^\\*)" +  regexPatternAlpha + "(?=\\!\\*$)"),
		IS_NULL (RegexWhere.IS_NULL, "^null$"),
		IS_NOT_NULL (RegexWhere.IS_NOT_NULL, "^not null$"),
		BETWEEN (RegexWhere.BETWEEN,"(?<=^)" + regexPatternDateTimeOrNumber + "(\\sbtwn\\s)" + regexPatternDateTimeOrNumber + "(?=$)"),
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
	
	public CriteriaFilterImpl(Class<T> objClass) {
		this.objClass = objClass;
	}
	
	public boolean isCollectionSelectionControl() {
		return collectionSelectionControl;
	}

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
		
		if(returnType.equals(this.objClass)){
			addSelect(fields);
		}else{
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
		}
		
		return this;
	}
		
	@Override
	public CriteriaFilterMetamodel<T> addSelect(Class<?> returnType, String... fields) throws ApplicationException{
		return addSelect(returnType, fields == null ? null : Arrays.asList(fields));
	}
		
	@Override
	public CriteriaFilterMetamodel<T> addSelect(String field, String alias){
		addSelect(field, SelectAggregate.FIELD, alias);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addSelect(Attribute<?, ?> attribute, String alias){
		addSelect(attribute.getName(), alias);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addSelect(String[] fields){
		for(String fld : fields){
			addSelect(fld);
		}
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelect(Attribute<?, ?>... attributes){
		for(Attribute<?, ?> fld : attributes){
			addSelect(fld);
		}
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(List<String> fields) {
		fields.forEach( item -> {
			addSelect(item);
		});
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addSelect(Map<String, String> fieldAlias){
		for(String k : fieldAlias.keySet()){
			addSelect(k, fieldAlias.get(k));
		}
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addSelectCount(String field, String alias){
		return addSelect(field, SelectAggregate.COUNT, alias);
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addSelectCount(Attribute<?, ?> attribute, String alias){
		addSelectCount(attribute.getName(), alias);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addSelectCount(String field){
		return addSelect(field, SelectAggregate.COUNT, field);
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectCount(Attribute<?, ?> attribute){
		addSelectCount(attribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addSelectUpper(String field){
		return addSelect(field, SelectAggregate.UPPER, field);
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectUpper(Attribute<?, ?> attribute){
		addSelectUpper(attribute.getName());
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectUpper(String field, String alias){
		return addSelect(field, SelectAggregate.UPPER, alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectUpper(Attribute<?, ?> attribute, String alias){
		addSelectUpper(attribute.getName(), alias);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectLower(String field){
		return addSelect(field, SelectAggregate.LOWER, field);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectLower(Attribute<?, ?> attribute){
		addSelectLower(attribute.getName());
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectLower(String field, String alias){
		return addSelect(field, SelectAggregate.LOWER, alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectLower(Attribute<?, ?> attribute, String alias){
		addSelectLower(attribute.getName(), alias);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addSelectMax(String field, String alias){
		return addSelect(field, SelectAggregate.MAX, alias);
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectMax(Attribute<?, ?> attribute, String alias){
		addSelectMax(attribute.getName(), alias);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMax(String field){
		return addSelect(field, SelectAggregate.MAX, field);
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectMax(Attribute<?, ?> attribute){
		addSelectMax(attribute.getName());
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMin(String field, String alias){
		return addSelect(field, SelectAggregate.MIN, alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMin(Attribute<?, ?> attribute, String alias){
		addSelectMin(attribute.getName(), alias);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addSelectMin(String field){
		return addSelect(field, SelectAggregate.MIN, field);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectMin(Attribute<?, ?> attribute){
		addSelectMin(attribute.getName());
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectSum(String field, String alias){
		return addSelect(field, SelectAggregate.SUM, alias);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectSum(Attribute<?, ?> attribute, String alias){
		addSelectSum(attribute.getName(), alias);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addSelectSum(String field){
		return addSelect(field, SelectAggregate.SUM, field);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelectSum(Attribute<?, ?> attribute){
		addSelectSum(attribute.getName());
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(String field){
		return addSelect(field, SelectAggregate.FIELD, field);
	}
	@Override
	public CriteriaFilterMetamodel<T> addSelect(Attribute<?, ?> attribute){
		addSelect(attribute.getName());
		return this;
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
		addOrderAsc(attribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addOrderDesc(String field){
		this.listOrder.put(field, Order.DESC);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addOrderDesc(Attribute<?, ?> attribute){
		addOrderDesc(attribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereEqual(String field){
		this.listWhere.put(field, Where.EQUAL);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereEqual(Attribute<?, ?> attribute){
		addWhereEqual(attribute.getName());
		return this;
	}
	/**
	 * 
	 * @param field
	 * @param where
	 * @param values
	 * @return
	 */
	private <E> CriteriaFilterMetamodel<T> addWhereListValues(String field, Where where, List<E> values){
		if(values != null){
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
			throw new ApplicationException(MessageSeverity.ERROR, "error.regex-config", value, field);
		}
		return this;
	}
	
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
					String[] val = m.group().replace(" ", "").split("btwn");
					if(NumberUtils.isCreatable(val[0])){
						this.whereRestriction.add(field, Where.BETWEEN, new Number[] {NumberUtils.createNumber(val[0]), NumberUtils.createNumber(val[1])});
						return true;
					}else{
						Date dt1, dt2;
						dt1 = DateUtil.parseDate(val[0]);
						dt2 = DateUtil.parseDate(val[1]);
						if(val[1].matches(regexPatternDate)){
								DateUtils.setHours(dt2, 23);
								DateUtils.setMinutes(dt2, 59);
								DateUtils.setSeconds(dt2, 59);
								DateUtils.setMilliseconds(dt2, 999);
						}
						addWhereBetween(field, dt1, dt2);
						return true;
					}
				}else if(where.equals(Where.IN) || where.equals(Where.NOT_IN)){
					String[] val = m.group().replace(" ", "").split("\\,");
					if(val[0] != null && (fieldType.equals(Date.class) || fieldType.equals(Calendar.class))){// || fieldType.equals(Temporal.class) || val[0].matches(regexPatternDateTime))){
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
					if(fieldType.equals(Date.class) || fieldType.equals(Calendar.class)){// val.matches(regexPatternDateTime)){
						Date dt = DateUtil.parseDate(m.group());
						if(val.matches(regexPatternDate) && (where.equals(Where.LESS_THAN) || where.equals(Where.LESS_THAN_OR_EQUAL_TO))){
							DateUtils.setHours(dt, 23);
							DateUtils.setMinutes(dt, 59);
							DateUtils.setSeconds(dt, 59);
							DateUtils.setMilliseconds(dt, 999);
						}
						this.whereRestriction.add(field, where, dt);
						return true;
					}else if (fieldType.equals(Number.class)){
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
					return configWhereRegex(field, fieldType, value, new RegexWhere[] {defaultIfNotMatch}, null);
				}
			}
		}
		return false;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereRegex(Attribute<?, ?> attribute, Class<?> fieldType, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException{
		boolean added = configWhereRegex(attribute.getName(), fieldType, value, regexToAnalyse, defaultIfNotMatch);
		if(!added && defaultIfNotMatch != null){
			throw new ApplicationException(MessageSeverity.ERROR, "error.regex-config", value, attribute.getName());
		}
		return this;
	}
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
		addWhereBetween(attribute.getName(), startValue, endValue);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(String field, Short startValue, Short endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new Short[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, Short startValue, Short endValue){
		addWhereBetween(attribute.getName(), startValue, endValue);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(String field, Long startValue, Long endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new Long[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, Long startValue, Long endValue){
		addWhereBetween(attribute.getName(), startValue, endValue);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanField(String field, String anotherField){
		this.whereRestriction.add(field, Where.LESS_THAN_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		addWhereLessThanField(attribute.getName(), anotherAttribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanField(String field, String anotherField){
		this.whereRestriction.add(field, Where.GREATER_THAN_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		addWhereGreaterThanField(attribute.getName(), anotherAttribute.getName());
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(String field, String anotherField){
		this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		addWhereLessThanOrEqualToField(attribute.getName(), anotherAttribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(String field, String anotherField){
		this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		addWhereGreaterThanOrEqualToField(attribute.getName(), anotherAttribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereEqualField(String field, String anotherField){
		this.whereRestriction.add(field, Where.EQUAL_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereEqualField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		addWhereEqualField(attribute.getName(), anotherAttribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqualField(String field, String anotherField){
		this.whereRestriction.add(field, Where.NOT_EQUAL_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqualField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		addWhereNotEqualField(attribute.getName(), anotherAttribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(String field, Date startValue, Date endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new Date[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, Date startValue, Date endValue){
		addWhereBetween(attribute.getName(), startValue, endValue);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(String field, LocalDate startValue, LocalDate endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new LocalDate[] {startValue, endValue});
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, LocalDate startValue, LocalDate endValue){
		addWhereBetween(attribute.getName(), startValue, endValue);
		return this;
	}
		
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(String field, LocalDateTime startValue, LocalDateTime endValue){	
		this.whereRestriction.add(field, Where.BETWEEN, new LocalDateTime[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(Attribute<?, ?> attribute, LocalDateTime startValue, LocalDateTime endValue){
		addWhereBetween(attribute.getName(), startValue, endValue);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(String field){
		this.listWhere.put(field, Where.GREATER_THAN);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<?, ?> attribute){
		addWhereGreaterThan(attribute.getName());
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(String field){
		this.listWhere.put(field, Where.GREATER_THAN_OR_EQUAL_TO);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<?, ?> attribute){
		addWhereGreaterThanOrEqualTo(attribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereIn(String field){
		this.listWhere.put(field, Where.IN);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereIn(Attribute<?, ?> attribute){
		addWhereIn(attribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereIsNotNull(String field){
		this.listWhere.put(field, Where.IS_NOT_NULL);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereIsNotNull(Attribute<?, ?> attribute){
		addWhereIsNotNull(attribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereIsNull(String field){
		this.listWhere.put(field, Where.IS_NULL);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereIsNull(Attribute<?, ?> attribute){
		addWhereIsNull(attribute.getName());
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(String field){
		this.listWhere.put(field, Where.LESS_THAN);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<?, ?> attribute){
		addWhereLessThan(attribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(String field){
		this.listWhere.put(field, Where.LESS_THAN_OR_EQUAL_TO);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<?, ?> attribute){
		addWhereLessThanOrEqualTo(attribute.getName());
		return this;
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
		addWhereNotLike(attribute.getName(), matchMode);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereLike(Attribute<?, ?> attribute, MatchMode matchMode){
		addWhereLike(attribute.getName(), matchMode);
		return this;
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
		addWhereNotILike(attribute.getName(), matchMode);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereILike(Attribute<?, ?> attribute, MatchMode matchMode){
		addWhereILike(attribute.getName(), matchMode);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqual(String field){
		this.listWhere.put(field, Where.NOT_EQUAL);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<?, ?> attribute){
		addWhereNotEqual(attribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotIn(String field){
		this.listWhere.put(field, Where.NOT_IN);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotIn(Attribute<?, ?> attribute){
		addWhereNotIn(attribute.getName());
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addGroupBy(String field){
		this.listGroupBy.add(field);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addGroupBy(Attribute<?, ?> attribute){
		addGroupBy(attribute.getName());
		return this;
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
		addWhereEqual(attribute.getName(), value);
		return this;
	}

	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(String field, E value) {
		this.whereRestriction.add(field, Where.NOT_EQUAL, value);
		return this;
	}

	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(Attribute<?, ?> attribute, E value) {
		addWhereNotEqual(attribute.getName(), value);
		return this;
	}
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereGreaterThan(String field, Date value) {
		this.whereRestriction.add(field, Where.GREATER_THAN, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<?, ?> attribute, Date value) {
		addWhereGreaterThan(attribute.getName(), value);
		return this;
	}

	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereGreaterThan(String field, Number value) {
		this.whereRestriction.add(field, Where.GREATER_THAN, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(Attribute<?, ?> attribute, Number value) {
		addWhereGreaterThan(attribute.getName(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(String field, Date value) {
		this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<?, ?> attribute, Date value) {
		addWhereGreaterThanOrEqualTo(attribute.getName(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(String field, Number value) {
		this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(Attribute<?, ?> attribute, Number value) {
		addWhereGreaterThanOrEqualTo(attribute.getName(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(String field, Date value) {
		this.whereRestriction.add(field, Where.LESS_THAN, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<?, ?> attribute, Date value) {
		addWhereLessThan(attribute.getName(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(String field, Number value) {
		this.whereRestriction.add(field, Where.LESS_THAN, value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(Attribute<?, ?> attribute, Number value) {
		addWhereLessThan(attribute.getName(), value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(String field, Date value) {
		this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO, value);
		return this;
	}
	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<?, ?> attribute, Date value) {
		addWhereLessThanOrEqualTo(attribute.getName(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(String field, Number value) {
		this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO, value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(Attribute<?, ?> attribute, Number value) {
		addWhereLessThanOrEqualTo(attribute.getName(), value);
		return this;
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
		this.addWhereNotLike(attribute.getName(), value, matchMode);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereLike(Attribute<?, ?> attribute, String value, MatchMode matchMode) {
		this.addWhereLike(attribute.getName(), value, matchMode);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereILike(Attribute<?, ?> attribute, String value, MatchMode matchMode) {
		this.addWhereILike(attribute.getName(), value, matchMode);
		return this;
	}
	
	@Override
	public CriteriaFilterMetamodel<T> addWhereNotILike(Attribute<?, ?> attribute, String value, MatchMode matchMode) {
		this.addWhereNotILike(attribute.getName(), value, matchMode);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereILike(ComplexAttribute attribute, String value, MatchMode matchMode) {
		addWhereILike(attribute.getMetamodelAttribute(), value, matchMode);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereILike(ComplexAttribute attribute, MatchMode matchMode) {
		addWhereILike(attribute.getMetamodelAttribute(), matchMode);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereNotILike(ComplexAttribute attribute, String value, MatchMode matchMode) {
		addWhereNotILike(attribute.getMetamodelAttribute(), value, matchMode);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereNotLike(ComplexAttribute attribute, String value, MatchMode matchMode) {
		addWhereNotLike(attribute.getMetamodelAttribute(), value, matchMode);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLike(ComplexAttribute attribute,	String value, MatchMode matchMode) {
		addWhereLike(attribute.getMetamodelAttribute(), value, matchMode);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereNotILike(ComplexAttribute attribute, MatchMode matchMode) {
		addWhereNotILike(attribute.getMetamodelAttribute(), matchMode);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereNotLike(ComplexAttribute attribute, MatchMode matchMode) {
		addWhereNotLike(attribute.getMetamodelAttribute(), matchMode);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLike(ComplexAttribute attribute, MatchMode matchMode) {
		addWhereLike(attribute.getMetamodelAttribute(), matchMode);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelect(ComplexAttribute attribute, String alias) {
		addSelect(attribute.getMetamodelAttribute(), alias);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelect(ComplexAttribute... attributes) {
		for(ComplexAttribute ca : attributes) {
			this.addSelect(ca.getMetamodelAttribute());
		}
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectCount(ComplexAttribute attribute, String alias) {
		addSelectCount(attribute.getMetamodelAttribute(), alias);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectCount(ComplexAttribute attribute) {
		addSelectCount(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectUpper(ComplexAttribute attribute) {
		addSelectUpper(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectUpper(ComplexAttribute attribute, String alias) {
		addSelectUpper(attribute.getMetamodelAttribute(), alias);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectLower(ComplexAttribute attribute) {
		addSelectLower(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectLower(ComplexAttribute attribute, String alias) {
		addSelectLower(attribute.getMetamodelAttribute(), alias);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectMax(ComplexAttribute attribute, String alias) {
		addSelectMax(attribute.getMetamodelAttribute(), alias);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectMax(ComplexAttribute attribute) {
		addSelectMax(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectMin(ComplexAttribute attribute, String alias) {
		addSelectMin(attribute.getMetamodelAttribute(), alias);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectMin(ComplexAttribute attribute) {
		addSelectMin(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectSum(ComplexAttribute attribute, String alias) {
		addSelectSum(attribute.getMetamodelAttribute(), alias);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelectSum(ComplexAttribute attribute) {
		addSelectSum(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addSelect(ComplexAttribute attribute) {
		addSelect(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addOrderAsc(ComplexAttribute attribute) {
		addOrderAsc(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addOrderDesc(ComplexAttribute attribute) {
		addOrderDesc(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereEqual(	ComplexAttribute attribute) {
		addWhereEqual(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereIn(ComplexAttribute attribute, List<E> values) {
		addWhereIn(attribute.getMetamodelAttribute(), values);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereIn(ComplexAttribute attribute, E... values) {
		addWhereIn(attribute.getMetamodelAttribute(), values);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotIn(ComplexAttribute attribute, E... values) {
		addWhereNotIn(attribute.getMetamodelAttribute(), values);
		return this;
	}

	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotIn(ComplexAttribute attribute, List<E> values) {
		addWhereNotIn(attribute.getMetamodelAttribute(), values);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute, E... values) {
		addWhereEqual(attribute.getMetamodelAttribute(), values);
		return this;
	}

	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute, List<E> values) {
		addWhereEqual(attribute.getMetamodelAttribute(), values);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, E... values) {
		addWhereNotEqual(attribute.getMetamodelAttribute(), values);
		return this;
	}

	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, List<E> values) {
		addWhereNotEqual(attribute.getMetamodelAttribute(), values);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Integer startValue, Integer endValue) {
		addWhereBetween(attribute.getMetamodelAttribute(), startValue, endValue);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Short startValue, Short endValue) {
		addWhereBetween(attribute.getMetamodelAttribute(), startValue, endValue);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Long startValue, Long endValue) {
		addWhereBetween(attribute.getMetamodelAttribute(), startValue, endValue);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute) {
		addWhereLessThanField(attribute.getMetamodelAttribute(), anotherAttribute.getName());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute) {
		addWhereGreaterThanField(attribute.getMetamodelAttribute(), anotherAttribute.getName());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualToField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute) {
		addWhereLessThanOrEqualToField(attribute.getMetamodelAttribute(), anotherAttribute.getName());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualToField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute) {
		addWhereGreaterThanOrEqualToField(attribute.getMetamodelAttribute(), anotherAttribute.getName());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereEqualField(ComplexAttribute attribute, Attribute<?, ?> anotherAttribute) {
		addWhereEqualField(attribute.getMetamodelAttribute(), anotherAttribute.getName());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqualField(ComplexAttribute attribute, 	Attribute<?, ?> anotherAttribute) {
		addWhereNotEqualField(attribute.getMetamodelAttribute(), anotherAttribute.getName());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, Date startValue, Date endValue) {
		addWhereBetween(attribute.getMetamodelAttribute(), startValue, endValue);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, LocalDate startValue, LocalDate endValue) {
		addWhereBetween(attribute.getMetamodelAttribute(), startValue, endValue);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereBetween(ComplexAttribute attribute, LocalDateTime startValue, LocalDateTime endValue) {
		addWhereBetween(attribute.getMetamodelAttribute(), startValue, endValue);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute) {
		addWhereGreaterThan(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute) {
		addWhereGreaterThanOrEqualTo(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereIsNotNull(ComplexAttribute attribute) {
		addWhereIsNotNull(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereIsNull(ComplexAttribute attribute) {
		addWhereIsNull(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereIn(ComplexAttribute attribute) {
		addWhereIn(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(ComplexAttribute attribute) {
		addWhereLessThan(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute) {
		addWhereLessThanOrEqualTo(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereNotEqual(	ComplexAttribute attribute) {
		addWhereNotEqual(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereNotIn(ComplexAttribute attribute) {
		addWhereNotIn(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addGroupBy(ComplexAttribute attribute) {
		addGroupBy(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addJoin(ComplexAttribute attribute, JoinType joinType, boolean fetch) {
		addJoin(attribute.getMetamodelAttribute(), joinType, fetch);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addJoin(ComplexAttribute attribute, JoinType joinType) {
		addJoin(attribute.getMetamodelAttribute(), joinType);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addJoin(ComplexAttribute attribute) {
		addJoin(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereEqual(ComplexAttribute attribute, E value) {
		addWhereEqual(attribute.getMetamodelAttribute());
		return this;
	}

	@Override
	public <E> CriteriaFilterMetamodel<T> addWhereNotEqual(ComplexAttribute attribute, E value) {
		addWhereNotEqual(attribute.getMetamodelAttribute(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute, Date value) {
		addWhereGreaterThan(attribute.getMetamodelAttribute(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThan(ComplexAttribute attribute, Number value) {
		addWhereGreaterThan(attribute.getMetamodelAttribute(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute, Date value) {
		addWhereGreaterThanOrEqualTo(attribute.getMetamodelAttribute(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereGreaterThanOrEqualTo(ComplexAttribute attribute, Number value) {
		addWhereGreaterThanOrEqualTo(attribute.getMetamodelAttribute(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(ComplexAttribute attribute, Date value) {
		addWhereLessThan(attribute.getMetamodelAttribute(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThan(ComplexAttribute attribute, Number value) {
		addWhereLessThan(attribute.getMetamodelAttribute(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Date value) {
		addWhereLessThanOrEqualTo(attribute.getMetamodelAttribute(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereLessThanOrEqualTo(ComplexAttribute attribute, Number value) {
		addWhereLessThanOrEqualTo(attribute.getMetamodelAttribute(), value);
		return this;
	}

	@Override
	public CriteriaFilterMetamodel<T> addWhereRegex(ComplexAttribute attribute, Class<?> fieldType, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException {
		addWhereRegex(attribute.getMetamodelAttribute(), fieldType, value, regexToAnalyse, defaultIfNotMatch);
		return this;
	}

}

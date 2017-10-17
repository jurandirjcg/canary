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
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
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
import br.com.jgon.canary.exception.MessageSeverity;
import br.com.jgon.canary.persistence.filter.CriteriaFilter;
import br.com.jgon.canary.util.DateUtil;

/**
 * Define os filtros que serao utilizados para construir a criteria 
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 * @param <T>
 */
class CriteriaFilterImpl<T> implements CriteriaFilter<T> {
	
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
		LIKE (RegexWhere.LIKE, "(?<=^\\=\\%)" + regexPatternAlpha + "(?!\\%)"),
		NOT_LIKE (RegexWhere.NOT_LIKE, "(?<=^\\!\\%)" + regexPatternAlpha + "(?!\\%)"),
		LIKE_ANY_BEFORE_AND_AFTER (RegexWhere.LIKE_ANY_BEFORE_AND_AFTER, "(?<=^\\%)" + regexPatternAlpha + "(?=\\%$)"),
		LIKE_ANY_BEFORE (RegexWhere.LIKE_ANY_BEFORE, "(?<=^\\%)" +  regexPatternAlpha + "(?!\\%$)"),
		LIKE_ANY_AFTER (RegexWhere.LIKE_ANY_AFTER, "(?<!^\\%)" +  regexPatternAlpha + "(?=\\%$)"),
		ILIKE (RegexWhere.ILIKE, "(?<=^\\=\\*)" + regexPatternAlpha + "(?!\\*)"),
		NOT_ILIKE (RegexWhere.NOT_ILIKE, "(?<=^\\!\\*)" + regexPatternAlpha + "(?!\\*)"),
		ILIKE_ANY_BEFORE_AND_AFTER (RegexWhere.ILIKE_ANY_BEFORE_AND_AFTER, "(?<=^\\*)" +  regexPatternAlpha + "(?=\\*$)"),
		ILIKE_ANY_BEFORE (RegexWhere.ILIKE_ANY_BEFORE, "(?<=^\\*)" +  regexPatternAlpha + "(?!\\*$)"),
		ILIKE_ANY_AFTER (RegexWhere.ILIKE_ANY_AFTER, "(?<!^\\*)" +  regexPatternAlpha + "(?=\\*$)"),
		IS_NULL (RegexWhere.IS_NULL, "^null$"),
		IS_NOT_NULL (RegexWhere.IS_NOT_NULL, "^not null$"),
		BETWEEN (RegexWhere.BETWEEN,"(?<=^\\()" + regexPatternDateTimeOrNumber + "(\\s&\\s)" + regexPatternDateTimeOrNumber + "(?=\\)$)"),
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
	private WhereRestriction whereRestriction = new WhereRestriction();// Map<String, SimpleEntry<Where, ?>> listWhereComplex = new LinkedHashMap<String, SimpleEntry<Where,?>>();
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
	private CriteriaFilter<T> addSelect(String field, SelectAggregate selectFunction, String alias){
		this.listSelection.put(field, new SimpleEntry<CriteriaFilterImpl.SelectAggregate, String>(selectFunction, alias));
		return this;
	}
	/**
	 * 
	 * @param returnType
	 * @return
	 * @throws ApplicationException 
	 */
	public CriteriaFilter<T> addSelect(Class<?> returnType) throws ApplicationException{
		return addSelect(returnType, (String[]) null);
	}
	
	@Override
	public CriteriaFilter<T> addSelect(Class<?> returnType, List<String> fields) throws ApplicationException{
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
	public CriteriaFilter<T> addSelect(Class<?> returnType, String... fields) throws ApplicationException{
		return addSelect(returnType, fields == null ? null : Arrays.asList(fields));
	}
		
	@Override
	public CriteriaFilter<T> addSelect(String field, String alias){
		return addSelect(field, SelectAggregate.FIELD, alias);
	}
	
	@Override
	public CriteriaFilter<T> addSelect(Attribute<?, ?> attribute, String alias){
		return addSelect(attribute.getName(), alias);
	}
	
	@Override
	public CriteriaFilter<T> addSelect(String[] fields){
		for(String fld : fields){
			addSelect(fld);
		}
		return this;
	}

	@Override
	public CriteriaFilter<T> addSelect(Attribute<?, ?>... attributes){
		for(Attribute<?, ?> fld : attributes){
			addSelect(fld);
		}
		return this;
	}
	@Override
	public CriteriaFilter<T> addSelect(List<String> fields) {
		fields.forEach( item -> {
			addSelect(item);
		});
		return this;
	}
	
	@Override
	public CriteriaFilter<T> addSelect(Map<String, String> fieldAlias){
		for(String k : fieldAlias.keySet()){
			addSelect(k, fieldAlias.get(k));
		}
		return this;
	}
	
	@Override
	public CriteriaFilter<T> addSelectCount(String field, String alias){
		return addSelect(field, SelectAggregate.COUNT, alias);
	}
	
	@Override
	public CriteriaFilter<T> addSelectCount(Attribute<?, ?> attribute, String alias){
		return addSelect(attribute.getName(), SelectAggregate.COUNT, alias);
	}
	
	@Override
	public CriteriaFilter<T> addSelectCount(String field){
		return addSelect(field, SelectAggregate.COUNT, field);
	}

	@Override
	public CriteriaFilter<T> addSelectCount(Attribute<?, ?> attribute){
		return addSelect(attribute.getName(), SelectAggregate.COUNT, attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addSelectUpper(String field){
		return addSelect(field, SelectAggregate.UPPER, field);
	}

	@Override
	public CriteriaFilter<T> addSelectUpper(Attribute<?, ?> attribute){
		return addSelect(attribute.getName(), SelectAggregate.UPPER, attribute.getName());
	}
	@Override
	public CriteriaFilter<T> addSelectUpper(String field, String alias){
		return addSelect(field, SelectAggregate.UPPER, alias);
	}
	@Override
	public CriteriaFilter<T> addSelectUpper(Attribute<?, ?> attribute, String alias){
		return addSelect(attribute.getName(), SelectAggregate.UPPER, alias);
	}
	@Override
	public CriteriaFilter<T> addSelectLower(String field){
		return addSelect(field, SelectAggregate.LOWER, field);
	}
	@Override
	public CriteriaFilter<T> addSelectLower(Attribute<?, ?> attribute){
		return addSelect(attribute.getName(), SelectAggregate.LOWER, attribute.getName());
	}
	@Override
	public CriteriaFilter<T> addSelectLower(String field, String alias){
		return addSelect(field, SelectAggregate.LOWER, alias);
	}
	@Override
	public CriteriaFilter<T> addSelectLower(Attribute<?, ?> attribute, String alias){
		return addSelect(attribute.getName(), SelectAggregate.LOWER, alias);
	}
	
	@Override
	public CriteriaFilter<T> addSelectMax(String field, String alias){
		return addSelect(field, SelectAggregate.MAX, alias);
	}

	@Override
	public CriteriaFilter<T> addSelectMax(Attribute<?, ?> attribute, String alias){
		return addSelect(attribute.getName(), SelectAggregate.MAX, alias);
	}
	@Override
	public CriteriaFilter<T> addSelectMax(String field){
		return addSelect(field, SelectAggregate.MAX, field);
	}

	@Override
	public CriteriaFilter<T> addSelectMax(Attribute<?, ?> attribute){
		return addSelect(attribute.getName(), SelectAggregate.MAX, attribute.getName());
	}
	@Override
	public CriteriaFilter<T> addSelectMin(String field, String alias){
		return addSelect(field, SelectAggregate.MIN, alias);
	}
	@Override
	public CriteriaFilter<T> addSelectMin(Attribute<?, ?> attribute, String alias){
		return addSelect(attribute.getName(), SelectAggregate.MIN, alias);
	}
	
	@Override
	public CriteriaFilter<T> addSelectMin(String field){
		return addSelect(field, SelectAggregate.MIN, field);
	}
	@Override
	public CriteriaFilter<T> addSelectMin(Attribute<?, ?> attribute){
		return addSelect(attribute.getName(), SelectAggregate.MIN, attribute.getName());
	}
	@Override
	public CriteriaFilter<T> addSelectSum(String field, String alias){
		return addSelect(field, SelectAggregate.SUM, alias);
	}
	@Override
	public CriteriaFilter<T> addSelectSum(Attribute<?, ?> attribute, String alias){
		return addSelect(attribute.getName(), SelectAggregate.SUM, alias);
	}
	
	@Override
	public CriteriaFilter<T> addSelectSum(String field){
		return addSelect(field, SelectAggregate.SUM, field);
	}
	@Override
	public CriteriaFilter<T> addSelectSum(Attribute<?, ?> attribute){
		return addSelect(attribute.getName(), SelectAggregate.SUM, attribute.getName());
	}
	@Override
	public CriteriaFilter<T> addSelect(String field){
		return addSelect(field, SelectAggregate.FIELD, field);
	}
	@Override
	public CriteriaFilter<T> addSelect(Attribute<?, ?> attribute){
		return addSelect(attribute.getName(), SelectAggregate.FIELD, attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addOrder(Class<?> returnType, String... order) throws ApplicationException{
		return addOrder(returnType, Arrays.asList(order));
	}
		
	@Override
	public CriteriaFilter<T> addOrder(Class<?> returnType, List<String> order) throws ApplicationException{
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
	public CriteriaFilter<T> addOrder(List<String> orderList){
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
	public CriteriaFilter<T> addOrder(String... order){
		return addOrder(Arrays.asList(order));
	}
	
	
	@Override
	public CriteriaFilter<T> addOrderAsc(String field){
		this.listOrder.put(field, Order.ASC);
		return this;
	}
	
	@Override
	public CriteriaFilter<T> addOrderAsc(Attribute<?, ?> attribute){
		return this.addOrderAsc(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addOrderDesc(String field){
		this.listOrder.put(field, Order.DESC);
		return this;
	}
	
	@Override
	public CriteriaFilter<T> addOrderDesc(Attribute<?, ?> attribute){
		return addOrderDesc(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereEqual(String field){
		this.listWhere.put(field, Where.EQUAL);
		return this;
	}
	
	@Override
	public CriteriaFilter<T> addWhereEqual(Attribute<?, ?> attribute){
		return this.addWhereEqual(attribute.getName());
	}
	/**
	 * 
	 * @param field
	 * @param where
	 * @param values
	 * @return
	 */
	private <E> CriteriaFilter<T> addWhereListValues(String field, Where where, List<E> values){
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
	private <E> CriteriaFilter<T> addWhereListValues(String field, Where where, E[] values){
		if(values != null){
			return addWhereListValues(field, where, Arrays.asList(values));
		}
		return this;
	}
	
	@Override
	public <E> CriteriaFilter<T> addWhereIn(String field, List<E> values){
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
	public CriteriaFilter<T> addWhereRegex(String field, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException{
		boolean added = configWhereRegex(field, value, regexToAnalyse, defaultIfNotMatch);
		if(!added && defaultIfNotMatch != null){
			throw new ApplicationException(MessageSeverity.ERROR, "error.regex-config", value, field);
		}
		return this;
	}
	
	public boolean configWhereRegex(String field, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException{
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
					String[] val = m.group().replace(" ", "").split("&");
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
					if(val[0] != null && val[0].matches(regexPatternDateTime)){
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
					Date dt = DateUtil.parseDate(m.group());
					if(val.matches(regexPatternDateTime)){
						if(val.matches(regexPatternDate) && (where.equals(Where.LESS_THAN) || where.equals(Where.LESS_THAN_OR_EQUAL_TO))){
							DateUtils.setHours(dt, 23);
							DateUtils.setMinutes(dt, 59);
							DateUtils.setSeconds(dt, 59);
							DateUtils.setMilliseconds(dt, 999);
						}
						this.whereRestriction.add(field, where, dt);
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
				final String multiWhere = "^(<|<=|=|!=|>=|>|)" + regexPatternDateTimeOrNumber +  "(;\\s?(<|<=|=|!=|>=|>|)" + regexPatternDateTimeOrNumber + "){1,}$";
				Pattern p = Pattern.compile(multiWhere);
				Matcher m = p.matcher(value);

				if(m.find()){
					found= true;
					String[] val = m.group().split(";");

					for(String v: val){
						boolean add = configWhereRegex(field, v, new RegexWhere[] {
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
				if(defaultIfNotMatch.equals(RegexWhere.EQUAL) && value.matches("^[a-zA-Z0-9]" + regexPatternAlpha + "$")){
					this.whereRestriction.add(field, Where.EQUAL, value);
					return true;
				}else{
					return configWhereRegex(field, value, new RegexWhere[] {defaultIfNotMatch}, null);
				}
			}
		}
		return false;
	}
	
	@Override
	public CriteriaFilter<T> addWhereRegex(Attribute<?, ?> attribute, String value, RegexWhere[] regexToAnalyse, RegexWhere defaultIfNotMatch) throws ApplicationException{
		return addWhereRegex(attribute.getName(), value, regexToAnalyse, defaultIfNotMatch);
	}
	private boolean checkRegex(String value, String regex){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);
		return m.find();
	}
		
	@Override
	public <E> CriteriaFilter<T> addWhereIn(Attribute<?, ?> attribute, List<E> values){
		return this.addWhereIn(attribute.getName(), values);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilter<T> addWhereIn(String field, E... values){
		return addWhereListValues(field, Where.IN, values);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilter<T> addWhereIn(Attribute<?, ?> attribute, E... values){
		return this.addWhereIn(attribute.getName(), values);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilter<T> addWhereNotIn(String field, E... values){
		return addWhereListValues(field, Where.NOT_IN, values);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <E> CriteriaFilter<T> addWhereNotIn(Attribute<?, ?> attribute, E... values){
		return this.addWhereNotIn(attribute.getName(), values);
	}
	
	@Override
	public <E> CriteriaFilter<T> addWhereNotIn(String field, List<E> values){
		return addWhereListValues(field, Where.NOT_IN, values);
	}
	@Override
	public <E> CriteriaFilter<T> addWhereNotIn(Attribute<?, ?> attribute, List<E> values){
		return this.addWhereNotIn(attribute.getName(), values);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilter<T> addWhereEqual(String field, E... values){
		return addWhereListValues(field, Where.EQUAL, values);
	}
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilter<T> addWhereEqual(Attribute<?, ?> attribute, E... values){
		return addWhereEqual(attribute.getName(), values);
	}
	
	@Override
	public <E> CriteriaFilter<T> addWhereEqual(String field, List<E> values){
		return addWhereListValues(field, Where.EQUAL, values);
	}
	@Override
	public <E> CriteriaFilter<T> addWhereEqual(Attribute<?, ?> attribute, List<E> values){
		return addWhereEqual(attribute.getName(), values);
	}
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilter<T> addWhereNotEqual(String field, E... values){
		return addWhereListValues(field, Where.NOT_EQUAL, values);
	}
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilter<T> addWhereNotEqual(Attribute<?, ?> attribute, E... values){
		return addWhereNotEqual(attribute.getName(), values);
	}
	@Override
	public <E> CriteriaFilter<T> addWhereNotEqual(String field, List<E> values){
		return addWhereListValues(field, Where.NOT_EQUAL, values);
	}
	@Override
	public <E> CriteriaFilter<T> addWhereNotEqual(Attribute<?, ?> attribute, List<E> values){
		return addWhereNotEqual(attribute.getName(), values);
	}
	/**
	 * 
	 * @param listWhere
	 * @return
	 */
	public CriteriaFilter<T> addAllWhere(Map<String, Where> listWhere){
		this.listWhere.putAll(listWhere);
		return this;
	}
	/**
	 * 
	 * @param listComplexWhere
	 * @return
	 */
	public CriteriaFilter<T> addAllWhereComplex(Map<String, List<SimpleEntry<Where, ?>>> listComplexWhere){
		this.whereRestriction.getRestrictions().putAll(listComplexWhere);
		return this;
	}
	/**
	 * 
	 * @param listJoin
	 * @return
	 */
	public CriteriaFilter<T> addAllJoin(Map<String, SimpleEntry<JoinType, Boolean>> listJoin){
		this.listJoin.putAll(listJoin);
		return this;
	}
	/**
	 * 
	 * @param listSelection
	 * @return
	 */
	public CriteriaFilter<T> addAllSelection(Map<String, SimpleEntry<SelectAggregate, String>> listSelection){
		this.listSelection.putAll(listSelection);
		return this;
	}
	
	@Override
	public CriteriaFilter<T> addWhereBetween(String field, Integer startValue, Integer endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new Integer[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereBetween(String field, Double startValue, Double endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new Double[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereBetween(Attribute<?, ?> attribute, Integer startValue, Integer endValue){
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilter<T> addWhereBetween(String field, Short startValue, Short endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new Short[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereBetween(Attribute<?, ?> attribute, Short startValue, Short endValue){
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilter<T> addWhereBetween(String field, Long startValue, Long endValue){
		this.whereRestriction.add(field, Where.BETWEEN, new Long[] {startValue, endValue});
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereBetween(Attribute<?, ?> attribute, Long startValue, Long endValue){
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	
	@Override
	public CriteriaFilter<T> addWhereLessThanField(String field, String anotherField){
		this.whereRestriction.add(field, Where.LESS_THAN_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereLessThanField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		return addWhereLessThanField(attribute.getName(), anotherAttribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereGreaterThanField(String field, String anotherField){
		this.whereRestriction.add(field, Where.GREATER_THAN_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereGreaterThanField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		return addWhereGreaterThanField(attribute.getName(), anotherAttribute.getName());
	}
	@Override
	public CriteriaFilter<T> addWhereLessThanOrEqualToField(String field, String anotherField){
		this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereLessThanOrEqualToField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		return addWhereLessThanOrEqualToField(attribute.getName(), anotherAttribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereGreaterThanOrEqualToField(String field, String anotherField){
		this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereGreaterThanOrEqualToField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		return addWhereGreaterThanField(attribute.getName(), anotherAttribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereEqualField(String field, String anotherField){
		this.whereRestriction.add(field, Where.EQUAL_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereEqualField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		return addWhereEqualField(attribute.getName(), anotherAttribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereNotEqualField(String field, String anotherField){
		this.whereRestriction.add(field, Where.NOT_EQUAL_OTHER_FIELD, anotherField);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereNotEqualField(Attribute<?, ?> attribute, Attribute<?, ?> anotherAttribute){
		return addWhereNotEqualField(attribute.getName(), anotherAttribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereBetween(String field, Date startValue, Date endValue){
		LocalDate dtSrt = startValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate dtEnd = endValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		return addWhereBetween(field, dtSrt, dtEnd);
	}
	@Override
	public CriteriaFilter<T> addWhereBetween(Attribute<?, ?> attribute, Date startValue, Date endValue){
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	@Override
	public CriteriaFilter<T> addWhereBetween(String field, LocalDate startValue, LocalDate endValue){
		Date dtSrt = Date.from(startValue.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date dtEnd = Date.from(LocalDateTime.of(endValue, LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
		
		this.whereRestriction.add(field, Where.BETWEEN, new Date[] {dtSrt, dtEnd});
		return this;
	}
	
	@Override
	public CriteriaFilter<T> addWhereBetween(Attribute<?, ?> attribute, LocalDate startValue, LocalDate endValue){
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
		
	@Override
	public CriteriaFilter<T> addWhereBetween(String field, LocalDateTime startValue, LocalDateTime endValue){
		Date dtSrt = Date.from(startValue.atZone(ZoneId.systemDefault()).toInstant());
		Date dtEnd = Date.from(endValue.atZone(ZoneId.systemDefault()).toInstant());
		
		this.whereRestriction.add(field, Where.BETWEEN, new Date[] {dtSrt, dtEnd});
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereBetween(Attribute<?, ?> attribute, LocalDateTime startValue, LocalDateTime endValue){
		return addWhereBetween(attribute.getName(), startValue, endValue);
	}
	
	@Override
	public CriteriaFilter<T> addWhereGreaterThan(String field){
		this.listWhere.put(field, Where.GREATER_THAN);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereGreaterThan(Attribute<?, ?> attribute){
		return addWhereGreaterThan(attribute.getName());
	}
	@Override
	public CriteriaFilter<T> addWhereGreaterThanOrEqualTo(String field){
		this.listWhere.put(field, Where.GREATER_THAN_OR_EQUAL_TO);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereGreaterThanOrEqualTo(Attribute<?, ?> attribute){
		return addWhereGreaterThanOrEqualTo(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereIn(String field){
		this.listWhere.put(field, Where.IN);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereIn(Attribute<?, ?> attribute){
		return addWhereIn(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereIsNotNull(String field){
		this.listWhere.put(field, Where.IS_NOT_NULL);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereIsNotNull(Attribute<?, ?> attribute){
		return addWhereIsNotNull(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereIsNull(String field){
		this.listWhere.put(field, Where.IS_NULL);
		return this;
	}

	@Override
	public CriteriaFilter<T> addWhereIsNull(Attribute<?, ?> attribute){
		return addWhereIsNull(attribute.getName());
	}
	@Override
	public CriteriaFilter<T> addWhereLessThan(String field){
		this.listWhere.put(field, Where.LESS_THAN);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereLessThan(Attribute<?, ?> attribute){
		return addWhereLessThan(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereLessThanOrEqualTo(String field){
		this.listWhere.put(field, Where.LESS_THAN_OR_EQUAL_TO);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereLessThanOrEqualTo(Attribute<?, ?> attribute){
		return addWhereLessThanOrEqualTo(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereLike(String field){
		this.listWhere.put(field, Where.LIKE);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereLike(Attribute<?, ?> attribute){
		return addWhereLike(attribute.getName());
	}
	@Override
	public CriteriaFilter<T> addWhereNotLike(String field){
		this.listWhere.put(field, Where.NOT_LIKE);
		return this;
	}

	@Override
	public CriteriaFilter<T> addWhereNotLike(Attribute<?, ?> attribute){
		return addWhereNotLike(attribute.getName());
	}
	@Override
	public CriteriaFilter<T> addWhereLikeAnyAfter(String field){
		this.listWhere.put(field, Where.LIKE_ANY_AFTER);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereLikeAnyAfter(Attribute<?, ?> attribute){
		return addWhereLikeAnyAfter(attribute.getName());
	}
	@Override
	public CriteriaFilter<T> addWhereLikeAnyBefore(String field){
		this.listWhere.put(field, Where.LIKE_ANY_BEFORE);
		return this;
	}

	@Override
	public CriteriaFilter<T> addWhereLikeAnyBefore(Attribute<?, ?> attribute){
		return addWhereLikeAnyBefore(attribute.getName());
	}
	@Override
	public CriteriaFilter<T> addWhereLikeAnyBeforeAfter(String field){
		this.listWhere.put(field, Where.LIKE_ANY_BEFORE_AND_AFTER);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereLikeAnyBeforeAfter(Attribute<?, ?> attribute){
		return addWhereLikeAnyBeforeAfter(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereILike(String field){
		this.listWhere.put(field, Where.ILIKE);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereILike(Attribute<?, ?> attribute){
		return addWhereILike(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereNotILike(String field){
		this.listWhere.put(field, Where.NOT_ILIKE);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereNotILike(Attribute<?, ?> attribute){
		return addWhereNotILike(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereILikeAnyAfter(String field){
		this.listWhere.put(field, Where.ILIKE_ANY_AFTER);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereILikeAnyAfter(Attribute<?, ?> attribute){
		return addWhereILikeAnyAfter(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereILikeAnyBefore(String field){
		this.listWhere.put(field, Where.ILIKE_ANY_BEFORE);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereILikeAnyBefore(Attribute<?, ?> attribute){
		return addWhereILikeAnyBefore(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereILikeAnyBeforeAfter(String field){
		this.listWhere.put(field, Where.ILIKE_ANY_BEFORE_AND_AFTER);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereILikeAnyBeforeAfter(Attribute<?, ?> attribute){
		return addWhereILikeAnyBeforeAfter(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereNotEqual(String field){
		this.listWhere.put(field, Where.NOT_EQUAL);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereNotEqual(Attribute<?, ?> attribute){
		return addWhereNotEqual(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addWhereNotIn(String field){
		this.listWhere.put(field, Where.NOT_IN);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereNotIn(Attribute<?, ?> attribute){
		return addWhereNotIn(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addGroupBy(String field){
		this.listGroupBy.add(field);
		return this;
	}
	
	@Override
	public CriteriaFilter<T> addGroupBy(Attribute<?, ?> attribute){
		return addGroupBy(attribute.getName());
	}
	
	@Override
	public CriteriaFilter<T> addJoin(String field, JoinType joinType, boolean fetch){
		this.listJoin.put(field, new SimpleEntry<JoinType, Boolean>(joinType, fetch));
		return this;
	}
	@Override
	public CriteriaFilter<T> addJoin(Attribute<?, ?> attribute, JoinType joinType, boolean fetch){
		return addJoin(attribute.getName(), joinType, fetch);
	}
	
	@Override
	public CriteriaFilter<T> addJoin(String field, JoinType joinType){
		return addJoin(field, joinType, false);
	}

	@Override
	public CriteriaFilter<T> addJoin(Attribute<?, ?> attribute, JoinType joinType){
		return addJoin(attribute.getName(), joinType);
	}
	@Override
	public CriteriaFilter<T> addJoin(String field){
		return addJoin(field, JoinType.INNER, false);
	}
	@Override
	public CriteriaFilter<T> addJoin(Attribute<?, ?> attribute){
		return addJoin(attribute.getName());
	}
	
	public Map<Class<?>, Map<String, SimpleEntry<SelectAggregate, String>>> getCollectionSelection() {
		return collectionSelection;
	}

	@Override
	public <E> CriteriaFilter<T> addWhereEqual(String field, E value) {
		this.whereRestriction.add(field, Where.EQUAL, value);
		return this;
	}
	@Override
	public <E> CriteriaFilter<T> addWhereEqual(Attribute<?, ?> attribute, E value) {
		return addWhereEqual(attribute.getName(), value);
	}

	@Override
	public <E> CriteriaFilter<T> addWhereNotEqual(String field, E value) {
		this.whereRestriction.add(field, Where.NOT_EQUAL, value);
		return this;
	}

	@Override
	public <E> CriteriaFilter<T> addWhereNotEqual(Attribute<?, ?> attribute, E value) {
		return addWhereNotEqual(attribute.getName(), value);
	}
	@Override
	public <E> CriteriaFilter<T> addWhereGreaterThan(String field, Date value) {
		this.whereRestriction.add(field, Where.GREATER_THAN, value);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereGreaterThan(Attribute<?, ?> attribute, Date value) {
		return addWhereGreaterThan(attribute.getName(), value);
	}

	@Override
	public <E> CriteriaFilter<T> addWhereGreaterThan(String field, Number value) {
		this.whereRestriction.add(field, Where.GREATER_THAN, value);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereGreaterThan(Attribute<?, ?> attribute, Number value) {
		return addWhereGreaterThan(attribute.getName(), value);
	}

	@Override
	public CriteriaFilter<T> addWhereGreaterThanOrEqualTo(String field, Date value) {
		this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO, value);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereGreaterThanOrEqualTo(Attribute<?, ?> attribute, Date value) {
		return addWhereGreaterThanOrEqualTo(attribute.getName(), value);
	}

	@Override
	public CriteriaFilter<T> addWhereGreaterThanOrEqualTo(String field, Number value) {
		this.whereRestriction.add(field, Where.GREATER_THAN_OR_EQUAL_TO, value);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereGreaterThanOrEqualTo(Attribute<?, ?> attribute, Number value) {
		return addWhereGreaterThanOrEqualTo(attribute.getName(), value);
	}

	@Override
	public CriteriaFilter<T> addWhereLessThan(String field, Date value) {
		this.whereRestriction.add(field, Where.LESS_THAN, value);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereLessThan(Attribute<?, ?> attribute, Date value) {
		return addWhereLessThan(attribute.getName(), value);
	}

	@Override
	public CriteriaFilter<T> addWhereLessThan(String field, Number value) {
		this.whereRestriction.add(field, Where.LESS_THAN, value);
		return this;
	}

	@Override
	public CriteriaFilter<T> addWhereLessThan(Attribute<?, ?> attribute, Number value) {
		return addWhereLessThan(attribute.getName(), value);
	}
	@Override
	public CriteriaFilter<T> addWhereLessThanOrEqualTo(String field, Date value) {
		this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO, value);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereLessThanOrEqualTo(Attribute<?, ?> attribute, Date value) {
		return addWhereLessThanOrEqualTo(attribute.getName(), value);
	}

	@Override
	public CriteriaFilter<T> addWhereLessThanOrEqualTo(String field, Number value) {
		this.whereRestriction.add(field, Where.LESS_THAN_OR_EQUAL_TO, value);
		return this;
	}

	@Override
	public CriteriaFilter<T> addWhereLessThanOrEqualTo(Attribute<?, ?> attribute, Number value) {
		return addWhereLessThanOrEqualTo(attribute.getName(), value);
	}
	@Override
	public CriteriaFilter<T> addWhereLike(String field, String value) {
		this.whereRestriction.add(field, Where.LIKE, value);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereLike(Attribute<?, ?> attribute, String value) {
		return addWhereLike(attribute.getName(), value);
	}

	@Override
	public CriteriaFilter<T> addWhereNotLike(String field, String value) {
		this.whereRestriction.add(field, Where.NOT_LIKE, value);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereNotLike(Attribute<?, ?> attribute, String value) {
		return addWhereNotLike(attribute.getName(), value);
	}

	@Override
	public CriteriaFilter<T> addWhereLikeAnyAfter(String field, String value) {
		this.whereRestriction.add(field, Where.LIKE_ANY_AFTER, value);
		return this;
	}

	@Override
	public CriteriaFilter<T> addWhereLikeAnyAfter(Attribute<?, ?> attribute, String value) {
		return addWhereLikeAnyAfter(attribute.getName(), value);
	}
	@Override
	public CriteriaFilter<T> addWhereLikeAnyBefore(String field, String value) {
		this.whereRestriction.add(field, Where.LIKE_ANY_BEFORE, value);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereLikeAnyBefore(Attribute<?, ?> attribute, String value) {
		return addWhereLikeAnyBefore(attribute.getName(), value);
	}

	@Override
	public CriteriaFilter<T> addWhereLikeAnyBeforeAfter(String field, String value) {
		this.whereRestriction.add(field, Where.LIKE_ANY_BEFORE_AND_AFTER, value);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereLikeAnyBeforeAfter(Attribute<?, ?> attribute, String value) {
		return addWhereLikeAnyBeforeAfter(attribute.getName(), value);
	}

	@Override
	public CriteriaFilter<T> addWhereILike(String field, String value) {
		this.whereRestriction.add(field, Where.ILIKE, value);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereILike(Attribute<?, ?> attribute, String value) {
		return addWhereILike(attribute.getName(), value);
	}

	@Override
	public CriteriaFilter<T> addWhereNotILike(String field, String value) {
		this.whereRestriction.add(field, Where.NOT_ILIKE, value);
		return this;
	}

	@Override
	public CriteriaFilter<T> addWhereNotILike(Attribute<?, ?> attribute, String value) {
		return addWhereNotILike(attribute.getName(), value);
	}
	@Override
	public CriteriaFilter<T> addWhereILikeAnyAfter(String field, String value) {
		this.whereRestriction.add(field, Where.ILIKE_ANY_AFTER, value);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereILikeAnyAfter(Attribute<?, ?> attribute, String value) {
		return addWhereILikeAnyAfter(attribute.getName(), value);
	}

	@Override
	public CriteriaFilter<T> addWhereILikeAnyBefore(String field, String value) {
		this.whereRestriction.add(field, Where.ILIKE_ANY_BEFORE, value);
		return this;
	}

	@Override
	public CriteriaFilter<T> addWhereILikeAnyBefore(Attribute<?, ?> attribute, String value) {
		return addWhereILikeAnyBefore(attribute.getName(), value);
	}
	@Override
	public CriteriaFilter<T> addWhereILikeAnyBeforeAfter(String field, String value) {
		this.whereRestriction.add(field, Where.ILIKE_ANY_BEFORE_AND_AFTER, value);
		return this;
	}
	@Override
	public CriteriaFilter<T> addWhereILikeAnyBeforeAfter(Attribute<?, ?> attribute, String value) {
		return addWhereILikeAnyBefore(attribute.getName(), value);
	}
}

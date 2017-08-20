package br.com.jgon.canary.jee.persistence;

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

import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;

import br.com.jgon.canary.jee.exception.ApplicationException;
import br.com.jgon.canary.jee.persistence.filter.CriteriaFilter;

/**
 * Define os filtros que serao utilizados para construir a criteria 
 * @author jurandir
 *
 * @param <T>
 */
class CriteriaFilterImpl<T> implements CriteriaFilter<T> {
	
	/**
	 * Filtro de restricao
	 * @author jurandir
	 *
	 */
	enum Where{
		IGNORE,
		EQUAL,
		LESS_THAN,
		LESS_THAN_OR_EQUAL_TO,
		GREATER_THAN,
		GREATER_THAN_OR_EQUAL_TO,
		NOT_EQUAL,
		IN,
		NOT_IN,
		LIKE,
		NOT_LIKE,
		LIKE_ANY_BEFORE_AND_AFTER,
		LIKE_ANY_BEFORE,
		LIKE_ANY_AFTER,
		ILIKE,
		NOT_ILIKE,
		ILIKE_ANY_BEFORE_AND_AFTER,
		ILIKE_ANY_BEFORE,
		ILIKE_ANY_AFTER,
		IS_NULL,
		IS_NOT_NULL,
		BETWEEN,
		EQUAL_OTHER_FIELD,
		LESS_THAN_OTHER_FIELD,
		GREATER_THAN_OTHER_FIELD,
		LESS_THAN_OR_EQUAL_TO_OTHER_FIELD,
		GREATER_THAN_OR_EQUAL_TO_OTHER_FIELD,
		NOT_EQUAL_OTHER_FIELD
	}
	
	/**
	 * Filtro de ordenacao
	 * @author jurandir
	 *
	 */
	enum Order{
		ASC,
		DESC
	}
	
	/**
	 * Filtro de selecao
	 * @author jurandir
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
	
	private Map<String, Where> listWhere = new LinkedHashMap<String, Where>(0);
	private Map<String, SimpleEntry<Where, ?>> listWhereComplex = new LinkedHashMap<String, SimpleEntry<Where,?>>();
	private Map<String, SimpleEntry<SelectAggregate, String>> listSelection = new LinkedHashMap<String, SimpleEntry<SelectAggregate, String>>(0);
	private Map<Class<?>, Map<String, SimpleEntry<SelectAggregate, String>>> collectionSelection = new LinkedHashMap<Class<?>, Map<String, SimpleEntry<SelectAggregate, String>>>();
	private Map<String, Order> listOrder = new LinkedHashMap<String, Order>(0);
	private Set<String> listGroupBy = new LinkedHashSet<String>();
	private Map<String, SimpleEntry<JoinType, Boolean>> listJoin = new LinkedHashMap<String, SimpleEntry<JoinType, Boolean>>();
	private T objBase;
	private Class<?> objClass;
	
	/**
	 * 
	 * @param objBase
	 */
	public CriteriaFilterImpl(T objBase, Class<?> objClass){
		this.objBase = objBase;
		this.objClass = objClass;
	}
	
	public CriteriaFilterImpl(Class<?> objClass) {
		this.objClass = objClass;
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
	public Map<String, SimpleEntry<Where, ?>> getListWhereComplex(){
		return this.listWhereComplex;
	}
	/**
	 * 
	 * @param field
	 * @return
	 */
	public SimpleEntry<Where, ?> getWhereComplex(String field){
		return this.listWhereComplex.get(field);
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
	private CriteriaFilterImpl<T> addSelect(String field, SelectAggregate selectFunction, String alias){
		this.listSelection.put(field, new SimpleEntry<CriteriaFilterImpl.SelectAggregate, String>(selectFunction, alias));
		return this;
	}
	/**
	 * 
	 * @param returnType
	 * @return
	 * @throws ApplicationException 
	 */
	public CriteriaFilterImpl<T> addSelect(Class<?> returnType) throws ApplicationException{
		return addSelect(returnType, (String[]) null);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelect(Class<?> returnType, List<String> fields) throws ApplicationException{
		StringBuilder fieldAux = new StringBuilder();
		if(fields != null){
			for(String f : fields){
				if(fieldAux.length() > 0){
					fieldAux.append(",");
				}
				fieldAux.append(f);
			}
		}
		
		Class<?> returnTypeAux = returnType == null ? this.objClass : returnType;
		List<SimpleEntry<String, String>> listaCampos = new SelectMapper(returnTypeAux, fieldAux.toString()).getFields();

		for(SimpleEntry<String, String> se : listaCampos){
				addSelect(se.getKey(), se.getValue());
		}
		
		return this;
	}
		
	@Override
	public CriteriaFilterImpl<T> addSelect(Class<?> returnType, String... fields) throws ApplicationException{
		return addSelect(returnType, fields == null ? null : Arrays.asList(fields));
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelect(String field, String alias){
		return addSelect(field, SelectAggregate.FIELD, alias);
	}
	
	@Override
	public CriteriaFilter<T> addSelect(Map<String, String> fieldAlias, Map<String, String> defaultFieldAlias){
		if(fieldAlias == null || defaultFieldAlias.isEmpty()){
			for(String k : defaultFieldAlias.keySet()){
				addSelect(k, defaultFieldAlias.get(k));
			}
		}else{
			for(String k : fieldAlias.keySet()){
				addSelect(k, fieldAlias.get(k));
			}
		}
		return this;
	}

	@Override
	public CriteriaFilter<T> addSelect(String... fields){
		for(String fld : fields){
			addSelect(fld);
		}
		return this;
	}

	@Override
	public CriteriaFilter<T> addSelect(List<String> fields) {
		return addSelect(fields.toArray(new String[fields.size()]));
	}
	
	@Override
	public CriteriaFilter<T> addSelect(Map<String, String> fieldAlias){
		return addSelect(fieldAlias, null);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelectCount(String field, String alias){
		return addSelect(field, SelectAggregate.COUNT, alias);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelectCount(String field){
		return addSelect(field, SelectAggregate.COUNT, field);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelectUpper(String field){
		return addSelect(field, SelectAggregate.UPPER, field);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelectUpper(String field, String alias){
		return addSelect(field, SelectAggregate.UPPER, alias);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelectLower(String field){
		return addSelect(field, SelectAggregate.LOWER, field);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelectLower(String field, String alias){
		return addSelect(field, SelectAggregate.LOWER, alias);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelectMax(String field, String alias){
		return addSelect(field, SelectAggregate.MAX, alias);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelectMax(String field){
		return addSelect(field, SelectAggregate.MAX, field);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelectMin(String field, String alias){
		return addSelect(field, SelectAggregate.MIN, alias);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelectMin(String field){
		return addSelect(field, SelectAggregate.MIN, field);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelectSum(String field, String alias){
		return addSelect(field, SelectAggregate.SUM, alias);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelectSum(String field){
		return addSelect(field, SelectAggregate.SUM, field);
	}
	
	@Override
	public CriteriaFilterImpl<T> addSelect(String field){
		return addSelect(field, SelectAggregate.FIELD, field);
	}
	/*
	@Override
	public CriteriaFilterImpl<T> addSelect(String... fields){
		if(fields != null){
			for(String f : fields){
				addSelect(f);
			}
		}
		return this;
	}*/
	/*
	@Override
	public CriteriaFilterImpl<T> addSelect(List<String> fields){
		if(fields != null){
			for(String f : fields){
				addSelect(f);
			}
		}
		return this;
	}*/
	
	@Override
	public CriteriaFilterImpl<T> addOrder(Class<?> returnType, String... order) throws ApplicationException{
		return addOrder(returnType, Arrays.asList(order));
	}
	
	@Override
	public CriteriaFilterImpl<T> addOrder(Class<?> returnType, List<String> order) throws ApplicationException{
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
	public CriteriaFilterImpl<T> addOrder(List<String> orderList){
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
	public CriteriaFilterImpl<T> addOrder(String... order){
		return addOrder(Arrays.asList(order));
	}
	
	
	@Override
	public CriteriaFilterImpl<T> addOrderAsc(String field){
		this.listOrder.put(field, Order.ASC);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addOrderDesc(String field){
		this.listOrder.put(field, Order.DESC);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereEqual(String field){
		this.listWhere.put(field, Where.EQUAL);
		return this;
	}
	/**
	 * 
	 * @param field
	 * @param where
	 * @param values
	 * @return
	 */
	private <E> CriteriaFilterImpl<T> addWhereListValues(String field, Where where, List<E> values){
		if(values != null){
			this.listWhereComplex.put(field, new SimpleEntry<CriteriaFilterImpl.Where, List<?>>(where, values));
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
	private <E> CriteriaFilterImpl<T> addWhereListValues(String field, Where where, E[] values){
		if(values != null){
			return addWhereListValues(field, where, Arrays.asList(values));
		}
		return this;
	}

	@Override
	public <E> CriteriaFilterImpl<T> addWhereIn(String field, List<E> values){
		return addWhereListValues(field, Where.IN, values);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilterImpl<T> addWhereIn(String field, E... values){
		return addWhereListValues(field, Where.IN, values);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilterImpl<T> addWhereNotIn(String field, E... values){
		return addWhereListValues(field, Where.NOT_IN, values);
	}
	
	@Override
	public <E> CriteriaFilterImpl<T> addWhereNotIn(String field, List<E> values){
		return addWhereListValues(field, Where.NOT_IN, values);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilterImpl<T> addWhereEqual(String field, E... values){
		return addWhereListValues(field, Where.EQUAL, values);
	}
	
	@Override
	public <E> CriteriaFilterImpl<T> addWhereEqual(String field, List<E> values){
		return addWhereListValues(field, Where.EQUAL, values);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilterImpl<T> addWhereNotEqual(String field, E... values){
		return addWhereListValues(field, Where.NOT_EQUAL, values);
	}
	
	@Override
	public <E> CriteriaFilterImpl<T> addWhereNotEqual(String field, List<E> values){
		return addWhereListValues(field, Where.NOT_EQUAL, values);
	}
	
	/**
	 * 
	 * @param listWhere
	 * @return
	 */
	public CriteriaFilterImpl<T> addAllWhere(Map<String, Where> listWhere){
		this.listWhere.putAll(listWhere);
		return this;
	}
	/**
	 * 
	 * @param listComplexWhere
	 * @return
	 */
	public CriteriaFilterImpl<T> addAllWhereComplex(Map<String, SimpleEntry<Where, ?>> listComplexWhere){
		this.listWhereComplex.putAll(listComplexWhere);
		return this;
	}
	/**
	 * 
	 * @param listJoin
	 * @return
	 */
	public CriteriaFilterImpl<T> addAllJoin(Map<String, SimpleEntry<JoinType, Boolean>> listJoin){
		this.listJoin.putAll(listJoin);
		return this;
	}
	/**
	 * 
	 * @param listSelection
	 * @return
	 */
	public CriteriaFilterImpl<T> addAllSelection(Map<String, SimpleEntry<SelectAggregate, String>> listSelection){
		this.listSelection.putAll(listSelection);
		return this;
	}
	
	/*@SuppressWarnings("unchecked")
	public <E> CriteriaFilter<T> addWhereBetween(String field, Class<E> typeValue, E startValue, E endValue){
		E[] betweenArray = (E[]) Array.newInstance(typeValue, 2);
		betweenArray[0] = startValue;
		betweenArray[1] = endValue;
		
		listWhereComplex.put(field, new SimpleEntry<CriteriaFilter.Where, E[]>(Where.BETWEEN, betweenArray));
		return this;
	}*/
	
	@Override
	public CriteriaFilterImpl<T> addWhereBetween(String field, Integer startValue, Integer endValue){
		listWhereComplex.put(field, new SimpleEntry<CriteriaFilterImpl.Where, Integer[]>(Where.BETWEEN, new Integer[] {startValue, endValue}));
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereBetween(String field, Short startValue, Short endValue){
		listWhereComplex.put(field, new SimpleEntry<CriteriaFilterImpl.Where, Short[]>(Where.BETWEEN, new Short[] {startValue, endValue}));
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereBetween(String field, Long startValue, Long endValue){
		listWhereComplex.put(field, new SimpleEntry<CriteriaFilterImpl.Where, Long[]>(Where.BETWEEN, new Long[] {startValue, endValue}));
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereLessThanField(String field, String anotherField){
		listWhereComplex.put(field, new SimpleEntry<CriteriaFilterImpl.Where, String>(Where.LESS_THAN_OTHER_FIELD, anotherField));
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereGreaterThanField(String field, String anotherField){
		listWhereComplex.put(field, new SimpleEntry<CriteriaFilterImpl.Where, String>(Where.GREATER_THAN_OTHER_FIELD, anotherField));
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereLessThanOrEqualToField(String field, String anotherField){
		listWhereComplex.put(field, new SimpleEntry<CriteriaFilterImpl.Where, String>(Where.LESS_THAN_OR_EQUAL_TO_OTHER_FIELD, anotherField));
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualToField(String field, String anotherField){
		listWhereComplex.put(field, new SimpleEntry<CriteriaFilterImpl.Where, String>(Where.GREATER_THAN_OR_EQUAL_TO_OTHER_FIELD, anotherField));
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereEqualField(String field, String anotherField){
		listWhereComplex.put(field, new SimpleEntry<CriteriaFilterImpl.Where, String>(Where.EQUAL_OTHER_FIELD, anotherField));
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereNotEqualField(String field, String anotherField){
		listWhereComplex.put(field, new SimpleEntry<CriteriaFilterImpl.Where, String>(Where.NOT_EQUAL_OTHER_FIELD, anotherField));
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereBetween(String field, Date startValue, Date endValue){
		LocalDate dtSrt = startValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate dtEnd = endValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		return addWhereBetween(field, dtSrt, dtEnd);
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereBetween(String field, LocalDate startValue, LocalDate endValue){
		Date dtSrt = Date.from(startValue.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date dtEnd = Date.from(LocalDateTime.of(endValue, LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
		
		listWhereComplex.put(field, new SimpleEntry<CriteriaFilterImpl.Where, Date[]>(Where.BETWEEN, new Date[] {dtSrt, dtEnd}));
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereBetween(String field, LocalDateTime startValue, LocalDateTime endValue){
		Date dtSrt = Date.from(startValue.atZone(ZoneId.systemDefault()).toInstant());
		Date dtEnd = Date.from(endValue.atZone(ZoneId.systemDefault()).toInstant());
		
		listWhereComplex.put(field, new SimpleEntry<CriteriaFilterImpl.Where, Date[]>(Where.BETWEEN, new Date[] {dtSrt, dtEnd}));
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereGreaterThan(String field){
		this.listWhere.put(field, Where.GREATER_THAN);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereGreaterThanOrEqualTo(String field){
		this.listWhere.put(field, Where.GREATER_THAN_OR_EQUAL_TO);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereIn(String field){
		this.listWhere.put(field, Where.IN);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereIsNotNull(String field){
		this.listWhere.put(field, Where.IS_NOT_NULL);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereIsNull(String field){
		this.listWhere.put(field, Where.IS_NULL);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereLessThan(String field){
		this.listWhere.put(field, Where.LESS_THAN);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereLessThanOrEqualTo(String field){
		this.listWhere.put(field, Where.LESS_THAN_OR_EQUAL_TO);
		return this;
	}
	/*
	public CriteriaFilter<T> addWhereLessThan(String field, Number value){
		this.listWhereComplex.put(field, new SimpleEntry<CriteriaFilter.Where, Number>(Where.LESS_THAN, value));
		return this;
	}
	
	public CriteriaFilter<T> addWhereLessThan(String field, Date value){
		this.listWhereComplex.put(field, new SimpleEntry<CriteriaFilter.Where, Date>(Where.LESS_THAN, value));
		return this;
	}
	
	public CriteriaFilter<T> addWhereGreaterThan(String field, Number value){
		this.listWhereComplex.put(field, new SimpleEntry<CriteriaFilter.Where, Number>(Where.GREATER_THAN, value));
		return this;
	}
	
	public CriteriaFilter<T> addWhereGreaterThan(String field, Date value){
		this.listWhereComplex.put(field, new SimpleEntry<CriteriaFilter.Where, Date>(Where.GREATER_THAN, value));
		return this;
	}
	*/
	
	@Override
	public CriteriaFilterImpl<T> addWhereLike(String field){
		this.listWhere.put(field, Where.LIKE);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereNotLike(String field){
		this.listWhere.put(field, Where.NOT_LIKE);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereLikeAnyAfter(String field){
		this.listWhere.put(field, Where.LIKE_ANY_AFTER);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereLikeAnyBefore(String field){
		this.listWhere.put(field, Where.LIKE_ANY_BEFORE);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereLikeAnyBeforeAfter(String field){
		this.listWhere.put(field, Where.LIKE_ANY_BEFORE_AND_AFTER);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereILike(String field){
		this.listWhere.put(field, Where.ILIKE);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereNotILike(String field){
		this.listWhere.put(field, Where.NOT_ILIKE);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereILikeAnyAfter(String field){
		this.listWhere.put(field, Where.ILIKE_ANY_AFTER);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereILikeAnyBefore(String field){
		this.listWhere.put(field, Where.ILIKE_ANY_BEFORE);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereILikeAnyBeforeAfter(String field){
		this.listWhere.put(field, Where.ILIKE_ANY_BEFORE_AND_AFTER);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereNotEqual(String field){
		this.listWhere.put(field, Where.NOT_EQUAL);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addWhereNotIn(String field){
		this.listWhere.put(field, Where.NOT_IN);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addGroupBy(String field){
		this.listGroupBy.add(field);
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addJoin(String field, JoinType joinType, boolean fetch){
		this.listJoin.put(field, new SimpleEntry<JoinType, Boolean>(joinType, fetch));
		return this;
	}
	
	@Override
	public CriteriaFilterImpl<T> addJoin(String field, JoinType joinType){
		return addJoin(field, joinType, false);
	}
	
	@Override
	public CriteriaFilterImpl<T> addJoin(String field){
		return addJoin(field, JoinType.INNER, false);
	}
	/**
	 * 
	 * @param entityManager
	 * @param entityClass
	 * @param queryClass
	 * @return
	 * @throws Exception
	 */
	public CriteriaManager<T> createCriteriaManager(EntityManager entityManager, Class<T> entityClass, Class<?> queryClass) throws Exception{
		return new CriteriaManager<T>(entityManager, entityClass, queryClass, this);
	}

	public Map<Class<?>, Map<String, SimpleEntry<SelectAggregate, String>>> getCollectionSelection() {
		return collectionSelection;
	}

	/*
	private static List<String> checkFields(List<String> fields, String... defaultFields){
		List<String> ret = fields;
		if(fields == null || fields.isEmpty()){
			ret = Arrays.asList(defaultFields);
		}
		
		return ret;
	}
	
	private static List<String> checkFields(String[] fields, String... defaultFields){
		if(fields == null){
			return Arrays.asList(defaultFields);
		}
		return Arrays.asList(fields);
	}*/

}

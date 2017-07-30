package br.com.jgon.canary.jee.persistence.filter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.JoinType;

import br.com.jgon.canary.jee.exception.ApplicationException;

/**
 * Define os filtros que serao utilizados para construir a criteria 
 * @author jurandir
 *
 * @param <T>
 */
public interface CriteriaFilter<T> {
	
	/**
	 * 
	 * @return
	 */
	public T getObjBase();
	
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
	 * @param defaultFields
	 * @return
	 * @throws ApplicationException 
	 */
	public CriteriaFilter<T> addSelect(Class<?> returnType, List<String> fields, String... defaultFields) throws ApplicationException;
	
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
	 * @param returnType
	 * @param fields
	 * @param defaultFields
	 * @return
	 * @throws ApplicationException 
	 */
	public CriteriaFilter<T> addSelect(Class<?> returnType, String[] fields, String... defaultFields) throws ApplicationException;
	
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
	 * @param fieldAlias
	 * @param defaultFieldAlias
	 * @return
	 */
	public CriteriaFilter<T> addSelect(Map<String, String> fieldAlias, Map<String, String> defaultFieldAlias);
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
	 *//*
	public CriteriaFilter<T> addSelect(String... fields);
	*/
	/**
	 * 
	 * @param fields
	 * @param defaultFields
	 * @return
	 * @throws ApplicationException 
	 */
	public CriteriaFilter<T> addSelect(String[] fields, String... defaultFields) throws ApplicationException;
	
	/**
	 * 
	 * @param fields
	 * @return
	 *//*
	public CriteriaFilter<T> addSelect(List<String> fields);*/
	
	/**
	 * 
	 * @param fields
	 * @param defaultFields
	 * @return
	 * @throws ApplicationException 
	 */
	public CriteriaFilter<T> addSelect(List<String> fields, String... defaultFields) throws ApplicationException;
	
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
	 * @param defaultOrder
	 * @return
	 * @throws ApplicationException 
	 */
	 
	public CriteriaFilter<T> addOrder(Class<?> returnType, String[] order, String... defaultOrder) throws ApplicationException;
	
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
	 * @param returnType
	 * @param order
	 * @param defaultOrder
	 * @return
	 * @throws ApplicationException 
	 */
	public CriteriaFilter<T> addOrder(Class<?> returnType, List<String> order, String... defaultOrder) throws ApplicationException;

	/**
	 * 
	 * @param order
	 * @return
	 */
	public CriteriaFilter<T> addOrder(List<String> order);
	
	/**
	 * 
	 * @param order
	 * @param defaultOrder
	 * @return
	 */
	public CriteriaFilter<T> addOrder(List<String> order, String... defaultOrder);
	
	/**
	 * 
	 * @param order
	 * @return
	 */
	public CriteriaFilter<T> addOrder(String... order);
	
	/**
	 * 
	 * @param order
	 * @param defaultOrder
	 * @return
	 */
	public CriteriaFilter<T> addOrder(String[] order, String...defaultOrder);
	
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
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> CriteriaFilter<T> addWhereNotEqual(String field, E... values);
	
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
	 * @return
	 */
	public CriteriaFilter<T> addWhereGreaterThanOrEqualTo(String field);
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
	 * @return
	 */
	public CriteriaFilter<T> addWhereLessThanOrEqualTo(String field);
	/*
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereLike(String field);
	
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereNotLike(String field);
	
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereLikeAnyAfter(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereLikeAnyBefore(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereLikeAnyBeforeAfter(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereILike(String field);
	
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereNotILike(String field);
	
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereILikeAnyAfter(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereILikeAnyBefore(String field);
	/**
	 * 
	 * @param field
	 * @return
	 */
	public CriteriaFilter<T> addWhereILikeAnyBeforeAfter(String field);
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

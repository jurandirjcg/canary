package br.com.jgon.canary.jee.persistence;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EmbeddedId;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Selection;

import br.com.jgon.canary.jee.exception.ApplicationException;
import br.com.jgon.canary.jee.exception.MessageSeverity;
import br.com.jgon.canary.jee.persistence.filter.CriteriaFilter;
import br.com.jgon.canary.jee.persistence.filter.QueryAttributeMapper;
import br.com.jgon.canary.jee.util.CollectionUtil;
import br.com.jgon.canary.jee.util.Pagination;
import br.com.jgon.canary.jee.util.ReflectionUtil;

/**
 * Responsável por generalizar as operações simples de SELECT, DELETE, UPDADE e INSERT
 *  
 * @author jurandir
 *
 * @param <T> - Entidade
 * @param <K> - Chave
 */

public abstract class GenericDAO<T, K extends Serializable> implements Serializable{

	protected static final String ERROR_SAVE_KEY = "error.save";
	protected static final String ERROR_UPDATE_KEY = "error.update";
	protected static final String ERROR_DELETE_KEY = "error.delete";
	protected static final String ERROR_READ_KEY = "error.read";
	protected static final String ERROR_READ_LIST_KEY = "error.read-list";
	protected static final String ERROR_CRITERIA = "error.criteria";
	protected static final String ERROR_FIELD_DOES_NOT_EXIST = "error.field-does-not-exist";
	
	private static final Integer LIMIT = 20; 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6642196262998558242L;
	
	private EntityManager entityManager;
	/**
	 * 
	 * @param entityManager
	 */
	public GenericDAO(EntityManager entityManager){
		this.entityManager = entityManager;
	}
	/**
	 * 
	 * @return
	 */
	protected EntityManager getEntityManager(){
		return this.entityManager;
	}
	
	/**
	 * 
	 * @param obj
	 * @throws ApplicationException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public T save(T obj) throws ApplicationException {
		try {
			getEntityManager().persist(obj);
			return obj;
		} catch (Exception e) {
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_SAVE_KEY, e, new String[] { getPrimaryClass().getSimpleName() });
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public T saveOrUpdate(T obj) throws ApplicationException {
		try {
			List<Field> flds = ReflectionUtil.listAttributeByAnnotation(getPrimaryClass(), Id.class);
			if(flds.isEmpty()){
				flds = ReflectionUtil.listAttributeByAnnotation(getPrimaryClass(), EmbeddedId.class);
			}
			boolean isSave = true;
			if(!flds.isEmpty()){
				isSave = ReflectionUtil.getAttributteValue(obj, flds.get(0)) == null;
			}
			if(isSave){
				getEntityManager().persist(obj);
				return obj;
			}else{
				return update(obj);
			}
		} catch (Exception e) {
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_SAVE_KEY, e, new String[] { getPrimaryClass().getSimpleName() });
		}
	}

	/**
	 * 
	 * @param obj
	 * @return
	 * @throws ApplicationException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public T update(T obj) throws ApplicationException {
		try {
			return getEntityManager().merge(obj);
		} catch (Exception e) {
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_UPDATE_KEY, e, new String[] { getPrimaryClass().getSimpleName() });
		}
	}

	/**
	 * 
	 * @param obj
	 * @throws ApplicationException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(T obj) throws ApplicationException {
		try {
			obj = getEntityManager().merge(obj);
			getEntityManager().remove(obj);
		} catch (Exception e) {
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_DELETE_KEY, e, new String[] { getPrimaryClass().getSimpleName() });
		}
	}

	/**
	 * Obtem a entidade pela chave, utiliza a instrução JPA - EntityManager.find(T.class, K) - para obter, 
	 * atenção pois normalmente este comando carrega todos os objetos relacionados com a entidade
	 * @param id
	 * @return
	 * @throws ApplicationException
	 */
	public T find(K id) throws ApplicationException {
		try {
			return getEntityManager().find(getPrimaryClass(), id);
		} catch (Exception e) {
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_READ_KEY, e, new String[] { getPrimaryClass().getSimpleName() });
		}
	}
	/**
	 * Retorna entidade sem retornar os objetos relacionados
	 * @param id
	 * @return
	 * @throws ApplicationException
	 */
	public T findReference(K id) throws ApplicationException {
		try {
			return getEntityManager().getReference(getPrimaryClass(), id);
		} catch (Exception e) {
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_READ_KEY, e, new String[] { getPrimaryClass().getSimpleName() });
		}
	}
	/**
	 * 
	 * @param id
	 * @param fields
	 * @return
	 * @throws ApplicationException
	 */
	public T find(K id, List<String> fields) throws ApplicationException {
		 return find(id, getPrimaryClass(), fields); 
	}
	/**
	 * 
	 * @param id
	 * @param resultClass
	 * @param fields
	 * @return
	 * @throws ApplicationException
	 */
	public <E> E find(K id, Class<E> resultClass, List<String> fields) throws ApplicationException {
		try {
			List<Field> flds = ReflectionUtil.listAttributeByAnnotation(getPrimaryClass(), Id.class);
			if(flds.isEmpty()){
				flds = ReflectionUtil.listAttributeByAnnotation(getPrimaryClass(), EmbeddedId.class);
			}
			if(!flds.isEmpty()){
				Field fld = flds.get(0);
				T objRef = getPrimaryClass().newInstance();
				ReflectionUtil.setFieldValue(objRef, fld, id);
				
				return find(objRef, resultClass, fields);
			}else{
				return null;
			}
		} catch (Exception e) {
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_READ_KEY, e, new String[] { getPrimaryClass().getSimpleName() });
		}
	}

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Class<T> getPrimaryClass() {
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		return (Class<T>) (type).getActualTypeArguments()[0];
	}

	/**
	 * 
	 * @param param
	 * @return
	 */
	public String translateUpper(String param) {
		return " UPPER(translate( trim(" + param + ") ,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','AAAAAAAAEEEEIIOOOOOOUUUUCC')) ";
	}
	
	/**
	 * 
	 * @param query
	 * @param pagina
	 * @param qtde
	 */
	protected void configPaginacao(Query query, Integer pagina, Integer qtde){
		if(pagina != null && qtde != null && pagina > 1 && qtde > 0){
			query.setFirstResult( (pagina - 1) * qtde);
		}
		if(qtde != null && qtde > 0){
			query.setMaxResults(qtde);
		}
	}
	
	/**
	 * 
	 * @param resultClass
	 * @param objRef
	 * @param fields
	 * @param sort
	 * @param pagina
	 * @param qtde
	 * @return
	 * @throws ApplicationException
	 */
	protected <E> List<E> getResultList(Class<E> resultClass, T objRef, List<String> fields, List<String> sort, Integer pagina, Integer qtde) throws ApplicationException {
		return getResultList(resultClass, objRef, fields, null, sort, null, pagina, qtde);
	}
	/**
	 * 
	 * @param resultClass
	 * @param objRef
	 * @param fields
	 * @param defaultFields
	 * @param sort
	 * @param defaultSort
	 * @param pagina
	 * @param qtde
	 * @return
	 * @throws ApplicationException
	 */
	protected <E> List<E> getResultList(Class<E> resultClass, T objRef, List<String> fields, String[] defaultFields, List<String> sort, String[] defaultSort, Integer pagina, Integer qtde) throws ApplicationException {
		CriteriaFilter<T> cf = getCriteriaFilter(objRef)
				.addSelect(resultClass, fields, defaultFields)
				.addOrder(sort, defaultSort);
		
		return getResultList(resultClass, cf, pagina, qtde);
	}
	
	/**
	 * 
	 * @param resultClass
	 * @param criteriaFilter
	 * @param pagina
	 * @param qtde
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	protected <E> List<E> getResultList(Class<E> resultClass, CriteriaFilter<T> criteriaFilter, Integer pagina, Integer qtde) throws ApplicationException {
		try {		
			if(resultClass == null){
				resultClass = (Class<E>) getPrimaryClass();
			}
			CriteriaQuery<?> query = getCriteriaManager(resultClass, (CriteriaFilterImpl<T>) criteriaFilter).getCriteriaQuery();
			return getResultList(resultClass, query, pagina, qtde);
		} catch (Exception e) {
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_READ_LIST_KEY, e, new String[] { getPrimaryClass().getSimpleName() });
		}
	}
	
	protected List<T> getResultList(CriteriaFilter<T> criteriaFilter, Integer pagina, Integer qtde) throws ApplicationException {
		return getResultList(getPrimaryClass(), criteriaFilter, pagina, qtde);
	}
	
	/**
	 * 
	 * @param resultClass
	 * @param criteriaFilter
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private <E> CriteriaManager<T> getCriteriaManager(Class<E> resultClass, CriteriaFilterImpl<T> criteriaFilter) throws ApplicationException{
		if(resultClass == null){
			resultClass = (Class<E>) getPrimaryClass();
		}
		boolean isTuple = criteriaFilter != null && !((CriteriaFilterImpl<T>) criteriaFilter).getListSelection().isEmpty() && !ReflectionUtil.isPrimitive(resultClass);

		try{
			if(isTuple){	
				return new CriteriaManager<T>(getEntityManager(), getPrimaryClass(), getPrimaryClass(), (CriteriaFilterImpl<T>) criteriaFilter);
			}else{
				return new CriteriaManager<T>(getEntityManager(), getPrimaryClass(), resultClass, (CriteriaFilterImpl<T>) criteriaFilter);
			}
		}catch (Exception e) {
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_CRITERIA, e, new String[] { getPrimaryClass().getSimpleName() });
		}
	}
	
	/**
	 * 
	 * @param resultClass
	 * @param criteriaQuery
	 * @param pagina
	 * @param qtde
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	protected <E> List<E> getResultList(Class<E> resultClass, CriteriaQuery<?> criteriaQuery, Integer pagina, Integer qtde) throws ApplicationException {
		try {
			boolean isTuple = criteriaQuery.getResultType().equals(Tuple.class);
		
			if(isTuple){
				CriteriaQuery<Tuple> query = (CriteriaQuery<Tuple>) criteriaQuery;
				TypedQuery<Tuple> tQuery = getEntityManager().createQuery(query);
				configPaginacao(tQuery, pagina, qtde);
				List<Tuple> tuple = tQuery.getResultList();
				
				List<E> listReturn = new ArrayList<E>();
				
				for(Tuple t: tuple){
					listReturn.add(tupleToResultClass(t, resultClass));
				}
								
				return listReturn;
			}else{
				CriteriaQuery<E> query = (CriteriaQuery<E>) criteriaQuery;
				TypedQuery<E> tQuery = getEntityManager().createQuery(query);
				configPaginacao(tQuery, pagina, qtde);
				return (List<E>) tQuery.getResultList();
			}
		} catch (Exception e) {
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_READ_LIST_KEY, e, new String[] { getPrimaryClass().getSimpleName() });
		}
	}
	/**
	 * 
	 * @param criteriaQuery
	 * @param pagina
	 * @param qtde
	 * @return
	 * @throws ApplicationException
	 */
	protected List<T> getResultList(CriteriaQuery<T> criteriaQuery, Integer pagina, Integer qtde) throws ApplicationException {
		return getResultList(getPrimaryClass(), criteriaQuery, pagina, qtde);
	}
	
	/**
	 * 
	 * @param resultClass
	 * @param objRef
	 * @param fields
	 * @param defaultFields
	 * @param sort
	 * @param defaultSort
	 * @param criteriaFilter
	 * @return
	 * @throws ApplicationException
	 */
	protected <E> E getSingleResult(Class<E> resultClass, T objRef, List<String> fields, String[] defaultFields, List<String> sort, String[] defaultSort, CriteriaFilter<T> criteriaFilter) throws ApplicationException {
		CriteriaFilter<T> cf = getCriteriaFilter(objRef)
				.addSelect(resultClass, fields, defaultFields)
				.addOrder(sort, defaultSort);
		
		return (E) getSingleResult(resultClass, cf);
	}
	/**
	 * 
	 * @param resultClass
	 * @param objRef
	 * @param fields
	 * @param sort
	 * @param criteriaFilter
	 * @return
	 * @throws ApplicationException
	 */
	protected <E> E getSingleResult(Class<E> resultClass, T objRef, List<String> fields, List<String> sort, CriteriaFilter<T> criteriaFilter) throws ApplicationException {
		return getSingleResult(resultClass, objRef, fields, null, sort, null, criteriaFilter);
	}
	/**
	 * 
	 * @param resultClass
	 * @param criteriaFilter
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	protected <E> E getSingleResult(Class<E> resultClass, CriteriaFilter<T> criteriaFilter) throws ApplicationException {
		try {
			if(resultClass == null){
				resultClass = (Class<E>) getPrimaryClass();
			}
			CriteriaQuery<?> query = getCriteriaManager(resultClass, (CriteriaFilterImpl<T>) criteriaFilter).getCriteriaQuery();
			return getSingleResult(resultClass, query);
		} catch (NoResultException nre){
			return null;
		} catch (Exception e) {
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_READ_KEY, e, new String[] { getPrimaryClass().getSimpleName() });
		}
	}

	/**
	 * 
	 * @param resultClass
	 * @param criteriaQuery
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	protected <E> E getSingleResult(Class<E> resultClass, CriteriaQuery<?> criteriaQuery) throws ApplicationException {
		try {
			boolean isTuple = criteriaQuery.getResultType().equals(Tuple.class);
			
			if(isTuple){
				CriteriaQuery<Tuple> query = (CriteriaQuery<Tuple>) criteriaQuery;
				TypedQuery<Tuple> tQuery = getEntityManager().createQuery(query);
				Tuple tuple = tQuery.getSingleResult();
				return tupleToResultClass(tuple, resultClass);
			}else{
				CriteriaQuery<E> query = (CriteriaQuery<E>) criteriaQuery;
				TypedQuery<E> tQuery = getEntityManager().createQuery(query);
				return tQuery.getSingleResult();
			}
		} catch (NoResultException nre){
			return null;
		} catch (Exception e) {
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_READ_KEY, e, new String[] { getPrimaryClass().getSimpleName() });
		}
	}
	/**
	 * 
	 * @param criteriaQuery
	 * @return
	 * @throws ApplicationException
	 */
	protected T getSingleResult(CriteriaQuery<T> criteriaQuery) throws ApplicationException {
		return getSingleResult(getPrimaryClass(), criteriaQuery);
	}
	/**
	 * 
	 * @param criteriaFilter
	 * @return
	 * @throws ApplicationException
	 */
	protected T getSingleResult(CriteriaFilter<T> criteriaFilter) throws ApplicationException {
		return getSingleResult(getPrimaryClass(), criteriaFilter);
	}
	/**
	 * 
	 * @param tuple
	 * @param resultClass
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private <E> E tupleToResultClass(Tuple tuple, Class<E> resultClass) throws InstantiationException, IllegalAccessException, ApplicationException{
		if(ReflectionUtil.isPrimitive(resultClass)){
			return (E) tuple.get(0);
		}
		
		E objReturn = resultClass.newInstance();
	
		for(TupleElement<?> te : tuple.getElements()){
			if(te.getAlias().contains(".")){
				String[] subObj = te.getAlias().split("\\.");
				Object objAux = objReturn;
				for(int i=0; i < subObj.length; i++){
					Field fldAux = ReflectionUtil.attributeByName(objAux.getClass(), subObj[i]);
				
					if(fldAux == null){
						throw new ApplicationException(MessageSeverity.ERROR, ERROR_FIELD_DOES_NOT_EXIST, new String[] { subObj[i], objAux.getClass().getSimpleName() });
					}
					fldAux.setAccessible(true);
					if(i == subObj.length - 1){
						fldAux.set(objAux, tuple.get(te));
					}else{
						Object objTemp = fldAux.get(objAux);
						if(objTemp == null){
							objTemp = fldAux.getType().newInstance();
							fldAux.set(objAux, objTemp);
						}
						if(objTemp instanceof Collection && fldAux.isAnnotationPresent(QueryAttributeMapper.class)){
							 Object objInCollection;
							if(!((Collection<?>) objTemp).isEmpty()){
								objInCollection = ((Collection<?>) objTemp).toArray()[	((Collection<?>) objTemp).size() -1 ];
							}else{
								objInCollection = fldAux.getAnnotation(QueryAttributeMapper.class).valueType().newInstance();
								((Collection<Object>) objTemp).add(objInCollection);
							}							
							objTemp = objInCollection;
						}
						objAux = objTemp;
					}
				}
			}else{
				ReflectionUtil.setFieldValue(objReturn, te.getAlias(), tuple.get(te));
			}
		}
		return objReturn;
	}
	
	/**
	 * 
	 * @param obj
	 * @return
	 * @throws ApplicationException
	 */
	public T find(T obj) throws ApplicationException {
		CriteriaFilterImpl<T> cf = (CriteriaFilterImpl<T>) getCriteriaFilter(obj);
		return getSingleResult(getPrimaryClass(), cf);
	}
	/**
	 * 
	 * @param obj
	 * @param resultClass
	 * @param fields
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public <E> E find(T obj, Class<E> resultClass, List<String> fields) throws ApplicationException {
		CriteriaFilterImpl<T> cf = (CriteriaFilterImpl<T>) getCriteriaFilter(obj);
		if(resultClass == null){
			cf.addSelect(getPrimaryClass(), fields);
			return (E) getSingleResult(getPrimaryClass(), cf);
		}else{
			cf.addSelect(resultClass, fields);
			return getSingleResult(resultClass, cf);
		}
		
	}
	/**
	 * 
	 * @param obj
	 * @param fields
	 * @return
	 * @throws ApplicationException
	 */
	public T find(T obj, List<String> fields) throws ApplicationException {
		return find(obj, getPrimaryClass(), fields);
	}
		
	/**
	 * 
	 * @param pagina
	 * @param qtde
	 * @return
	 * @throws ApplicationException
	 */
	public List<T> list(Integer pagina, Integer qtde) throws ApplicationException {
		return this.list(null, pagina, qtde);
	}
	
	/**
	 * 
	 * @param pagina
	 * @param qtde
	 * @param criteriaFilter
	 * @return
	 * @throws ApplicationException
	 */
	public List<T> list(T obj, Integer pagina, Integer qtde) throws ApplicationException {
		CriteriaFilterImpl<T> cf = (CriteriaFilterImpl<T>) getCriteriaFilter(obj);
		return getResultList(getPrimaryClass(), cf, pagina, qtde);
	}
			
	/**
	 * 
	 * @param objRef
	 * @return
	 */
	protected CriteriaFilter<T> getCriteriaFilter(T objRef){
		if(objRef == null){
			return getCriteriaFilter();
		}
		return new CriteriaFilterImpl<T>(objRef, getPrimaryClass());
	}
	
	/**
	 * 
	 * @return
	 */
	protected CriteriaFilter<T> getCriteriaFilter(){
		return new CriteriaFilterImpl<T>(getPrimaryClass());
	}
	
	/**
	 * Retorna lista paginada com a mesma assinatura da entidade 
	 * @param criteriaFilter
	 * @param page
	 * @param qtde
	 * @return
	 * @throws ApplicationException
	 */
	protected Pagination<T> getResultPaginate(CriteriaFilter<T> criteriaFilter, Integer page, Integer qtde) throws ApplicationException{
		return getResultPaginate(getPrimaryClass(), criteriaFilter, page, qtde);
	}
	/**
	 * 
	 * @param obj
	 * @param page
	 * @param qtde
	 * @return
	 * @throws ApplicationException
	 */
	public Pagination<T> paginate(T obj, Integer page, Integer qtde) throws ApplicationException{
		CriteriaFilterImpl<T> cf = (CriteriaFilterImpl<T>) getCriteriaFilter(obj);
		return getResultPaginate(getPrimaryClass(), cf, page, qtde);
	}
	/**
	 * 
	 * @param pagina
	 * @param qtde
	 * @param fields
	 * @param sort
	 * @return
	 * @throws ApplicationException
	 */
	public Pagination<T> paginate(List<String> fields, List<String> sort, Integer pagina, Integer qtde) throws ApplicationException{
		return paginate(null, fields, sort, pagina, qtde);
	}
	/**
	 * 
	 * @param obj
	 * @param pagina
	 * @param qtde
	 * @param fields
	 * @param sort
	 * @return
	 * @throws ApplicationException
	 */
	public Pagination<T> paginate(T obj, List<String> fields, List<String> sort, Integer pagina, Integer qtde) throws ApplicationException{
		return paginate(obj, null, fields, sort, pagina, qtde);
	}
	
	/**
	 * 
	 * @param obj
	 * @param fields
	 * @param sort
	 * @param pagina
	 * @param qtde
	 * @return
	 * @throws ApplicationException
	 */
	public Pagination<T> paginate(T obj, String[] fields, String[] sort, Integer pagina, Integer qtde) throws ApplicationException{
		return paginate(obj, null, fields, sort, pagina, qtde);
	}
	
	/**
	 * 
	 * @param obj
	 * @param resultClass
	 * @param pagina
	 * @param qtde
	 * @param fields
	 * @param sort
	 * @return
	 * @throws ApplicationException
	 */
	public <E> Pagination<E> paginate(T obj, Class<E> resultClass, List<String> fields, List<String> sort, Integer pagina, Integer qtde) throws ApplicationException{
		CriteriaFilterImpl<T> cf = (CriteriaFilterImpl<T>) getCriteriaFilter(obj);
		
		if(resultClass == null){
			cf.addSelect(fields);
			cf.addOrder(sort);
		}else{
			cf.addSelect(resultClass, fields);
			cf.addOrder(resultClass, sort);
		}
	
		return getResultPaginate(resultClass, cf, pagina, qtde);
	}
	
	/**
	 * 
	 * @param obj
	 * @param resultClass
	 * @param pagina
	 * @param qtde
	 * @param fields
	 * @param sort
	 * @return
	 * @throws ApplicationException
	 */
	public <E> Pagination<E> paginate(T obj, Class<E> resultClass, String[] fields, String[] sort, Integer pagina, Integer qtde) throws ApplicationException{
		return paginate(obj, resultClass, CollectionUtil.convertArrayToList(fields), CollectionUtil.convertArrayToList(sort), pagina, qtde);
	}
	
	/**
	 * 
	 * @param obj
	 * @param resultClass
	 * @param pagina
	 * @param qtde
	 * @param fields
	 * @param sort
	 * @return
	 * @throws ApplicationException
	 */
	public <E> List<E> list(T obj, Class<E> resultClass, List<String> fields, List<String> sort, Integer pagina, Integer qtde) throws ApplicationException{
		CriteriaFilterImpl<T> cf = (CriteriaFilterImpl<T>) getCriteriaFilter(obj);
		
		if(resultClass == null){
			cf.addSelect(fields);
			cf.addOrder(sort);
		}else{
			cf.addSelect(resultClass, fields);
			cf.addOrder(resultClass, sort);
		}
	
		return (List<E>) getResultList(resultClass, cf, pagina, qtde);
	}
	
	/**
	 * 
	 * @param obj
	 * @param resultClass
	 * @param fields
	 * @param sort
	 * @param pagina
	 * @param qtde
	 * @return
	 * @throws ApplicationException
	 */
	public <E> List<E> list(T obj, Class<E> resultClass, String[] fields, String[] sort, Integer pagina, Integer qtde) throws ApplicationException{
		return list(obj, resultClass, CollectionUtil.convertArrayToList(fields), CollectionUtil.convertArrayToList(sort), pagina, qtde);
	}
	/**
	 * 
	 * @param obj
	 * @param pagina
	 * @param qtde
	 * @param fields
	 * @param sort
	 * @return
	 * @throws ApplicationException
	 */
	public List<T> list(T obj, List<String> fields, List<String> sort, Integer pagina, Integer qtde) throws ApplicationException{
		return list(obj, null, fields, sort, pagina, qtde);
	}
	
	/**
	 * 
	 * @param obj
	 * @param fields
	 * @param sort
	 * @param pagina
	 * @param qtde
	 * @return
	 * @throws ApplicationException
	 */
	public List<T> list(T obj, String[] fields, String[] sort, Integer pagina, Integer qtde) throws ApplicationException{
		return list(obj, null, fields, sort, pagina, qtde);
	}
	/**
	 * 
	 * @param pagina
	 * @param qtde
	 * @param fields
	 * @param sort
	 * @return
	 * @throws ApplicationException
	 */
	public List<T> list(List<String> fields, List<String> sort, Integer pagina, Integer qtde) throws ApplicationException{
		return list(null, null, fields, sort, pagina, qtde);
	}
	
	/**
	 * 
	 * @param fields
	 * @param sort
	 * @param pagina
	 * @param qtde
	 * @return
	 * @throws ApplicationException
	 */
	public List<T> list(String[] fields, String[] sort, Integer pagina, Integer qtde) throws ApplicationException{
		return list(null, null, fields, sort, pagina, qtde);
	}
	
	/**
	 * 
	 * @param returnClass
	 * @param objRef
	 * @param fields
	 * @param sort
	 * @param page
	 * @param limit
	 * @return
	 * @throws ApplicationException
	 */
	protected <E> Pagination<E> getResultPaginate(Class<E> returnClass, T objRef, List<String> fields, List<String> sort, Integer page, Integer limit) throws ApplicationException{
		return getResultPaginate(returnClass, objRef, fields, null, sort, null, page, limit);
	}
	
	/**
	 * 
	 * @param returnClass
	 * @param objRef
	 * @param fields
	 * @param defaultFields
	 * @param sort
	 * @param defaultSort
	 * @param page
	 * @param limit
	 * @return
	 * @throws ApplicationException
	 */
	protected <E> Pagination<E> getResultPaginate(Class<E> returnClass, T objRef, List<String> fields, String[] defaultFields, List<String> sort, String[] defaultSort, Integer page, Integer limit) throws ApplicationException{
		CriteriaFilter<T> cf = getCriteriaFilter(objRef)
				.addSelect(returnClass, fields, defaultFields)
				.addOrder(sort, defaultSort);
		
		return getResultPaginate(returnClass, cf, page, limit);
	}
	
	/**
	 * Retorna lista paginada com retorno customizado
	 * @param returnClass
	 * @param criteriaFilter
	 * @param page
	 * @param limit - default 20
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <E> Pagination<E> getResultPaginate(Class<E> returnClass, CriteriaFilter<T> criteriaFilter, Integer page, Integer limit) throws ApplicationException{
		CriteriaManager<T> criteriaManager = getCriteriaManager(returnClass, (CriteriaFilterImpl<T>) criteriaFilter);	
		CriteriaQuery<?> query = criteriaManager.getCriteriaQuery();
		
		//SELECT
		Selection<?> sel = query.getSelection();
		//ORDER
		List<Order> orderList = new ArrayList<Order>(query.getOrderList()); 
		query.getOrderList().clear();
		//GROUP
		List<Expression<?>> groupList = new ArrayList<Expression<?>>(query.getGroupList());
		query.getGroupList().clear();
		
		List<Field> flds = ReflectionUtil.listAttributeByAnnotation(getPrimaryClass(), Id.class);
		
		if(flds.size() == 1){
			Field fldId = flds.get(0);
			query.select((Selection) criteriaManager.getCriteriaBuilder().tuple(criteriaManager.getCriteriaBuilder().count(criteriaManager.getRootEntry().get(fldId.getName())).alias(fldId.getName())));
		}else{
			query.select((Selection) criteriaManager.getCriteriaBuilder().count(criteriaManager.getRootEntry()));
		}
		
		Long qtdeReg = getSingleResult(Long.class, query);
		
		Integer limitAux = getPagLimit();
		if(limit > limitAux){
			limit = limitAux;
		}
		
		Pagination<E> paginacao = new Pagination<E>(qtdeReg, limit, page);
		
		if(qtdeReg > 0){			
			query.select((Selection) sel);
			query.orderBy(orderList);
			query.groupBy(groupList);
			
			paginacao.setRegistros(getResultList(returnClass, query, page, limit));
		}
		
		return paginacao;
	}
	
	/**
	 * Retorna a quantidade maxima de registros por página
	 * @return
	 */
	protected Integer getPagLimit(){
		return LIMIT;
	}
}

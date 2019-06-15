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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EmbeddedId;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.persistence.CriteriaFilterImpl.SelectAggregate;
import br.com.jgon.canary.persistence.exception.RemoveEntityException;
import br.com.jgon.canary.persistence.exception.SaveEntityException;
import br.com.jgon.canary.persistence.exception.UpdateEntityException;
import br.com.jgon.canary.persistence.filter.CriteriaFilter;
import br.com.jgon.canary.persistence.filter.CriteriaFilterMetamodel;
import br.com.jgon.canary.persistence.filter.QueryAttribute;
import br.com.jgon.canary.util.CollectionUtil;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.util.Pagination;
import br.com.jgon.canary.util.ReflectionUtil;

/**
 * Responsável por generalizar as operações simples de SELECT, DELETE, UPDADE e INSERT
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 * @param <T>
 * @param <K>
 */
public abstract class GenericDAO<T, K extends Serializable>{

	protected static final String ERROR_FIND_KEY = "error.find";
	protected static final String ERROR_FIND_LIST_KEY = "error.find-list";
	protected static final String ERROR_CRITERIA = "error.criteria";
	protected static final String ERROR_FIELD_DOES_NOT_EXIST = "error.field-does-not-exist";
			
	@Inject
	private Logger logger;
	/**
	 * 
	 */
	private Field fieldId;
	
	public GenericDAO(){
		this.fieldId = DAOUtil.getFieldId(getPrimaryClass());
		fieldId.setAccessible(true);
	}
	/**
	 * 
	 * @return {@link EntityManager}
	 */
	protected abstract EntityManager getEntityManager();
	
	/**
	 * Entity manager utilizado para pesquisa, default getEntityManager()
	 * @return {@link EntityManager}
	 */
	protected EntityManager getSearchEntityManager(){
		return this.getEntityManager();
	}
	
	/**
	 * 
	 * @param obj  objeto a ser persistido
	 * @return objeto  persistido
	 * @throws SaveEntityException  erro ao salvar
	 */
	@Transactional(Transactional.TxType.MANDATORY)
	public T save(T obj) throws SaveEntityException {
		try {
			getEntityManager().persist(obj);
			return obj;
		} catch (Exception e) {
			logger.error("[save]", e);
			throw new SaveEntityException(e, getPrimaryClass());
		}
	}
	
	/**
	 * 
	 * @param obj  objeto a ser persistido ou atualizado
	 * @return objeto  persistido ou atualizado
	 * @throws UpdateEntityException  erro ao atualizar
	 * @throws SaveEntityException  erro ao salvar
	 */
	@Transactional(Transactional.TxType.MANDATORY)
	public T saveOrUpdate(T obj) throws UpdateEntityException, SaveEntityException {
		boolean isSave = true;
		if(fieldId != null){
			isSave = ReflectionUtil.getAttributteValue(obj, fieldId) == null;
		}
		try {
			if(isSave){
				return save(obj);
			}else{
				return update(obj);
			}
		} catch (SaveEntityException e){
			throw e;
		} catch (UpdateEntityException e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param obj  obeto a ser atualizado
	 * @return  objeto atualizado
	 * @throws UpdateEntityException  erro ao atualizar
	 */
	@Transactional(Transactional.TxType.MANDATORY)
	public T update(T obj) throws UpdateEntityException {
		try {
			return getEntityManager().merge(obj);
		} catch (Exception e) {
			logger.error("[update]", e);
			throw new UpdateEntityException(e, getPrimaryClass());
		}
	}

	@Transactional(Transactional.TxType.MANDATORY)
	public int update(CriteriaFilterImpl<T> criteriaFilter) throws ApplicationException{
		CriteriaManager<T> criteriaManager = new CriteriaManager<T>(getEntityManager(), getPrimaryClass(), criteriaFilter);
		CriteriaUpdate<T> update = criteriaManager.getCriteriaUpdate();
		return getEntityManager().createQuery(update).executeUpdate();
	}
	/**
	 * 
	 * @param obj  objeto a ser removido
	 * @throws RemoveEntityException  erro ao remover
	 */
//	@TransactionAttribute(TransactionAttributeType.MANDATORY)
//	public void remove(T obj) throws RemoveEntityException {
//		try {
//			obj = find(obj);
//			getEntityManager().remove(obj);
//		} catch (Exception e) {
//			logger.error("[remove]", e);
//			throw new RemoveEntityException(e, getPrimaryClass());
//		}
//	}

	/**
	 * 
	 * @param id  identificardo do objeto
	 * @throws RemoveEntityException  erro ao remover
	 */
	@Transactional(Transactional.TxType.MANDATORY)
	public void remove(K id) throws RemoveEntityException {
		try {
			T obj = find(id);
			getEntityManager().remove(obj);
		} catch (Exception e) {
			logger.error("[remove]", e);
			throw new RemoveEntityException(e, getPrimaryClass());
		}
	}	
	/**
	 * Obtem a entidade pela chave, utiliza a instrução JPA - EntityManager.find(T.class, K) - para obter, 
	 * atenção pois normalmente este comando carrega todos os objetos relacionados com a entidade
	 * @param id  identificador
	 * @return objeto encontrado
	 * @throws ApplicationException 
	 */
	public T find(K id) throws ApplicationException {
		try {
			return getSearchEntityManager().find(getPrimaryClass(), id);
		} catch (IllegalArgumentException e) {
			logger.error("[find]", e);
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_FIND_KEY, new String[] { getPrimaryClass().getSimpleName() });
		}
	}
	/**
	 * Retorna entidade sem retornar os objetos relacionados
	 * @param id  identificador
	 * @return objeto econtrado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public T findReference(K id) throws ApplicationException {
		try {
			return getSearchEntityManager().getReference(getPrimaryClass(), id);
		} catch (IllegalArgumentException e) {
			logger.error("[findReference]", e);
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_FIND_KEY, new String[] { getPrimaryClass().getSimpleName() });
		}
	}
	/**
	 * 
	 * @param id  identificador
	 * @param fields  atributos retornados
	 * @return objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public T find(K id, List<String> fields) throws ApplicationException {
		 return find(id, getPrimaryClass(), fields); 
	}
	/**
	 * 
	 * @param id  identificador
	 * @param fields  campos retornados
	 * @return  objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public T find(K id, String[] fields) throws ApplicationException {
		 return find(id, CollectionUtil.convertArrayToList(fields)); 
	}
	/**
	 * 
	 * @param id  identificador
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param fields  campos retornados
	 * @param <E> 
	 * @return objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public <E> E find(K id, Class<E> resultClass, String[] fields) throws ApplicationException {
		return find(id, resultClass, CollectionUtil.convertArrayToList(fields));
	}
	/**
	 * 
	 * @param id  identificador
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param fields  campos retornados
	 * @return objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public <E> E find(K id, Class<E> resultClass, List<String> fields) throws ApplicationException {
		try {
			List<Field> flds = ReflectionUtil.listAttributesByAnnotation(getPrimaryClass(), Id.class);
			if(flds.isEmpty()){
				flds = ReflectionUtil.listAttributesByAnnotation(getPrimaryClass(), EmbeddedId.class);
			}
			if(!flds.isEmpty()){
				Field fld = flds.get(0);
				T objRef = getPrimaryClass().newInstance();
				ReflectionUtil.setFieldValue(objRef, fld, id);
				
				return find(objRef, resultClass, fields);
			}else{
				return null;
			}
		} catch (ApplicationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("[find]", e);
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_FIND_KEY, getPrimaryClass().getSimpleName());
		}
	}
	/**
	 * 
	 * @param id  identificador
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param fieldAlias  padrao chave valor entre atributos da entidade e atributos retornados
	 * @return objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public <E> E find(K id, Class<E> resultClass, Map<String, String> fieldAlias) throws ApplicationException {
		try {
			List<Field> flds = ReflectionUtil.listAttributesByAnnotation(getPrimaryClass(), Id.class);
			if(flds.isEmpty()){
				flds = ReflectionUtil.listAttributesByAnnotation(getPrimaryClass(), EmbeddedId.class);
			}
			if(!flds.isEmpty()){
				Field fld = flds.get(0);
				T objRef = getPrimaryClass().newInstance();
				ReflectionUtil.setFieldValue(objRef, fld, id);
				
				return find(objRef, resultClass, fieldAlias);
			}else{
				return null;
			}
		} catch (ApplicationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("[find]", e);
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_FIND_KEY, new String[] { getPrimaryClass().getSimpleName() });
		}
	}

	/**
	 * 
	 * @return classe da entidade
	 */
	@SuppressWarnings("unchecked")
	protected Class<T> getPrimaryClass() {
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		return (Class<T>) type.getActualTypeArguments()[0];
	}

	/**
	 * 
	 * @param param  string a ser traduzida
	 * @return  string traduzida
	 */
	public String translateUpper(String param) {
		return " UPPER(translate( trim(" + param + ") ,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','AAAAAAAAEEEEIIOOOOOOUUUUCC')) ";
	}
	
	/**
	 * 
	 * @param query  {@link Query}
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros por pagina
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
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param objRef  objeto de referencia para pesquisa
	 * @param fields  campos retornados
	 * @param sort ordenacao
	 * @param pagina numero da pagina
	 * @param qtde quantidade de registros por pagina
	 * @return lista de obejtos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	protected <E> List<E> getResultList(Class<E> resultClass, T objRef, List<String> fields, List<String> sort, Integer pagina, Integer qtde) throws ApplicationException {
		CriteriaFilter<T> cf = getCriteriaFilter(objRef)
				.addSelect(resultClass, fields)
				.addOrder(sort);
		
		return getResultList(resultClass, cf, pagina, qtde);
	}
	
	/**
	 * 
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param objRef  objeto de referencia para pesquisa
	 * @param fieldAlias  campos retornados
	 * @param sort  ordenacao
	 * @param pagina numero da pagina
	 * @param qtde quantidade de registros por pagina
	 * @return lista de objetos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	protected <E> List<E> getResultList(Class<E> resultClass, T objRef, Map<String, String> fieldAlias, List<String> sort, Integer pagina, Integer qtde) throws ApplicationException {
		CriteriaFilter<T> cf = getCriteriaFilter(objRef)
				.addSelect(fieldAlias)
				.addOrder(sort);
		
		return getResultList(resultClass, cf, pagina, qtde);
	}
		
	/**
	 * 
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param criteriaFilter  filtro de pesquisa {@link CriteriaFilter}
	 * @param pagina numero da pagina
	 * @param qtde quantidade de registros por pagina
	 * @return lista de objetos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	@SuppressWarnings("unchecked")
	protected <E> List<E> getResultList(Class<E> resultClass, CriteriaFilter<T> criteriaFilter, Integer pagina, Integer qtde) throws ApplicationException {
		try {		
			if(resultClass == null){
				resultClass = (Class<E>) getPrimaryClass();
			}
			CriteriaManager<T> criteriaManager = getCriteriaManager(resultClass, (CriteriaFilterImpl<T>) criteriaFilter);
			CriteriaQuery<?> query = criteriaManager.getCriteriaQuery();
			List<SimpleEntry<?, E>> returnList = getPreparedResultList(resultClass, query, pagina, qtde);
						
			return checkTupleResultList(criteriaManager.getListCollectionRelation(), returnList);
		} catch (ApplicationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("[getResultList]", e);
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_FIND_LIST_KEY, new String[] { getPrimaryClass().getSimpleName() });
		}
	}
	
	/**
	 * 
	 * @autor jurandirjcg
	 * @param resultClass
	 * @param criteriaFilter
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	protected <E> CriteriaQuery<T> getCriteriaQuery(Class<E> resultClass, CriteriaFilter<T> criteriaFilter) throws ApplicationException {
		if(resultClass == null){
			resultClass = (Class<E>) getPrimaryClass();
		}
		CriteriaManager<T> criteriaManager = getCriteriaManager(resultClass, (CriteriaFilterImpl<T>) criteriaFilter);
		return criteriaManager.getCriteriaQuery();
	}
	
	/**
	 * 
	 * @param listCollectionRelation
	 * @param returnList  lista de objetos para verificar colecao
	 * @return lista de objetos co colecao associada
	 * @throws InstantiationException  erro ao instanciar
	 * @throws IllegalAccessException  erro ao acessar atributo
	 * @throws ApplicationException  erro generico ao pesquisar
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private <E> List<E> checkTupleResultList(Map<String, CriteriaFilterImpl<?>> listCollectionRelation, List<SimpleEntry<?, E>> returnList) throws InstantiationException, IllegalAccessException, ApplicationException{
		//------------ ADICIONADO PARA TRATAR COLLECTION
		if(listCollectionRelation != null && !listCollectionRelation.isEmpty()){
			Field fldAux = null;
						
			//ID DO OBJETO
			List<K> listId= new ArrayList<K>();
			for(SimpleEntry<?, E> ret: returnList){
				Object idAux;
				if(ret.getKey() != null){
					idAux = ret.getKey();
				}else{
					idAux = fieldId.get(ret);
				}
				
				if(!listId.contains(ret.getKey())) {
					listId.add((K) idAux);
				}
			}
						
			for(String k : listCollectionRelation.keySet()){
				fldAux = ReflectionUtil.getAttribute(getPrimaryClass(), k);
				fldAux.setAccessible(true);
				
				CriteriaFilterImpl<T> cf = (CriteriaFilterImpl<T>) listCollectionRelation.get(k)
						.addSelect(fieldId.getName())
						.addWhereIn(fieldId.getName(), listId);
									
				List<?> listAux = getResultList(cf, null, null);
			
				if(listAux != null && !listAux.isEmpty() ) {
					for(SimpleEntry<?, E> se : returnList){
						for(Object retList: listAux){
							if(se.getKey().equals(fieldId.get(retList))) {//fieldId.get(se.getValue()) != null &&fieldId.get(se.getValue()).equals(fieldId.get(retList))){
								Collection col = (Collection<?>) fldAux.get(se.getValue());
								if(col == null){
									col = instanceCollection(fldAux.getType());
									fldAux.set(se.getValue(), col);
								}
								col.addAll((Collection<?>) fldAux.get(retList));
							}
						}
					}
				}
			}
		}

		List<E> listReturn = new LinkedList<E>();
		for(SimpleEntry<?, E> se : returnList){
			listReturn.add(se.getValue());
		}
		
		return listReturn;
	}
	/**
	 * 
	 * @param criteriaManager  {@link CriteriaManager}
	 * @param result objeto para verificar colecao
	 * @return  objeto com colecao associada
	 * @throws InstantiationException  erro ao instanciar
	 * @throws IllegalAccessException  erro ao acessar atributo
	 * @throws ApplicationException  erro generico ao pesquisar
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private <E> E checkTupleSingleResult(CriteriaManager<T> criteriaManager, SimpleEntry<?, E> result) throws InstantiationException, IllegalAccessException, ApplicationException{
		if(result == null){
			return null;
		}
		E ret = result.getValue();
		if(!criteriaManager.getListCollectionRelation().isEmpty()){
			Field fldAux = null;
			
			T objAux;
			for(String k : criteriaManager.getListCollectionRelation().keySet()){
				fldAux = ReflectionUtil.getAttribute(getPrimaryClass(), k);
				fldAux.setAccessible(true);	
				
				objAux = getPrimaryClass().newInstance();
				if(result.getKey() == null){
					fieldId.set(objAux, fieldId.get(ret));
				}else{
					fieldId.set(objAux, result.getKey());
				}
				
				CriteriaFilterImpl<T> cf = (CriteriaFilterImpl<T>) criteriaManager.getListCollectionRelation().get(k);
				cf.setObjBase(objAux);
				
				Map<String, SimpleEntry<SelectAggregate, String>> selAux= cf.getListSelection();
				for(String key : selAux.keySet()) {
					String valueAux = selAux.get(key).getValue(); 
					selAux.get(key).setValue(valueAux.replace(k.concat("."), ""));
				}
				
				List<?> resultCollection = getResultList(DAOUtil.getCollectionClass(fldAux), cf, null, null);

				Collection col = (Collection<?>) fldAux.get(ret);
				if(col == null){
					col = instanceCollection(fldAux.getType());
					fldAux.set(ret, col);
				}
				col.addAll(resultCollection);
			}
		}
		return ret;
	}
	/**
	 * 
	 * @param fldClass  atributo para verificar e instnciar a colecao
	 * @return colecao
	 */
	private <E> Collection<E> instanceCollection(Class<E> fldClass){
		if(fldClass.isAssignableFrom(List.class)){
			return new ArrayList<E>();
		}else if(fldClass.isAssignableFrom(Set.class)){
			return new HashSet<E>();
		} else {
			return new ArrayList<E>();
		}
	}
	/**
	 * 
	 * @param criteriaFilter  filtro de pesquisa {@link CriteriaFilter}
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros
	 * @return lista de objetos
	 * @throws ApplicationException  erro ao pequisar
	 */
	protected List<T> getResultList(CriteriaFilter<T> criteriaFilter, Integer pagina, Integer qtde) throws ApplicationException {
		return getResultList(getPrimaryClass(), criteriaFilter, pagina, qtde);
	}
	
	/**
	 * 
	 * @since 13/06/2019
	 * @author Jurandir Cordeiro Gonçalves
	 * @param criteriaFilter
	 * @return
	 * @throws ApplicationException
	 * @rastreabilidade_requisito
	 */
	protected T getFirstResult(CriteriaFilter<T> criteriaFilter) throws ApplicationException {
		return getFirstResult(getPrimaryClass(), criteriaFilter);
	}
	
	/**
	 * 
	 * @since 13/06/2019
	 * @author Jurandir Cordeiro Gonçalves
	 * @param <E>
	 * @param resultClass
	 * @param criteriaQuery
	 * @return
	 * @throws ApplicationException
	 * @rastreabilidade_requisito
	 */
	protected <E> E getFirstResult(Class<E> resultClass, CriteriaQuery<?> criteriaQuery) throws ApplicationException{
		List<E> list = new LinkedList<E>();
		for(SimpleEntry<?, E> se : getPreparedResultList(resultClass, criteriaQuery, 1, 1)){
			list.add(se.getValue());
		}
		
		if(list == null || list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}
	
	/**
	 * 
	 * @since 13/06/2019
	 * @author Jurandir Cordeiro Gonçalves
	 * @param criteriaQuery
	 * @return
	 * @throws ApplicationException
	 * @rastreabilidade_requisito
	 */
	protected T getFirstResult(CriteriaQuery<T> criteriaQuery) throws ApplicationException{
		return getFirstResult(getPrimaryClass(), criteriaQuery);
	}
	
	/**
	 * 
	 * @since 13/06/2019
	 * @author Jurandir Cordeiro Gonçalves
	 * @param <E>
	 * @param resultClass
	 * @param criteriaFilter
	 * @return
	 * @throws ApplicationException
	 * @rastreabilidade_requisito
	 */
	@SuppressWarnings("unchecked")
	protected <E> E getFirstResult(Class<E> resultClass, CriteriaFilter<T> criteriaFilter) throws ApplicationException{
		List<E> list = (List<E>) getResultList(criteriaFilter, 1, 1);
		if(list == null || list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}
	
	/**
	 * 
	 * @since 13/06/2019
	 * @author Jurandir Cordeiro Gonçalves
	 * @param <E>
	 * @param resultClass
	 * @param objRef
	 * @param fields
	 * @return
	 * @throws ApplicationException
	 * @rastreabilidade_requisito
	 */
	protected <E> E getFirstResult(Class<E> resultClass, T objRef, List<String> fields) throws ApplicationException {
		CriteriaFilter<T> cf = getCriteriaFilter(objRef)
				.addSelect(resultClass, fields);
		
		return (E) getFirstResult(resultClass, cf);
	}
	
	/**
	 * 
	 * @since 13/06/2019
	 * @author Jurandir Cordeiro Gonçalves
	 * @param <E>
	 * @param resultClass
	 * @param objRef
	 * @param fieldAlias
	 * @return
	 * @throws ApplicationException
	 * @rastreabilidade_requisito
	 */
	protected <E> E getFirstResult(Class<E> resultClass, T objRef, Map<String, String> fieldAlias) throws ApplicationException {
		CriteriaFilter<T> cf = getCriteriaFilter(objRef)
				.addSelect(fieldAlias);
		
		return (E) getFirstResult(resultClass, cf);
	}
	
	/**
	 * 
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param criteriaFilter  filtro de pesquisa {@link CriteriaFilter}
	 * @return  {@link CriteriaManager}
	 * @throws ApplicationException  erro ao configurar gerenciador
	 */
	@SuppressWarnings("unchecked")
	private <E> CriteriaManager<T> getCriteriaManager(Class<E> resultClass, CriteriaFilterImpl<T> criteriaFilter) throws ApplicationException {
		if(resultClass == null){
			resultClass = (Class<E>) getPrimaryClass();
		}
		boolean isTuple = criteriaFilter != null && !((CriteriaFilterImpl<T>) criteriaFilter).getListSelection().isEmpty() && !ReflectionUtil.isPrimitive(resultClass);

		if(isTuple){	
			return new CriteriaManager<T>(getSearchEntityManager(), getPrimaryClass(), getPrimaryClass(), resultClass, (CriteriaFilterImpl<T>) criteriaFilter);
		}else{
			return new CriteriaManager<T>(getSearchEntityManager(), getPrimaryClass(), resultClass, resultClass, (CriteriaFilterImpl<T>) criteriaFilter);
		}
	}
	
	/**
	 * 
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param criteriaQuery  {@link CriteriaQuery}
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @return  lista de objetos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	protected <E> List<E> getResultList(Class<E> resultClass, CriteriaQuery<?> criteriaQuery, Integer pagina, Integer qtde) throws ApplicationException {
		List<E> listReturn = new LinkedList<E>();
		for(SimpleEntry<?, E> se : getPreparedResultList(resultClass, criteriaQuery, pagina, qtde)){
			listReturn.add(se.getValue());
		}
		return listReturn;
	}
	/**
	 * 
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param criteriaQuery  {@link CriteriaQuery}
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @return  lista de objetos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	@SuppressWarnings("unchecked")
	private <E> List<SimpleEntry<?, E>> getPreparedResultList(Class<E> resultClass, CriteriaQuery<?> criteriaQuery, Integer pagina, Integer qtde) throws ApplicationException {
		try {
			boolean isTuple = criteriaQuery.getResultType().equals(Tuple.class);
			List<SimpleEntry<?, E>> listReturn = new LinkedList<SimpleEntry<?, E>>();
			
			if(isTuple){
				CriteriaQuery<Tuple> query = (CriteriaQuery<Tuple>) criteriaQuery;
				TypedQuery<Tuple> tQuery = getSearchEntityManager().createQuery(query);
				configPaginacao(tQuery, pagina, qtde);
				List<Tuple> tuple = tQuery.getResultList();
				
				for(Tuple t: tuple){
					listReturn.add(tupleToResultClass(t, resultClass));
				}
				
				return listReturn;
			}else{
				CriteriaQuery<E> query = (CriteriaQuery<E>) criteriaQuery;
				TypedQuery<E> tQuery = getSearchEntityManager().createQuery(query);
				configPaginacao(tQuery, pagina, qtde);
				for(E oRet : tQuery.getResultList()){
					listReturn.add(new SimpleEntry<Object, E>(null, oRet));
				}
				return listReturn;
			}
		} catch (ApplicationException e) {
			throw e;
		} catch (InstantiationException e) {
				logger.error("[getPreparedResultList]", e);
				throw new ApplicationException(MessageSeverity.ERROR, "error.instantiation", new String[] { getPrimaryClass().getSimpleName() });
		} catch (IllegalAccessException e) {
				logger.error("[getPreparedResultList]", e);
				throw new ApplicationException(MessageSeverity.ERROR, "error.field.access", new String[] { getPrimaryClass().getSimpleName() });
		} 
	}
	
	/**
	 * 
	 * @param criteriaQuery  {@link CriteriaQuery}
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @return lista de objetos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	protected List<T> getResultList(CriteriaQuery<T> criteriaQuery, Integer pagina, Integer qtde) throws ApplicationException {
		return getResultList(getPrimaryClass(), criteriaQuery, pagina, qtde);
	}
	
	/**
	 * 
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param objRef  objeto de referencia para pesquisa
	 * @param fieldAlias  associacao entre atributo da entidade e atributo de retorno
	 * @return objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	protected <E> E getSingleResult(Class<E> resultClass, T objRef, Map<String, String> fieldAlias) throws ApplicationException {
		CriteriaFilter<T> cf = getCriteriaFilter(objRef)
				.addSelect(fieldAlias);
		
		return (E) getSingleResult(resultClass, cf);
	}
	/**
	 * 
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param objRef  objeto de referencia para pesquisa
	 * @param fields  campos retornados
	 * @return  objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisa
	 */
	protected <E> E getSingleResult(Class<E> resultClass, T objRef, List<String> fields) throws ApplicationException {
		CriteriaFilter<T> cf = getCriteriaFilter(objRef)
				.addSelect(resultClass, fields);
		
		return (E) getSingleResult(resultClass, cf);
	}
	/**
	 * 
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param criteriaFilter  filtros da pesquisa {@link CriteriaFilter}
	 * @return  objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	@SuppressWarnings("unchecked")
	protected <E> E getSingleResult(Class<E> resultClass, CriteriaFilter<T> criteriaFilter) throws ApplicationException {
		try {
			if(resultClass == null){
				resultClass = (Class<E>) getPrimaryClass();
			}
			CriteriaManager<T> criteriaManager = getCriteriaManager(resultClass, (CriteriaFilterImpl<T>) criteriaFilter); 
			CriteriaQuery<?> query = criteriaManager.getCriteriaQuery();
			
			SimpleEntry<?, E> result = getPreparedSingleResult(resultClass, query);
			return checkTupleSingleResult(criteriaManager, result);
		} catch (ApplicationException e) {
			throw e;
		} catch (NoResultException nre){
			return null;
		} catch (Exception e) {
			logger.error("[getSingleResult]", e);
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_FIND_KEY, new String[] { getPrimaryClass().getSimpleName() });
		}
	}

	/**
	 * 
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param criteriaQuery  {@link CriteriaQuery}
	 * @return objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	protected <E> E getSingleResult(Class<E> resultClass, CriteriaQuery<?> criteriaQuery) throws ApplicationException {
		return getPreparedSingleResult(resultClass, criteriaQuery).getValue();
	}
	/**
	 * 
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param criteriaQuery  {@link CriteriaQuery}
	 * @return objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	@SuppressWarnings("unchecked")
	private <E> SimpleEntry<?, E> getPreparedSingleResult(Class<E> resultClass, CriteriaQuery<?> criteriaQuery) throws ApplicationException {
		try {
			boolean isTuple = criteriaQuery.getResultType().equals(Tuple.class);
			
			if(isTuple){
				CriteriaQuery<Tuple> query = (CriteriaQuery<Tuple>) criteriaQuery;
				TypedQuery<Tuple> tQuery = getSearchEntityManager().createQuery(query);
				Tuple tuple = tQuery.getSingleResult();
				return tupleToResultClass(tuple, resultClass);
			}else{
				CriteriaQuery<E> query = (CriteriaQuery<E>) criteriaQuery;
				TypedQuery<E> tQuery = getSearchEntityManager().createQuery(query);
				return new SimpleEntry<Object, E>(null, tQuery.getSingleResult());
			}
		} catch (ApplicationException e) {
			throw e;
		} catch (NoResultException nre){
			return null;
		} catch (IllegalArgumentException e) {
			logger.error("[getPreparedSingleResult]", e);
			throw new ApplicationException(MessageSeverity.ERROR, "message", new String[] { e.getMessage() });
		} catch (Exception e) {
			logger.error("[getPreparedSingleResult]", e);
			throw new ApplicationException(MessageSeverity.ERROR, ERROR_FIND_KEY, new String[] { getPrimaryClass().getSimpleName() });
		}
	}
	
	/**
	 * 
	 * @param criteriaQuery {@link CriteriaQuery}
	 * @return objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	protected T getSingleResult(CriteriaQuery<T> criteriaQuery) throws ApplicationException {
		return getSingleResult(getPrimaryClass(), criteriaQuery);
	}
	/**
	 * 
	 * @param criteriaFilter  filtros da pesquisa {@link CriteriaFilter}
	 * @return objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	protected T getSingleResult(CriteriaFilter<T> criteriaFilter) throws ApplicationException {
		return getSingleResult(getPrimaryClass(), criteriaFilter);
	}
	/**
	 * 
	 * @param tuple  {@link Tuple}
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @return tuple da pesquisa
	 * @throws InstantiationException  erro ao instanciar objeto
	 * @throws IllegalAccessException  erro ao acessar atributo
	 * @throws ApplicationException  erro ao pesquisar
	 */
	@SuppressWarnings("unchecked")
	private <E> SimpleEntry<?, E> tupleToResultClass(Tuple tuple, Class<E> resultClass) throws ApplicationException, InstantiationException, IllegalAccessException {
		if(resultClass == null){
			resultClass = (Class<E>) getPrimaryClass();
		}
		
		if(ReflectionUtil.isPrimitive(resultClass)){
			return new SimpleEntry<Object, E>(null, (E) tuple.get(0));
		}
		
		E objReturn;
		
		objReturn = resultClass.newInstance();
			
		SimpleEntry<?, E> seReturn = null;
		
		for(TupleElement<?> te : tuple.getElements()){
			if(StringUtils.isBlank(te.getAlias())) {
				ApplicationException ae = new ApplicationException(MessageSeverity.ERROR, "genericdao-tuple-alias-not-found", resultClass.getName());
				logger.error("[tupleToResultClass]", ae.getMessage());
				throw ae;
			}
			
			if(te.getAlias().contains(".")){
				if(tuple.get(te) == null) {
					continue;
				}
				String[] subObj = te.getAlias().split("\\.");
				Object objAux = objReturn;
				for(int i=0; i < subObj.length; i++){
					Field fldAux = ReflectionUtil.getAttribute(objAux.getClass(), subObj[i]);
				
					if(fldAux == null){
						ApplicationException ae = new ApplicationException(MessageSeverity.ERROR, ERROR_FIELD_DOES_NOT_EXIST, new String[] { subObj[i], objAux.getClass().getSimpleName() });
						logger.error("[tupleToResultClass]", ae.getMessage());
						throw ae;
					}
					fldAux.setAccessible(true);
					if(i == subObj.length - 1){
						fldAux.set(objAux, tuple.get(te));
					}else{
						Object objTemp = fldAux.get(objAux);
						if(objTemp == null){
							if(ReflectionUtil.isCollection(fldAux.getType())){
								objTemp = createCollectionInstance(fldAux.getType());
							}else{
								objTemp = fldAux.getType().newInstance();
							}
							fldAux.set(objAux, objTemp);
						}
						if(objTemp instanceof Collection 
								&& (fldAux.isAnnotationPresent(QueryAttribute.class) 
										|| fldAux.isAnnotationPresent(OneToMany.class)
										|| fldAux.isAnnotationPresent(ManyToMany.class))){
							
							Object objInCollection = null;
							if(!((Collection<?>) objTemp).isEmpty()){
								objInCollection = ((Collection<?>) objTemp).toArray()[	((Collection<?>) objTemp).size() -1 ];
							}else{
								objInCollection = getObjectCollectionInstance(fldAux);
								if(objInCollection != null){
									((Collection<Object>) objTemp).add(objInCollection);
								}
							}
							objTemp = objInCollection;
						}
						objAux = objTemp;
					}
				}
			}else{
				if(te.getAlias().equals(CriteriaManager.ALIAS_ATTR_FORCED_ID)){
					seReturn = new SimpleEntry<Object, E>(tuple.get(te), objReturn);
				}else{
					if(te.getAlias().equals(fieldId.getName())){
						seReturn = new SimpleEntry<Object, E>(tuple.get(te), objReturn);
					}
					try {
						ReflectionUtil.setFieldValue(objReturn, te.getAlias(), tuple.get(te));
					} catch (Exception e) {
						logger.error("[tupleToResultClass]", e);
						throw new ApplicationException(MessageSeverity.ERROR, "genericdao-field-not-found", resultClass.getName(), te.getAlias());
					}
				}
			}
		}
		if(seReturn == null){
			return new SimpleEntry<Object, E>(null, objReturn);
		}else{
			return seReturn;
		}
	}
	/**
	 * 
	 * @param fld  campo para analise
	 * @return  colecao instanciada
	 * @throws ApplicationException  objeto nao e colecao
	 * @throws InstantiationException  erro ao instanciar
	 * @throws IllegalAccessException  erro ao acessar atributo
	 */
	private Object getObjectCollectionInstance(Field fld) throws ApplicationException{
		Class<?> classAux = DAOUtil.getCollectionClass(fld);
		if(classAux == null){
			ApplicationException ae = new ApplicationException(MessageSeverity.ERROR, "query-mapper.field-collection-not-definied", fld.getDeclaringClass().getName() + "." + fld.getName()); 
			logger.error("[getObjectCollectionInstance]", ae.getMessage());
			throw ae; 
		}else{
			try {
				return classAux.newInstance();
			} catch (Exception e) {
				logger.error("[getObjectCollectionInstance]", e);
				throw new ApplicationException(MessageSeverity.ERROR, "error.instantiation", classAux.getName());
			}
		}		
	}
	
	/**
	 * 
	 * @param klass  tipo de colecao
	 * @return colecao instanciada
	 * @throws ApplicationException  erro ao instanciar colecao
	 */
	private Collection<Object> createCollectionInstance(Class<?> klass) throws ApplicationException{
		if(klass.isAssignableFrom(List.class)){
			return new ArrayList<Object>();
		}else if(klass.isAssignableFrom(Set.class)){
			return new HashSet<Object>();
		} else if(klass.isAssignableFrom(Collection.class)){
			return new HashSet<Object>();
		} 
		ApplicationException ae = new ApplicationException(MessageSeverity.ERROR, "error.collection-instance", new String[] {klass.getName()});
		logger.error("[createCollectionInstance]", ae.getMessage());
		throw ae;
	}
	
	/**
	 * 
	 * @param obj  objeto de pesquisa
	 * @return obejto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public T find(T obj) throws ApplicationException {
		CriteriaFilterImpl<T> cf = (CriteriaFilterImpl<T>) getCriteriaFilter(obj);
		return getSingleResult(getPrimaryClass(), cf);
	}
	/**
	 * 
	 * @param obj  objeto de pesquisa
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param fields  campos retornados
	 * @return objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public <E> E find(T obj, Class<E> resultClass, String[] fields) throws ApplicationException{
		return find(obj, resultClass, CollectionUtil.convertArrayToList(fields));
	}
	
	/**
	 * 
	 * @param obj  objeto de pesquisa
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param fields  campos tr
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
	 * @param obj  objeto de pesquisa
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param fieldAlias  referencia entre atributo da entidade e atributo de retorno
	 * @return  objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public <E> E find(T obj, Class<E> resultClass, Map<String, String> fieldAlias) throws ApplicationException {
		CriteriaFilterImpl<T> cf = (CriteriaFilterImpl<T>) getCriteriaFilter(obj)
			.addSelect(fieldAlias);
		
		return getSingleResult(resultClass, cf);
	}
	/**
	 * 
	 * @param obj  objeto de pesquisa
	 * @param fields  campos retornados
	 * @return objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public T find(T obj, List<String> fields) throws ApplicationException {
		return find(obj, getPrimaryClass(), fields);
	}
	/**
	 * 
	 * @param obj  objeto de pesquisa
	 * @param fields  campos retornados
	 * @return objeto pesquisado
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public T find(T obj, String[] fields) throws ApplicationException {
		return find(obj, CollectionUtil.convertArrayToList(fields));
	}
	/**
	 * 
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @return lista de objetos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public List<T> list(Integer pagina, Integer qtde) throws ApplicationException {
		return this.list(null, pagina, qtde);
	}
	
	/**
	 * 
	 * @param obj  objeto de pesquisa
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @return lista de objetos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public List<T> list(T obj, Integer pagina, Integer qtde) throws ApplicationException {
		CriteriaFilterImpl<T> cf = (CriteriaFilterImpl<T>) getCriteriaFilter(obj);
		return getResultList(getPrimaryClass(), cf, pagina, qtde);
	}
			
	/**
	 * 
	 * @param objRef  objeto de referencia da pesquisa
	 * @return filtro da pesquisa
	 */
	protected CriteriaFilter<T> getCriteriaFilter(T objRef){
		if(objRef == null){
			return getCriteriaFilter();
		}
		return new CriteriaFilterImpl<T>(objRef, getPrimaryClass());
	}
	
	/**
	 * 
	 * @return filtro da pesquisa
	 */
	protected CriteriaFilter<T> getCriteriaFilter(){
		return new CriteriaFilterImpl<T>(getPrimaryClass());
	}
	
	/**
	 * 
	 * @param objRef  objeto de referencia da pesquisa
	 * @return filtro da pesquisa
	 */
	protected CriteriaFilterMetamodel<T> getCriteriaFilterMetamodel(T objRef){
		if(objRef == null){
			return getCriteriaFilterMetamodel();
		}
		return new CriteriaFilterImpl<T>(objRef, getPrimaryClass());
	}
	
	/**
	 * 
	 * @return filtro da pesquisa
	 */
	protected CriteriaFilterMetamodel<T> getCriteriaFilterMetamodel(){
		return new CriteriaFilterImpl<T>(getPrimaryClass());
	}
	
	/**
	 * Retorna lista paginada com a mesma assinatura da entidade 
	 * @param criteriaFilter  filtro da pesquisa {@link CriteriaFilter}
	 * @param page  numero da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @return lista de objetos paginada
	 * @throws ApplicationException  erro ao pesquisar
	 */
	protected Pagination<T> getResultPaginate(CriteriaFilter<T> criteriaFilter, int page, int qtde) throws ApplicationException{
		return getResultPaginate(getPrimaryClass(), criteriaFilter, page, qtde);
	}
	/**
	 * 
	 * @param obj  objeto de referencia da pesquisa
	 * @param page  numero da pagina
	 * @param qtde  numero de registros por pagina
	 * @return  lista de objetos paginada
	 * @throws ApplicationException  erro ao paginar
	 */
	public Pagination<T> paginate(T obj, int page, int qtde) throws ApplicationException{
		CriteriaFilterImpl<T> cf = (CriteriaFilterImpl<T>) getCriteriaFilter(obj);
		return getResultPaginate(getPrimaryClass(), cf, page, qtde);
	}
	/**
	 * 
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @param fields  campos retornados
	 * @param sort  ordenacao
	 * @return lista de objetos paginada
	 * @throws ApplicationException  erro ao paginar
	 */
	public Pagination<T> paginate(List<String> fields, List<String> sort, int pagina, int qtde) throws ApplicationException{
		return paginate(null, fields, sort, pagina, qtde);
	}
	/**
	 * 
	 * @param obj  objeto de referencia da pesquisa
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @param fields  campos retornados
	 * @param sort  ordenacao
	 * @return lista de objetos paginada
	 * @throws ApplicationException  erro ao paginar
	 */
	public Pagination<T> paginate(T obj, List<String> fields, List<String> sort, int pagina, int qtde) throws ApplicationException{
		return paginate(obj, null, fields, sort, pagina, qtde);
	}
	
	/**
	 * 
	 * @param obj  objeto de referencia da pesquisa
	 * @param fields  campos retornados
	 * @param sort  ordenacao
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @return  lista de objetos paginada
	 * @throws ApplicationException  erro ao paginar
	 */
	public Pagination<T> paginate(T obj, String[] fields, String[] sort, int pagina, int qtde) throws ApplicationException{
		return paginate(obj, null, fields, sort, pagina, qtde);
	}
	
	/**
	 * 
	 * @param obj  objeto de referencia da pesquisa
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param pagina  numero da pagina
	 * @param qtde  qunatidade de registros por pagina
	 * @param fields  campos retornados
	 * @param sort  ordenacao
	 * @return lista de objetos paginada
	 * @throws ApplicationException  erro ao paginar
	 */
	public <E> Pagination<E> paginate(T obj, Class<E> resultClass, List<String> fields, List<String> sort, int pagina, int qtde) throws ApplicationException{
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
	 * @param obj  objeto de referencia da pesquisa
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param fieldAlias  relacao atributo da entidade com atributo de retorno
	 * @param sort  ordenacao
	 * @param pagina numero da pagina
	 * @param qtde  quantidade de regitros por pagina
	 * @return  lista de objetos paginada
	 * @throws ApplicationException  erro ao paginar
	 */
	public <E> Pagination<E> paginate(T obj, Class<E> resultClass, Map<String, String> fieldAlias, List<String> sort, int pagina, int qtde) throws ApplicationException{
		return getResultPaginate(resultClass, obj, fieldAlias, sort, pagina, qtde);
	}
	
	/**
	 * 
	 * @param obj  objeto de referencia da pesquisa
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param pagina  numetro da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @param fields  campos retornados
	 * @param sort  ordenacao
	 * @return lista de objetos paginada
	 * @throws ApplicationException  erro ao paginar
	 */
	public <E> Pagination<E> paginate(T obj, Class<E> resultClass, String[] fields, String[] sort, int pagina, int qtde) throws ApplicationException{
		return paginate(obj, resultClass, CollectionUtil.convertArrayToList(fields), CollectionUtil.convertArrayToList(sort), pagina, qtde);
	}
	/**
	 * 
	 * @param obj  objeto de referencia da pesquisa
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param fieldAlias  relacao atributo da entidade com atributo de retorno
	 * @param sort  ordenacao
	 * @param pagina  numero da pagina
	 * @param qtde  qunatidade de registros por pagina
	 * @return  lista de objetos paginada
	 * @throws ApplicationException  erro ao paginar
	 */
	public <E> Pagination<E> paginate(T obj, Class<E> resultClass, Map<String, String> fieldAlias, String[] sort, int pagina, int qtde) throws ApplicationException{
		return paginate(obj, resultClass, fieldAlias, CollectionUtil.convertArrayToList(sort), pagina, qtde);
	}
	
	/**
	 * 
	 * @param obj  objeto de referencia da pesquisa
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param pagina  numero da pagina
	 * @param qtde  qunatidade de registros por pagina
	 * @param fields  campos retornados
	 * @param sort  ordenacao
	 * @return  lista de objetos paginada
	 * @throws ApplicationException  erro ao paginar
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
	 * @param obj  objeto de referencia da pesuisa
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param fieldAlias  relacao atributos da entidade e atributos de retorno 
	 * @param sort  ordenacao
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @return  lista de objetos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public <E> List<E> list(T obj, Class<E> resultClass, Map<String, String> fieldAlias, List<String> sort, Integer pagina, Integer qtde) throws ApplicationException{
		return getResultList(resultClass, obj, fieldAlias, sort, pagina, qtde);
	}
	
	/**
	 * 
	 * @param obj  objeto de referencia da pesquisa
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param fields  campos retornados
	 * @param sort  ordenacao
	 * @param pagina  numero da paigna
	 * @param qtde  quantidade de registros por pagina
	 * @return lista de objetos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public <E> List<E> list(T obj, Class<E> resultClass, String[] fields, String[] sort, Integer pagina, Integer qtde) throws ApplicationException{
		return list(obj, resultClass, CollectionUtil.convertArrayToList(fields), CollectionUtil.convertArrayToList(sort), pagina, qtde);
	}
	/**
	 * 
	 * @param obj  objeto de referencia da pesquisa
	 * @param resultClass  classe que indica o tipo de objeto de retorno
	 * @param fieldAlias  relacao atributo da entidade com atributo de retorno
	 * @param sort  ordencao
	 * @param pagina  numero da pagina
	 * @param qtde  qunatidade de registros por pagina
	 * @return  lista de obejtos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public <E> List<E> list(T obj, Class<E> resultClass, Map<String, String> fieldAlias, String[] sort, Integer pagina, Integer qtde) throws ApplicationException{
		return list(obj, resultClass, fieldAlias, CollectionUtil.convertArrayToList(sort), pagina, qtde);
	}
	/**
	 * 
	 * @param obj  objeto de referencia da pesquisa
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @param fields  campos retornados
	 * @param sort  ordenacao
	 * @return lsita de objetos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public List<T> list(T obj, List<String> fields, List<String> sort, Integer pagina, Integer qtde) throws ApplicationException{
		return list(obj, null, fields, sort, pagina, qtde);
	}
	
	/**
	 * 
	 * @param obj  objeto de referencia da pesquisa
	 * @param fields  campos retornados
	 * @param sort  ordenacao
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @return  lsita de objetos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public List<T> list(T obj, String[] fields, String[] sort, Integer pagina, Integer qtde) throws ApplicationException{
		return list(obj, null, fields, sort, pagina, qtde);
	}
	/**
	 * 
	 * @param pagina  numero da pagina
	 * @param qtde  qunatidade de registros por pagina
	 * @param fields  campos retornados
	 * @param sort  ordenacao
	 * @return lsita de objetos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public List<T> list(List<String> fields, List<String> sort, Integer pagina, Integer qtde) throws ApplicationException{
		return list(null, null, fields, sort, pagina, qtde);
	}
	
	/**
	 * 
	 * @param fields  campos retornados
	 * @param sort  ordenacao
	 * @param pagina  numero da pagina
	 * @param qtde  quantidade de registros por pagina
	 * @return  lista de objetos
	 * @throws ApplicationException  erro ao pesquisar
	 */
	public List<T> list(String[] fields, String[] sort, Integer pagina, Integer qtde) throws ApplicationException{
		return list(null, null, fields, sort, pagina, qtde);
	}
	
	/**
	 * 
	 * @param returnClass  classe que indica o tipo de objeto de retorno
	 * @param objRef  objeto de referencia da pesquisa
	 * @param fields  campos retornaddos
	 * @param sort  ordendacao
	 * @param page  numero da pagina
	 * @param limit  quantidade de registros por pagina
	 * @return  lista de objetos paginada
	 * @throws ApplicationException  erro ao paginar
	 */
	protected <E> Pagination<E> getResultPaginate(Class<E> returnClass, T objRef, List<String> fields, List<String> sort, int page, int limit) throws ApplicationException{
		CriteriaFilter<T> cf = getCriteriaFilter(objRef)
				.addSelect(returnClass, fields)
				.addOrder(sort);
		
		return getResultPaginate(returnClass, cf, page, limit);
	}
	
	/**
	 * 
	 * @param returnClass  classe que indica o tipo de objeto de retorno
	 * @param objRef  objeto de referencia da pesquisa
	 * @param fieldAlias  relacao atributo da entidade com atributo de retorno
	 * @param sort  ordencao
	 * @param page numero da pagina
	 * @param limit  quantidade de registros por pagina
	 * @return  lista de objetos paginada
	 * @throws ApplicationException  erro ao paginar
	 */
	protected <E> Pagination<E> getResultPaginate(Class<E> returnClass, T objRef, Map<String, String> fieldAlias, List<String> sort, int page, int limit) throws ApplicationException{
		CriteriaFilter<T> cf = getCriteriaFilter(objRef)
				.addSelect(fieldAlias)
				.addOrder(sort);
		return getResultPaginate(returnClass, cf, page, limit);
	}
	
	/**
	 * Retorna lista paginada com retorno customizado
	 * @param returnClass  classe que indica o tipo de objeto de retorno
	 * @param criteriaFilter  filtro de pesquisa {@link CriteriaFilter}
	 * @param page  numero da pagina
	 * @param limit  quantidade de registros por pagina
	 * @return lista de objetos paginada
	 * @throws ApplicationException  erro ao paginar
	 */
	protected <E> Pagination<E> getResultPaginate(Class<E> returnClass, CriteriaFilter<T> criteriaFilter, int page, int limit) throws ApplicationException{
	/*	
		CriteriaFilterImpl<T> cFilter = (CriteriaFilterImpl<T>) criteriaFilter; 
		for(String key : cFilter.getListSelection().keySet()) {
			cFilter.addGroupBy(key);
		}*/
		
		CriteriaManager<T> criteriaManager = getCriteriaManager(returnClass, (CriteriaFilterImpl<T>) criteriaFilter);	
		CriteriaQuery<?> query = criteriaManager.getCriteriaQuery();
		
		return getResultPaginate(returnClass, query, criteriaManager.getRootEntry(), criteriaManager.getListCollectionRelation(), page, limit);
		/*//SELECT
		Selection<?> sel = query.getSelection();
		//ORDER
		List<Order> orderList = new ArrayList<Order>(query.getOrderList()); 
		query.getOrderList().clear();
		//GROUP
		List<Expression<?>> groupList = new ArrayList<Expression<?>>(query.getGroupList());
		query.getGroupList().clear();
				
		List<Field> flds = ReflectionUtil.listAttributesByAnnotation(getPrimaryClass(), Id.class);
		boolean existEmbeddedId = false;
		if(flds.isEmpty()){
			existEmbeddedId = ReflectionUtil.existAnnotation(getPrimaryClass(), null, EmbeddedId.class);
		}
		
		if(flds.size() == 1){
			Field fldId = flds.get(0);
			query.select((Selection) criteriaManager.getCriteriaBuilder().tuple(criteriaManager.getCriteriaBuilder().countDistinct(criteriaManager.getRootEntry().get(fldId.getName())).alias(fldId.getName())));
		} else if(existEmbeddedId){
			query.select((Selection) criteriaManager.getCriteriaBuilder().tuple(criteriaManager.getCriteriaBuilder().countDistinct(criteriaManager.getRootEntry())));
		} else{
			query.select((Selection) criteriaManager.getCriteriaBuilder().countDistinct(criteriaManager.getRootEntry()));
		}
		
		Long qtdeReg = getSingleResult(Long.class, query);
		
		Pagination<E> paginacao = new Pagination<E>(qtdeReg, limit, page);
		
		if(qtdeReg > 0){			
			query.select((Selection) sel);
			query.orderBy(orderList);
			
			for(Iterator<Expression<?>> itExp = groupList.iterator(); itExp.hasNext();) {
				Expression expAux = itExp.next();
				boolean findSel = false;
				for(Selection selAux : sel.getCompoundSelectionItems()) {
					if(expAux.getAlias() != null && selAux.getAlias() != null && expAux.getAlias().equals(selAux.getAlias())) {
						findSel = true;
						break;
					}
				}
				if(!findSel) {
					itExp.remove();
				}
			}

			query.groupBy(groupList);
			
			List<SimpleEntry<?, E>> returnList = getPreparedResultList(returnClass, query, page, limit);
			
			try {
				paginacao.setElements(checkTupleResultList(criteriaManager, returnList));
			} catch (ApplicationException e) {
				throw e;
			} catch (Exception e) {
				throw new ApplicationException(MessageSeverity.ERROR, ERROR_FIND_LIST_KEY, e, new String[] { getPrimaryClass().getSimpleName() });
			}			
		}		
		return paginacao;*/
	}

	/**
	 * 
	 * @autor jurandirjcg
	 * @param returnClass
	 * @param criteriaQuery
	 * @param criteriaComplex - ignora a criteriaQuery deste objeto
	 * @param page
	 * @param limit
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	protected <E> Pagination<E> getResultPaginate(Class<E> returnClass, CriteriaQuery<?> criteriaQuery, int page, int limit) throws ApplicationException{
		Root<T> root = null;
		for(Root<?> rootAux : criteriaQuery.getRoots()) {
			if(rootAux.getJavaType().equals(getPrimaryClass())) {
				root = (Root<T>) rootAux;
				break;
			}
		}
		
		return getResultPaginate(returnClass, criteriaQuery, root,  null,  page, limit);
	}
	/**
	 * 
	 * @autor jurandirjcg
	 * @param returnClass
	 * @param criteriaQuery
	 * @param listCollectionRelation
	 * @param root
	 * @param page
	 * @param limit
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <E> Pagination<E> getResultPaginate(Class<E> returnClass, CriteriaQuery<?> criteriaQuery, Root<T> root, Map<String, CriteriaFilterImpl<?>> listCollectionRelation,  int page, int limit) throws ApplicationException{
			
		//SELECT
		Selection<?> sel = criteriaQuery.getSelection();
		//ORDER
		List<Order> orderList = new ArrayList<Order>(criteriaQuery.getOrderList()); 
		criteriaQuery.getOrderList().clear();
		//GROUP
		List<Expression<?>> groupList = new ArrayList<Expression<?>>(criteriaQuery.getGroupList());
		criteriaQuery.getGroupList().clear();
				
		List<Field> flds = ReflectionUtil.listAttributesByAnnotation(getPrimaryClass(), Id.class);
		boolean existEmbeddedId = false;
		if(flds.isEmpty()){
			existEmbeddedId = ReflectionUtil.existAnnotation(getPrimaryClass(), null, EmbeddedId.class);
		}
		
		CriteriaBuilder criteriaBuilder = getSearchEntityManager().getCriteriaBuilder();
		
		if(flds.size() == 1){
			Field fldId = flds.get(0);
			criteriaQuery.select((Selection) criteriaBuilder.tuple(criteriaBuilder.countDistinct(root.get(fldId.getName())).alias(fldId.getName())));
		} else if(existEmbeddedId){
			criteriaQuery.select((Selection) criteriaBuilder.tuple(criteriaBuilder.countDistinct(root)));
		} else{
			criteriaQuery.select((Selection) criteriaBuilder.countDistinct(root));
		}
		
		Long qtdeReg = getSingleResult(Long.class, criteriaQuery);
		
		Pagination<E> paginacao = new Pagination<E>(qtdeReg, limit, page);
		
		if(qtdeReg > 0){			
			criteriaQuery.select((Selection) sel);
			criteriaQuery.orderBy(orderList);
			/*
			for(Iterator<Expression<?>> itExp = groupList.iterator(); itExp.hasNext();) {
				Expression expAux = itExp.next();
				boolean findSel = false;
				for(Selection selAux : sel.getCompoundSelectionItems()) {
					if(expAux.getAlias() != null && selAux.getAlias() != null && expAux.getAlias().equals(selAux.getAlias())) {
						findSel = true;
						break;
					}
				}
				if(!findSel) {
					itExp.remove();
				}
			}*/

			criteriaQuery.groupBy(groupList);
			
			List<SimpleEntry<?, E>> returnList = getPreparedResultList(returnClass, criteriaQuery, page, limit);
			
			try {
				paginacao.setElements(checkTupleResultList(listCollectionRelation, returnList));
			} catch (ApplicationException e) {
				throw e;
			} catch (Exception e) {
				logger.error("[getResultPaginate]", e);
				throw new ApplicationException(MessageSeverity.ERROR, ERROR_FIND_LIST_KEY, new String[] { getPrimaryClass().getSimpleName() });
			}			
		}		
		return paginacao;
	}
	
/*	*//**
	 * 
	 * @autor jurandirjcg
	 * @param criteriaFilter
	 * @return
	 * @throws ApplicationException
	 *//*
	@SuppressWarnings("unchecked")
	protected Long getResultCount(CriteriaFilter<T> criteriaFilter) throws ApplicationException {
		
		CriteriaFilterImpl<T> cf= (CriteriaFilterImpl<T>) criteriaFilter;
		cf.getListSelection()
		
		CriteriaManager<T> criteriaManager = getCriteriaManager(Long.class, (CriteriaFilterImpl<T>) criteriaFilter);	
		CriteriaQuery<?> query = criteriaManager.getCriteriaQuery();
		
		//SELECT
		Selection<?> sel = query.getSelection();
		//ORDER
		query.getOrderList().clear();
		//GROUP
		query.getGroupList().clear();
				
		List<Field> flds = ReflectionUtil.listAttributesByAnnotation(getPrimaryClass(), Id.class);
		boolean existEmbeddedId = false;
		if(flds.isEmpty()){
			existEmbeddedId = ReflectionUtil.existAnnotation(getPrimaryClass(), null, EmbeddedId.class);
		}
		
		if(flds.size() == 1){
			Field fldId = flds.get(0);
			query.select((Selection) criteriaManager.getCriteriaBuilder().tuple(criteriaManager.getCriteriaBuilder().countDistinct(criteriaManager.getRootEntry().get(fldId.getName())).alias(fldId.getName())));
		} else if(existEmbeddedId){
			query.select((Selection) criteriaManager.getCriteriaBuilder().tuple(criteriaManager.getCriteriaBuilder().countDistinct(criteriaManager.getRootEntry())));
		} else{
			query.select((Selection) criteriaManager.getCriteriaBuilder().countDistinct(criteriaManager.getRootEntry()));
		}
		
		return getSingleResult(Long.class, query);
	}*/
}

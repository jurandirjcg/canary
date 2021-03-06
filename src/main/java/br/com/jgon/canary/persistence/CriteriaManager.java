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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.persistence.CriteriaFilterImpl.SelectAggregate;
import br.com.jgon.canary.persistence.CriteriaFilterImpl.Where;
import br.com.jgon.canary.util.CollectionUtil;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.util.ReflectionUtil;

/**
 * Classe responsavel por criar a JPA Criteria e realizar as configuracoes e conversoes para realizar as consultas
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 * @param <T>
 */
class CriteriaManager<T> {
		
	public static final String ALIAS_ATTR_FORCED_ID = "ATTR_FORCED_ID";
	private EntityManager entityManager;
	private CriteriaFilterImpl<T> criteriaFilter;
	private CriteriaAssociations criteriaAssociations;
	private Map<String, CriteriaFilterImpl<?>> listCollectionRelation = new HashMap<String, CriteriaFilterImpl<?>>(0);
	private Root<T> rootEntry;
	private Class<?> queryClass;
	private Class<?> resultClass;
	private CriteriaBuilder criteriaBuilder;
	private CriteriaQuery<?> criteriaQuery;
	private Class<T> entityClass;
	
	private Logger logger = LoggerFactory.getLogger(CriteriaManager.class);
		
	/**
	 * 
	 * @param entityManager
	 * @param entityClass
	 * @param queryClass
	 * @param resultClass
	 * @param criteriaFilter
	 * @throws ApplicationException 
	 */
	public CriteriaManager(EntityManager entityManager, Class<T> entityClass, Class<?> queryClass, Class<?> resultClass, CriteriaFilterImpl<T> criteriaFilter) throws ApplicationException {
		this.entityManager = entityManager;
		this.queryClass = queryClass;
		this.criteriaFilter = criteriaFilter;
		this.criteriaAssociations = new CriteriaAssociations();
		this.entityClass = entityClass;
		this.resultClass = resultClass;
		createCriteria();
	}
	
	/**
	 * 
	 * @param entityManager
	 * @param entityClass
	 * @param criteriaFilterUpdate
	 * @throws ApplicationException
	 */
	public CriteriaManager(EntityManager entityManager, Class<T> entityClass, CriteriaFilterImpl<T> criteriaFilterUpdate) throws ApplicationException {
		this.entityManager = entityManager;
		this.criteriaFilter = criteriaFilterUpdate;
		this.criteriaAssociations = new CriteriaAssociations();
		this.entityClass = entityClass;
	}
	
	public Root<T> getRootEntry(){
		return this.rootEntry;
	}
	
	public CriteriaBuilder getCriteriaBuilder(){
		return this.criteriaBuilder;
	}
	
	public CriteriaAssociations getCriteriaAssociations(){
		return this.criteriaAssociations;
	}

	@SuppressWarnings("unchecked")
	public <E> CriteriaQuery<E> getCriteriaQuery() throws ApplicationException{
		if(this.criteriaQuery == null){
			this.createCriteria();
		}
		return (CriteriaQuery<E>) this.criteriaQuery;
	}
	
	/**
	 * 
	 * @param queryClass
	 * @param criteriaFilter
	 * @return
	 * @throws ApplicationException 
	 */
	@SuppressWarnings("unchecked")
	private void createCriteria() throws ApplicationException  {
		boolean isTuple = !criteriaFilter.getListSelection().isEmpty() && !ReflectionUtil.isPrimitive(queryClass);
		
		T obj = criteriaFilter.getObjBase();
		
		this.criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<?> query = isTuple ? criteriaBuilder.createTupleQuery() : criteriaBuilder.createQuery(queryClass);
	
		this.rootEntry = query.from(entityClass);
		
		//JOINS //String key : criteriaFilter.getListJoin().keySet()
//		for(Iterator<Entry<String, JoinMapper>> it = criteriaFilter.getListJoin().entrySet().iterator(); it.hasNext(); ){
//			Entry<String, JoinMapper> entryJoin = it.next();
//			if(entryJoin.getValue().getForce()) {
//				if(entryJoin.getKey().contains(".")){
//					From<?, ?> lastFrom = rootEntry;
//					String lastAttribute = entryJoin.getKey();
//					String[] assAux = entryJoin.getKey().split("\\.");
//					lastAttribute = null;
//					for(int i=0; i < assAux.length; i++){
//						lastFrom = configAssociation(assAux[i], lastAttribute, lastFrom);
//						lastAttribute = StringUtils.isBlank(lastAttribute) ? assAux[i] : lastAttribute.concat(".").concat(assAux[i]);
//					}
//					lastAttribute = assAux[assAux.length - 1];
//				}else{
//					configAssociation(entryJoin.getKey(), null, rootEntry);
//				}
//			}
//		}
		this.configForcedAssociations();
		
		//SELECT
		query.select(configSelections(rootEntry));
		
		List<Predicate> predicates = new ArrayList<Predicate>();
		//WHERE 
		if (obj != null) {
			predicates.addAll(configPredicates(obj, null, rootEntry));
		}
		
		predicates.addAll(configComplexPredicates(entityClass, null, rootEntry));
		
		if(!predicates.isEmpty()){
			query.where(predicates.toArray(new Predicate[] {}));
		}
		
		//GROUP BY
		if(criteriaFilter != null && !criteriaFilter.getListGroupBy().isEmpty()){
			List<Expression<?>> listGroupBy = new LinkedList<Expression<?>>();
			
			for(String key : criteriaFilter.getListGroupBy()){
				SimpleEntry<String, From<?, ?>> assocAux = configAssociation(rootEntry, key);
				
				listGroupBy.add(assocAux.getValue().get(assocAux.getKey()));
			}
			
			query.groupBy(listGroupBy);
		}
		
		//ORDER
		if (criteriaFilter != null && !criteriaFilter.getListOrder().isEmpty()) {
			List<Order> listOrder = new LinkedList<Order>();
			
			for(String key : criteriaFilter.getListOrder().keySet()){
				SimpleEntry<String, From<?, ?>> assocAux = configAssociation(rootEntry, key);
				
				br.com.jgon.canary.persistence.CriteriaFilterImpl.Order ord = criteriaFilter.getListOrder().get(key);
				if(ord.equals(br.com.jgon.canary.persistence.CriteriaFilterImpl.Order.ASC)){
					listOrder.add(criteriaBuilder.asc(assocAux.getValue().get(assocAux.getKey())));
				}else{
					listOrder.add(criteriaBuilder.desc(assocAux.getValue().get(assocAux.getKey())));
				}
			}
			
			query.orderBy(listOrder);
		}
	
		criteriaQuery = query;
	}
	
	/**
	 * 
	 * @since 24/06/2019
	 */
	private void configForcedAssociations() {
		for(Iterator<Entry<String, JoinMapper>> it = criteriaFilter.getListJoin().entrySet().iterator(); it.hasNext(); ){
			Entry<String, JoinMapper> entryJoin = it.next();
			if(entryJoin.getValue().getForce()) {
				if(entryJoin.getKey().contains(".")){
					From<?, ?> lastFrom = rootEntry;
					String lastAttribute = entryJoin.getKey();
					String[] assAux = entryJoin.getKey().split("\\.");
					lastAttribute = null;
					for(int i=0; i < assAux.length; i++){
						lastFrom = configAssociation(assAux[i], lastAttribute, lastFrom);
						lastAttribute = StringUtils.isBlank(lastAttribute) ? assAux[i] : lastAttribute.concat(".").concat(assAux[i]);
					}
					lastAttribute = assAux[assAux.length - 1];
				}else{
					configAssociation(entryJoin.getKey(), null, rootEntry);
				}
			}
		}
	}
	
	/**
	 * 
	 * @throws ApplicationException
	 */
	public CriteriaUpdate<T> getCriteriaUpdate() throws ApplicationException{
		T obj = criteriaFilter.getObjBase();
		
		this.configForcedAssociations();
			
		this.criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate<T> update = criteriaBuilder.createCriteriaUpdate(entityClass);
		this.rootEntry = update.from(entityClass);
		
		List<Predicate> predicates = configAllPredicates(obj);
		Map<String, Object> listUpdate = criteriaFilter.getListUpdate();

		for(Iterator<Entry<String, Object>> it = listUpdate.entrySet().iterator(); it.hasNext();){
			Entry<String, Object> attr = it.next();
			update.set(attr.getKey(), attr.getValue());
		}
		
		if(!predicates.isEmpty()){
			update.where(predicates.toArray(new Predicate[] {}));
		}
		
		return update;
	}
	/**
	 * 
	 * @return
	 * @throws ApplicationException
	 */
	public CriteriaDelete<T> getCriteriaDelete() throws ApplicationException{
		T obj = criteriaFilter.getObjBase();
		
		this.configForcedAssociations();
		
		this.criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaDelete<T> delete = criteriaBuilder.createCriteriaDelete(entityClass);
		this.rootEntry = delete.from(entityClass);
		
		List<Predicate> predicates = configAllPredicates(obj);
		
		if(!predicates.isEmpty()){
			delete.where(predicates.toArray(new Predicate[] {}));
		}
		
		return delete;
	}
	/**
	 * 
	 * @param obj
	 * @return
	 * @throws ApplicationException
	 */
	private List<Predicate> configAllPredicates(T obj) throws ApplicationException{
		List<Predicate> predicates = new ArrayList<Predicate>();
		//WHERE 
		if (obj != null) {
			predicates.addAll(configPredicates(obj, null, rootEntry));
		}
		
		predicates.addAll(configComplexPredicates(entityClass, null, rootEntry));
		
		return predicates;
	}
	
	/**
	 * Configura os campos de retorno do SELECT
	 * @param from
	 * @param associations
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Selection configSelections(From<?, ?> from){
		if(criteriaFilter != null && criteriaFilter.getListSelection().size() > 0){
			List<Selection> lista = new ArrayList<Selection>(0);
						
			for(String key: criteriaFilter.getListSelection().keySet()){
				
				//--- ADICIONADO PARA VERIFICAR COLECAO
				Field field = null;
				if(key.contains(".")){
					field = ReflectionUtil.getAttribute(entityClass, key.substring(0, key.indexOf(".")));
				}else{
					field = ReflectionUtil.getAttribute(entityClass, key);
				}

				if(field != null && ReflectionUtil.isCollection(field.getType()) && criteriaFilter.isCollectionSelectionControl()) {
					SimpleEntry<SelectAggregate, String> selectionAux = criteriaFilter.getListSelection().get(key);

					int idxDot = selectionAux.getValue().indexOf(".");
					boolean addCollection = true;
					if(!entityClass.equals(resultClass)){						
						Field fldResult = ReflectionUtil.getAttribute(resultClass, idxDot >= 0 ? selectionAux.getValue().substring(0, idxDot) : selectionAux.getValue());
						addCollection = ReflectionUtil.isCollection(fldResult.getType());
					}
					
					if(addCollection){
						//Verficar se com objeto diferente da entity funciona
						if(!entityClass.equals(resultClass)){
							selectionAux.setValue(idxDot >= 0 ? selectionAux.getValue().substring(idxDot + 1) : selectionAux.getValue());
						}
						
						if(listCollectionRelation.containsKey(field.getName())){
							listCollectionRelation.get(field.getName()).getListSelection().put(key, selectionAux);
						}else{
							Class<?> entityClass = DAOUtil.getCollectionClass(field);
							CriteriaFilterImpl<?> criteriaFilterCollectionRelation = new CriteriaFilterImpl(entityClass);
							
							criteriaFilterCollectionRelation.getListSelection().put(key, selectionAux);
							criteriaFilterCollectionRelation.setCollectionSelectionControl(false);
							listCollectionRelation.put(field.getName(), criteriaFilterCollectionRelation);
						}
						continue;
					}
				}

				// -----------------------------
				
				SimpleEntry<String, From<?, ?>> assocAux = configAssociation(from, key); 
						
				SimpleEntry<SelectAggregate, String> se = criteriaFilter.getListSelection().get(key);

				Path path = null;
				try{
					path = assocAux.getValue().get(assocAux.getKey());
				}catch(IllegalArgumentException e)  {
					logger.error("[configSelections]", MessageSeverity.ERROR, "genericdao-field-not-found", e, assocAux.getValue().getJavaType().getName(), assocAux.getKey());
					throw e;
				}
				
				switch (se.getKey()) {
					case COUNT:
						if(!assocAux.getKey().contains("*")){
							lista.add(criteriaBuilder.count(path).alias(se.getValue()));
						}else{
							lista.add(criteriaBuilder.count(assocAux.getValue()).alias(se.getValue()));
						}
						break;
					case MAX:
						lista.add(criteriaBuilder.max(path).alias(se.getValue()));
						break;
					case MIN:
						lista.add(criteriaBuilder.min(path).alias(se.getValue()));
						break;
					case SUM:
						lista.add(criteriaBuilder.sum(path).alias(se.getValue()));
						break;
					case UPPER:
						lista.add(criteriaBuilder.upper(path).alias(se.getValue()));
						break;
					case LOWER:
						lista.add(criteriaBuilder.lower(path).alias(se.getValue()));
						break;
					case FIELD:
					default:
						lista.add(path.alias(se.getValue()));
						break;
				}
			}

			//ADD O ATRIBUTO ID CASO NAO ESTEJA NO SELECT
			if(from.equals(rootEntry) && !listCollectionRelation.isEmpty()){
				Field fldId =  DAOUtil.getFieldId(entityClass);
				if(!criteriaFilter.getListSelection().containsKey(fldId.getName())){
					if(lista.isEmpty()) {
						criteriaFilter.getListGroupBy().add(fldId.getName());
					}
					
					lista.add(rootEntry.get(fldId.getName()).alias(ALIAS_ATTR_FORCED_ID));//fldId.getName()));
				}
			}
			return criteriaBuilder.tuple(CollectionUtil.convertCollectionToArray(Selection.class, lista));
			
		}else{
			return ((Selection<? extends T>) from);
		}
	}
	
	/**
	 * Verifica os modificadores do campo, STATIC, PUBLIC, PRIVATE, FINAL
	 * @param field
	 * @return
	 */
	private boolean checkField(Field field){
		return field.getModifiers() != (Modifier.STATIC + Modifier.FINAL) 
				&& field.getModifiers() != (Modifier.PRIVATE + Modifier.STATIC + Modifier.FINAL)
				&& field.getModifiers() != (Modifier.PUBLIC + Modifier.STATIC + Modifier.FINAL);
	}
	
	/*private boolean isModifierValid(Field fld){
		boolean valid = Modifier.isStatic(fld.getModifiers())
				|| Modifier.isAbstract(fld.getModifiers())
				|| Modifier.isFinal(fld.getModifiers());
		
		return !valid;
	}
	*/
	/**
	 * Configura os JOINS com base nos campos passados como restrições do SELECT ou WHERE 
	 * @param from
	 * @param key
	 * @return
	 */
	private SimpleEntry<String, From<?, ?>> configAssociation(From<?, ?> from, String key){
		From<?, ?> lastFrom = from;
		String lastAttribute = key;
		if(key.contains(".")){
			String[] assAux = key.split("\\.");
			lastAttribute = null;
			for(int i=0; i < assAux.length - 1; i++){
				lastFrom = configAssociation(assAux[i], lastAttribute, lastFrom);
				lastAttribute = StringUtils.isBlank(lastAttribute) ? assAux[i] : lastAttribute.concat(".").concat(assAux[i]);
			}
			int idx = assAux.length - 1;
			if(lastFrom.get(assAux[idx]).getJavaType().isAnnotationPresent(Entity.class)) {
				lastAttribute = StringUtils.isBlank(lastAttribute) ? assAux[idx] : lastAttribute.concat(".").concat(assAux[idx]);
			}else {
				lastAttribute = assAux[idx];
			}
		} else {
			if(lastFrom.get(key).getJavaType().isAnnotationPresent(Entity.class)) {
				lastFrom = configAssociation(key, null, lastFrom);
			}
		}
		return new SimpleEntry<String, From<?, ?>>(lastAttribute, lastFrom);
	}
	
	/**
	 * Configura os JOINS
	 * @param attribute
	 * @param parentAttribute
	 * @param parentEntry
	 * @return
	 */
	private From<?, ?> configAssociation(String attribute, String parentAttribute, From<?, ?> parentEntry){
		String nomeAs = StringUtils.isBlank(parentAttribute) ? attribute : parentAttribute.concat(".").concat(attribute);
		
		if(criteriaFilter != null && !criteriaAssociations.exists(nomeAs)){
			JoinMapper joinMapper = criteriaFilter.getListJoin().get(nomeAs);
			Join<?, ?> childEntry;
			
			if(joinMapper == null || !joinMapper.getFetch()){
				childEntry = parentEntry.join(attribute, joinMapper == null ? JoinType.INNER : joinMapper.getJoinType());
			}else{
				childEntry = (Join<?, ?>) parentEntry.fetch(attribute, joinMapper == null ? JoinType.INNER : joinMapper.getJoinType());
			}

			criteriaAssociations.add(nomeAs, childEntry);
			
			return childEntry;
		}else{
			return criteriaAssociations.getAssociation(nomeAs);
		}
	}
	
	/**
	 * Configura as condicionais do WHERE
	 * @param obj
	 * @param attributeParent
	 * @param pathEntry
	 * @return
	 * @throws ApplicationException
	 */
	private <E> List<Predicate> configPredicates(E obj, String attributeParent, From<?, ?> pathEntry) throws ApplicationException{
		List<Predicate> predicates = new ArrayList<Predicate>();
		
		Object auxObj;
		String attributeName;
		Boolean isEntityType;
		
		List<Field> listFields = ReflectionUtil.listAttributes(obj);
		
		for (Field field : listFields) {
			// Verifiando se o metodo não é transiente
			if (checkField(field) && !ReflectionUtil.existAnnotation(field, Transient.class)) {
				field.setAccessible(true);
				
				attributeName = StringUtils.isBlank(attributeParent) ? field.getName() : attributeParent.concat(".").concat(field.getName());
				
				Where predicateOperation = criteriaFilter != null ? criteriaFilter.getWhere(attributeName) : Where.EQUAL;
				
				if(predicateOperation == null){
					predicateOperation = Where.EQUAL;
				}
				
				if(predicateOperation == Where.IGNORE){
					continue;
				}
				
				//Where para as listas, somente quando tiver retorno com collection (SELECT EXECUTA PRIMEIRO E SETA A LISTA
				if(!listCollectionRelation.isEmpty() && ReflectionUtil.isCollection(field.getType())){
					for(String k : criteriaFilter.getListWhere().keySet()){
						String checkKey = k.contains(".") ? k.substring(0, k.indexOf(".")) : k;
						if(checkKey.equals(field.getName())){
							if(listCollectionRelation.containsKey(field.getName())){
								listCollectionRelation.get(field.getName()).getListWhere().put(k, criteriaFilter.getListWhere().get(k));
							}
						}
					}
				}
				
				try {
					auxObj = obj != null ? field.get(obj) : null;
				} catch (Exception e) {
					logger.error("[configPredicates]", e);
					throw new ApplicationException(MessageSeverity.ERROR, "error.field.access", field.getName(), obj.getClass().getName());
				}
				isEntityType = field.getType().isAnnotationPresent(Entity.class) || field.getType().isAnnotationPresent(Embeddable.class);
					
				if(predicateOperation.equals(Where.IS_NULL)){
					if(field.getType().isAnnotationPresent(OneToMany.class) 
							|| field.getType().isAnnotationPresent(OneToOne.class) 
							|| field.getType().isAnnotationPresent(ManyToMany.class) 
							|| field.getType().isAnnotationPresent(ManyToOne.class)){
						
						From<?, ?> join = configAssociation(field.getName(), attributeParent, pathEntry);
						applyPredicate(pathEntry, predicates, field.getName(), field.getType(), Where.IS_NULL, join);
						continue;
					}else{
						applyPredicate(pathEntry, predicates, field.getName(), field.getType(), Where.IS_NULL, null);
					}
				}else if(predicateOperation.equals(Where.IS_NOT_NULL)){
					if(ReflectionUtil.existAnnotation(field, OneToMany.class) 
							|| ReflectionUtil.existAnnotation(field, OneToOne.class) 
							|| ReflectionUtil.existAnnotation(field, ManyToMany.class)
							|| ReflectionUtil.existAnnotation(field, ManyToOne.class)){
						
						From<?, ?> join = configAssociation(field.getName(), attributeParent, pathEntry);
						applyPredicate(pathEntry, predicates, field.getName(), field.getType(), Where.IS_NOT_NULL, join);
						continue;
					}else{
						applyPredicate(pathEntry, predicates, field.getName(), field.getType(), Where.IS_NOT_NULL, null);
					}
				}else if(auxObj != null){
					// Somente adiciona um criterio se o conteudo for != vazio
					
					//JOIN
					if (isEntityType){
						From<?, ?> join = configAssociation(field.getName(), attributeParent, pathEntry);
						predicates.addAll(configPredicates(auxObj, attributeName, join));
					//COLLECTION
					} else if (auxObj instanceof Collection){
						if((ReflectionUtil.existAnnotation(field, OneToMany.class) || ReflectionUtil.existAnnotation(field, ManyToMany.class))
								&& ((Collection<?>) auxObj).size() > 0){

							From<?, ?> join = configAssociation(field.getName(), attributeParent, pathEntry);
							Collection<?> col = (Collection<?>) auxObj;
							boolean verificaComplex = true;
							for(Object item : col){
								predicates.addAll(configPredicates(item, attributeName, join));
								if(verificaComplex){
									verificaComplex = false;
									predicates.addAll(configComplexPredicates(item.getClass(), attributeName, join));
								}
							}
						}
					}else if (auxObj instanceof Object[]){
						if((ReflectionUtil.existAnnotation(field, OneToMany.class) || ReflectionUtil.existAnnotation(field, ManyToMany.class))
								&& ((Object[]) auxObj).length > 0){

							From<?, ?> join = configAssociation(field.getName(), attributeParent, pathEntry);
							Object[] col = (Object[]) auxObj;
							boolean verificaComplex = true;
							for(Object item : col){
								predicates.addAll(configPredicates(item, attributeName, join));
								if(verificaComplex){
									verificaComplex = false;
									predicates.addAll(configComplexPredicates(item.getClass(), attributeName, join));
								}
							}
						}
					} else {
						applyPredicate(pathEntry, predicates, field.getName(), field.getType(), predicateOperation, auxObj);
					}
				}
			}
		}
		return predicates;
	}
	
	/**
	 * 
	 * @param pathEntry
	 * @param predicates
	 * @param attributeName
	 * @param attributeClass
	 * @param operation
	 * @param value
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private <E> void applyPredicate(From<?, ?> pathEntry, List<Predicate> predicates, String attributeName, Class<?> attributeClass, Where operation, E value){
		boolean isStringType = attributeClass.equals(String.class);
		String stringValue = null;
		if(isStringType && value != null){
			stringValue = ((String) value).replace("%", "");
		}else if (value != null){
			stringValue = value.toString().replace("%", "");
		}
		
		Expression<?> pathExpression = pathEntry.get(attributeName);
				
		switch (operation) {
			case IS_NULL:
				//if(value == null){
				predicates.add(pathExpression.isNull());
				/*}else{
					predicates.add(criteriaBuilder.isNull(pathExpression);
				}*/
				break;
			case IS_NOT_NULL:
				//	if(value == null){
				predicates.add(pathExpression.isNotNull());
				/*	}else{
					predicates.add(criteriaBuilder.isNotNull(pathExpression));
				}*/
				break;
			case LIKE_EXACT:
				predicates.add(criteriaBuilder.like((Expression<String>) pathExpression, stringValue));
				break;
			case LIKE_NOT_EXACT:
				predicates.add(criteriaBuilder.notLike((Expression<String>) pathExpression, stringValue));
				break;
			case LIKE_MATCH_END:
				predicates.add(criteriaBuilder.like((Expression<String>) pathExpression, "%".concat(stringValue)));
				break;
			case LIKE_MATCH_START:
				predicates.add(criteriaBuilder.like((Expression<String>) pathExpression, stringValue .concat("%")));
				break;
			case LIKE_MATCH_ANYWHERE:
				predicates.add(criteriaBuilder.like((Expression<String>) pathExpression, "%".concat(stringValue).concat("%")));
				break;
			case LIKE_NOT_MATCH_END:
				predicates.add(criteriaBuilder.notLike((Expression<String>) pathExpression, "%".concat(stringValue)));
				break;
			case LIKE_NOT_MATCH_START:
				predicates.add(criteriaBuilder.notLike((Expression<String>) pathExpression, stringValue .concat("%")));
				break;
			case LIKE_NOT_MATCH_ANYWHERE:
				predicates.add(criteriaBuilder.notLike((Expression<String>) pathExpression, "%".concat(stringValue).concat("%")));
				break;
			case ILIKE_EXACT:
				predicates.add(criteriaBuilder.like(criteriaBuilder.upper((Expression<String>) pathExpression), criteriaBuilder.upper(criteriaBuilder.literal(stringValue))));
				break;
			case ILIKE_NOT_EXACT:
				predicates.add(criteriaBuilder.notLike(criteriaBuilder.upper((Expression<String>) pathExpression), criteriaBuilder.upper(criteriaBuilder.literal(stringValue))));
				break;
			case ILIKE_MATCH_END:
				predicates.add(criteriaBuilder.like(criteriaBuilder.upper((Expression<String>) pathExpression), criteriaBuilder.upper(criteriaBuilder.literal("%".concat(stringValue)))));
				break;
			case ILIKE_MATCH_START:
				predicates.add(criteriaBuilder.like(criteriaBuilder.upper((Expression<String>) pathExpression), criteriaBuilder.upper(criteriaBuilder.literal(stringValue.concat("%")))));
				break;
			case ILIKE_MATCH_ANYWHERE:
				predicates.add(criteriaBuilder.like(criteriaBuilder.upper((Expression<String>) pathExpression), criteriaBuilder.upper(criteriaBuilder.literal("%".concat(stringValue).concat("%")))));
				break;
			case ILIKE_NOT_MATCH_END:
				predicates.add(criteriaBuilder.notLike(criteriaBuilder.upper((Expression<String>) pathExpression), criteriaBuilder.upper(criteriaBuilder.literal("%".concat(stringValue)))));
				break;
			case ILIKE_NOT_MATCH_START:
				predicates.add(criteriaBuilder.notLike(criteriaBuilder.upper((Expression<String>) pathExpression), criteriaBuilder.upper(criteriaBuilder.literal(stringValue.concat("%")))));
				break;
			case ILIKE_NOT_MATCH_ANYWHERE:
				predicates.add(criteriaBuilder.notLike(criteriaBuilder.upper((Expression<String>) pathExpression), criteriaBuilder.upper(criteriaBuilder.literal("%".concat(stringValue.concat("%"))))));
				break;
			case IN:
				predicates.add(pathExpression.in((Collection<?>) value));
				break;
			case NOT_IN:
				predicates.add(criteriaBuilder.not(pathExpression.in((Collection<?>) value)));
				break;			
			case NOT_EQUAL:
				if(value instanceof Collection){
					Collection<?> listNe = (Collection<?>) value;
					for(Object wValue: listNe){
						if(isStringType){
							predicates.add(criteriaBuilder.notEqual(criteriaBuilder.upper((Expression<String>) pathExpression), criteriaBuilder.upper(criteriaBuilder.literal(wValue.toString()))));
						}else{
							predicates.add(criteriaBuilder.notEqual(pathExpression, wValue));
						}
					}
				//Permite somente not equal diferente de null
				}else if (value!= null) {
					if(isStringType){
						predicates.add(criteriaBuilder.notEqual(pathExpression, (String) value));
					}else{
						predicates.add(criteriaBuilder.notEqual(pathExpression, value));
					}
				}
				break;
			case BETWEEN:
				if(attributeClass.equals(Integer.class)){
					Integer[] between = (Integer[]) value;
					predicates.add(criteriaBuilder.between(((Expression<Integer>) pathExpression), between[0], between[1]));
				}else if(attributeClass.equals(Long.class)){
					Long[] between = (Long[]) value;
					predicates.add(criteriaBuilder.between(((Expression<Long>) pathExpression), between[0], between[1]));
				}else if(attributeClass.equals(Short.class)){
					Short[] between = (Short[]) value;
					predicates.add(criteriaBuilder.between(((Expression<Short>) pathExpression), between[0], between[1]));
				}else if(attributeClass.equals(Float.class)){
					Float[] between = (Float[]) value;
					predicates.add(criteriaBuilder.between(((Expression<Float>) pathExpression), between[0], between[1]));
				}else if(attributeClass.equals(Double.class)){
					Double[] between = (Double[]) value;
					predicates.add(criteriaBuilder.between(((Expression<Double>) pathExpression), between[0], between[1]));
				}else if(attributeClass.equals(Date.class)){
					Date[] between = (Date[]) value;
					predicates.add(criteriaBuilder.between(((Expression<Date>) pathExpression), between[0], between[1]));
				}else if(attributeClass.equals(LocalDate.class)){
					LocalDate[] between = (LocalDate[]) value;
					predicates.add(criteriaBuilder.between(((Expression<LocalDate>) pathExpression), between[0], between[1]));
				}else if(attributeClass.equals(LocalDateTime.class)){
					LocalDateTime[] between = (LocalDateTime[]) value;
					predicates.add(criteriaBuilder.between(((Expression<LocalDateTime>) pathExpression), between[0], between[1]));
				}else if(attributeClass.equals(BigInteger.class)){
					BigInteger[] between = (BigInteger[]) value;
					predicates.add(criteriaBuilder.between(((Expression<BigInteger>) pathExpression), between[0], between[1]));
				}
				break;
			case LESS_THAN:
				if(value instanceof Date){
					predicates.add(criteriaBuilder.lessThan(((Expression<Date>) pathExpression), (Date) value));
				}else if(value instanceof Expression){
					predicates.add(criteriaBuilder.lessThan((Expression) pathExpression, (Expression) value));
				}else{
					predicates.add(criteriaBuilder.lt(((Expression<Number>) pathExpression), (Number) value));
				}
				break;
			case LESS_THAN_OR_EQUAL_TO:
				if(value instanceof Date){
					predicates.add(criteriaBuilder.lessThanOrEqualTo(((Expression<Date>) pathExpression), (Date) value));
				}else if(value instanceof Expression){
					predicates.add(criteriaBuilder.lessThanOrEqualTo((Expression) pathExpression, (Expression) value));
				}else{
					predicates.add(criteriaBuilder.le(((Expression<Number>) pathExpression), (Number) value));
				}
				break;
			case GREATER_THAN:
				if(value instanceof Date){
					predicates.add(criteriaBuilder.greaterThan((Expression<Date>) pathExpression, (Date) value));
				}else if(value instanceof Expression){
					predicates.add(criteriaBuilder.greaterThan((Expression) pathExpression, (Expression) value));
				}else{
					predicates.add(criteriaBuilder.gt(((Expression<Number>) pathExpression), (Number) value));
				}
				break;
			case GREATER_THAN_OR_EQUAL_TO:
				if(value instanceof Date){
					predicates.add(criteriaBuilder.greaterThanOrEqualTo(((Expression<Date>) pathExpression), (Date) value));
				}else if(value instanceof Expression){
					predicates.add(criteriaBuilder.greaterThanOrEqualTo((Expression) pathExpression, (Expression) value));
				}else{
					predicates.add(criteriaBuilder.ge(((Expression<Number>) pathExpression), (Number) value));
				}
				break;
			case EQUAL_OTHER_FIELD: 
				String oFieldName =  (String) value;
				if(oFieldName.contains(".")){
					From<?, ?> pathAux = configAssociation(oFieldName.substring(0, oFieldName.lastIndexOf(".")) , null, pathEntry);
					predicates.add(criteriaBuilder.equal(pathExpression, pathAux.get(oFieldName.substring(oFieldName.lastIndexOf(".") + 1))));
				}else{
					predicates.add(criteriaBuilder.equal(((Expression<Long>) pathExpression), rootEntry.get(oFieldName)));
				}						
				break;
			case NOT_EQUAL_OTHER_FIELD:
				String oFieldNameN =  (String) value;
				if(oFieldNameN.contains(".")){
					From<?, ?> pathAux = configAssociation(oFieldNameN.substring(0, oFieldNameN.lastIndexOf(".")) , null, pathEntry);
					predicates.add(criteriaBuilder.notEqual(pathExpression, pathAux.get(oFieldNameN.substring(oFieldNameN.lastIndexOf(".") + 1))));
				}else{
					predicates.add(criteriaBuilder.notEqual(pathExpression, rootEntry.get(oFieldNameN)));
				}	
				break;
			case LESS_THAN_OTHER_FIELD:
				String oFieldNameL =  (String) value;
				if(oFieldNameL.contains(".")){
					From<?, ?> pathAux = configAssociation(oFieldNameL.substring(0, oFieldNameL.lastIndexOf(".")) , null, pathEntry);
					predicates.add(criteriaBuilder.lessThan(pathEntry.get(attributeName), pathAux.get(oFieldNameL.substring(oFieldNameL.lastIndexOf(".") + 1))));
				}else{
					predicates.add(criteriaBuilder.lessThan(pathEntry.get(attributeName), rootEntry.get(oFieldNameL)));
				}
				break;
			case LESS_THAN_OR_EQUAL_TO_OTHER_FIELD:
				String oFieldNameLE =  (String) value;
				if(oFieldNameLE.contains(".")){
					From<?, ?> pathAux = configAssociation(oFieldNameLE.substring(0, oFieldNameLE.lastIndexOf(".")) , null, pathEntry);
					predicates.add(criteriaBuilder.lessThanOrEqualTo(pathEntry.get(attributeName), pathAux.get(oFieldNameLE.substring(oFieldNameLE.lastIndexOf(".") + 1))));
				}else{
					predicates.add(criteriaBuilder.lessThanOrEqualTo(pathEntry.get(attributeName), rootEntry.get(oFieldNameLE)));
				}	
				break;
			case GREATER_THAN_OTHER_FIELD:
				String oFieldNameG =  (String) value;
				if(oFieldNameG.contains(".")){
					From<?, ?> pathAux = configAssociation(oFieldNameG.substring(0, oFieldNameG.lastIndexOf(".")) , null, pathEntry);
					predicates.add(criteriaBuilder.greaterThan(pathEntry.get(attributeName), pathAux.get(oFieldNameG.substring(oFieldNameG.lastIndexOf(".") + 1))));
				}else{
					predicates.add(criteriaBuilder.greaterThan(pathEntry.get(attributeName), rootEntry.get(oFieldNameG)));
				}	
				break;
			case GREATER_THAN_OR_EQUAL_TO_OTHER_FIELD:
				String oFieldNameGE =  (String) value;
				if(oFieldNameGE.contains(".")){
					From<?, ?> pathAux = configAssociation(oFieldNameGE.substring(0, oFieldNameGE.lastIndexOf(".")) , null, pathEntry);
					predicates.add(criteriaBuilder.greaterThanOrEqualTo(pathEntry.get(attributeName), pathAux.get(oFieldNameGE.substring(oFieldNameGE.lastIndexOf(".") + 1))));
				}else{
					predicates.add(criteriaBuilder.greaterThanOrEqualTo(pathEntry.get(attributeName), rootEntry.get(oFieldNameGE)));
				}	
				break;
			case EQUAL:
			default:
				if(value instanceof Collection){
					Collection<?> list = (Collection<?>) value;
					for(Object wValue: list){
						if(isStringType){
							predicates.add(criteriaBuilder.equal(criteriaBuilder.upper((Expression<String>) pathExpression), wValue.toString().toUpperCase()));
						}else{
							predicates.add(criteriaBuilder.equal(pathExpression, wValue));
						}
					}
				//Permite somente equal diferente de null
				}else if (value!= null){
					if(isStringType){
						predicates.add(criteriaBuilder.equal(pathExpression, (String) value));
					}else{
						predicates.add(criteriaBuilder.equal(pathExpression, value));
					}
				}
				break;
			}
	}
		
	/**
	 * Configura os campos com clausulas de restricao complexas
	 * @param obj
	 * @param attributeParent
	 * @param pathEntry
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private <E> List<Predicate> configComplexPredicates(Class<E> obj, String attributeParent, From<?, ?> pathEntry){
		List<Predicate> predicates = new ArrayList<Predicate>();
	
		String attributeName;
		Boolean isEntityType;
		Boolean isCollectionEntity;
		
		List<Field> listFields = ReflectionUtil.listAttributes(obj);
		
		for (Field field : listFields) {
			// Verificando se o metodo não é transiente
			if (checkField(field) && !ReflectionUtil.existAnnotation(field, Transient.class)) {
				field.setAccessible(true);
				
				attributeName = StringUtils.isBlank(attributeParent) ? field.getName() : attributeParent.concat(".").concat(field.getName());
			
				isEntityType = field.getType().isAnnotationPresent(Entity.class) || field.getType().isAnnotationPresent(Embeddable.class);
				isCollectionEntity = ReflectionUtil.existAnnotation(field, OneToMany.class) || ReflectionUtil.existAnnotation(field, ManyToMany.class);
				
				//Where para as listas, somente quando tiver retorno com collection (SELECT EXECUTA PRIMEIRO E SETA A LISTA)
				if(!listCollectionRelation.isEmpty() && ReflectionUtil.isCollection(field.getType())){
					for(String k : criteriaFilter.getWhereRestriction().getRestrictions().keySet()){
						String checkKey = k.contains(".") ? k.substring(0, k.indexOf(".")) : k;
						if(checkKey.equals(field.getName())){
							if(listCollectionRelation.containsKey(field.getName())){
								listCollectionRelation.get(field.getName()).getWhereRestriction().addAll(k, criteriaFilter.getWhereRestriction(k));
							}
						}
					}
				}
				
				if(isEntityType || isCollectionEntity){
					boolean contains = false;
					for(Iterator<Entry<String, List<SimpleEntry<Where, ?>>>> it = criteriaFilter.getWhereRestriction().getRestrictions().entrySet().iterator(); it.hasNext(); ){
						Entry<String, List<SimpleEntry<Where, ?>>> entry = it.next();
						
						if(entry.getKey().contains(StringUtils.isNotBlank(attributeParent) ? attributeName.concat(".") : field.getName().concat("."))){
							for(SimpleEntry<Where, ?> sEntry: entry.getValue()) {
								if(entry.getValue() != null || (sEntry.getKey().equals(Where.IS_NULL) || sEntry.getKey().equals(Where.IS_NOT_NULL))) {
									contains = true;
									break;
								}
							}
							if(contains) {
								break;
							}
						}
					}
					if(contains){
						Class<?> type;
											
						if(isCollectionEntity){
							type = DAOUtil.getCollectionClass(field);
						}else{
							type=field.getType();
						}
			
						From<?, ?> join = configAssociation(field.getName(), attributeParent, pathEntry);
						predicates.addAll(configComplexPredicates(type, attributeName, join));
						continue;
					}
				}
				// --------- COMPLEX ----
				List<SimpleEntry<Where, ?>> listWithValues = criteriaFilter != null ? criteriaFilter.getWhereRestriction(attributeName) : null;
				if(listWithValues != null && !listWithValues.isEmpty()){
					for(SimpleEntry<Where, ?> withValues : listWithValues){
						applyPredicate(pathEntry, predicates, field.getName(), field.getType(), withValues.getKey(), withValues.getValue());
					}
				}
			}
		}
		
		return predicates;
	}
	/**
	 * 
	 * @return
	 */
	public Map<String, CriteriaFilterImpl<?>> getListCollectionRelation() {
		return listCollectionRelation;
	}

}

package br.com.jgon.canary.jee.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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

import br.com.jgon.canary.jee.persistence.CriteriaFilterImpl.SelectAggregate;
import br.com.jgon.canary.jee.persistence.CriteriaFilterImpl.Where;
import br.com.jgon.canary.jee.util.CollectionUtil;
import br.com.jgon.canary.jee.util.ReflectionUtil;

/**
 * Classe responsavel por criar a JPA Criteria e realizar as configuracoes e conversoes para realizar as consultas
 * @author jurandir
 *
 * @param <T>
 */
class CriteriaManager<T> {
	
	private EntityManager entityManager;
	private CriteriaFilterImpl<T> criteriaFilter;
	//private Map<Class<?>, Map<String, SimpleEntry<SelectAggregate, String>>> collectionRelationSelect = new LinkedHashMap<Class<?>, Map<String, SimpleEntry<SelectAggregate, String>>>();
	private CriteriaAssociations criteriaAssociations;
	private Map<String, CriteriaFilterImpl<?>> listCollectionRelation = new HashMap<String, CriteriaFilterImpl<?>>(0);
	private Root<T> rootEntry;
	private Class<?> queryClass;
	private CriteriaBuilder criteriaBuilder;
	private CriteriaQuery<?> criteriaQuery;
	private Class<T> entityClass;
	
	public CriteriaManager(EntityManager entityManager, Class<T> entityClass, Class<?> queryClass, CriteriaFilterImpl<T> criteriaFilter) throws Exception {
		this.entityManager = entityManager;
		this.queryClass = queryClass;
		this.criteriaFilter = criteriaFilter;
		this.criteriaAssociations = new CriteriaAssociations();
		this.entityClass = entityClass;
		
		createCriteria();
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
	
	public Map<String, CriteriaFilterImpl<?>> getListCollectionRelation() {
		return listCollectionRelation;
	}

	@SuppressWarnings("unchecked")
	public <E> CriteriaQuery<E> getCriteriaQuery(){
		return (CriteriaQuery<E>) this.criteriaQuery;
	}
	
	/**
	 * 
	 * @param queryClass
	 * @param criteriaFilter
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void createCriteria() throws Exception {
		boolean isTuple = !criteriaFilter.getListSelection().isEmpty() && !ReflectionUtil.isPrimitive(queryClass);
		
		T obj = criteriaFilter.getObjBase();
		
		this.criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<?> query = isTuple ? criteriaBuilder.createTupleQuery() : criteriaBuilder.createQuery(queryClass);
	
		this.rootEntry = query.from(entityClass);
		
		//JOINS
		for(String key : criteriaFilter.getListJoin().keySet()){
			if(key.contains(".")){
				From<?, ?> lastFrom = rootEntry;
				String lastAttribute = key;
				String[] assAux = key.split("\\.");
				lastAttribute = null;
				for(int i=0; i < assAux.length; i++){
					lastFrom = configAssociation(assAux[i], lastAttribute, lastFrom);
					lastAttribute = StringUtils.isBlank(lastAttribute) ? assAux[i] : lastAttribute.concat(".").concat(assAux[i]);
				}
				lastAttribute = assAux[assAux.length - 1];
			}else{
				configAssociation(key, null, rootEntry);
			}
		}
		
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
				
				br.com.jgon.canary.jee.persistence.CriteriaFilterImpl.Order ord = criteriaFilter.getListOrder().get(key);
				if(ord.equals(br.com.jgon.canary.jee.persistence.CriteriaFilterImpl.Order.ASC)){
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
				
				if(field != null && ReflectionUtil.isCollection(field.getType())){
					if(listCollectionRelation.containsKey(field.getName())){
						listCollectionRelation.get(field.getName()).getListSelection().put(key, criteriaFilter.getListSelection().get(key));
					}else{
						CriteriaFilterImpl<?> criteriaFilterCollectionRelation = new CriteriaFilterImpl(field.getType());
						criteriaFilterCollectionRelation.getListSelection().put(key, criteriaFilter.getListSelection().get(key));
						listCollectionRelation.put(field.getName(), criteriaFilterCollectionRelation);
					}
					continue;
				}
				
				// -----------------------------
				
				SimpleEntry<String, From<?, ?>> assocAux = configAssociation(from, key); 
						
				SimpleEntry<SelectAggregate, String> se = criteriaFilter.getListSelection().get(key);

				Path path = null;
				try{
					path = assocAux.getValue().get(assocAux.getKey());
				}catch(IllegalArgumentException e)  {
					continue;
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
			
			lastAttribute = assAux[assAux.length - 1];
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
			SimpleEntry<JoinType, Boolean> joinType = criteriaFilter.getListJoin().get(nomeAs);
			Join<?, ?> childEntry;
			
			if(joinType == null || !joinType.getValue()){
				childEntry = parentEntry.join(attribute, joinType == null ? JoinType.INNER : joinType.getKey());
			}else{
				childEntry = (Join<?, ?>) parentEntry.fetch(attribute, joinType == null ? JoinType.INNER : joinType.getKey());
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
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	private <E> List<Predicate> configPredicates(E obj, String attributeParent, From<?, ?> pathEntry) throws IllegalArgumentException, IllegalAccessException{
		List<Predicate> predicates = new ArrayList<Predicate>();
		
		Object auxObj;
		String attributeName;
		Boolean isStringType;
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
			
				auxObj = obj != null ? field.get(obj) : null;
				
				isStringType = field.getType().equals(String.class);
				isEntityType = field.getType().isAnnotationPresent(Entity.class) || field.getType().isAnnotationPresent(Embeddable.class);
				
				Expression<?> pathExpression = pathEntry.get(field.getName());
				
				if(predicateOperation.equals(Where.IS_NULL)){
					if(ReflectionUtil.existAnnotation(field, OneToMany.class)){
						From<?, ?> join = configAssociation(field.getName(), attributeParent, pathEntry);
						predicates.add(criteriaBuilder.isNull(join));
						continue;
					}else{
						predicates.add(pathExpression.isNull());
					}
				}else if(predicateOperation.equals(Where.IS_NOT_NULL)){
					if(ReflectionUtil.existAnnotation(field, OneToMany.class)){
						From<?, ?> join = configAssociation(field.getName(), attributeParent, pathEntry);
						predicates.add(criteriaBuilder.isNull(join));
						continue;
					}else{
						predicates.add(pathExpression.isNotNull());
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
						
						Expression<String> pathStringUpper=null, pathString = null;
						if(isStringType){
							//STRING - UPPER
							pathString = (Expression<String>) pathExpression;
							pathStringUpper = criteriaBuilder.upper((Expression<String>) pathExpression);
						}
						
						switch (predicateOperation) {
							case LIKE:
								if(isStringType){
									predicates.add(criteriaBuilder.like(pathString, auxObj.toString()));
								}else{
									predicates.add(criteriaBuilder.like(pathExpression.as(String.class), auxObj.toString()));
								}
								break;
							case NOT_LIKE:
								if(isStringType){
									predicates.add(criteriaBuilder.notLike(pathString, auxObj.toString()));
								}else{
									predicates.add(criteriaBuilder.notLike(pathExpression.as(String.class), auxObj.toString()));
								}
								break;
							case LIKE_ANY_BEFORE:
								if(isStringType){
									predicates.add(criteriaBuilder.like(pathString, "%".concat(auxObj.toString())));
								}else{
									predicates.add(criteriaBuilder.like(pathExpression.as(String.class), "%".concat(auxObj.toString())));
								}
								break;
							case LIKE_ANY_AFTER:
								if(isStringType){
									predicates.add(criteriaBuilder.like(pathString, auxObj.toString().concat("%")));
								}else{
									predicates.add(criteriaBuilder.like(pathExpression.as(String.class), auxObj.toString().concat("%")));
								}
								break;
							case LIKE_ANY_BEFORE_AND_AFTER:
								if(isStringType){
									predicates.add(criteriaBuilder.like(pathString, "%".concat(auxObj.toString().concat("%"))));
								}else{
									predicates.add(criteriaBuilder.like(pathExpression.as(String.class), "%".concat(auxObj.toString().concat("%"))));
								}
								break;
							case ILIKE:
								if(isStringType){
									predicates.add(criteriaBuilder.like(pathStringUpper, auxObj.toString().toUpperCase()));
								}else{
									predicates.add(criteriaBuilder.like(criteriaBuilder.upper(pathExpression.as(String.class)), auxObj.toString().toUpperCase()));
								}
								break;
							case NOT_ILIKE:
								if(isStringType){
									predicates.add(criteriaBuilder.notLike(pathStringUpper, auxObj.toString().toUpperCase()));
								}else{
									predicates.add(criteriaBuilder.notLike(criteriaBuilder.upper(pathExpression.as(String.class)), auxObj.toString().toUpperCase()));
								}
								break;
							case ILIKE_ANY_BEFORE:
								if(isStringType){
									predicates.add(criteriaBuilder.like(pathStringUpper, "%".concat(auxObj.toString().toUpperCase())));
								}else{
									predicates.add(criteriaBuilder.like(criteriaBuilder.upper(pathExpression.as(String.class)), "%".concat(auxObj.toString().toUpperCase())));
								}
								break;
							case ILIKE_ANY_AFTER:
								if(isStringType){
									predicates.add(criteriaBuilder.like(pathStringUpper, auxObj.toString().toUpperCase().concat("%")));
								}else{
									predicates.add(criteriaBuilder.like(criteriaBuilder.upper(pathExpression.as(String.class)), auxObj.toString().toUpperCase().concat("%")));
								}
								break;
							case ILIKE_ANY_BEFORE_AND_AFTER:
								if(isStringType){
									predicates.add(criteriaBuilder.like(pathStringUpper, "%".concat(auxObj.toString().toUpperCase().concat("%"))));
								}else{
									predicates.add(criteriaBuilder.like(criteriaBuilder.upper(pathExpression.as(String.class)), "%".concat(auxObj.toString().toUpperCase().concat("%"))));
								}
								break;
							case LESS_THAN_OR_EQUAL_TO:
								if(auxObj instanceof Date){
									Date dt = (Date) auxObj;
									predicates.add(criteriaBuilder.lessThanOrEqualTo((Expression<Date>) pathExpression, dt));
								}else {
									predicates.add(criteriaBuilder.le((Expression<Number>) pathExpression, (Number) auxObj));
								}
								break;
							case LESS_THAN:
								if(auxObj instanceof Date){
									Date dt = (Date) auxObj;
									predicates.add(criteriaBuilder.lessThan((Expression<Date>) pathExpression, dt));
								}else {
									predicates.add(criteriaBuilder.lt((Expression<Number>) pathExpression, (Number) auxObj));
								}
								break;
							case GREATER_THAN_OR_EQUAL_TO:
								if(auxObj instanceof Date){
									Date dt = (Date) auxObj;
									predicates.add(criteriaBuilder.greaterThanOrEqualTo((Expression<Date>) pathExpression, dt));
								}else {
									predicates.add(criteriaBuilder.ge((Expression<Number>) pathExpression, (Number) auxObj));
								}
								break;
							case GREATER_THAN:
								if(auxObj instanceof Date){
									Date dt = (Date) auxObj;
									predicates.add(criteriaBuilder.greaterThan((Expression<Date>) pathExpression, dt));
								}else {
									predicates.add(criteriaBuilder.gt((Expression<Number>) pathExpression, (Number) auxObj));
								}
								break;
							case NOT_EQUAL:
								if(isStringType){
									predicates.add(criteriaBuilder.notEqual(pathString, auxObj.toString()));
								}else{
									predicates.add(criteriaBuilder.notEqual(pathExpression, auxObj));
								}
								break;
							case EQUAL:
							default:
								if(isStringType){
									predicates.add(criteriaBuilder.equal(pathString, auxObj.toString()));
								}else{
									predicates.add(criteriaBuilder.equal(pathExpression, auxObj));
								}
								break;
						}
					}
				}
			}
		}
		
		return predicates;
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
	@SuppressWarnings("unchecked")
	private <E> List<Predicate> configComplexPredicates(Class<E> obj, String attributeParent, From<?, ?> pathEntry) throws IllegalArgumentException, IllegalAccessException{
		List<Predicate> predicates = new ArrayList<Predicate>();
		
		String attributeName;
		Boolean isStringType;
		Boolean isEntityType;
		Boolean isCollectionEntity;
		
		List<Field> listFields = ReflectionUtil.listAttributes(obj);
		
		for (Field field : listFields) {
			// Verifiando se o metodo não é transiente
			if (checkField(field) && !ReflectionUtil.existAnnotation(field, Transient.class)) {
				field.setAccessible(true);
				
				attributeName = StringUtils.isBlank(attributeParent) ? field.getName() : attributeParent.concat(".").concat(field.getName());
				
				isStringType = field.getType().equals(String.class);
				isEntityType = field.getType().isAnnotationPresent(Entity.class) || field.getType().isAnnotationPresent(Embeddable.class);
				isCollectionEntity = ReflectionUtil.existAnnotation(field, OneToMany.class) || ReflectionUtil.existAnnotation(field, ManyToMany.class);
				
				Expression<?> pathExpression = pathEntry.get(field.getName());
				
				if(isEntityType || isCollectionEntity){
					boolean contains = false;
					for(String k : criteriaFilter.getListWhereComplex().keySet()){
						if(k.contains(StringUtils.isNotBlank(attributeParent) ? attributeName.concat(".") : field.getName().concat("."))){
							contains = true;
							break;
						}
					}
					if(contains){
						Class<?> type;
						if(isCollectionEntity 
								&& ((field.isAnnotationPresent(OneToMany.class) && !field.getAnnotation(OneToMany.class).targetEntity().equals(void.class))
								||  (field.isAnnotationPresent(ManyToMany.class) && !field.getAnnotation(ManyToMany.class).targetEntity().equals(void.class))) ){
							
							type=field.isAnnotationPresent(OneToMany.class) ? field.getAnnotation(OneToMany.class).targetEntity() : field.getAnnotation(ManyToMany.class).targetEntity();
						}else{
							type=field.getType();
						}
			
						From<?, ?> join = configAssociation(field.getName(), attributeParent, pathEntry);
						predicates.addAll(configComplexPredicates(type, attributeName, join));
						continue;
					}
				}
				// --------- COMPLEX ----
				SimpleEntry<Where, ?> withValues = criteriaFilter != null ? criteriaFilter.getWhereComplex(attributeName) : null;
				if(withValues != null){
					switch (withValues.getKey()) {
					case IN:
						predicates.add(pathExpression.in(withValues.getValue()));
						break;
					case NOT_IN:
						predicates.add(criteriaBuilder.not(pathExpression.in(withValues.getValue())));
						break;
					case EQUAL:
						List<?> list = (List<?>) withValues.getValue();
						for(Object wValue: list){
							if(isStringType){
								predicates.add(criteriaBuilder.equal(criteriaBuilder.upper((Expression<String>) pathExpression), wValue.toString().toUpperCase()));
							}else{
								predicates.add(criteriaBuilder.equal(pathExpression, wValue));
							}
						}
						break;
					case NOT_EQUAL:
						List<?> listNe = (List<?>) withValues.getValue();
						for(Object wValue: listNe){
							if(isStringType){
								predicates.add(criteriaBuilder.notEqual(criteriaBuilder.upper((Expression<String>) pathExpression), wValue.toString().toUpperCase()));
							}else{
								predicates.add(criteriaBuilder.notEqual(pathExpression, wValue));
							}
						}
						break;
					case BETWEEN:
						if(field.getType().equals(Integer.class)){
							Integer[] between = (Integer[]) withValues.getValue();
							predicates.add(criteriaBuilder.between(pathEntry.get(field.getName()), between[0], between[1]));
						}else if(field.getType().equals(Long.class)){
							Long[] between = (Long[]) withValues.getValue();
							predicates.add(criteriaBuilder.between(pathEntry.get(field.getName()), between[0], between[1]));
						}else if(field.getType().equals(Short.class)){
							Short[] between = (Short[]) withValues.getValue();
							predicates.add(criteriaBuilder.between(pathEntry.get(field.getName()), between[0], between[1]));
						}else if(field.getType().equals(Date.class)){
							Date[] between = (Date[]) withValues.getValue();
							predicates.add(criteriaBuilder.between(pathEntry.get(field.getName()), between[0], between[1]));
						}
						break;
					case LESS_THAN:
						if(withValues.getValue() instanceof Date){
							Date objValue =  (Date) withValues.getValue();
							predicates.add(criteriaBuilder.lessThan(pathEntry.get(field.getName()), objValue));
						}else{
							Number objValue =  (Number) withValues.getValue();
							predicates.add(criteriaBuilder.lt(pathEntry.get(field.getName()), objValue));
						}
						break;
					case LESS_THAN_OR_EQUAL_TO:
						if(withValues.getValue() instanceof Date){
							Date objValue =  (Date) withValues.getValue();
							predicates.add(criteriaBuilder.lessThanOrEqualTo(pathEntry.get(field.getName()), objValue));
						}else{
							Number objValue =  (Number) withValues.getValue();
							predicates.add(criteriaBuilder.le(pathEntry.get(field.getName()), objValue));
						}
						break;
					case GREATER_THAN:
						if(withValues.getValue() instanceof Date){
							Date objValue =  (Date) withValues.getValue();
							predicates.add(criteriaBuilder.greaterThan(pathEntry.get(field.getName()), objValue));
						}else{
							Number objValue =  (Number) withValues.getValue();
							predicates.add(criteriaBuilder.gt(pathEntry.get(field.getName()), objValue));
						}
						break;
					case GREATER_THAN_OR_EQUAL_TO:
						if(withValues.getValue() instanceof Date){
							Date objValue =  (Date) withValues.getValue();
							predicates.add(criteriaBuilder.greaterThanOrEqualTo(pathEntry.get(field.getName()), objValue));
						}else{
							Number objValue =  (Number) withValues.getValue();
							predicates.add(criteriaBuilder.ge(pathEntry.get(field.getName()), objValue));
						}
						break;
					case EQUAL_OTHER_FIELD: 
						String oFieldName =  (String) withValues.getValue();
						if(oFieldName.contains(".")){
							From<?, ?> pathAux = configAssociation(oFieldName.substring(0, oFieldName.lastIndexOf(".")) , null, pathEntry);
							predicates.add(criteriaBuilder.equal(pathEntry.get(field.getName()), pathAux.get(oFieldName.substring(oFieldName.lastIndexOf(".") + 1))));
						}else{
							predicates.add(criteriaBuilder.equal(pathEntry.get(field.getName()), rootEntry.get(oFieldName)));
						}						
						break;
					case NOT_EQUAL_OTHER_FIELD:
						String oFieldNameN =  (String) withValues.getValue();
						if(oFieldNameN.contains(".")){
							From<?, ?> pathAux = configAssociation(oFieldNameN.substring(0, oFieldNameN.lastIndexOf(".")) , null, pathEntry);
							predicates.add(criteriaBuilder.notEqual(pathEntry.get(field.getName()), pathAux.get(oFieldNameN.substring(oFieldNameN.lastIndexOf(".") + 1))));
						}else{
							predicates.add(criteriaBuilder.notEqual(pathEntry.get(field.getName()), rootEntry.get(oFieldNameN)));
						}	
						break;
					case LESS_THAN_OTHER_FIELD:
						String oFieldNameL =  (String) withValues.getValue();
						if(oFieldNameL.contains(".")){
							From<?, ?> pathAux = configAssociation(oFieldNameL.substring(0, oFieldNameL.lastIndexOf(".")) , null, pathEntry);
							predicates.add(criteriaBuilder.lessThan(pathEntry.get(field.getName()), pathAux.get(oFieldNameL.substring(oFieldNameL.lastIndexOf(".") + 1))));
						}else{
							predicates.add(criteriaBuilder.lessThan(pathEntry.get(field.getName()), rootEntry.get(oFieldNameL)));
						}	
						break;
					case LESS_THAN_OR_EQUAL_TO_OTHER_FIELD:
						String oFieldNameLE =  (String) withValues.getValue();
						if(oFieldNameLE.contains(".")){
							From<?, ?> pathAux = configAssociation(oFieldNameLE.substring(0, oFieldNameLE.lastIndexOf(".")) , null, pathEntry);
							predicates.add(criteriaBuilder.lessThanOrEqualTo(pathEntry.get(field.getName()), pathAux.get(oFieldNameLE.substring(oFieldNameLE.lastIndexOf(".") + 1))));
						}else{
							predicates.add(criteriaBuilder.lessThanOrEqualTo(pathEntry.get(field.getName()), rootEntry.get(oFieldNameLE)));
						}	
						break;
					case GREATER_THAN_OTHER_FIELD:
						String oFieldNameG =  (String) withValues.getValue();
						if(oFieldNameG.contains(".")){
							From<?, ?> pathAux = configAssociation(oFieldNameG.substring(0, oFieldNameG.lastIndexOf(".")) , null, pathEntry);
							predicates.add(criteriaBuilder.greaterThan(pathEntry.get(field.getName()), pathAux.get(oFieldNameG.substring(oFieldNameG.lastIndexOf(".") + 1))));
						}else{
							predicates.add(criteriaBuilder.greaterThan(pathEntry.get(field.getName()), rootEntry.get(oFieldNameG)));
						}	
						break;
					case GREATER_THAN_OR_EQUAL_TO_OTHER_FIELD:
						String oFieldNameGE =  (String) withValues.getValue();
						if(oFieldNameGE.contains(".")){
							From<?, ?> pathAux = configAssociation(oFieldNameGE.substring(0, oFieldNameGE.lastIndexOf(".")) , null, pathEntry);
							predicates.add(criteriaBuilder.greaterThanOrEqualTo(pathEntry.get(field.getName()), pathAux.get(oFieldNameGE.substring(oFieldNameGE.lastIndexOf(".") + 1))));
						}else{
							predicates.add(criteriaBuilder.greaterThanOrEqualTo(pathEntry.get(field.getName()), rootEntry.get(oFieldNameGE)));
						}	
						break;
					default:
						break;
					}
				}
			}
		}
		
		return predicates;
	}
	
	/**
	 * Classe responsável por organizar os relacionamentos entre as entidades
	 *
	 */
	public class CriteriaAssociations {
		private Map<String, From<?, ?>> listAssociation = new HashMap<String, From<?, ?>>(0);
		
		public void add(String field, From<?, ?> join){
			if(!exists(field)){
				listAssociation.put(field, join);
			}
		}
		
		public boolean exists(String field){
			return listAssociation.containsKey(field);
		}
		
		public From<?, ?> getAssociation(String field){
			return listAssociation.get(field);
		}
	}

}

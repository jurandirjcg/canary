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
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import br.com.jgon.canary.exception.ApplicationRuntimeException;
import br.com.jgon.canary.persistence.filter.QueryAttribute;
import br.com.jgon.canary.util.MessageFactory;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.util.ReflectionUtil;

/**
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class DAOUtil {

	/**
	 * 
	 * @param entityClass - Class with @Entity annotation 
	 * @return {@link Field}
	 */
	public static Field getFieldId(Class<?> entityClass){
		List<Field> flds = ReflectionUtil.listAttributesByAnnotation(entityClass, Id.class);
		
		if(flds.isEmpty()){
			if( ReflectionUtil.existAnnotation(entityClass, null, EmbeddedId.class)){
				flds = ReflectionUtil.listAttributesByAnnotation(entityClass, EmbeddedId.class);
			}
		}
		
		return flds.isEmpty() ? null :  flds.get(0);
	}
	/**
	 * Retorna o tipo de coleção do attributo
	 * @param fld - Attributo a ser verificado 
	 * @return Classe da colecao
	 */
	public static Class<?> getCollectionClass(Field fld){
		if(!ReflectionUtil.isCollection(fld.getType())){
			throw new ApplicationRuntimeException(MessageSeverity.ERROR, null, MessageFactory.getMessage("daoutil-collection", fld.getName()));
		}
		
		//FIXME Remover
//		Type genericFieldType = fld.getGenericType();
//
//		if(genericFieldType instanceof ParameterizedType){
//		    ParameterizedType aType = (ParameterizedType) genericFieldType;
//		    Type fieldArgTypes = aType.getActualTypeArguments()[0];
//		    if(fieldArgTypes != null){
//		    	return (Class<? extends Collection<?>>) fieldArgTypes;
//		    }
//		}
		
		Class<?> klass = ReflectionUtil.returnParameterType(fld.getGenericType(), 0);
        if(klass != null) {
            return klass;
        }
        
		if(fld.getAnnotation(QueryAttribute.class) != null && !fld.getAnnotation(QueryAttribute.class).collectionTarget().equals(void.class)){
			return fld.getAnnotation(QueryAttribute.class).collectionTarget();
		}else if (fld.getAnnotation(OneToMany.class) != null && !fld.getAnnotation(OneToMany.class).targetEntity().equals(void.class)){
			return fld.getAnnotation(OneToMany.class).targetEntity();
		}else if(fld.getAnnotation(ManyToMany.class) != null && !fld.getAnnotation(ManyToMany.class).targetEntity().equals(void.class)){
			return fld.getAnnotation(ManyToMany.class).targetEntity();
		}else if(fld.getAnnotation(ManyToOne.class) != null && !fld.getAnnotation(ManyToOne.class).targetEntity().equals(void.class)){
			return fld.getAnnotation(ManyToOne.class).targetEntity();
		}
		return fld.getType();
	}
}

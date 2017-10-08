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
package br.com.jgon.canary.jee.persistence;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import br.com.jgon.canary.jee.persistence.filter.QueryAttributeMapper;
import br.com.jgon.canary.jee.util.ReflectionUtil;

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
	@SuppressWarnings("unchecked")
	public static Class<? extends Collection<?>> getCollectionClass(Field fld){
		if(fld.getAnnotation(QueryAttributeMapper.class) != null && !fld.getAnnotation(QueryAttributeMapper.class).collectionTarget().equals(void.class)){
			return (Class<? extends Collection<?>>) fld.getAnnotation(QueryAttributeMapper.class).collectionTarget();
		}else if (fld.getAnnotation(OneToMany.class) != null && !fld.getAnnotation(OneToMany.class).targetEntity().equals(void.class)){
			return fld.getAnnotation(OneToMany.class).targetEntity();
		}else if(fld.getAnnotation(ManyToMany.class) != null && !fld.getAnnotation(ManyToMany.class).targetEntity().equals(void.class)){
			return fld.getAnnotation(ManyToMany.class).targetEntity();
		}else if(fld.getAnnotation(ManyToOne.class) != null && !fld.getAnnotation(ManyToOne.class).targetEntity().equals(void.class)){
			return fld.getAnnotation(ManyToOne.class).targetEntity();
		}
		return (Class<? extends Collection<?>>) fld.getType();
	}
}

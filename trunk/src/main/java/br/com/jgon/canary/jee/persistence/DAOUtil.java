package br.com.jgon.canary.jee.persistence;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import br.com.jgon.canary.jee.persistence.filter.QueryAttributeMapper;
import br.com.jgon.canary.jee.util.ReflectionUtil;

public class DAOUtil {

	public static Field getFieldId(Class<?> entityClass){
		List<Field> flds = ReflectionUtil.listAttributesByAnnotation(entityClass, Id.class);
		
		if(flds.isEmpty()){
			if( ReflectionUtil.existAnnotation(entityClass, null, EmbeddedId.class)){
				flds = ReflectionUtil.listAttributesByAnnotation(entityClass, EmbeddedId.class);
			}
		}
		
		return flds.isEmpty() ? null :  flds.get(0);
	}
	
	public static Class<?> getCollectionClass(Field fld){
		if(fld.getAnnotation(QueryAttributeMapper.class) != null && !fld.getAnnotation(QueryAttributeMapper.class).collectionTarget().equals(void.class)){
			return fld.getAnnotation(QueryAttributeMapper.class).collectionTarget();
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

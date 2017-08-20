package br.com.jgon.canary.jee.persistence;

import java.lang.reflect.Field;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import br.com.jgon.canary.jee.exception.ApplicationException;
import br.com.jgon.canary.jee.persistence.filter.QueryAttributeMapper;
import br.com.jgon.canary.jee.util.ReflectionUtil;

/**
 * Realiza a conversao e comparacao dos campos recebidos para correta utilizacao do JPA Criteria
 * @author jurandir
 *
 */
class SelectMapper extends QueryMapper {

	private static final String expField = "[a-zA-Z]+\\{[a-zA-Z,\\.]+\\}";
	private String fields;
	
	/**
	 * 
	 * @param responseClass
	 * @param fields
	 */
	public SelectMapper(Class<?> responseClass, String fields) {
		super(responseClass);
		this.fields = fields;
	}

	/** 
	 * @return
	 * @throws ApplicationException
	 */
	public List<SimpleEntry<String, String>> getFields() throws ApplicationException{
		List<SimpleEntry<String, String>> ret = getCamposAjustados(fields, expField);

		if(ret == null){
			return getAllFields();
		}else{
			return ret;
		}
	}
	
	/**
	 * Retorna todos os campos do da classe, independente dos parâmetros informados
	 * @return
	 * @throws ApplicationException
	 */
	private List<SimpleEntry<String, String>> getAllFields() throws ApplicationException{
		List<SimpleEntry<String, String>> campos = new ArrayList<SimpleEntry<String, String>>(0);
		QueryAttributeMapper queryMapperAttribute;
		for(Field fld : ReflectionUtil.listAttributes(responseClass)){
			
			if(fld.isAnnotationPresent(Transient.class)){
				continue;
			}
			
			if(fld.isAnnotationPresent(QueryAttributeMapper.class)){
				queryMapperAttribute = fld.getAnnotation(QueryAttributeMapper.class);
				campos.add(new SimpleEntry<String, String>(StringUtils.isNotBlank(queryMapperAttribute.value()) ? queryMapperAttribute.value() : fld.getName(), fld.getName()));
			}else{
				campos.add(new SimpleEntry<String, String>(fld.getName(), fld.getName()));
			}
		}
		
		return campos;
	}
	
}

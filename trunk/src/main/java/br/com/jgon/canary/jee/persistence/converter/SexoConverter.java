package br.com.jgon.canary.jee.persistence.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 
 * @author jurandir
 *
 */
@Converter
public class SexoConverter implements AttributeConverter<Sexo, Integer>{

	@Override
	public Integer convertToDatabaseColumn(Sexo arg0) {
		if(arg0 == null){
			return null;
		}
		return arg0.getChave();
	}

	@Override
	public Sexo convertToEntityAttribute(Integer arg0) {
		if(arg0 == null){
			return null;
		}
		return Sexo.valueOf(arg0);
	}
}

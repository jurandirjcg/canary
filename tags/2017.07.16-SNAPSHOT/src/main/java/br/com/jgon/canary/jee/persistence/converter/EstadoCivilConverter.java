package br.com.jgon.canary.jee.persistence.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 
 * @author jurandir
 *
 */
@Converter
public class EstadoCivilConverter implements AttributeConverter<EstadoCivil, Integer>{

	@Override
	public Integer convertToDatabaseColumn(EstadoCivil arg0) {
		if(arg0 == null){
			return null;
		}
		return arg0.getChave();
	}

	@Override
	public EstadoCivil convertToEntityAttribute(Integer arg0) {
		if(arg0 == null){
			return null;
		}
		return EstadoCivil.valueOf(arg0);
	}
}

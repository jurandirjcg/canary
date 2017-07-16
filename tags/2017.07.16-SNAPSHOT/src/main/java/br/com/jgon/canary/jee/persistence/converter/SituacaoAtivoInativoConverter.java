package br.com.jgon.canary.jee.persistence.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 
 * @author jurandir
 *
 */
@Converter
public class SituacaoAtivoInativoConverter implements AttributeConverter<SituacaoAtivoInativo, Integer>{

	@Override
	public Integer convertToDatabaseColumn(SituacaoAtivoInativo arg0) {
		if(arg0 == null){
			return null;
		}
		return arg0.getChave();
	}

	@Override
	public SituacaoAtivoInativo convertToEntityAttribute(Integer arg0) {
		if(arg0 == null){
			return null;
		}
		return SituacaoAtivoInativo.valueOf(arg0);
	}
}

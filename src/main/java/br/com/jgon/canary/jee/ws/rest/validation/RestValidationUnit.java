package br.com.jgon.canary.jee.ws.rest.validation;

import java.lang.annotation.Annotation;
import java.util.List;

import br.com.jgon.canary.jee.ws.rest.validation.enumerator.ParameterTypeEnum;

/**
 * Unidade de validacao
 * @author jurandir, alexandre
 *
 */
public class RestValidationUnit {

	private ParameterTypeEnum type;
	private String name;
	private String value;
	private List<Annotation> apiAnnotations = null;

	public ParameterTypeEnum getType() {
		return type;
	}

	public void setType(ParameterTypeEnum type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public List<Annotation> getApiAnnotations() {
		return apiAnnotations;
	}

	public void setApiAnnotations(List<Annotation> apiAnnotations) {
		this.apiAnnotations = apiAnnotations;
	}

	public void setType(Class<?> type) {
		this.type = ParameterTypeEnum.valueOf(type);
	}
}

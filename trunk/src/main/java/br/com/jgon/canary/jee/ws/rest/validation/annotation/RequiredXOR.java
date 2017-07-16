package br.com.jgon.canary.jee.ws.rest.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Esta anotaçao permite que um (e apenas um) dos parametros seja fornecido
 * @author alexandre
 * @parameter Lista de String (nomes dos parametros a ser comparados) <br/> <br/>
 * Uso: <pre> @RequiredXOR({"paramName1","paramName2","paramName3"}) @PathParam("foo") String foo {@code}</pre>
 * No caso acima, será obrigatório que um parâmetro (e apenas um) seja válido entre 
 * "foo", "paramName1", "paramName2" e "paramName3"
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredXOR {
	String[] value();
}
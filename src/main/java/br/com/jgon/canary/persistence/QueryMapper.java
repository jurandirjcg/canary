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
import java.lang.reflect.Modifier;
import java.time.temporal.Temporal;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.persistence.filter.QueryAttribute;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.util.ReflectionUtil;

/**
 * Realiza o mapeamento dos atributos da Classe de retorno para construção das consultas de seleção e ordenação dos atributos das entidades
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
abstract class QueryMapper {
	
	protected Class<?> responseClass;
		
	public QueryMapper(Class<?> responseClass){
		this.responseClass = responseClass;
	}
	/**
	 * Retorna o campo referenciado da entidade bem como o alias utilizado no objeto de retorno
	 * @param campos
	 * @param expression
	 * @return
	 * @throws ApplicationException
	 */
	protected List<SimpleEntry<String, String>> getCamposAjustados(String campos, String expression) throws ApplicationException{
		if(StringUtils.isNotBlank(campos)){
			String fieldsAjustados = ajustaCampos(campos.replace(" ", ""), expression);
			if(fieldsAjustados != null){
				String[] fldAux = fieldsAjustados.split(",");
				List<SimpleEntry<String, String>> retorno = new LinkedList<SimpleEntry<String, String>>();

				for(String fNome: fldAux){
					Field fldCheck=null;
					if(fNome.contains(".")){
						int idxStr = 0;
						int idxEnd;
						Class<?> testClass = responseClass;
						do{
							idxEnd = fNome.indexOf(".", idxStr);
							fldCheck = ReflectionUtil.getAttribute(testClass, fNome.substring(idxStr, idxEnd < 0 ? fNome.length() - idxStr : idxEnd));
							if(fldCheck == null){
								break;
							}
							idxStr = fNome.indexOf(".", idxStr + 1);
							testClass = fldCheck.getType();
						}while(idxStr >= 0);
					}
					
					fldCheck = fldCheck != null ? fldCheck :  ReflectionUtil.getAttribute(responseClass, fNome);
					if(fldCheck != null && !isPrimitive(fldCheck.getType())){
						retorno.addAll(verificaCampoObject(fldCheck, fNome.contains(".") ? fNome.substring(0, fNome.lastIndexOf(".")) : fNome));
					}else{
						SimpleEntry<String, String> campoVerificado = verificaCampo(responseClass, fNome); 
						if(campoVerificado != null){
							retorno.add(campoVerificado);
						}else{
							throw new ApplicationException(MessageSeverity.ERROR, "query-mapper.field-not-found", fNome);
						}
					}
				}
				return retorno.stream().distinct().collect(Collectors.toList());
			}
		}
		return null;
	}
	/**
	 * 
	 * @param fldCheck
	 * @param fNome
	 * @return
	 * @throws ApplicationException
	 */
	private List<SimpleEntry<String, String>> verificaCampoObject(Field fldCheck, String fNome) throws ApplicationException{
		QueryAttribute queryMapperAttribute = null;
		if(fldCheck.isAnnotationPresent(QueryAttribute.class)){
			queryMapperAttribute = fldCheck.getAnnotation(QueryAttribute.class);
		}
		
		List<SimpleEntry<String, String>> retorno = new ArrayList<SimpleEntry<String, String>>(0);
		for(Field fldCheckAux : ReflectionUtil.listAttributes(fldCheck.getType())){
			if(isModifierValid(fldCheckAux)){
				SimpleEntry<String, String> campoVerificado = verificaCampo(fldCheck.getType(), fldCheckAux.getName()); 
				if(campoVerificado != null){
					if(queryMapperAttribute != null && StringUtils.isNotBlank(queryMapperAttribute.value())){
						retorno.add(new SimpleEntry<String, String>(queryMapperAttribute.value().concat(".").concat(campoVerificado.getKey()), queryMapperAttribute.value().concat(".").concat(campoVerificado.getValue())));
					}else{
						retorno.add(new SimpleEntry<String, String>(fNome.concat(".").concat(campoVerificado.getKey()), fNome.concat(".").concat(campoVerificado.getValue())));
					}
				}else{
					throw new ApplicationException(MessageSeverity.ERROR, "query-mapper.field-not-found", fNome);
				}
			}
		}
		return retorno;
	}
	
	
	/**
	 * 
	 * @param klass
	 * @return
	 */
	private boolean isPrimitive(Class<?> klass){
		return ReflectionUtil.isPrimitive(klass)
				|| klass.equals(Date.class)
				|| klass.equals(Calendar.class)
				|| klass.equals(Temporal.class)
				|| klass.isEnum();
	}
	
	/**
	 * 
	 * @param fld
	 * @return
	 */
	private boolean isModifierValid(Field fld){
		boolean valid = Modifier.isStatic(fld.getModifiers())
				|| Modifier.isAbstract(fld.getModifiers())
				|| Modifier.isFinal(fld.getModifiers());
		
		return !valid;
	}
	/**
	 * Verifica as propriedades do campo e configura os filhos se existir
	 * @param klass
	 * @param fieldName
	 * @return
	 * @throws ApplicationException 
	 */
	private SimpleEntry<String, String> verificaCampo(Class<?> klass, String fieldName) throws ApplicationException{
		String partField;
		
		boolean multiLevel = false;
		if(fieldName.contains(".")){
			multiLevel = true;
			partField = fieldName.substring(0, fieldName.indexOf("."));
		}else{
			partField = fieldName.contains(":") ? fieldName.substring(0, fieldName.indexOf(":")) : fieldName;
		}
		
		StringBuilder sb = new StringBuilder();
		List<Field> fieldClass = ReflectionUtil.listAttributes(klass);

		for(Field fl : fieldClass){
			/*if(fl.isAnnotationPresent(QueryMapperIgnore.class) || fl.isAnnotationPresent(Transient.class)){
				continue;
			}*/
			if(fl.isAnnotationPresent(Transient.class)){
				continue;
			}
			
			QueryAttribute queryMapperAttribute = null;
			if(fl.isAnnotationPresent(QueryAttribute.class)){
				queryMapperAttribute = fl.getAnnotation(QueryAttribute.class);
			}
			
			boolean isEnum = fl.getClass().isEnum() || (queryMapperAttribute != null && queryMapperAttribute.isEnum());
			
			if(fl.getName().equals(partField)){
				boolean add = true;
				SimpleEntry<String, String> campoMultiLevel = null;
				if(!isEnum && multiLevel){
					Class<?> attrType = null;

					if(ReflectionUtil.isCollection(fl.getType())) {
						attrType = DAOUtil.getCollectionClass(fl);
					}else {
						attrType = fl.getType();
					}
					/*if(fl.isAnnotationPresent(OneToMany.class) && !fl.getAnnotation(OneToMany.class).targetEntity().equals(void.class)){
						attrType = fl.getAnnotation(OneToMany.class).targetEntity();
					}else if(fl.isAnnotationPresent(ManyToMany.class) && !fl.getAnnotation(ManyToMany.class).targetEntity().equals(void.class)){
						attrType = fl.getAnnotation(ManyToMany.class).targetEntity();
					}else if(queryMapperAttribute != null && !queryMapperAttribute.collectionTarget().equals(void.class)){
						attrType = queryMapperAttribute.collectionTarget();
					}else{
						attrType = fl.getType();
					}
					*/
					
					if(ReflectionUtil.isCollection(attrType)){
						throw new ApplicationException(MessageSeverity.ERROR, "query-mapper.field-collection-not-definied", klass.getName() + "." + fl.getName());
					}
										
					campoMultiLevel = verificaCampo(attrType, fieldName.substring(fieldName.indexOf(".") + 1));
					add = campoMultiLevel != null;
				}
				
				if(add){
					if(queryMapperAttribute != null && StringUtils.isNotBlank(queryMapperAttribute.value())){
						sb.append(queryMapperAttribute.value());
					}else{
						sb.append(partField);
					}
					
					if(!isEnum){
						if(campoMultiLevel != null){
							sb.append(".").append(campoMultiLevel.getKey());
						}
						if(fieldName.contains(":")){
							//sb.append(fieldName.substring(fieldName.indexOf(":")));
							return new SimpleEntry<String, String>(sb.toString(), fieldName.substring(fieldName.indexOf(":") + 1));
						}
					}
				}
				return new SimpleEntry<String, String>(sb.length() == 0 ? fieldName : sb.toString() , fieldName);
			}
		}

		return null;
	}
	
	/**
	 * Ajusta os campos, caso tenham vindo com o caractere especial "{"
	 * Ex: pessoa{id,nome} - retorna pessoa.id,pessoa.nome
	 * @param ini
	 * @param exp
	 * @return
	 */
	public String ajustaCampos(String ini, String exp){
		Pattern pattern = Pattern.compile(exp);
		Matcher m = pattern.matcher(ini);
		String aux = ini;
		
		do{
			m.reset();
			aux = configuraRelacoes(m);
			m = pattern.matcher(aux);
		}while(m.find());
		
		return aux;
	}
	
	/**
	 * Metodo auxiliar que realiza a quebra dos caracteres "{" e ","
	 * @param matcher
	 * @return
	 */
	private String configuraRelacoes(Matcher matcher){
		StringBuffer sb = new StringBuffer();
		while(matcher.find()){
			String ss = matcher.group();
			String objAttr = ss.substring(0, ss.indexOf("{"));
			String paramAttr = ss.substring(ss.indexOf("{") + 1).replace("}", "");
			String[] sAux = paramAttr.split(",");

			StringBuilder builder = new StringBuilder();
			for(int i=0; i< sAux.length; i++){
				if(i > 0){
					builder.append(",");
				}
				builder.append(objAttr).append(".").append(sAux[i]);
				
			}
			matcher.appendReplacement(sb, builder.toString());
		}

		matcher.appendTail(sb);
		return sb.toString();
	}
}

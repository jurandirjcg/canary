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
package br.com.jgon.canary.ws.rest.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.jgon.canary.exception.ApplicationException;
import br.com.jgon.canary.persistence.DAOUtil;
import br.com.jgon.canary.util.MessageSeverity;
import br.com.jgon.canary.util.ReflectionUtil;

/**
 * Configura os parametros recebidos da requisicao
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
@RequestScoped
public class WSMapper {
		
	private static final String expSort = "[a-zA-Z]+\\{(([-+a-zA-Z\\.]+(:(asc|desc))?),*)+\\}";
	private static final String expFields = "[a-zA-Z]+\\{[a-zA-Z,\\.]+\\}";
	public static final String RESPONSE_ALL = "RESPONSE_ALL"; 
	
	private Logger logger = LoggerFactory.getLogger(WSMapper.class);
	
	public WSMapper(){
		
	}
	
	public List<String> getSort(Class<?> responseClass, String sort) throws ApplicationException{
		return getCamposAjustados(responseClass, expSort, sort.replace("/", ".").replace("(", "{").replace(")", "}"));
	}
	
	public List<String> getFields(Class<?> responseClass, String fields) throws ApplicationException{
		return getCamposAjustados(responseClass, expFields, fields.replace("/*", "").replace("/", ".").replace("(", "{").replace(")", "}"));
	}
	/**
	 * 
	 * @param responseClass
	 * @param expression
	 * @param campos
	 * @return
	 * @throws ApplicationException
	 */
	protected List<String> getCamposAjustados(Class<?> responseClass, String expression, String campos) throws ApplicationException{
		boolean sortAux = expression.equals(expSort);
		
		if(StringUtils.isNotBlank(campos)){
			String fieldsAjustados = ajustaCampos(campos.replace(" ", ""), expression);
			if(fieldsAjustados != null){
				String[] fldAux = fieldsAjustados.split(",");
				List<String> retorno = new LinkedList<String>();

				boolean subObject = false;
				for(String fNome: fldAux){
					Field fldCheck=null;
					if(fNome.contains(".")){
						int idxStr = -1;
						int idxEnd;
						Class<?> testClass = responseClass;
						do{
							idxStr++;
							idxEnd = fNome.indexOf(".", idxStr);
							fldCheck = ReflectionUtil.getAttribute(testClass, fNome.substring(idxStr, idxEnd < 0 ? fNome.length() : idxEnd));
							if(fldCheck == null){
								subObject = false;
								break;
							}
							subObject = true;
							idxStr = fNome.indexOf(".", idxStr);
							testClass = fldCheck.getType();
						}while(idxStr >= 0);
					}
					
					fldCheck = fldCheck != null ? fldCheck :  ReflectionUtil.getAttribute(responseClass, fNome.contains(":") ? fNome.substring(0, fNome.indexOf(":")) : fNome);
					
					WSAttribute wsMapperAttribute = null;
					if(fldCheck != null && fldCheck.isAnnotationPresent(WSAttribute.class)){
						wsMapperAttribute = fldCheck.getAnnotation(WSAttribute.class);
					}
					
					if(fldCheck != null && !isPrimitive(fldCheck.getType()) && (wsMapperAttribute == null || !wsMapperAttribute.fixed())){
						String fldNomeTemp = wsMapperAttribute != null && StringUtils.isNotBlank(wsMapperAttribute.value()) ? wsMapperAttribute.value() : fNome;
						retorno.addAll(verificaCampoObject(fldCheck, !subObject && fNome.contains(".") ? fldNomeTemp + fNome.substring(fNome.length() - 1, fNome.lastIndexOf(".")) : fldNomeTemp));
					}else{
						String campoVerificado = verificaCampo(responseClass, fNome); 
						if(StringUtils.isNotBlank(campoVerificado)){
							if(sortAux){
								retorno.add(campoVerificado.contains(":desc") ? campoVerificado : campoVerificado.concat(":asc"));
							}else{
								retorno.add(campoVerificado);
							}
						}else{
							ApplicationException ae = new ApplicationException(MessageSeverity.ERROR, "query-mapper.field-not-found", fNome);
							logger.error("[getCamposAjustados]", ae);
							throw ae;
						}
					}
				}
				return retorno.stream().distinct().collect(Collectors.toList());
			}
		}
		return Collections.emptyList();
	}
	
	/**
	 * 
	 * @param fldCheck
	 * @param fNome
	 * @param sortAux
	 * @return
	 * @throws ApplicationException
	 */
	private List<String> verificaCampoObject(Field fldCheck, String fNome) throws ApplicationException{

		WSAttribute wsMapperAttribute = null;
		if(fldCheck.isAnnotationPresent(WSAttribute.class)){
			wsMapperAttribute = fldCheck.getAnnotation(WSAttribute.class);
		}
		
		Class<?> type;
		if(!ReflectionUtil.isCollection(fldCheck.getType())){
			type = fldCheck.getType();
		}else {
			type = wsMapperAttribute == null || wsMapperAttribute.collectionType().equals(void.class) ? DAOUtil.getCollectionClass(fldCheck) : wsMapperAttribute.collectionType();
		}
		
		List<String> retorno = new ArrayList<String>(0);
		for(Field fldCheckAux : ReflectionUtil.listAttributes(type)){
			if(isModifierValid(fldCheckAux)){
				WSAttribute wsMapperAttributeFieldAux = null;
				if(fldCheckAux.isAnnotationPresent(WSAttribute.class)){
					wsMapperAttributeFieldAux = fldCheckAux.getAnnotation(WSAttribute.class);
					
					if(!wsMapperAttributeFieldAux.autoInclude()) {
						continue;
					}
				}
				
				if(!isPrimitive(fldCheckAux.getType()) && (wsMapperAttributeFieldAux == null || !wsMapperAttributeFieldAux.fixed())){
					retorno.addAll(verificaCampoObject(fldCheckAux, fNome.concat(".").concat(fldCheckAux.getName())));
				}else {
					String campoVerificado = verificaCampo(type, fldCheckAux.getName()); 
					if(StringUtils.isNotBlank(campoVerificado)){
						if(wsMapperAttribute != null && StringUtils.isNotBlank(wsMapperAttribute.value())){
							String prefix = fNome.contains(".") ? fNome.substring(0, fNome.lastIndexOf('.') + 1) : "";
							retorno.add(prefix.concat(wsMapperAttribute.value()).concat(".").concat(campoVerificado));
						}else{
							retorno.add(fNome.concat(".").concat(campoVerificado));
						}
					}else{
						ApplicationException ae =new ApplicationException(MessageSeverity.ERROR, "query-mapper.field-not-found", fNome);
						logger.error("[verificaCampoObject]", ae);
						throw ae;
					}
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
	 * 
	 * @param klass
	 * @param fieldName
	 * @return
	 */
	private String verificaCampo(Class<?> klass, String fieldName){
		String partField;
		
		boolean multiLevel = false;
		if(fieldName.contains(".")){
			multiLevel = true;
			partField = fieldName.substring(0, fieldName.indexOf("."));
		}else{
			partField = fieldName.contains(":") ? fieldName.substring(0, fieldName.indexOf(":")) : fieldName;
		}
		
		boolean sort = false;
		if(partField.indexOf("-") == 0){
			sort = true;
			partField = partField.substring(1, partField.length());
		}else if(partField.indexOf("+") == 0){
			partField = partField.substring(1, partField.length());
		}
		
		StringBuilder sb = new StringBuilder();
		List<Field> fieldClass = ReflectionUtil.listAttributes(klass);

		for(Field fl : fieldClass){
			if(fl.isAnnotationPresent(WSTransient.class)){
				continue;
			}
			WSAttribute wsMapperAttribute = null;
			if(fl.isAnnotationPresent(WSAttribute.class)){
				wsMapperAttribute = fl.getAnnotation(WSAttribute.class);
			}
			
			//boolean isEnum = fl.getClass().isEnum() || (wsMapperAttribute != null && wsMapperAttribute.isEnum());
			
			if(fl.getName().equals(partField)){
				boolean add = true;
				String campoMultiLevel = null;
				if(/*!isEnum && */multiLevel){
					Class<?> attrType = wsMapperAttribute != null && !wsMapperAttribute.collectionType().equals(void.class) ? wsMapperAttribute.collectionType() : fl.getType(); 
					
					if(ReflectionUtil.isCollection(attrType)) {
						attrType = DAOUtil.getCollectionClass(fl);
					}
					
					campoMultiLevel = verificaCampo(attrType, fieldName.substring(fieldName.indexOf(".") + 1));
					add = StringUtils.isNotBlank(campoMultiLevel);
				}
				
				if(add){
					if(wsMapperAttribute != null && StringUtils.isNotBlank(wsMapperAttribute.value())){
						sb.append(wsMapperAttribute.value());
					}else{
						sb.append(partField);
					}
					
					//Para a execucao pois e um campo enum
					/*if(isEnum){
						break;
					}*/
					
					if(StringUtils.isNotBlank(campoMultiLevel)){
						sb.append(".").append(campoMultiLevel);
					}else if(fieldName.contains(":desc")){
						sb.append(":desc");
					}
					
					if(sort){
						sb.append(":desc");
					}
				}
				break;
			}
		}
	
		return sb.toString();
	}
	/**
	 * 
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
	 * 
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

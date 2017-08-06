package br.com.jgon.canary.jee.ws.rest.util;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;

import org.apache.commons.lang3.StringUtils;

import br.com.jgon.canary.jee.exception.ApplicationException;
import br.com.jgon.canary.jee.exception.MessageSeverity;
import br.com.jgon.canary.jee.util.ReflectionUtil;

/**
 * Configura os parametros recebidos da requisicao
 * @author jurandir
 *
 */
@RequestScoped
public class WSMapper {
		
	private static final String expSort = "[a-zA-Z]+\\{(([-+a-zA-Z\\.]+(:(asc|desc))?),*)+\\}";
	private static final String expFields = "[a-zA-Z]+\\{[a-zA-Z,\\.]+\\}";
	
	public WSMapper(){
		
	}
	
	public List<String> getSort(Class<?> responseClass, String sort) throws ApplicationException{
		return getCamposAjustados(responseClass, expSort, sort);
	}
	
	public List<String> getFields(Class<?> responseClass, String fields) throws ApplicationException{
		return getCamposAjustados(responseClass, expFields, fields);
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

				for(String fNome: fldAux){
					String campoVerificado = verificaCampo(responseClass, fNome); 
					if(StringUtils.isNotBlank(campoVerificado)){
						if(sortAux){
							retorno.add(campoVerificado.contains(":desc") ? campoVerificado : campoVerificado.concat(":asc"));
						}else{
							retorno.add(campoVerificado);
						}
					}else{
						throw new ApplicationException(MessageSeverity.ERROR, "query-mapper.field-not-found", fNome);
					}
				}
				return retorno.stream().distinct().collect(Collectors.toList());
			}
		}
		return Collections.emptyList();
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
		List<Field> fieldClass = ReflectionUtil.listAttributes(klass, ReflectionUtil.REG_EXP_TODOS);

		for(Field fl : fieldClass){
			if(fl.isAnnotationPresent(WSTransient.class)){
				continue;
			}
			WSAttributeMapper wsMapperAttribute = null;
			if(fl.isAnnotationPresent(WSAttributeMapper.class)){
				wsMapperAttribute = fl.getAnnotation(WSAttributeMapper.class);
			}
			
			boolean isEnum = fl.getClass().isEnum() || (wsMapperAttribute != null && wsMapperAttribute.isEnum());
			
			if(fl.getName().equals(partField)){
				boolean add = true;
				String campoMultiLevel = null;
				if(!isEnum && multiLevel){
					Class<?> attrType = wsMapperAttribute != null && !wsMapperAttribute.valueType().equals(void.class) ? wsMapperAttribute.valueType() : fl.getType(); 
					
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
					if(isEnum){
						break;
					}
					
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
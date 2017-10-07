package br.com.jgon.canary.jee.persistence;

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

import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import br.com.jgon.canary.jee.exception.ApplicationException;
import br.com.jgon.canary.jee.exception.MessageSeverity;
import br.com.jgon.canary.jee.persistence.filter.QueryAttributeMapper;
import br.com.jgon.canary.jee.util.ReflectionUtil;

/**
 * Realiza o mapeamento dos atributos da Classe de retorno para construção das consultas de seleção e ordenação dos atributos das entidades
 * 
 * @author jurandir
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
	//Campo, Alias
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
	
	private List<SimpleEntry<String, String>> verificaCampoObject(Field fldCheck, String fNome) throws ApplicationException{
		QueryAttributeMapper queryMapperAttribute = null;
		if(fldCheck.isAnnotationPresent(QueryAttributeMapper.class)){
			queryMapperAttribute = fldCheck.getAnnotation(QueryAttributeMapper.class);
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
		boolean valid = ReflectionUtil.checkModifier(fld, Modifier.STATIC)
				|| ReflectionUtil.checkModifier(fld, Modifier.ABSTRACT)
				|| ReflectionUtil.checkModifier(fld, Modifier.FINAL);
		
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
			
			QueryAttributeMapper queryMapperAttribute = null;
			if(fl.isAnnotationPresent(QueryAttributeMapper.class)){
				queryMapperAttribute = fl.getAnnotation(QueryAttributeMapper.class);
			}
			
			boolean isEnum = fl.getClass().isEnum() || (queryMapperAttribute != null && queryMapperAttribute.isEnum());
			
			if(fl.getName().equals(partField)){
				boolean add = true;
				SimpleEntry<String, String> campoMultiLevel = null;
				if(!isEnum && multiLevel){
					Class<?> attrType = null;

					if(fl.isAnnotationPresent(OneToMany.class) && !fl.getAnnotation(OneToMany.class).targetEntity().equals(void.class)){
						attrType = fl.getAnnotation(OneToMany.class).targetEntity();
					}else if(fl.isAnnotationPresent(ManyToMany.class) && !fl.getAnnotation(ManyToMany.class).targetEntity().equals(void.class)){
						attrType = fl.getAnnotation(ManyToMany.class).targetEntity();
					}else if(queryMapperAttribute != null && !queryMapperAttribute.collectionTarget().equals(void.class)){
						attrType = queryMapperAttribute.collectionTarget();
					}else{
						attrType = fl.getType();
					}
					
					if(ReflectionUtil.isCollection(klass)){
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
	
	/*public List<WSAttributeJoin> listJoin(String ini){
		List<WSAttributeJoin> joins = new ArrayList<WSAttributeJoin>(0);
		
		String exp = "[a-zA-Z]+:((join)|(inner)|(left)|(right))(_fetch)?";
		Pattern pattern = Pattern.compile(exp);
		Matcher m = pattern.matcher(ini);
		
		//"profissao:left_fetch"
		
		WSAttributeJoin join;
		String[] aj;
		while(m.find()){
			aj = m.group().split(":");
			join = new WSAttributeJoin();
			join.setAttribute(aj[0]);	
						
			if(aj[1].equalsIgnoreCase("right")){
				join.setJoinType(JoinType.RIGHT);
				join.setFetch(false);
			}else if(aj[1].equalsIgnoreCase("right_fetch")){
				join.setJoinType(JoinType.RIGHT);
				join.setFetch(true);
			}else if(aj[1].equalsIgnoreCase("left")){
				join.setJoinType(JoinType.LEFT);
				join.setFetch(false);
			}else if(aj[1].equalsIgnoreCase("left_fetch")){
				join.setJoinType(JoinType.LEFT);
				join.setFetch(true);
			}else if(aj[1].equalsIgnoreCase("inner")){
				join.setJoinType(JoinType.INNER);
				join.setFetch(false);
			}else if(aj[1].equalsIgnoreCase("inner_fetch")){
				join.setJoinType(JoinType.INNER);
				join.setFetch(true);
			}
		}
		
		return joins;
	}*/
	
	/*public static void main(String[] args){
		QueryMapper a = new QueryMapper(ResponsePaciente.class);
		try {
			System.out.println(a.getOrder("nome:asc,nomeMae:desc,profissao{id:asc,descricao:desc},id:asc").toString());
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//a.ajustaCampos("nome:asc,idade:desc,profissao{nome:asc,id:desc,medico{nome:asc,idade:desc},teste:asc},id:asc", expOrder));
	}*/
	
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

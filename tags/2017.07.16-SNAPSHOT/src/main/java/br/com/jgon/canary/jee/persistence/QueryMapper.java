package br.com.jgon.canary.jee.persistence;

import java.lang.reflect.Field;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
					SimpleEntry<String, String> campoVerificado = verificaCampo(responseClass, fNome); 
					if(campoVerificado != null){
						retorno.add(campoVerificado);
					}else{
						throw new ApplicationException(MessageSeverity.ERROR, "query-mapper.field-not-found", fNome);
					}
				}
				return retorno.stream().distinct().collect(Collectors.toList());
			}
		}
		return null;
	}
	/**
	 * Verifica as propriedades do campo e configura os filhos se existir
	 * @param klass
	 * @param fieldName
	 * @return
	 */
	private SimpleEntry<String, String> verificaCampo(Class<?> klass, String fieldName){
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
					Class<?> attrType = queryMapperAttribute != null && !queryMapperAttribute.valueType().equals(void.class) ? queryMapperAttribute.valueType() : fl.getType();
					
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
				return new SimpleEntry<String, String>(sb.toString(), fieldName);
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

package br.com.jgon.canary.jee.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author jurandir
 *
 */
public class ReflectionUtil {
	
	/**
	 * Expressão para retorno de metodos get
	 */
	public static final String REG_EXP_METODOS_GET = "(get){1}[A-Z]+.*";
	/**
	 * Expressão para retorno de metodos set
	 */
	public static final String REG_EXP_METODOS_SET = "(set){1}[A-Z]+.*";
	/**
	 * Expressão para retorno de todos os atributos ou metodos
	 */
	public static final String REG_EXP_TODOS	= ".*";
	
	/**
	 * Lista os métodos get de um objeto passado como parâmetro.
	 * @param Object - objeto a ser verificado
	 * @return List - lista de métodos da classe
	 */
	public static List<Method> listMethodsGet(Object o) {
		return listMethods(o, REG_EXP_METODOS_GET);
    }
        
	/**
	 * Lista os métodos set de um objeto passado como parametro
	 * @param Object - objeto a ser verificado
	 * @return List - lista de matodos da classe
	 */
	public static List<Method> listMethodsSet(Object o) {
		return listMethods(o, REG_EXP_METODOS_SET);
	}

	/**
	 * Lista os todos os metodos declarados de um objeto passado como parametro
	 * @param Object - objeto a ser verificado
	 * @return List - lista de metodos da classe
	 */
	public static List<Method> listMethods(Object o) {
		return listMethods(o, REG_EXP_TODOS);
	}
	
	/**
	 * Devolve uma lista de metodos declarados pelo programador em um objeto
	 * @param o - objeto a ser verificado
	 * @param pattern - uma expressao regular dos metodos que desejam ser listados
	 * @return List - lista de metodos da classe Method
	 * <br>
	 * Exemplo de utilizacao<br>
	 * List l = new ArrayList();<br>
	 * l = Util.listMethods(new Object(), ".*");  // todos os metodos da classe Object<br>
	 * for (int i = 0; i < l.size(); i++)<br>
	 * 		System.out.println(((Method)l.get(i)).getName());<br>
	 * 
	 */
	public static List<Method> listMethods(Object o, String pattern) {
		return listMethods(o.getClass(), pattern);
	}

	/**
	 * Retorna os metodos da classe
	 * @param klass
	 * @param pattern
	 * @return
	 */
	public static List<Method> listMethods(Class<?> klass, String pattern){
		List<Method> l = new ArrayList<Method>();
		
		Method[] m = klass.getDeclaredMethods();
   		    
		for (int i = 0; i < m.length; i++) {
			if (m[i].getName().matches(pattern))
				l.add(m[i]);
		}
			
		if(klass.getSuperclass() != null && !klass.getSuperclass().equals(Object.class) && !klass.getSuperclass().equals(Class.class)){
			l.addAll(listMethods(klass.getSuperclass(), pattern));
		}
		
		return l;
	}
	/**
	 * Lista os todos os atributos declarados de um objeto passado como parametro.
	 * @param Object - objeto a ser verificado
	 * @return List - lista de atributos da classe Method 
	 */
	
	public static List<Field> listAttributes(Object o) {
		return listAttributes(o, REG_EXP_TODOS);
	}
	
	/**
	 * Retorna lista de atributos com tipo declarado no parametro
	 * @param klass - Classe de pesquisa
	 * @param attributeType - parametro da pesquisa. Ex String.class
	 * @return
	 */
	public static List<Field> listAttributes(Class<?> klass, Class<?> attributeType){
		List<Field> listFields = new ArrayList<Field>();
		
		for(Field field : listAttributes(klass, REG_EXP_TODOS)){
			if(field.getType().equals(attributeType)){
				listFields.add(field);
			}
		}
		
		return listFields;
	}
	/**
	 * Devolve uma lista de atributos declarados em um objeto.
	 * @param o - objeto a ser verificado
	 * @param pattern - uma expressao regular dos atributos que desejam ser listados
	 * @return
	 */
	public static List<Field> listAttributes(Object o, String pattern) {		
		return listAttributes(o.getClass(), pattern);
	}
	
	/**
	 * 
	 * @param fld
	 * @param modifier
	 * @return
	 */
	public static boolean checkAttributeModifier(Field fld, int modifier){
		return fld.getModifiers() == modifier;
	}
	
	/**
	 * Retorna lista de atributos que contenham o tipo especificado e a annotation
	 * @param klass - Classe de pesquisa
	 * @param fieldKlass - Tipo do atributo pesquisado
	 * @param annotationClass - Tipo de annotation a ser localizada
	 * @return
	 */
	public static List<Field> listAttributes(Class<?> klass, Class<?> fieldClass, Class<? extends Annotation> annotationClass){
		List<Field> fields = listAttributes(klass, REG_EXP_TODOS);
		for(Iterator<Field> itField = fields.iterator(); itField.hasNext();){
			Field fld = itField.next();
			if(!fld.getType().equals(fieldClass) || getAnnotationFieldOrMethod(fld, annotationClass) == null)
				itField.remove();
		}
	
		return fields;
	}
	
	/**
	 * 
	 * @param klass
	 * @return
	 */
	public static List<Field> listAttributes(Class<?> klass){
		return listAttributes(klass, REG_EXP_TODOS);
	}
	/**
	 * Retorna os atributos da classe
	 * @param klass - Classe de pesquisa
	 * @param pattern - uma expressao regular dos atributos que desejam ser listados
	 * @return
	 */
	public static List<Field> listAttributes(Class<?> klass, String pattern){
		List<Field> l = new ArrayList<Field>(0);
		
		Field[] f = klass.getDeclaredFields();
 		
		Class<?> classObj = klass;
		while (classObj.getSuperclass() != null){
			classObj = classObj.getSuperclass();
			f = ArrayUtils.addAll(f, classObj.getDeclaredFields());
		}
 		
		for (int i = 0; i < f.length; i++) {
			if (f[i].getName().matches(pattern))
				l.add(f[i]);
		}
   
		return l;
	}

	/**
	 * Verifica se determinado field existe na classe. Case Sensitive
	 * @param klass
	 * @param name
	 * @return
	 */
	public static boolean existAttribute(Class<?> klass, String name){
		List<Field> listField = listAttributes(klass, name);
		boolean exist = false;
		for(Field fl : listField)
			if(fl.getName().equals(name)){
				exist = true;
				break;
			}
				
		return exist;
	}
	/**
	 * Verifica se o metodo existe na classe
	 * @param klass
	 * @param name
	 * @return
	 */
	public static boolean existMethod(Class<?> klass, String name){
		return existMethod(klass, name, REG_EXP_TODOS);
	}
	
	/**
	 * Verifica se o metodo get existe na classe
	 * @param klass
	 * @param name
	 * @return
	 */
	public static boolean existMethodGet(Class<?> klass, String name){
		return existMethod(klass, name, REG_EXP_METODOS_GET);
	}
	
	/**
	 * Verifica se o metodo set existe na classe
	 * @param klass
	 * @param name
	 * @return
	 */
	public static boolean existMethodSet(Class<?> klass, String name){
		return existMethod(klass, name, REG_EXP_METODOS_SET);
	}
	
	/**
	 * Verifica se o metodo existe na classe, informando pattern
	 * @param klass
	 * @param name
	 * @param pattern
	 * @return
	 */
	public static boolean existMethod(Class<?> klass, String name, String pattern){
		List<Method> listMethod = listMethods(klass, pattern);
		boolean exist = false;
		for(Method mt : listMethod){
			if(mt.getName().equals(name)){
				exist = true;
				break;
			}
		}
		
		return exist;
	}
	/**
	 * Verifica se existe Annotation no atributo ou metodo Get
	 * @param klass - Classe de pesquisa
	 * @param atributo
	 * @param annotationClass - annotations a ser pesquisada
	 * @return
	 */
	public static boolean existAnnotation(Class<?> klass, String atributoNome, Class<? extends Annotation> annotationClass){
		List<Field> listFields = listAttributes(klass, atributoNome);
		for(Field atributo : listFields)
			if(getAnnotationFieldOrMethod(atributo, annotationClass) != null)
					return true;
		
		return false;
	}
	
	/**
	 * Verifica se existe Annotation no atributo ou metodo Get
	 * @param klass - Classe de pesquisa
	 * @param atributo
	 * @param annotationClass - annotations a ser pesquisada
	 * @return
	 */
	public static boolean existAnnotation(Field atributo, Class<? extends Annotation> annotationClass){
		if(getAnnotationFieldOrMethod(atributo, annotationClass) != null)
					return true;
		
		return false;
	}
	/**
	 * Retorna annotation tanto do field quanto do method do tipo GET, null caso nao encontre
	 * @param <T>
	 * @param klass
	 * @param field
	 * @param annotationClass
	 * @return
	 */
	public static <T extends Annotation> T getAnnotationFieldOrMethod(Field field, Class<T> annotationClass){

		if(field.isAnnotationPresent(annotationClass))
			return field.getAnnotation(annotationClass);
		else
			try {
			 Method method = getMethodTypeGet(field.getDeclaringClass(), field.getName());
			if(method.isAnnotationPresent(annotationClass))
				return method.getAnnotation(annotationClass);
			
			} catch (Exception e) {
				return null;
			}
		return null;
	}
	
	/**
	 * 
	 * @param klass
	 * @param methodName
	 * @param annotationClass
	 * @return
	 */
	public static <T extends Annotation> T getAnnotationMethod(Class<?> klass, String methodName, Class<T> annotationClass){
		try {
			for(Method m : klass.getDeclaredMethods()){
				if(m.getName().equals(methodName) && m.isAnnotationPresent(annotationClass)){
					return m.getAnnotation(annotationClass);
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	/**
	 * 
	 * @param klass
	 * @param methodName
	 * @return
	 */
	public static Method getMethodByName(Class<?> klass, String methodName){
		try {
			for(Method m : klass.getDeclaredMethods()){
				if(m.getName().equals(methodName)){
					return m;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	
	/**
	 * Retorna lista de atributos que contenham a(s) annotation(s) pesquisa tambem realizada nos metodos
	 * @param klass
	 * @param annotationClass
	 * @return
	 */
	@SafeVarargs
	public static List<Field> fieldsContainAnnotation(Class<?> klass, Class<? extends Annotation>... annotationClass){
		List<Field> listField = new ArrayList<Field>(0);
		for(Field field : listAttributes(klass, REG_EXP_TODOS)){
			for(int i=0; i < annotationClass.length; i++)
				if(getAnnotationFieldOrMethod(field, annotationClass[i]) != null)
					listField.add(field);
		}
		
		return listField;
	}
	/**
	 * Invoca um metodo getXxxxx() para o objeto e o atributo informado.
	 * @param obj - o objeto cujo metodo get sera chamado
	 * @param atributo - o nome do atributo que identifica qual getter chamar
	 * @param objParams - os parametros que devem ser passados para a invocar do metodo - geralmente null
	 * @return
	 * @throws Exception
	 */
	public static Object invocaGet(Object obj, String atributo, Object[] objParams) throws Exception {
		Method method = getMethodTypeGet(obj.getClass(), atributo, objParams);
		if(!method.isAccessible()){
			method.setAccessible(true);
		}
		if(objParams == null){
			return method.invoke(obj, (Object[])null);
		}
		
		return method.invoke(obj, objParams);
	}
	
	/**
	 * Invoca o metodo
	 * @param klass
	 * @param metodo
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Object invocaStaticMetodo(Class<?> klass, String metodo, Object... params) throws Exception{
		Method[] m = klass.getDeclaredMethods();
		    
		Method mt = null;
		for (int i = 0; i < m.length; i++) {
			if (m[i].getName().equals(metodo)){
				mt = m[i];
				break;
			}
		}
		
		if(mt == null)
			throw new Exception("Método não encontrado");
		
		return mt.invoke(klass, params);
	}
	
	/**
	 * 
	 * @param obj
	 * @param metodo
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Object invocaMetodo(Object obj, String metodo, Object... params) throws Exception{
		List<Method> lstMethods = listMethods(obj, metodo);
		
		if(lstMethods.isEmpty())
			throw new Exception("Consulta não retornou resultados, operação não pode ser executada.");
		if(lstMethods.size() > 1)
			throw new Exception("Consulta retornou mais de um método, operação não pode ser executada.");
		
		Method method = lstMethods.get(0);
		if(!method.isAccessible()){
			method.setAccessible(true);
		}
		return method.invoke(obj, params);
	}
	/**
	 * Retorna o metodo do tipo get do atributo
	 * @param klass
	 * @param atributoNome
	 * @param objParams
	 * @return
	 */
	
	public static Method getMethodTypeGet(Class<?> klass, String atributeName, Object... objParams) throws Exception{
		String nomeGetter = "get" + StringUtils.capitalize(atributeName);
		Method method = null;
		Class<?>[] tipos = null;
		try {
        	if (objParams != null){
        		int tam = 0;
        		for (int i=0; i< objParams.length; i++) {
        			if(objParams[i] != null)
        				tam++;
        		}
        		if(tam > 0){
        			tipos = new Class[tam];
        			for (int i=0; i< objParams.length; i++) {
        				if(objParams[i] != null)
        					tipos[i] = objParams[i].getClass();
        			}
        		}
        	}

        	do{
        		method = klass.getMethod(nomeGetter, tipos);
        		klass = klass.getSuperclass();
        	}while(klass.getSuperclass() != null && method == null);

        	return method;

        } catch (Exception e) {
        	throw e;
        }
	}
	
	/**
	 * 
	 * @param klass
	 * @param methodName
	 * @param objParams
	 * @return
	 * @throws Exception
	 */
	public static Method getMethod(Class<?> klass, String methodName, Object... objParams) throws Exception{
		Method method = null;
		Class<?>[] tipos = null;
		try {
        	if (objParams != null){
        		int tam = 0;
        		for (int i=0; i< objParams.length; i++) {
        			if(objParams[i] != null)
        				tam++;
        		}
        		if(tam > 0){
        			tipos = new Class[tam];
        			for (int i=0; i< objParams.length; i++) {
        				if(objParams[i] != null)
        					tipos[i] = objParams[i].getClass();
        			}
        		}
        	}

        	do{
        		method = klass.getMethod(methodName, tipos);
        		klass = klass.getSuperclass();
        	}while(klass.getSuperclass() != null && method == null);

        	return method;

        } catch (Exception e) {
        	throw e;
        }
	}

	/**
	 * Invoca um metodo getXxxxx() para o objeto sem passar parametros.
	 * @param obj - o objeto cujo metodo get sera chamado
	 * @param atributo - o nome do atributo que identifica qual getter chamar
	 * @return
	 * @throws Exception
	 */
	public static Object invocaGet(Object obj, String atributo) throws Exception {
	    return invocaGet(obj, atributo, null);
	}

	/**
	 * Invoca o metodo setXxxx para o objeto e atributo informados.
	 * @param obj - o objeto a invocar o metodo set
	 * @param atributo - o atributo cujo metodo set será invocado
	 * @param objParams - um array de objetos com os parametros do setter
	 * @return
	 * @throws Exception
	 */
	public static Object invocaSet(Object obj, String atributo, Object... objParams) throws Exception {
		String nomeSetter = "set" + StringUtils.capitalize(atributo);

		boolean paramIsNull = true;
		
		if(objParams != null){
			for(Object oAux : objParams)
				if(oAux != null)
					oAux = false;
		}
		
		if (paramIsNull)
			//return obj.getClass().getMethod(nomeSetter, (Class[])null).invoke(obj, (Object[])null);
			return invocaMetodo(obj, nomeSetter, objParams);

		Class<?>[] tipos = new Class[objParams.length];
		for (int i=0; i< objParams.length; i++) {
			tipos[i] = objParams[i].getClass();
		}
		Method method = obj.getClass().getMethod(nomeSetter, tipos);
		
		if(!method.isAccessible()){
			method.setAccessible(true);
		}
		return method.invoke(obj, objParams);
	}
	
	
	/**
	 * Recupera o valor de uma propriedade de determinado objeto utilizando Reflection.
	 * 
	 * <b> Exemplo: </b><br/>
	 * Object obj = Reflexao.invocaGetA(objetoContainer, "nomepropriedade.nomenested.maisumnome");
	 * Ir� retornar:
	 * objetoContainer.getNomepropriedade().getNomenested().getMaisumnome();
	 * 
	 * @param obj
	 * @param nomePropriedade - nome da propriedade cujo valor deve ser recuperado
	 * @return
	 * @throws Exception
	 */
	
	public static Object invocaGetAninhado(Object obj, String nomePropriedade) throws Exception {
		Object result = null;
		Class<?> classe;
		
		// se objeto for nulo, valor é nulo
		if (obj != null)
			classe = obj.getClass();
		else
			return null;
		
		String metodo = new String();
		String propriedade = new String(nomePropriedade);
		
		// verificando necessidade de chamadas recursivas
		boolean rec = false;                        
		if (propriedade.indexOf('.') > 0) { // se encontrou ponto vai precisar de recursao
			rec = true;
			metodo = propriedade.substring(0, propriedade.indexOf('.')); // parte 1: nome da propriedade que guarda o objeto
			propriedade = propriedade.substring(propriedade.indexOf('.') + 1); // parte 2: o que vem depois do ponto
		} else 
			metodo = nomePropriedade;            
		
		// procedimento normal
		metodo = "get" + metodo.substring(0,1).toUpperCase() + metodo.substring(1); // getter
		Method met = classe.getMethod(metodo, (Class[])null);
		
		if(!met.isAccessible()){
			met.setAccessible(true);
		}
		
		result = met.invoke(obj, (Object[])null);
		
		return rec ? invocaGetAninhado(result, propriedade) : result;
	}
	
	/**
	 * Recupera a classe utilizando Reflection.
	 * 
	 * @param className - String contendo o nome completo da Classe incluindo o pacote
	 * @return
	 * @throws ClassNotFoundException
	 */
	
	public static Class<?> classForName(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException cnfe) {
            try {
                Thread thread = Thread.currentThread();
                ClassLoader threadClassLoader = thread.getContextClassLoader();
                return Class.forName(className, false, threadClassLoader);
            } catch (ClassNotFoundException ex) {
                throw ex;
            }
        }
    }
	
	/**
	 * Responsavel por atribuir os novos valores ao campos
	 * se for do tipo String, Double, Float, Integer ou primitivo retorna a junção ou soma dos valores
	 * @param field - campo que será 
	 * @param obj
	 * @param obj2
	 * @return
	 */
	private static Object newValueField(Field field, Object obj, Object obj2) throws Exception{
		Object val1 = null, val2 = null;

		val1 = invocaGet(obj, field.getName());
		val2 = invocaGet(obj2, field.getName());
		
		if(val1 == null)
			return val2;
		else if (val2 == null)
			return val1;
		
		if(field.getType().equals (String.class)){
			return ((String) val1) + ((String)val2);
		}else if(field.getType().equals(Long.class) || field.getType().equals(long.class)){			
			return ((Long) val1) + ((Long)val2);
		}else if(field.getType().equals(Float.class) || field.getType().equals(float.class)){
			return ((Float) val1) + ((Float)val2);
		}else if(field.getType().equals(Double.class) || field.getType().equals(double.class)){
			return ((Double) val1) + ((Double)val2);
		}else if(field.getType().equals(Integer.class) || field.getType().equals(int.class)){
			return ((Integer) val1) + ((Integer)val2);
		}else{
			return val2;
		}
	}
	
	/**
	 * Soma ou junta os valores dos campos do objeto
	 * @param obj
	 * @param obj2
	 * @param fieldOff - campos que nao serao concatenados
	 */
	
	public static <T>  T concatenaObject(T obj, T obj2, String... fieldOff) throws Exception{
		if(obj == null)
			obj = obj2;
		else if (obj2 != null)
			for(Field field :listAttributes(obj.getClass(), REG_EXP_TODOS)){				
				boolean concatena = true;
				for(int i = 0; i < fieldOff.length ; i++){
					if(field.getName().equalsIgnoreCase(fieldOff[i])){
						concatena = false;
						break;
					}
				}
				if(concatena){
					Object novoValor = newValueField(field, obj, obj2);
					if(novoValor != null)
						invocaSet(obj, field.getName(), novoValor);
				}
			}
		
		return obj;
	}
	
	/**
	 * Instancia novo objeto com os valores do Map
	 * @param obj
	 * @param fieldValue - Map<String, Object[]> - Map<atributo, valores>
	 * @return
	 * @throws Exception 
	 */
	public static Object intanciaObject(Object obj, Map<String, Object> fieldValue) throws Exception{
		for(String atributo : fieldValue.keySet()){
			if(fieldValue.get(atributo) == null)
				continue;
			
			try {
				invocaSet(obj, atributo, fieldValue.get(atributo));
			} catch (Exception e) {
				if(fieldValue.get(atributo).getClass().equals(Long.class) || fieldValue.get(atributo).getClass().equals(long.class)){			
					invocaSet(obj, atributo, Long.parseLong(String.valueOf(fieldValue.get(atributo))));
				}else if(fieldValue.get(atributo).getClass().equals(Float.class) || fieldValue.get(atributo).getClass().equals(float.class)){
					invocaSet(obj, atributo, Float.parseFloat(String.valueOf(fieldValue.get(atributo))));
				}else if(fieldValue.get(atributo).getClass().equals(Double.class) || fieldValue.get(atributo).getClass().equals(double.class)){
					invocaSet(obj, atributo, Double.parseDouble(String.valueOf(fieldValue.get(atributo))));
				}else if(fieldValue.get(atributo).getClass().equals(Integer.class) || fieldValue.get(atributo).getClass().equals(int.class))
					invocaSet(obj, atributo, Integer.parseInt(String.valueOf(fieldValue.get(atributo))));
			}
		}
		return obj;
	}
	
	/**
	 * Compara as classes, inclusive interface
	 * @param klass1
	 * @param klass2
	 * @return
	 */
	public static boolean compareClass(Class<?> class1, Class<?> class2){
		if(class1.equals(class2))
			return true;
		
		Class<?> klassAux = class1;
		while (klassAux.getSuperclass() != null) {
			klassAux = klassAux.getSuperclass();
			if(klassAux.equals(class2))
				return true;
		}
		
		for(Class<?> cAux : class1.getInterfaces()){
			if(cAux.equals(class2))
				return true;
		}
		
		return false;
	}
	/**
	 * 
	 * @param obj
	 * @param field
	 * @param value
	 */
	public static void setFieldValue(Object obj, Field field, Object value){
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param obj
	 * @param fieldName
	 * @param value
	 */
	public static void setFieldValue(Object obj, String fieldName, Object value){
		try {
			List<Field> lFld = listAttributes(obj.getClass(), fieldName);
			if(lFld.size() == 1){
				Field fld = lFld.get(0);
				fld.setAccessible(true);
				fld.set(obj, value);
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * 
	 * @param klass
	 * @param name
	 * @return
	 */
	public static Field attributeByName(Class<?> klass, String name){
		List<Field> lFld = listAttributes(klass, name);
		
		for(Field fld: lFld){
			if(fld.getName().equals(name)){
				return fld;
			}
		}
		
		return null;
	}
	/**
	 * 
	 * @param klass
	 * @param annotationClass
	 * @return
	 */
	public static List<Field> listAttributeByAnnotation(Class<?> klass, Class<? extends Annotation> annotationClass){
		List<Field> fields = listAttributes(klass, REG_EXP_TODOS);
		for(Iterator<Field> itField = fields.iterator(); itField.hasNext();){
			Field fld = itField.next();
			if(getAnnotationFieldOrMethod(fld, annotationClass) == null){
				itField.remove();
			}
		}
		return fields;
	}
	
	/**
	 * Verifica se a classe e um wrapper para o tipo primitivo
	 * @param klass
	 * @return
	 */
	public static boolean isPrimitive(Class<?> klass){
		return klass.isPrimitive()
				|| klass.equals(java.lang.String.class)
				|| klass.equals(Long.class)
				|| klass.equals(Integer.class)
				|| klass.equals(Short.class)
				|| klass.equals(Boolean.class)
				|| klass.equals(Character.class);
	}
	
	public static boolean isCollection(Class<?> klass){
		return ArrayUtils.contains(klass.getInterfaces(), Collection.class) || ArrayUtils.contains(klass.getInterfaces(), List.class) || ArrayUtils.contains(klass.getInterfaces(), Set.class);
	}
	/**
	 * 
	 * @param obj
	 * @param field
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAttributteValue(Object obj, Field field){
		field.setAccessible(true);
		try {
			return (T) field.get(obj);
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * Retorna valor do atributo, verifica o objeto recursivamente.
	 * Ex: campo.campo1.campo2 - retorna o valor do campo2 da hierarquia de objetos
	 * Retorna null se null ou não encontrado 
	 * @param obj
	 * @param field
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAttributteValue(Object obj, String field){
		try{
			if(obj == null || StringUtils.isBlank(field)){
				return null;
			}
			if(field.contains(".")){
				String[] listFields = field.split("\\.");
				Object objValue = obj;
				for(String fld: listFields){
					objValue = invocaGet(objValue, fld);
					if(objValue == null){
						return null;
					}
				}
				return (T) objValue;
			}else{
				return (T) invocaGet(obj, field);
			}
		}catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Ref: https://rationaleemotions.wordpress.com/2016/05/27/changing-annotation-values-at-runtime/
	 * @param klass
	 * @param annotationToAlter
	 * @param newAnnotation
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void changeAnnotation(Class<?> klass, Class<? extends Annotation> annotationToAlter, Annotation newAnnotation) throws Exception{
	
		//JAVA 7
		/*
		 Field annotations = Class.class.getDeclaredField("annotations");
         annotations.setAccessible(true);
         Map<Class<? extends Annotation>, Annotation> map =
             (Map<Class<? extends Annotation>, Annotation>) annotations.get(clazzToLookFor);
         map.put(annotationToAlter, annotationValue);
		 */
//InvocationHandler ih = Proxy.getInvocationHandler(linkResource);
		//JAVA 8
		Method method = Class.class.getDeclaredMethod("annotationData");
		method.setAccessible(true);
		Object annotationData = method.invoke(klass);
		Field annotations = annotationData.getClass().getDeclaredField("declaredAnnotations");
		annotations.setAccessible(true);

		Map<Class<? extends Annotation>, Annotation> map = (Map<Class<? extends Annotation>, Annotation>) annotations.get(annotationData);
		map.put(annotationToAlter, newAnnotation);
	}
	
	/**
	 * Seta o valor para nulo, com cascade (quando encontra ".")
	 * @param obj
	 * @param attributeName
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static void setValueToNullCascade(Object obj, String... listAttributeName) throws IllegalArgumentException, IllegalAccessException{
		if(obj != null){
			List<Field> listField = listAttributes(obj);
			for(String attrName : listAttributeName){
				if(attrName.contains(".")){
					setValueToNullCascade(getAttributteValue(obj, attrName.substring(0, attrName.indexOf("."))), attrName.substring(attrName.indexOf(".") + 1));
				}else{
					for(Field fld : listField){
						if(fld.getName().equals(attrName)){
							fld.setAccessible(true);
							fld.set(obj, null);
						}
					}
				}
			}
		}
	}
}
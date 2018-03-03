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
package br.com.jgon.canary.util;

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
 * Biblioteca de Reflection, baseada na bilioteca de reflection do Framework Pinhão Paraná (http://www.frameworkpinhao.pr.gov.br)
 * 
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0
 *
 */
public class ReflectionUtil {
	
	private static final String REG_EXP_METHOD_GET = "(get){1}[A-Z]+.*";
	private static final String REG_EXP_METHOD_SET = "(set){1}[A-Z]+.*";
	private static final String REG_EXP_ALL	= ".*";
	
	/**
	 * Lista os métodos get de um objeto passado como parâmetro.
	 * @param o - objeto a ser verificado
	 * @return List - lista de métodos da classe
	 */
	public static List<Method> listMethodsGet(Object o) {
		return listMethods(o, REG_EXP_METHOD_GET);
    }
        
	/**
	 * Lista os métodos set de um objeto passado como parametro
	 * @param o - objeto a ser verificado
	 * @return List - lista de matodos da classe
	 */
	public static List<Method> listMethodsSet(Object o) {
		return listMethods(o, REG_EXP_METHOD_SET);
	}

	/**
	 * Lista os todos os metodos declarados de um objeto passado como parametro
	 * @param o - objeto a ser verificado
	 * @return List - lista de metodos da classe
	 */
	public static List<Method> listMethods(Object o) {
		return listMethods(o, REG_EXP_ALL);
	}
	
	/**
	 * 
	 * @param o
	 * @param pattern
	 * @return
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
	 * @param o - objeto a ser verificado
	 * @return List - lista de atributos da classe Method 
	 */
	public static List<Field> listAttributes(Object o) {
		return listAttributes(o, REG_EXP_ALL);
	}
	
	/**
	 * Retorna lista de atributos com tipo declarado no parametro
	 * @param klass - Classe de pesquisa
	 * @param attributeType - parametro da pesquisa. Ex String.class
	 * @return
	 */
	public static List<Field> listAttributes(Class<?> klass, Class<?> attributeType){
		List<Field> listFields = new ArrayList<Field>();
		
		for(Field field : listAttributes(klass, REG_EXP_ALL)){
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
	public static boolean checkModifier(Field fld, int modifier){
		return fld.getModifiers() == modifier;
	}
	
	/**
	 * Retorna lista de atributos que contenham o tipo especificado e a annotation
	 * @param klass - Classe de pesquisa
	 * @param fieldClass - Tipo do atributo pesquisado
	 * @param annotationClass - Tipo de annotation a ser localizada
	 * @return
	 */
	@SafeVarargs
	public static List<Field> listAttributes(Class<?> klass, Class<?> fieldClass, Class<? extends Annotation>... annotationClass){
		List<Field> fields = listAttributes(klass, REG_EXP_ALL);
		for(Iterator<Field> itField = fields.iterator(); itField.hasNext();){
			Field fld = itField.next();
			if(fieldClass != null && !fld.getType().equals(fieldClass)){
				itField.remove();
			}else{
				for(Class<? extends Annotation> a : annotationClass){
					if(getAnnotation(fld, a) == null){
						itField.remove();
					}
				}
			}
		}
		return fields;
	}
	
	/**
	 * Retorna lista de atributos que contenham a(s) annotation(s) pesquisa tambem realizada nos metodos
	 * @param klass
	 * @param annotationClass
	 * @return
	 */
	@SafeVarargs
	public static List<Field> listAttributesByAnnotation(Class<?> klass, Class<? extends Annotation>... annotationClass){
		return listAttributes(klass, null, annotationClass);
	}
	
	/**
	 * 
	 * @param klass
	 * @return
	 */
	public static List<Field> listAttributes(Class<?> klass){
		return listAttributes(klass, REG_EXP_ALL);
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
		return existMethod(klass, name, REG_EXP_ALL);
	}
	
	/**
	 * Verifica se o metodo get existe na classe
	 * @param klass
	 * @param name
	 * @return
	 */
	public static boolean existMethodGet(Class<?> klass, String name){
		return existMethod(klass, name, REG_EXP_METHOD_GET);
	}
	
	/**
	 * Verifica se o metodo set existe na classe
	 * @param klass
	 * @param name
	 * @return
	 */
	public static boolean existMethodSet(Class<?> klass, String name){
		return existMethod(klass, name, REG_EXP_METHOD_SET);
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
	 * @param atributoNome
	 * @param annotationClass - annotations a ser pesquisada
	 * @return
	 */
	public static boolean existAnnotation(Class<?> klass, String atributoNome, Class<? extends Annotation> annotationClass){
		List<Field> listFields = listAttributes(klass, StringUtils.isBlank(atributoNome) ? REG_EXP_ALL : atributoNome);
		for(Field atributo : listFields)
			if(getAnnotation(atributo, annotationClass) != null)
					return true;
		
		return false;
	}
	
	/**
	 * Verifica se existe Annotation no atributo ou metodo Get
	 * @param atributo
	 * @param annotationClass - annotations a ser pesquisada
	 * @return
	 */
	public static boolean existAnnotation(Field atributo, Class<? extends Annotation> annotationClass){
		return atributo.isAnnotationPresent(annotationClass);
	}
	/**
	 * Retorna annotation tanto do field quanto do method do tipo GET, null caso nao encontre
	 * @param <T>
	 * @param field
	 * @param annotationClass
	 * @return
	 */
	public static <T extends Annotation> T getAnnotation(Field field, Class<T> annotationClass){

		if(field.isAnnotationPresent(annotationClass))
			return field.getAnnotation(annotationClass);
		else
			try {
			 Method method = getMethodGet(field.getDeclaringClass(), field.getName());
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
	public static <T extends Annotation> T getAnnotation(Class<?> klass, String methodName, Class<T> annotationClass){
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
	public static Method getMethod(Class<?> klass, String methodName){
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
	 * @param obj
	 * @param atributo
	 * @param objParams
	 * @return
	 * @throws Exception
	 */
	public static Object executeGet(Object obj, String atributo, Object[] objParams) throws Exception {
		Method method = getMethodGet(obj.getClass(), atributo, objParams);
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
	public static Object executeStaticMethod(Class<?> klass, String metodo, Object... params) throws Exception{
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
	public static Object executeMethod(Object obj, String metodo, Object... params) throws Exception{
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
	 * 
	 * @param klass
	 * @param atributeName
	 * @param objParams
	 * @return
	 * @throws Exception
	 */
	public static Method getMethodGet(Class<?> klass, String atributeName, Object... objParams) throws Exception{
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
	public static Object executeGet(Object obj, String atributo) throws Exception {
	    return executeGet(obj, atributo, null);
	}

	/**
	 * Invoca o metodo setXxxx para o objeto e atributo informados.
	 * @param obj - o objeto a invocar o metodo set
	 * @param atributo - o atributo cujo metodo set será invocado
	 * @param objParams - um array de objetos com os parametros do setter
	 * @return
	 * @throws Exception
	 */
	public static Object executeSet(Object obj, String atributo, Object... objParams) throws Exception {
		String nomeSetter = "set" + StringUtils.capitalize(atributo);

		boolean paramIsNull = true;
		
		if(objParams != null){
			for(Object oAux : objParams)
				if(oAux != null)
					oAux = false;
		}
		
		if (paramIsNull)
			//return obj.getClass().getMethod(nomeSetter, (Class[])null).invoke(obj, (Object[])null);
			return executeMethod(obj, nomeSetter, objParams);

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
	 * @param obj
	 * @param nomePropriedade - nome da propriedade cujo valor deve ser recuperado
	 * @return
	 * @throws Exception
	 */
	
	public static Object executeGetCascade(Object obj, String nomePropriedade) throws Exception {
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
		
		return rec ? executeGetCascade(result, propriedade) : result;
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
	private static Object concatValue(Field field, Object obj, Object obj2) throws Exception{
		Object val1 = null, val2 = null;

		val1 = executeGet(obj, field.getName());
		val2 = executeGet(obj2, field.getName());
		
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
			return val1.toString() + val2.toString();
		}
	}
	
	/**
	 * Soma ou junta os valores dos campos do objeto
	 * @param obj
	 * @param obj2
	 * @param fieldOff - campos que nao serao concatenados
	 */
	
	public static <T>  T concatObject(T obj, T obj2, String... fieldOff) throws Exception{
		if(obj == null)
			obj = obj2;
		else if (obj2 != null)
			for(Field field :listAttributes(obj.getClass(), REG_EXP_ALL)){				
				boolean concatena = true;
				for(int i = 0; i < fieldOff.length ; i++){
					if(field.getName().equalsIgnoreCase(fieldOff[i])){
						concatena = false;
						break;
					}
				}
				if(concatena){
					Object novoValor = concatValue(field, obj, obj2);
					if(novoValor != null)
						executeSet(obj, field.getName(), novoValor);
				}
			}
		
		return obj;
	}
	
	/**
	 * Instancia novo objeto com os valores do Map
	 * @param obj
	 * @param fieldValue
	 * @return
	 * @throws Exception 
	 */
	public static Object instanceObject(Object obj, Map<String, Object> fieldValue) throws Exception{
		for(String atributo : fieldValue.keySet()){
			if(fieldValue.get(atributo) == null)
				continue;
			
			try {
				executeSet(obj, atributo, fieldValue.get(atributo));
			} catch (Exception e) {
				if(fieldValue.get(atributo).getClass().equals(Long.class) || fieldValue.get(atributo).getClass().equals(long.class)){			
					executeSet(obj, atributo, Long.parseLong(String.valueOf(fieldValue.get(atributo))));
				}else if(fieldValue.get(atributo).getClass().equals(Float.class) || fieldValue.get(atributo).getClass().equals(float.class)){
					executeSet(obj, atributo, Float.parseFloat(String.valueOf(fieldValue.get(atributo))));
				}else if(fieldValue.get(atributo).getClass().equals(Double.class) || fieldValue.get(atributo).getClass().equals(double.class)){
					executeSet(obj, atributo, Double.parseDouble(String.valueOf(fieldValue.get(atributo))));
				}else if(fieldValue.get(atributo).getClass().equals(Integer.class) || fieldValue.get(atributo).getClass().equals(int.class))
					executeSet(obj, atributo, Integer.parseInt(String.valueOf(fieldValue.get(atributo))));
			}
		}
		return obj;
	}
	
	/**
	 * Compara as classes, inclusive interface
	 * @param class1
	 * @param class2
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
	 * @throws Exception 
	 */
	public static void setFieldValue(Object obj, Field field, Object value) throws Exception{
			field.setAccessible(true);
			field.set(obj, value);
	}
	/**
	 * 
	 * @param obj
	 * @param fieldName
	 * @param value
	 * @throws Exception 
	 */
	public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception{
		List<Field> lFld = listAttributes(obj.getClass(), fieldName);
		if(lFld.size() == 1){
			Field fld = lFld.get(0);
			fld.setAccessible(true);
			fld.set(obj, value);
		}
	}
	/**
	 * 
	 * @param klass
	 * @param name
	 * @return
	 */
	public static Field getAttribute(Class<?> klass, String name){
		List<Field> lFld = listAttributes(klass, name);
		
		for(Field fld: lFld){
			if(fld.getName().equals(name)){
				return fld;
			}
		}
		
		return null;
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
	
	/**
	 * Verifica se o atributo da classe é do tipo primitivo
	 * @param klass
	 * @param name
	 * @return
	 */
	public static boolean isPrimitiveField(Class<?> klass, String name) {
		Field field = getAttribute(klass, name);
		return isPrimitive(field.getType());
	}
	
	/**
	 * Verifica se é instancia de Collection, List, ou Set
	 * @param klass
	 * @return
	 */
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
	 * 
	 * @param obj
	 * @param field
	 * @return
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
					objValue = executeGet(objValue, fld);
					if(objValue == null){
						return null;
					}
				}
				return (T) objValue;
			}else{
				return (T) executeGet(obj, field);
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
	 * @param listAttributeName
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
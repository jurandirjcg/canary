package br.com.jgon.canary.jee.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Util para colecoes
 * @author jurandir
 * 
 * @version 1.0 - 31/07/2011
 */
public class CollectionUtil {
	/**
	 * Concatena arrays  
	 * @param <T> - Tipo do retorno esperado
	 * @param classe - Classe do retorno
	 * @param arrays - Arrays, [], dos objetos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] concat (Class<T> classe, T[]... arrays) {  
		int length = 0;  
		for (T[] array : arrays) { length += array.length; }  

		T[] ret = (T[]) Array.newInstance (classe, length);  
		int destPos = 0;  
		for (T[] array : arrays) {  
			System.arraycopy (array, 0, ret, destPos, array.length);  
			destPos += array.length;  
		}  
		return ret;  
	}  
	
	/**
	 * Concatena arrays do tipo int
	 * @param arrays
	 * @return
	 */
	public static int[] concat (int[]... arrays) {  
		int length = 0;  
		for (int[] array : arrays) { length += array.length; }  

		int[] ret = new int[length];  
		int destPos = 0;  
		for (int[] array : arrays) {  
			System.arraycopy (array, 0, ret, destPos, array.length);  
			destPos += array.length;  
		}  
		return ret;  
	}  
	/**
	 * Concatena arrays do tipo double
	 * @param arrays
	 * @return
	 */
	public static double[] concat (double[]... arrays) {  
		int length = 0;  
		for (double[] array : arrays) { length += array.length; }  

		double[] ret = new double[length];  
		int destPos = 0;  
		for (double[] array : arrays) {  
			System.arraycopy (array, 0, ret, destPos, array.length);  
			destPos += array.length;  
		}  
		return ret;  
	}  
	
	/**
	 * Converte colecao Set para List
	 * @param colSet
	 * @return
	 */
	public static <T> List<T> convertSetToList(Set<T> colSet){
		/*List<T> listAux = new ArrayList<T>(0);
		
		for(T obj :colSet)
			listAux.add(obj);*/
		
		//return listAux;
		
		return new ArrayList<T>(colSet);
	}
	
	/**
	 * Converte colecao List para Set
	 * @param colList
	 * @return
	 */
	public static <T> Set<T> convertListToSet(List<T> colList){
		/*Set<T> listAux = new HashSet<T>(0);
		
		for(T obj :colList)
			listAux.add(obj);
		
		return listAux;*/
		return new HashSet<T>(colList);
	}
	
	/**
	 * Converte um array[] em List
	 * @param array
	 * @return
	 */
	public static <T> List<T> convertArrayToList(T[] array){
		if(array == null){
			return null;
		}
		return Arrays.asList(array);
		/*List<T> list = new ArrayList<T>(0);
		for(int i=0; i<array.length; i++){
			list.add(array[i]);
		}
		
		return list;*/
	}
	
	/**
	 * Converte Set em List ordenado com base no Comparator
	 * @param <T>
	 * @param colSet
	 * @param comparator
	 * @return
	 */
	public static <T> List<T> convertSetToListShort(Set<T> colSet, Comparator<T> comparator){
		List<T> listAux = convertSetToList(colSet);
		Collections.sort(listAux, comparator);
		return listAux;
	}
	
	/**
	 * Converte qualquer colecao (List, Set, ArrayList ou outra que implemente a interface Collection em array simples [] 
	 * @param <T>
	 * @param klass - tipo do array. Ex String[], Object[]
	 * @param collection - colecao de objetos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] convertCollectionToArray(Class<T> klass, Collection<T> collection){
		T[] ret = (T[]) Array.newInstance(klass, collection.size());
		int i =0;
		for(T aux: collection){
			ret[i] = aux;
			i++;
		}
		return ret;
	}
	
	/**
	 * Converte colecao de Object em colecao de String 
	 * @param collection
	 * @return
	 */
	public static <T> Collection<String> convertToCollectionString(Collection<T> collection){
		Collection<String> colAux = new ArrayList<String>(collection.size());
		for(T obj:collection)
			colAux.add(String.valueOf(obj));
		
		return colAux;
	}
	
	/**
	 * Converte colecao de String em colecao de Integer
	 * @param collection
	 * @return
	 */
	public static Collection<Integer> convertToCollectionInteger(Collection<String> collection){
		Collection<Integer> colAux = new ArrayList<Integer>(collection.size());
		for(String str:collection)
			colAux.add(Integer.parseInt(str));
		
		return colAux;
	}
	/**
	 * 
	 * @param list
	 * @param value
	 * @return
	 */
	public static boolean constainsValue(Collection<String> list, String value){
		for(String s : list){
			if(s.contains(value)){
				return true;
			}
		}		
		return false;
	}
}
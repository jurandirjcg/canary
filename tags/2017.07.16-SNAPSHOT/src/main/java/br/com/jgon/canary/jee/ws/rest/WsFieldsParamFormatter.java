package br.com.jgon.canary.jee.ws.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;

import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.util.FindAnnotation;

import br.com.jgon.canary.jee.exception.ApplicationException;
import br.com.jgon.canary.jee.util.CollectionUtil;
import br.com.jgon.canary.jee.util.ReflectionUtil;
import br.com.jgon.canary.jee.ws.rest.util.link.LinkPaginate;
import br.com.jgon.canary.jee.ws.rest.util.link.LinkResource;
import br.com.jgon.canary.jee.ws.rest.util.link.LinkResources;
/**
 * Intercepta a requisicao para tratamento dos campos
 * @author jurandir
 *
 */
public class WsFieldsParamFormatter implements StringParameterUnmarshaller<WSFieldsParam>{
	
	private Class<?> returnType;
	private String[] forceFields;
	
	private static final String REGEX_PATH_PARAMETERS = "(\\#|\\$)\\{[a-z-A-Z\\.]+\\}";
	private static final String REGEX_REPLACE_PARAM = "\\#|\\$|\\{|\\}";
	
	@Context
	ResourceInfo resourceInfo;
	
	@Override
	public void setAnnotations(Annotation[] annotations) {
		WSParamFormat wsParamFormat = FindAnnotation.findAnnotation(annotations, WSParamFormat.class);
		
		returnType = wsParamFormat.value();
		forceFields = wsParamFormat.forceFields();
	}

	@Override
	public WSFieldsParam fromString(String str) {
		try{
			StringBuilder sb = new StringBuilder();
			sb.append(str);
			
			for(String f : forceFields){
				if(!str.contains(f)){
					sb.append(",");
					sb.append(f);
				}
			}
			
			String fieldsReconfig = configRequiredParam(resourceInfo.getResourceMethod(), sb.toString());
			return new WSFieldsParam(returnType, fieldsReconfig);
		}catch (ApplicationException e){
			RuntimeException r = new RuntimeException(e);
			r.addSuppressed(e);
			throw r;
		}
	}
	/**
	 * 
	 * @param serviceMethod
	 * @param params
	 * @return
	 */
	public String configRequiredParam(Method serviceMethod, String params) {
		List<LinkResource> listResources = new ArrayList<LinkResource>(1);
		listResources.addAll(paramFields(serviceMethod));

		if(!listResources.isEmpty()){
			Pattern pattern = Pattern.compile(REGEX_PATH_PARAMETERS);
			Matcher matcher;
			Set<String> listFieldParam = new HashSet<String>();
			
			listFieldParam.add(params);
			
			for(LinkResource linkResource : listResources){
				for(String qp : linkResource.queryParameters()){
					matcher = pattern.matcher(qp);
					while(matcher.find()){
						listFieldParam.add(matcher.group().replaceAll(REGEX_REPLACE_PARAM, ""));
					}
				}

				for(String qp : linkResource.pathParameters()){
					matcher = pattern.matcher(qp);
					while(matcher.find()){
						listFieldParam.add(matcher.group().replaceAll(REGEX_REPLACE_PARAM, ""));
					}
				}
			}

			StringBuilder sb = new StringBuilder();
			listFieldParam.forEach(item -> {
				if(sb.length() != 0){
					sb.append(",");
				}
				sb.append(item);
				
			});
			
			return sb.toString();
		}
		return params;
	}
	
	/**
	 * 
	 * @param method
	 * @return
	 */
	private List<LinkResource> paramFields(Method method){
		Annotation[][] parametrosAnotados = method.getParameterAnnotations();
		Class<?>[] parameterTypes = method.getParameterTypes();
		
		WSParamFormat wsAnnotation = null;
		
		for(int i=0; i < parametrosAnotados.length; i++){
			Annotation[] parametroAnotado = parametrosAnotados[i];
			if(parameterTypes[i].equals(WSFieldsParam.class)){
				for(Annotation a : parametroAnotado){
					if(a instanceof WSParamFormat){
						wsAnnotation = (WSParamFormat) a;
					}
				}
				break;
			}
		}
		
		if(wsAnnotation != null){
			List<LinkResource> listResources = new ArrayList<LinkResource>(1);

			LinkResource lr = method.getAnnotation(LinkResource.class);
			LinkResources lrs = method.getAnnotation(LinkResources.class);
			LinkPaginate lp = method.getAnnotation(LinkPaginate.class);

			if(lr != null){
				listResources.add(lr);
			}

			if(lrs != null){
				if(lrs.value().length > 0){
					listResources.addAll(CollectionUtil.convertArrayToList(lrs.value()));
				}
			
				if(!lrs.serviceClass().equals(void.class)){
					listResources.addAll(paramFields(ReflectionUtil.getMethodByName(lrs.serviceClass(), lrs.serviceMethodName())));
				}
			}

			if(lp != null){
				if(lp.collectionLinks().value().length > 0){
					listResources.addAll(CollectionUtil.convertArrayToList(lp.collectionLinks().value()));
				}
	
				if(!lp.collectionLinks().serviceClass().equals(void.class)){
					listResources.addAll(paramFields(ReflectionUtil.getMethodByName(lp.collectionLinks().serviceClass(), lp.collectionLinks().serviceMethodName())));
				}
			}
			
			return listResources;
		}
		
		return null;
	}
}

package br.com.jgon.canary.jee.ws.rest.util.link;

import java.lang.reflect.Method;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
/**
 * Configura os metodos que deverao ajustar o link para response
 * Basicamente os que possuirem alguma annotation de criacao de link
 * Ex: {@link LinkResource}
 * @author jurandir
 *
 */
public class LinkResponseFeature implements DynamicFeature{

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		Class<?> clazz = resourceInfo.getResourceClass();
		Method method = resourceInfo.getResourceMethod();
		
		if(method.isAnnotationPresent(LinkResource.class) 
				|| method.isAnnotationPresent(LinkResources.class)
				|| method.isAnnotationPresent(LinkPaginate.class)){

			context.register(new LinkResponseFilter(clazz, method));
		}
	}  
}

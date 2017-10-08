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
package br.com.jgon.canary.jee.ws.rest.util.link;

import java.lang.reflect.Method;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
/**
 * Configura os metodos que deverao ajustar o link para response
 * Basicamente os que possuirem alguma annotation de criacao de link
 * Ex: {@link LinkResource}
 *
 * @author Jurandir C. Goncalves
 * 
 * @version 1.0.0
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

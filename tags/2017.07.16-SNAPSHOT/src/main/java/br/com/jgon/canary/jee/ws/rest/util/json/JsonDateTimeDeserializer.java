package br.com.jgon.canary.jee.ws.rest.util.json;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.util.DateUtil;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

/**
 * Utilizar a annotation {@link JsonFormat} parametro pattern para definir o pattern de retorno - default: yyyy-MM-dd HH:mm:ss
 * @author jurandir
 *
 */
public class JsonDateTimeDeserializer extends JsonDeserializer<Date> implements ContextualDeserializer{

	private String pattern = "yyyy-MM-dd HH:mm:ss";
	
	public JsonDateTimeDeserializer() {
		
	}
	
	public JsonDateTimeDeserializer(String pattern){
		this.pattern = pattern;
	}
	

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
		JsonFormat jf;
		if(property != null){
			jf = property.getAnnotation(JsonFormat.class);
			if(jf != null && StringUtils.isNotBlank(jf.pattern())){
				return new JsonDateTimeDeserializer(jf.pattern());
			}
		}
		
		return new JsonDateTimeDeserializer();
	}

	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return DateUtil.parseDate(p.getValueAsString(), Arrays.asList(new String[]{pattern}));
	}
}

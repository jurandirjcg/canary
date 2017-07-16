package br.com.jgon.canary.jee.ws.rest.util.json;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.util.DateUtil;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

/**
 * Utilizar a annotation {@link JsonFormat} parametro pattern para definir o pattern de retorno - default: yyyy-MM-dd HH:mm:ss
 * @author jurandir
 *
 */
public class JsonDateTimeSerializer extends JsonSerializer<Date> implements ContextualSerializer{

	private String pattern = "yyyy-MM-dd HH:mm:ss";
	
	public JsonDateTimeSerializer() {
		
	}
	
	public JsonDateTimeSerializer(String pattern){
		this.pattern = pattern;
	}
	
	@Override
	public void serialize(Date date, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {
		gen.writeString(DateUtil.formatDate(date, pattern));
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
		JsonFormat jf;
		if(property != null){
			jf = property.getAnnotation(JsonFormat.class);
			if(jf != null && StringUtils.isNotBlank(jf.pattern())){
				return new JsonDateTimeSerializer(jf.pattern());
			}
		}
		
		return new JsonDateTimeSerializer();
	}

}

package br.com.jgon.canary.jee.ws.rest.validation.enumerator;

public enum ParameterTypeEnum {

	BOOLEAN (Boolean.class),
	BYTE (Byte.class),
	BYTEARRAY (Byte[].class),
	CHAR (Character.class),
	DOUBLE (Double.class),
	FLOAT (Float.class),
	INTEGER (Integer.class),
	LONG (Long.class),
	SHORT (Short.class),
	STRING (String.class);
	
	public Class<?> value;
	
	ParameterTypeEnum(Class<?> value) {
		this.value = value;
	}
	
	public static ParameterTypeEnum valueOf(Class<?> value){
		for(ParameterTypeEnum obj : ParameterTypeEnum.values()){
			if(obj.value.equals(value) ){
				return obj;
			}
		}
		
		return null;
	}
}

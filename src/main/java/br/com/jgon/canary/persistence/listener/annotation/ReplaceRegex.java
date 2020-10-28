package br.com.jgon.canary.persistence.listener.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.jgon.canary.persistence.listener.annotation.ReplaceRegex.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(List.class)
@Documented
public @interface ReplaceRegex {
    
    /**
     * 
     * @return regex para realizr o replace
     */
    String regex();

    /**
     * 
     * @return caracteres para subsituicao
     */
    String replacement();

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        ReplaceRegex[] value();
    }
}

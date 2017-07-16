package br.com.jgon.canary.jee.ws.rest.util.link;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Configura o link para serializacao
 * @author jurandir
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LinkEntity {
	
	private String rel;
	private String href;
	private String title;
	private String type;
	private String templated;
	
	public LinkEntity(){
		
	}
	
	public LinkEntity(String href, String rel, String title, String type, String templated) {
		super();
		this.rel = rel;
		this.href = href;
		this.title = title;
		this.type = type;
		this.templated = templated;
	}
	
	public LinkEntity(String href, String rel) {
		super();
		this.href = href;
		this.rel = rel;
	}

	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getTemplated() {
		return templated;
	}

	public void setTemplated(String templated) {
		this.templated = templated;
	}
	
}

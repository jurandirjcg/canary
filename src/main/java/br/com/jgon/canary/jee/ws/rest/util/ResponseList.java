package br.com.jgon.canary.jee.ws.rest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Link;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jurandir
 *
 * @param <T>
 */
public class ResponseList<T> {

	private Collection<T> data;
	@JsonProperty("_link")
	private List<Link> link = new ArrayList<Link>(0);
	
	public ResponseList(){
		
	}
	
	public ResponseList(Collection<T> data){
		this.data = data;
	}

	public Collection<T> getData() {
		return data;
	}
	public void setData(Collection<T> data) {
		this.data = data;
	}
	public List<Link> getLink() {
		return link;
	}
	public void setLink(List<Link> link) {
		this.link = link;
	}

}

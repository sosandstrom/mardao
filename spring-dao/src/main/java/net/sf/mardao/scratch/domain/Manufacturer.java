package net.sf.mardao.scratch.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Manufacturer {
	
	@Id
	private Long id;
	
	private String name;
	
	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}

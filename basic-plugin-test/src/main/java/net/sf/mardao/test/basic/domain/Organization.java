package net.sf.mardao.test.basic.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Organization {
	
	@Id
	private Long id;
	
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}

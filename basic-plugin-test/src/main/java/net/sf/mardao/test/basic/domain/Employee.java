package net.sf.mardao.test.basic.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Employee {
	
	@Id
	private Long id;

	private String name;
	
	@ManyToOne
	private Organization currentEmployer;

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

	public void setCurrentEmployer(Organization currentEmployer) {
		this.currentEmployer = currentEmployer;
	}

	public Organization getCurrentEmployer() {
		return currentEmployer;
	}

}

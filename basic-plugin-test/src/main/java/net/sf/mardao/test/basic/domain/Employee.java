package net.sf.mardao.test.basic.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames="name"),
		@UniqueConstraint(columnNames={"signum","currentEmployer"})})
public class Employee {
	
	@Id
	private Long id;

	private String name;
	private String signum;
	
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

	public void setSignum(String signum) {
		this.signum = signum;
	}

	public String getSignum() {
		return signum;
	}

}

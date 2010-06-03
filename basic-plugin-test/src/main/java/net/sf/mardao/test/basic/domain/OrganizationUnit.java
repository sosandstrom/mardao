package net.sf.mardao.test.basic.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="tblOrganizationUnit", uniqueConstraints=@UniqueConstraint(columnNames="name"))
public class OrganizationUnit {
	
	@Id
	@Column(name="ouID")
	private Long key;
	
	private String name;
	
	@ManyToOne
	@Column(name="orgID")
	private Organization organization;
	
	@ManyToOne
	@Column(name="parentID")
	private OrganizationUnit parentUnit;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public Long getKey() {
		return key;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setParentUnit(OrganizationUnit parentUnit) {
		this.parentUnit = parentUnit;
	}

	public OrganizationUnit getParentUnit() {
		return parentUnit;
	}

}

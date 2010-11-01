package net.sf.mardao.test.jpa.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity(name="JPAEmployee")
@Table(uniqueConstraints={@UniqueConstraint(columnNames="name")})//, @UniqueConstraint(columnNames={"signum","currentEmployer"})})
public class JPAEmployee {
	
   @Id
   private Long id;

	private String name;
	private String signum;
	
	@ManyToOne()
	private JPAOrganization currentEmployer;
	
	@ManyToOne
	private JPAOrganizationUnit currentUnit;

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

	public void setCurrentEmployer(JPAOrganization currentEmployer) {
		this.currentEmployer = currentEmployer;
	}

	public JPAOrganization getCurrentEmployer() {
		return currentEmployer;
	}

	public void setSignum(String signum) {
		this.signum = signum;
	}

	public String getSignum() {
		return signum;
	}

	public void setCurrentUnit(JPAOrganizationUnit currentUnit) {
		this.currentUnit = currentUnit;
	}

	public JPAOrganizationUnit getCurrentUnit() {
		return currentUnit;
	}

}

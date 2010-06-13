package net.sf.mardao.scratch.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="tblType",uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
public class Type {
	
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

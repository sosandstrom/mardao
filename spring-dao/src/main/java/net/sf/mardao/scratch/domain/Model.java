package net.sf.mardao.scratch.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Model {
	
	@Id
	private Long id;
	
	private String name;
	
	@ManyToOne
	private Manufacturer manufacturer;
	
	@ManyToOne
	private Type type;
	
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
	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}
	public Manufacturer getManufacturer() {
		return manufacturer;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Type getType() {
		return type;
	}
}

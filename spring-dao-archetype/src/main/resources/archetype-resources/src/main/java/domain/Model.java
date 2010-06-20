#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
public class Model {
	
	@Id
	private Long id;
	
	private String name;
	
	@ManyToOne
	@Column(name="manufacturerId")
	private Manufacturer manufacturer;
	
	@ManyToOne
	@Column(name="typeId")
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

package net.sf.mardao.test.aed.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Chapter extends AbstractPrimaryKeyEntity {
	@Id
	private Long id;
	
	private String name;
	
	/** References the book's ISBN */
	// @ManyToOne
	@Basic
	private String book;

	@Override
	public Object getPrimaryKey() {
		return id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBook() {
		return book;
	}

	public void setBook(String book) {
		this.book = book;
	}

}

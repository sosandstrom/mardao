package net.sf.mardao.test.aed.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Book extends AbstractPrimaryKeyEntity {
	@Id
	private String ISBN;
	
	private String title;

	@Override
	public Object getPrimaryKey() {
		return ISBN;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String name) {
		this.ISBN = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}

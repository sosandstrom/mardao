package net.sf.mardao.test.aed.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Footnote extends AbstractPrimaryKeyEntity {
	@Id
	private Key key;
	
	private String name;
	
	/** References the page key*/
	// @ManyToOne
	@Basic
	private Key page;

	@Override
	public Object getPrimaryKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getPage() {
		return page;
	}

	public void setPage(Key page) {
		this.page = page;
	}

}

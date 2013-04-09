package net.sf.mardao.core.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class IdBean {

	@Id
	private Long	_id;

	@Basic
	private String	message;

	public void set_id(final Long _id) {
		this._id = _id;
	}

	public Long get_id() {
		return _id;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}

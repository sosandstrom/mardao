package net.sf.mardao.core.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;

import net.sf.mardao.core.domain.AndroidLongEntity;

@Entity
public class ExtendsBean extends AndroidLongEntity {

	@Basic
	private String	message;

	public void setMessage(final String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}

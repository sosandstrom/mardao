package net.sf.mardao.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * The domain object for Groups, corresponding to packages, in the class graph.
 * @author f94os
 *
 */

public class Group {
	private String name;
	private String daoPackageName;
	private final Map<String, Entity> entities = new HashMap<String, Entity>();
	

	public Map<String,Entity> getEntities() {
		return entities;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getName() {
		return name;
	}


	public void setDaoPackageName(String daoPackageName) {
		this.daoPackageName = daoPackageName;
	}


	public String getDaoPackageName() {
		return daoPackageName;
	}
}

package net.sf.mardao.domain;

/*
 * #%L
 * net.sf.mardao:mardao-maven-plugin
 * %%
 * Copyright (C) 2010 - 2014 Wadpam
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


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

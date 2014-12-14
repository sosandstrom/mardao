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


import net.sf.mardao.plugin.ProcessDomainMojo;


/**
 * The domain object for fields in the class graph.
 * @author f94os
 *
 */
public class Field implements Comparable<Field> {
	private String columnName;
	private String name;
	private String type;
	private String mappedBy;
	/** for many-to-ones */
	private Entity entity;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
        
        public String getNameUpper() {
            return null != name ? name.toUpperCase() : null;
        }

        public String getNameFirst() {
            return ProcessDomainMojo.firstToUpper(name);
        }

        public String getNameLower() {
            return ProcessDomainMojo.firstToLower(name);
        }

	public void setType(String type) {
		this.type = type;
	}


	public String getType() {
		return type;
	}
	
	public String getSimpleType() {
		String returnValue = this.type;
		int beginIndex = type.lastIndexOf('.');
		if (-1 < beginIndex) {
			returnValue = type.substring(beginIndex+1);
		}
		return returnValue;
	}
	
	@Override
	public String toString() {
		return name;
	}


	@Override
	public int compareTo(Field other) {
		return toString().compareTo(other.toString());
	}


	public void setMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}


	public String getMappedBy() {
		return mappedBy;
	}


	public void setEntity(Entity entity) {
		this.entity = entity;
	}


	public Entity getEntity() {
		return entity;
	}


	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}


	public String getColumnName() {
		if (null == columnName) {
			return name;
		}
		return columnName;
	}
}

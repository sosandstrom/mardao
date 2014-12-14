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

public class MergeTemplate {
	private String templatePrefix = "";
	private String templateMiddle = "";
	private String templateSuffix = ".vm";
	private String destFolder = "targetDao";
	private String filePrefix = "";
	private String fileMiddle = "";
	private String fileSuffix = ".java";
	private boolean entity = true;
	private boolean typeSpecific = false;
	private boolean typeAppend = true;
	private boolean listingEntities = false;

        /**
         * @since 2.2.4
         */
        private String requiresOnClasspath = null;
	
	public void setListingEntities(boolean listingEntities) {
		this.listingEntities = listingEntities;
	}
	public String getTemplatePrefix() {
		return templatePrefix;
	}
	public void setTemplatePrefix(String templatePrefix) {
		this.templatePrefix = templatePrefix;
	}
	public String getTemplateSuffix() {
		return templateSuffix;
	}
	public void setTemplateSuffix(String templateSuffix) {
		this.templateSuffix = templateSuffix;
	}
	public String getDestFolder() {
		return destFolder;
	}
	public void setDestFolder(String destFolder) {
		this.destFolder = destFolder;
	}
	public String getFilePrefix() {
		return filePrefix;
	}
	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}
	public String getFileSuffix() {
		return fileSuffix;
	}
	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}
	public boolean isEntity() {
		return entity;
	}
	public void setEntity(boolean entity) {
		this.entity = entity;
	}
	public boolean isTypeSpecific() {
		return typeSpecific;
	}
	public void setTypeSpecific(boolean typeSpecific) {
		this.typeSpecific = typeSpecific;
	}
	public String getTemplateMiddle() {
		return templateMiddle ;
	}
	public void setTemplateMiddle(String templateMiddle) {
		this.templateMiddle = templateMiddle;
	}
	public void setFileMiddle(String fileMiddle) {
		this.fileMiddle = fileMiddle;
	}
	public String getFileMiddle() {
		return fileMiddle;
	}
	public boolean isListingEntities() {
		return listingEntities;
	}
	public void setTypeAppend(boolean typeAppend) {
		this.typeAppend = typeAppend;
	}
	public boolean isTypeAppend() {
		return typeAppend;
	}

    public String getRequiresOnClasspath() {
        return requiresOnClasspath;
    }

    public void setRequiresOnClasspath(String requiresOnClasspath) {
        this.requiresOnClasspath = requiresOnClasspath;
    }
	
	
}

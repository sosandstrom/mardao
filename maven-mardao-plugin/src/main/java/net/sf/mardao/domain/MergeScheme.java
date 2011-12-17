package net.sf.mardao.domain;

import java.util.ArrayList;
import java.util.List;

public class MergeScheme {
	
	private List<MergeTemplate> templates;
	
	public MergeScheme() {
		this.templates = new ArrayList<MergeTemplate>();
	}
	
	public MergeScheme(MergeScheme parent) {
		this();
		setTemplates(parent.templates);
	}
	
	public List<MergeTemplate> getTemplates() {
		return templates;
	}

	public void setTemplates(List<MergeTemplate> templatesToAppend) {
            templatesToAppend.addAll(templates);
            templates = templatesToAppend;
		// templates.addAll(templatesToAppend);
	}
}

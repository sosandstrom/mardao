package net.sf.mardao.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

/**
 * The domain object for Entities in the class graph.
 * @author f94os
 *
 */
public class Entity {
	private String className;
	private String simpleName;
	private Field pk;
	private final Set<Field> fields = new TreeSet<Field>();
	private final Set<Field> manyToOnes = new TreeSet<Field>();
	private final Set<Field> manyToManys = new TreeSet<Field>();
	private final List<Set<String>> uniqueConstraints = new ArrayList<Set<String>>();
	private final Map<String,Field> mappedBy = new HashMap<String,Field>();

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public Field getPk() {
		return pk;
	}

	public void setPk(Field pk) {
		this.pk = pk;
	}

	public Set<Field> getFields() {
		return fields;
	}

	public Set<Field> getManyToOnes() {
		return manyToOnes;
	}

	public List<Set<String>> getUniqueConstraints() {
		return uniqueConstraints;
	}

	public boolean isUnique(String fieldName) {
		for (Set<String> uniqueConstraint : uniqueConstraints) {
			if (1 == uniqueConstraint.size()
					&& uniqueConstraint.contains(fieldName)) {
				return true;
			}
		}
		return false;
	}
	
	public Map<String,Field> getAllFields() {
		final Map<String,Field> returnValue = new TreeMap<String,Field>();
		for (Field f : getFields()) {
			returnValue.put(f.getName(), f);
		}
		for (Field f : getManyToOnes()) {
			returnValue.put(f.getName(), f);
		}
		for (Field f : getManyToManys()) {
			returnValue.put(f.getName(), f);
		}
		return returnValue;
	}

	public List<Set<Field>> getUniqueFieldsSets() {
		final List<Set<Field>> returnValue = new ArrayList<Set<Field>>();
		Map<String,Field> allFields = getAllFields();
		for (Set<String> uniqueConstraint : uniqueConstraints) {
			if (1 < uniqueConstraint.size()) {
				Set<Field> fieldsSet = new TreeSet<Field>();
				for (String fieldName : uniqueConstraint) {
					fieldsSet.add(allFields.get(fieldName));
				}
				returnValue.add(fieldsSet);
			}
		}
		return returnValue;
	}
	
	public Field getFirstUniqueField() {
		Field returnValue = null;
		
		for (Entry<String,Field> entry : getAllFields().entrySet()) {
			if (isUnique(entry.getKey())) {
				returnValue = entry.getValue();
				break;
			}
		}
		
		return returnValue;
	}

	public Set<Field> getManyToManys() {
		return manyToManys;
	}

	public Map<String,Field> getMappedBy() {
		return mappedBy;
	}
	
	@Override
	public String toString() {
		return getClassName() + "<" + getSimpleName() + ">";
	}
}

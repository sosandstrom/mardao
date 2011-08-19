package net.sf.mardao.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The domain object for Entities in the class graph.
 * 
 * @author f94os
 * 
 */
public class Entity implements Comparable<Entity> {
    private String                   className;
    private String                   simpleName;
    private String                   tableName;
    private Field                    parent;
    private Field                    pk;
    private final Set<Field>         fields            = new TreeSet<Field>();
    private final Set<Field>         oneToOnes         = new TreeSet<Field>();
    private final Set<Field>         manyToOnes        = new TreeSet<Field>();
    private final Set<Field>         manyToManys       = new TreeSet<Field>();
    private final List<Set<String>>  uniqueConstraints = new ArrayList<Set<String>>();
    private final Map<String, Field> mappedBy          = new HashMap<String, Field>();
    private final Set<Entity>        dependsOn         = new TreeSet<Entity>();
    private List<Entity>             ancestors         = new ArrayList<Entity>();
    private List<Entity>             parents           = new ArrayList<Entity>();
    private final Set<Entity>        children          = new TreeSet<Entity>();
    private Field                    createdDate;
    private Field                    createdBy;
    private Field                    updatedDate;
    private Field                    updatedBy;

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
        for(Field f : getOneToOnes()) {
            if (f.getName().equals(fieldName)) {
                return true;
            }
        }
        for(Set<String> uniqueConstraint : uniqueConstraints) {
            if (1 == uniqueConstraint.size() && uniqueConstraint.contains(fieldName)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Field> getAllFields() {
        final Map<String, Field> returnValue = new TreeMap<String, Field>();
        for(Field f : getFields()) {
            returnValue.put(f.getName(), f);
        }
        for(Field f : getOneToOnes()) {
            returnValue.put(f.getName(), f);
        }
        for(Field f : getManyToOnes()) {
            returnValue.put(f.getName(), f);
        }
        for(Field f : getManyToManys()) {
            returnValue.put(f.getName(), f);
        }
        return returnValue;
    }

    public List<Set<Field>> getUniqueFieldsSets() {
        final List<Set<Field>> returnValue = new ArrayList<Set<Field>>();
        Map<String, Field> allFields = getAllFields();
        for(Set<String> uniqueConstraint : uniqueConstraints) {
            if (1 < uniqueConstraint.size()) {
                Set<Field> fieldsSet = new TreeSet<Field>();
                for(String fieldName : uniqueConstraint) {
                    for(Field column : allFields.values()) {
                        if (fieldName.equals(column.getColumnName())) {
                            fieldsSet.add(column);
                            break;
                        }
                    }
                }
                returnValue.add(fieldsSet);
            }
        }
        return returnValue;
    }

    public Field getFirstUniqueField() {
        Field returnValue = null;

        for(Entry<String, Field> entry : getAllFields().entrySet()) {
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

    public Map<String, Field> getMappedBy() {
        return mappedBy;
    }

    @Override
    public String toString() {
        return getClassName() + "<" + getSimpleName() + ">";
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * If there is no <code>@@Table(name)</code> annotation, just return the simpleName
     * 
     * @return
     */
    public String getTableName() {
        if (null == tableName) {
            return simpleName;
        }
        return tableName;
    }

    public Set<Field> getOneToOnes() {
        return oneToOnes;
    }

    public Set<Entity> getDependsOn() {
        return dependsOn;
    }

    @Override
    public int compareTo(Entity other) {
        return this.className.compareTo(other.className);
    }

    public void setParent(Field parent) {
        this.parent = parent;
    }

    public Field getParent() {
        return parent;
    }

    public void setAncestors(List<Entity> ancestors) {
        this.ancestors = ancestors;
    }

    public List<Entity> getAncestors() {
        return ancestors;
    }

    public void setParents(List<Entity> parents) {
        this.parents = parents;
    }

    public List<Entity> getParents() {
        return parents;
    }

    public Set<Entity> getChildren() {
        return children;
    }

    public final Field getCreatedDate() {
        return createdDate;
    }

    public final void setCreatedDate(Field createdDate) {
        this.createdDate = createdDate;
    }

    public final Field getCreatedBy() {
        return createdBy;
    }

    public final void setCreatedBy(Field createdBy) {
        this.createdBy = createdBy;
    }

    public final Field getUpdatedDate() {
        return updatedDate;
    }

    public final void setUpdatedDate(Field updatedDate) {
        this.updatedDate = updatedDate;
    }

    public final Field getUpdatedBy() {
        return updatedBy;
    }

    public final void setUpdatedBy(Field updatedBy) {
        this.updatedBy = updatedBy;
    }
}

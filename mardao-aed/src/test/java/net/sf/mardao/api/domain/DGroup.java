package net.sf.mardao.api.domain;

import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 *
 * @author os
 */
@Entity
public class DGroup extends DStringEntity {
    @Id
    private String name;
    
    @ManyToMany(targetEntity=DEmployee.class)
    private Collection<DEmployee> members;

    @Override
    public String getSimpleKey() {
        return name;
    }

    @Override
    public void setSimpleKey(String simpleKey) {
        this.name = simpleKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<DEmployee> getMembers() {
        return members;
    }

    public void setMembers(Collection<DEmployee> members) {
        this.members = members;
    }
    
    
}

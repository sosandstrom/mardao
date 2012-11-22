package net.sf.mardao.test.domain;

import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 *
 * @author os
 */
@Entity
public class DGroup {
    @Id
    private String name;
    
    @ManyToMany(targetEntity=DEmployee.class)
    private Collection<DEmployee> members;

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

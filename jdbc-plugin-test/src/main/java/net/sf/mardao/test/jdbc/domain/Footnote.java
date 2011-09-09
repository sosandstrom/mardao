package net.sf.mardao.test.jdbc.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.mardao.api.Parent;
import net.sf.mardao.api.domain.JDBCLongEntity;

@Entity
public class Footnote extends JDBCLongEntity {
    private static final long serialVersionUID = 1L;

    @Id
    private Long              key;

    private String            name;

    @Parent(kind = "Page")
    private Long              page;

    @Override
    public Long getSimpleKey() {
        return key;
    }

    @Override
    public Long getParentKey() {
        return page;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

}

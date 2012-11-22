package net.sf.mardao.test.domain;

/**
 * Just a class which should not be scanned as Entity
 * @author os
 */
public class NonEntity {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}

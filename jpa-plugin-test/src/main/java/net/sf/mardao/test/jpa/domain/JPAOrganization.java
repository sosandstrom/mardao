package net.sf.mardao.test.jpa.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity(name = "JPAOrganization")
@Table(uniqueConstraints =
@UniqueConstraint(columnNames = "name"))
public class JPAOrganization {

   @Id
   private Long id;
   private String name;

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public Long getId() {
      return id;
   }
}

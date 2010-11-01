package net.sf.mardao.test.jpa.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity(name = "JPAOrganizationUnit")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class JPAOrganizationUnit {

   @Id
   private Long id;
   private String name;
   @ManyToOne
   private JPAOrganization organization;
   @ManyToOne
   private JPAOrganizationUnit parentUnit;

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

   public void setOrganization(JPAOrganization organization) {
      this.organization = organization;
   }

   public JPAOrganization getOrganization() {
      return organization;
   }

   public void setParentUnit(JPAOrganizationUnit parentUnit) {
      this.parentUnit = parentUnit;
   }

   public JPAOrganizationUnit getParentUnit() {
      return parentUnit;
   }
}

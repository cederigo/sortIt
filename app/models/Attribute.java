package models;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;

import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Attribute extends Model {

  @ManyToMany (mappedBy="attributes")
  public List<Element> elements;

  @Required
  public String name;

  @Required
  public String description;

  @PreRemove
  public void detatch() {
    Logger.debug("preRemove hook %s", Attribute.class.getName());
    elements = null;
  }

  public String toString() {
    return name;
  }

}

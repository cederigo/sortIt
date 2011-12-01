package models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Attribute extends Model {

  @ManyToMany(cascade=CascadeType.ALL)
  public Set<Element> elements;
  
  @OneToMany(cascade=CascadeType.ALL)
  public Set<Relation> relations;
  
  @Required
  public String name;
  
  @Required
  public String description;

  public String toString() {
    return name;
  }
  
}

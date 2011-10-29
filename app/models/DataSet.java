package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class DataSet extends Model {

  @Required
  public String name;

  @OneToMany(targetEntity = Element.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
  public List<Element> elements;

  @OneToMany(targetEntity = Relation.class, fetch=FetchType.LAZY)
  public List<Relation> relations;
  
  public String toString() {
    return name;
  }

}

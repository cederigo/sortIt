package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.data.validation.Equals;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Relation extends Model {

  @ManyToOne(targetEntity = DataSet.class)
  public DataSet set;

  @OneToOne
  public Attribute attribute;

  @Required
  @OneToOne
  public Element a;

  @Required
  @OneToOne
  public Element b;
  /*-1,0,+1 for use in compareTo*/
  public int value;
  
  public String toString() {
    return a + " <-> " + b;
  }

}

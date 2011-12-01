package models;

import java.util.List;

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

  @ManyToOne(targetEntity = Attribute.class)
  public Attribute attribute;
   
  @Required
  @OneToOne
  public Element a;

  @Required
  @OneToOne
  public Element b;
  
  public int value = 0;
  public int votes = 0;
  
  public String toString() {
    return a + " <-> " + b;
  }
  
  public static Relation withMostVotes(Element e) {

    String query = "select r from Relation r where r.a = ? or r.b = ? order by r.votes";

    return Relation.find(query, e, e).first();

  }  
 
  public static boolean vote(Element a, Element b, boolean isForA) {
    
    if( a.id == b.id) return false;
    
    String query = "select r from Relation r where r.a = ? and r.b = ?";
    Relation r = Relation.find(query, a,b).first();
    
    if( r == null) {
      /*switch a<->b*/
      r = Relation.find(query, b,a).first();
      if (r != null) {
        /*switch vote*/
        isForA = !isForA;
      } else {
        r = new Relation();
        r.a = a;
        r.b = b;
        r.set = a.set;
        r.set.relations.add(r);
      }
    }
    
    return r.vote(isForA);
    
  }
  
  
  private boolean vote(boolean isForA) {
    value += (isForA ? 1 : -1);
    votes++;
    return validateAndSave();
  }
  

}

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
  
  /**
   * Get a Relation for Element a and b.
   * Create one if it does not exist.
   * Note that the order of param a and b does not matter.
   * 
   * @param a
   * @param b
   * @return a Relation in any case
   */
  public static Relation get(Element a, Element b) {
    
    if( a.id == b.id) return null;
    
    String query = "select r from Relation r where r.a = ? and r.b = ?";
    Relation r = Relation.find(query, a,b).first();
    
    if( r == null) {
      /*switch a<->b*/
      r = Relation.find(query, b,a).first();
      if (r == null) {
        /*create a new one*/
        r = new Relation();
        r.a = a;
        r.b = b;
        r.set = a.set;
        r.value = 0;
        r.validateAndSave();
      }
    }
    
    return r;
  }
  
  /**
   * Decide a or b for a given Attribute. 
   * 
   * @param isForA when you are for a ;-)
   */
  public void vote(boolean isForA) {
    value += (isForA ? 1 : -1);
    votes++;
    save();
  }
  

}

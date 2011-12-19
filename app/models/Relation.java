package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
  
  public int votesA = 0;
  public int votesB = 0;

  public String toString() {
    return a + " <-> " + b;
  }

  /**
   * Return a value that can be used in a comparator.<br/>
   * Compare <code>ref</code> (a or b) to the other (b or a).
   * 
   * @param ref
   *          one of the elements in this relation
   * @return 1 if ref > other, -1 if ref < other, 0 otherwise
   */
  public int valueFor(Element ref) {
    if (ref == null)
      return 0;
    int res = 0;
    if (ref.equals(a)) {
      res = votesA - votesB;
    } else if (ref.equals(b)) {
      res = votesB - votesA;
    }
    return res == 0 ? 0 : res > 0 ? 1 : -1;
  }

  public void voteFor(Element e) {

    if (e == null)
      return;

    if (e.equals(a)) {
      votesA++;
      save();
    } else if (e.equals(b)) {
      votesB++;
      save();
    }
  }

  public Element getOther(Element e) {
    if (e.equals(a)) {
      return b;
    } else if (e.equals(b)) {
      return a;
    }

    return null;
  }

  public int getVotesFor(Element e) {
    if (e.equals(a)) {
      return votesA;
    } else if (e.equals(b)) {
      return votesB;
    }
    return 0;
  }

  public int getVotes() {
    return votesA + votesB;
  }

  @PreRemove
  public void detatch() {
    a.set.relations.remove(this);
    a = null;
    b = null;
  }

  public static Relation findIn(List<Relation> list, Element a, Element b) {
    for (Relation r : list) {
      if (r.a.equals(a) || r.b.equals(a)) {
        if (r.a.equals(a) && r.b.equals(b))
          return r;
        if (r.b.equals(a) && r.a.equals(b))
          return r;
      }
    }
    return null;
  }

  public static void vote(DataSet set, Element a, Element b, boolean isForA) {

    if (a.id == b.id)
      return;

    Relation r = get(set, a, b);

    if (isForA) {
      r.voteFor(a);
    } else {
      r.voteFor(b);
    }

  }

  /* Private */
  private static Relation get(DataSet set, Element a, Element b) {

    String query = "select r from Relation r where r.a = ? and r.b = ?";
    Relation r = Relation.find(query, a, b).first();

    if (r == null) {
      /* switch a<->b */
      r = Relation.find(query, b, a).first();
      if (r == null) {
        /* implicitly create one */
        r = new Relation();
        r.set = set;
        r.a = a;
        r.b = b;
        set.relations.add(r);
        set.validateAndSave();
      }
    }
    return r;
  }
}

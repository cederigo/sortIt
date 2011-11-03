package models;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostRemove;
import javax.persistence.PreRemove;

import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Element extends Model implements Comparable<Element> {

  @ManyToOne(fetch = FetchType.LAZY)
  public DataSet set;

  @ManyToMany(cascade = CascadeType.ALL)
  public Set<Attribute> attributes;

  @Required
  public Blob blob;

  @Override
  public int compareTo(Element other) {
    // find comparisons: me <-> other

    return 0;
  }

  @PostRemove
  public void cleanup() {
    if (blob != null) {
      blob.getFile().delete();
    }
  }

  public String toString() {
    return "entry " + id + " [ " + set + " ]";
  }

}

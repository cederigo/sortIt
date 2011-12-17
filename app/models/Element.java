package models;

import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostRemove;
import javax.persistence.PreRemove;

import org.hibernate.annotations.Cascade;

import play.Logger;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Element extends Model implements Comparable<Element>{

  @ManyToOne
  public DataSet set;

  @ManyToMany(cascade=CascadeType.ALL)
  public List<Attribute> attributes;
  
  @Required
  public String value;
  
  @Required
  public int pos = -1;
  
  public int votes = 0;

  public String toString() {
    return "el id:" + id + ", votes:" + votes + ", set: " + set;
  }

  @Override
  public int compareTo(Element o) {
    return pos > o.pos ? 1 : (pos == 0 ? 0 : -1);
  }
 
}

package models;

import java.net.URL;
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

import org.hibernate.annotations.Cascade;

import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Element extends Model {

  @ManyToOne(fetch = FetchType.LAZY)
  public DataSet set;

  @ManyToMany(cascade = CascadeType.ALL)
  public Set<Attribute> attributes;
  
  public Blob blob;
  //or
  public String url;
  
  public int pos = -1;

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

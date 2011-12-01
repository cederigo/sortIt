package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;

import org.apache.commons.io.IOUtils;
import org.hibernate.annotations.FetchMode;

import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class DataSet extends Model {

  @Required
  public String name;

  @OneToMany(targetEntity = Element.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  public List<Element> elements;

  @OneToMany(targetEntity = Relation.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  public List<Relation> relations;

  public String toString() {
    return name;
  }

  public List<Element> randomElements(int limit) {
    List<Element> result = new LinkedList<Element>();

    for (int i = 0; i < limit; i++) {
      int rIdx = (int) (Math.random() * elements.size());
      result.add(elements.get(rIdx));
    }

    return result;

  }
  
  public List<Element> orderedElements(int limit) {
    
    String query = "select e from Element e where e.pos != -1 and e.set = ? order by e.pos";
    
    List<Element> result;
    
    if (limit > 0) {
      result = DataSet.find(query, this).fetch(limit); 
    } else {
      result = DataSet.find(query, this).fetch();
    }
    
    return result; 
  }

  public boolean addElements(Collection<File> files, Set<Attribute> elementAttributes,
      String blobType) throws FileNotFoundException {

    FileInputStream fis = null;

    for (File f : files) {

      try {
        fis = new FileInputStream(f);
        Blob b = new Blob();
        b.set(fis, blobType);
        Element e = new Element();
        e.blob = b;
        if (elementAttributes.size() > 0) {
          e.attributes = elementAttributes;
        }
        e.set = this;
        elements.add(e);

      } finally {
        IOUtils.closeQuietly(fis);
      }
    }
    return validateAndSave();
  }
  
  /**
   * We have a problem here. currently its O(n^2)
   */
  public void reorder() {
        
    List<Element> oEls = orderedElements(-1);
    List<Element> result = new LinkedList<Element>();
    Relation relCandidate;
    Relation rel = null;
    
    if (oEls.size() == 0) {
      /*first ordering*/
      oEls = elements;
    }
    
    result = new LinkedList<Element>(oEls);
    
    for (Element oEl : oEls) {
      /* choose the relation with the most votes*/
      for (Element e : elements) {
        if (e.id == oEl.id) continue;
        relCandidate = Relation.get(e, oEl);
        rel = (rel == null ? relCandidate : 
          (rel.votes < relCandidate.votes ? relCandidate : rel));
      }
      
      int idxA = result.indexOf(rel.a);
      int idxB = result.indexOf(rel.b);
      
      if ( ((idxA - idxB) > 0 && rel.value > 0) || (idxA -idxB) < 0 && rel.value < 0 ){
        /*correctly ordered*/
      } else {
        result.remove(idxB);
        if (rel.value > 0) result.add(idxA, rel.b);
        else {
          if (idxA + 1 < result.size())
            result.add(idxA + 1 , rel.b);
          else
            result.add(rel.b);
        }
      }
           
    }
    
    //update elements
    for (int i = 0; i < result.size(); i++) {
      Element e = result.get(i);
      e.pos = i;
      e.save();
    }
    
  }

}

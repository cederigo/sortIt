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
   * We have a problem here. Its not really performant
   */
  public void reorder() {
        
    List<Long> result = new LinkedList<Long>();
    Relation rel = null;
    
    for (Element e : elements) {
      rel = Relation.withMostVotes(e);
      if (rel == null) continue;
      
      int idxA = result.indexOf(rel.a.id);
      int idxB = result.indexOf(rel.b.id);
      
      if (idxA >= 0 && idxB >= 0) {
        //both already inside, check if still correct
        if ((rel.value > 0 && (idxB - idxA) > 0) || (rel.value < 0 && (idxB - idxA) < 0)) {
          //correct, do nothing
        } else {
          result.remove(idxB);
          if (rel.value > 0) addWithCheck(result, idxA + 1, rel.b.id);
          else addWithCheck(result,idxA,rel.b.id);
        }
      } else if(idxA >= 0 && idxB < 0) {
        //a is inside
        if (rel.value > 0) addWithCheck(result, idxA + 1, rel.b.id);
        else addWithCheck(result,idxA,rel.b.id);
        
      } else if(idxB >= 0 && idxA < 0) {
        //b is inside
        if (rel.value > 0) addWithCheck(result, idxB, rel.a.id);
        else addWithCheck(result,idxB + 1,rel.a.id);
        
      } else {
        //none is inside, add them at the end
        result.add(rel.a.id);
        if (rel.value > 0) result.add(rel.b.id); 
        else result.add(result.size()-1, rel.b.id);
      }
    }
    Logger.info("ordered %s elements",result.size());
    
    //update elements
    for (int i = 0; i < result.size(); i++) {
      Element e = Element.findById(result.get(i));
      e.pos = i;
      e.save();
    }
    
  }
  
  private void addWithCheck(List dest, int idx, Object el ) {
    if (idx < dest.size() - 1 && idx >= 0) {
      dest.add(idx, el);
    } else {
      dest.add(el);
    }
  }

}

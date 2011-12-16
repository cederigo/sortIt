package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.UniqueConstraint;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.hibernate.annotations.FetchMode;

import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.JPA;
import play.db.jpa.Model;
import sortIt.DataSorter;

@Entity
public class DataSet extends Model {

  @Required
  public String name;

  @OneToMany(cascade = {CascadeType.PERSIST}, orphanRemoval=true)
  public List<Element> elements;

  @OneToMany(cascade = CascadeType.ALL)
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

  public void doSort(DataSorter sorter) {
    sorter.doSort(elements, relations);
    save();
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

  public boolean addElements(Collection<String> values, List<Attribute> attributes) {
    for (String url : values) {
      Element e = new Element();
      
      e.value = url;
      e.attributes = attributes;
      elements.add(e);

    }
    return validateAndSave();
  }
  
  public static DataSet delete(long id) {
    DataSet set = DataSet.findById(id);
    if (set == null) return null;    
        
    return set.delete();
    
    
  }

}

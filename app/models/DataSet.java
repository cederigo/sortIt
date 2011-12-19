package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
  @Column(unique = true)
  public String name;

  @OneToMany(cascade = { CascadeType.PERSIST }, orphanRemoval = true)
  public List<Element> elements;

  @OneToMany(cascade = { CascadeType.PERSIST }, orphanRemoval = true)
  public List<Relation> relations;

  public String toString() {
    return name;
  }

  public Set<Element> nextElements(int limit) {

    /*order matters*/
    Set<Element> result = new LinkedHashSet<Element>();
    Collections.sort(elements); 
    
    /*start at a random pos*/
    int i = 1 + (int)(Math.random()*(elements.size()-1));
    int j = 0;
    Logger.debug("starting with i=%s", i);

    outer:
    for(;i < elements.size(); i++) {
      
      for (; j < i; j++) {
        if(result.size() >= limit) break outer;
        Element a = elements.get(i);
        Element b = elements.get(j);

        if (Relation.findIn(relations, a, b) == null) {
          /*
           * sort algorithm has no relation for those elements.Present them to
           * the user
           */
          result.add(a);
          result.add(b);
          if(i < elements.size() -1) {            
            j++;
            break; //because otherwise we would always present the same element(a)
          }
        }
        
      }
      
      
    }
    
    Logger.debug("added %s elements out of missing relations",result.size());

    if (result.size() <= limit) {
      /*
       * cool we can take decisions on most elementsadd some random elements
       */
      while(result.size() < limit) {
        result.add(elements.get((int) (Math.random() * elements.size())));
      }
      
    }

    return result;
  }

  public void doSort(DataSorter sorter) {
    
    Collections.sort(elements);
    
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
      e.set = this;
      e.value = url;
      e.attributes = attributes;
      elements.add(e);

    }
    return validateAndSave();
  }

  public static DataSet delete(long id) {
    DataSet set = DataSet.findById(id);
    if (set == null)
      return null;

    return set.delete();

  }

}

package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

  public List<Element> nextElements(int limit) {

    List<Element> els = Element.find(
        "select e from Element e where e.set = ? order by e.votes asc", this).fetch(limit);

    return els;
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

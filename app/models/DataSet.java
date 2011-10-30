package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;

import org.apache.commons.io.IOUtils;

import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class DataSet extends Model {

  @Required
  public String name;

  @OneToMany(targetEntity = Element.class, cascade=CascadeType.ALL, fetch = FetchType.LAZY)
  public List<Element> elements;

  @OneToMany(targetEntity = Relation.class, fetch = FetchType.LAZY)
  public List<Relation> relations;

  public String toString() {
    return name;
  }

  public boolean addElements(Collection<File> files,
      Set<Attribute> elementAttributes, String blobType)
      throws FileNotFoundException {

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

}

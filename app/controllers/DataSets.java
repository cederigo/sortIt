package controllers;

import java.io.IOException;
import java.util.List;

import models.DataSet;
import models.Element;
import models.Relation;
import play.data.validation.Required;
import play.mvc.Controller;
import sortIt.Importer;

public class DataSets extends Controller {


  public static void create(@Required String name) {
    
    DataSet set = new DataSet();
    set.name = name;
    
    if (!set.validateAndCreate()) {
      validation.keep();
      flash.error("could not create dataSet named %s ", name);
    }
    
    flash.success("DataSet created");
    SortIt.index();
    
  }
  
  public static void delete(@Required long id) {
    DataSet deleted = DataSet.delete(id);
    
    if(deleted != null) {
      flash.success("deleted %s successfully", deleted.name);
    } else {
      flash.success("could not delete DataSet with id %S", id);
    }
    SortIt.index();
  }
  
  public static void doImport(long setId, long[] attributeIds, String tag, int limit)
      throws IOException {

    DataSet dataSet = DataSet.findById(setId);

    if (dataSet == null) {
      String msg = "DataSet with id: " + setId + "not found";
      flash.error(msg);
      SortIt.index();
      return;
    }

    boolean success = true;
    if (tag != null) {
      success &= Importer.taggedWith(dataSet, tag, attributeIds, limit);
    }

    if (success) {
      flash.success("import into %s successful", dataSet.name);
    } else {
      flash.error("faild to import into %s", dataSet);
    }

    SortIt.index();
  }
  
  
  
  public static void get(@Required long id, int limit) {
    DataSet set = DataSet.findById(id);
    render(set,limit);
  }
  
  public static void elements(@Required String name) {
    
    DataSet set = DataSet.find("byName",name).first();
    
    if (set != null) {
      List<Element> els = set.orderedElements(-1);
      render(name,els);
    } else {
      error("could not find set with name " + name);
    }
    
  }

}

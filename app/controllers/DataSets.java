package controllers;

import java.util.List;

import models.DataSet;
import models.Element;
import models.Relation;
import play.data.validation.Required;
import play.mvc.Controller;

public class DataSets extends Controller {

  /* JavaScript JSON responses */

  public static void get(@Required long id) {
    DataSet set = DataSet.findById(id);
    render(set);
  }
  
  public static void elements(@Required long id) {
    
    DataSet set = DataSet.findById(id);
    
    if (set != null) {
      List<Element> els = set.orderedElements(-1);
      String name = set.name;
      render(name,els);
    } else {
      error("could not find set with id " + id);
    }
    
  }

}

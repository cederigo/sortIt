package controllers;

import java.util.List;

import models.Attribute;
import models.DataSet;
import play.mvc.Controller;

public class SortIt extends Controller {

  
  
  public static void index() {
    
    List<DataSet> sets = DataSet.findAll();
    List<Attribute> attributes = Attribute.findAll();
    
    render(sets,attributes);
    
  }
  
  
}

package controllers;

import models.DataSet;
import play.data.validation.Required;
import play.mvc.Controller;

public class DataSets extends Controller {

  /*JavaScript JSON responses*/
  
  public static void get(@Required long id) {
    DataSet set = DataSet.findById(id);
    render(set);
  }
   
}

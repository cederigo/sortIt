package controllers;

import models.DataSet;
import play.data.validation.Required;
import play.mvc.Controller;

/**
 * A Game that creates Relations used to sort data
 * 
 * @author cede
 */

public class Memory extends Controller {

  public static void index(@Required String dataSetName) {
    
    long setId = -1;
    DataSet dataSet = DataSet.find("name", dataSetName).first();
    setId = dataSet.id;
    
    render(setId);
    
    
  }

}

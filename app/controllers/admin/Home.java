package controllers.admin;

import java.util.List;

import models.Attribute;
import models.DataSet;

import play.mvc.Controller;
import controllers.CRUD;


public class Home extends Controller {
  
  public static void index() {
    
    List<Attribute> attributes = Attribute.all().fetch();
    List<DataSet> dataSets = DataSet.all().fetch();
    
    render("CRUD/index.html", attributes, dataSets);
    
  }
  
  
}

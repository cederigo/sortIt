package controllers;

import models.DataSet;
import models.Element;
import models.Relation;
import play.data.validation.Required;
import play.mvc.Controller;

public class Relations extends Controller {

  public static void vote(
      @Required long setId,
      @Required long aId, 
      @Required long bId, @Required boolean isForA) {

    DataSet set = DataSet.findById(setId);
    Element a = Element.findById(aId);
    Element b = Element.findById(bId);

    if (a == null || b == null) {
      error("invalid elements: " + aId + "," + bId);
    }
    
    Relation.vote(set, a, b, isForA);

  }

}

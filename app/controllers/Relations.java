package controllers;

import models.Element;
import models.Relation;
import play.data.validation.Required;
import play.mvc.Controller;

public class Relations extends Controller {

  public static void vote(@Required long aId, @Required long bId, @Required boolean isForA) {

    Element a = Element.findById(aId);
    Element b = Element.findById(bId);

    if (a == null || b == null) {
      error("invalid elements: " + aId + "," + bId);
    }
    
    Relation.vote(a, b, isForA);

  }

}

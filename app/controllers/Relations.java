package controllers;

import models.Element;
import models.Relation;
import play.data.validation.Required;
import play.mvc.Controller;

public class Relations extends Controller {

  public static void add(@Required long aId, @Required long bId, @Required boolean isForA) {

    Element a = Element.findById(aId);
    Element b = Element.findById(bId);

    /* implicitly creates it */
    Relation r = Relation.get(a, b);

    if (r != null) {
      r.votes++;
      r.value += isForA ? +1 : -1;
      r.save();
    }

  }

}

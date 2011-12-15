package controllers;

import org.hibernate.annotations.OptimisticLock;

import models.Element;
import play.mvc.Controller;

public class Elements extends Controller {

  /* data */
  public static void data(long id) {
    Element e = Element.findById(id);
    
    if (e == null) {
      error("element with id:" + id + " not found");
    } else {
      if (e.blob != null) {
        renderBinary(e.blob.get(), "el-" + e.id, e.blob.type(), true);
      } else {
        renderText(e.url);
      }
    }
    
  }

}

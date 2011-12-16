package controllers;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.hibernate.annotations.OptimisticLock;

import models.Element;
import play.Logger;
import play.libs.F.Action;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Controller;

public class Elements extends Controller {

  /* data */
  public static void data(long id) {
    Element e = Element.findById(id);
    
    if (e == null) {
      error("element with id:" + id + " not found");
    } else {
      if (e.blob != null && e.blob.length() > 0) {
        renderBinary(e.blob.get(), "el-" + e.id, e.blob.type(), true);
      } else {
        Promise<HttpResponse> promise = WS.url(e.url).getAsync();
        HttpResponse resp;
        try {
          resp = promise.get(1, TimeUnit.SECONDS);
          renderBinary(resp.getStream(), "el-" + e.id, resp.getContentType() , true);
        } catch (Exception ex) {
          String msg = "timed out: " + ex;
          Logger.warn(msg);
          error(msg);
        }
      }
    }
    
  }

}

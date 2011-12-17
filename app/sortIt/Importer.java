package sortIt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import play.Play;
import play.db.jpa.Blob;
import play.libs.Files;
import models.Attribute;
import models.DataSet;
import models.Element;
import sortIt.flickr.FlickrApi;
import sortIt.flickr.Photos;
import sortIt.flickr.Photos.Photo;

public class Importer {
  

  public static boolean taggedWith(DataSet set, String tag, long[] attributeIds) {

    Photos photos = FlickrApi.search(tag);

    if (photos != null && photos.pages > 0) {

      /* collect urls */
      List<String> urls = new LinkedList<String>();

      for (Photo p : photos.photo) {
        urls.add(FlickrApi.urlFor(p, "m"));
      }
      
      return set.addElements(urls, getAttributes(attributeIds));
    }
    
    return false;
    
  }
  
  private static List<Attribute> getAttributes(long[] attributeIds) {
    
    List<Attribute> attributes = new LinkedList<Attribute>();
    
    if (attributeIds != null) {
      for(long aId : attributeIds) {
        Attribute a = Attribute.findById(aId);
        if (a != null) {
          attributes.add(a);        
        }
      }      
    }
    
    return attributes;
  }

}

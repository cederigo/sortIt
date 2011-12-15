package sortIt;

import java.util.LinkedList;
import java.util.List;

import play.db.jpa.Blob;
import models.DataSet;
import models.Element;
import sortIt.flickr.FlickrApi;
import sortIt.flickr.Photos;
import sortIt.flickr.Photos.Photo;

public class FlickrImporter {

  public boolean importIn(DataSet set) {

    Photos photos = FlickrApi.searchWithTag("portrait");

    if (photos != null && photos.pages > 0) {

      /* collect urls */
      List<String> urls = new LinkedList<String>();

      for (Photo p : photos.photo) {
        urls.add(FlickrApi.urlFor(p, "t"));
      }
      
      return set.addElements(urls);
    }
    
    return false;

  }

}

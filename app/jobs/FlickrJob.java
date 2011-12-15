package jobs;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import jobs.FlickrJob.Photos.Photo;

import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import play.libs.WS;
import play.utils.HTTP;

@Every("10s")
public class FlickrJob extends Job {

  private static final String API_KEY = "cffb4e8b9ed3729f5051e2dc246b825e";
  //private static final String API_SECRET = "3d6fc7e9c9f12351";
  private static final String API_ENDPOINT = "http://api.flickr.com/services/rest/?";
  
  public static class PhotosWrapper {
    Photos photos;
  }
  
  public static class Photos {
    int page;
    int pages;
    int perpage;
    int total;
    public Photo[] photo;    
    
    public static class Photo {
      String id;
      String owner;
      String secret;
      String server;
      String farm;
      String title;
      boolean ispublic;
    }
    
  }
  
  /*methods*/
  private static final String API_SEARCH = "flickr.photos.search";
  
//  Size Suffixes
//
//  The letter suffixes are as follows:
//  s small square 75x75
//  t thumbnail, 100 on longest side
//  m small, 240 on longest side
//  - medium, 500 on longest side
//  z medium 640, 640 on longest side
//  b large, 1024 on longest side*
//  o original image, either a jpg, gif or png, depending on source format
//  "http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg"
  private static final String IURL_TPL = "http://farm%s.staticflickr.com/%s/%s_%s_%s.jpg";

  public void doJob() {
    
    JsonElement jsonResp = WS.url(prepareRequestUrl(API_SEARCH)+"&tags=%s","portrait").get().getJson();
    
    Gson g = new Gson();
    Photos photos = g.fromJson(
        jsonResp.getAsJsonObject(),
        PhotosWrapper.class).photos;
    
    if (photos.pages > 0) {
      /*only page 1*/
      for (Photo p : photos.photo) {
        
        String photoUrl = String.format(IURL_TPL,p.farm,p.server,p.id,p.secret,"t");
        Logger.debug("got flickr image url: " + photoUrl);
        /*add to dataset*/
        
      }
    }
    
  }
  
  
  private String prepareRequestUrl(String method) {
    String result = API_ENDPOINT;
    result += "api_key=" + API_KEY;
    result += "&method=" + method;
    result += "&format=json";
    result += "&nojsoncallback=1";
    return result;
  }

}

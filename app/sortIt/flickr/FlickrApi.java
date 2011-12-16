package sortIt.flickr;

import play.Logger;
import play.libs.WS;
import sortIt.flickr.Photos.Photo;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class FlickrApi {
  
  private static final String API_KEY = "cffb4e8b9ed3729f5051e2dc246b825e";
  //private static final String API_SECRET = "3d6fc7e9c9f12351";
  private static final String API_ENDPOINT = "http://api.flickr.com/services/rest/?";
  
  public static class PhotosWrapper {
    Photos photos;
  }
  
  
  /*methods*/
  private static final String API_SEARCH = "flickr.photos.search";
  
/*
  Size Suffixes

  The letter suffixes are as follows:
  s small square 75x75
  t thumbnail, 100 on longest side
  m small, 240 on longest side
  - medium, 500 on longest side
  z medium 640, 640 on longest side
  b large, 1024 on longest side*
  o original image, either a jpg, gif or png, depending on source format
  "http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg"
  */
  private static final String IURL_TPL = "http://farm%s.staticflickr.com/%s/%s_%s_%s.jpg";
  
  public static Photos taggedWith(String tag) {
    return getPhotos(API_SEARCH,"&tags=%s",tag);
  }
  
  public static String urlFor(Photo p, String size) {
    if (p == null) return null;
    return String.format(IURL_TPL, p.farm,p.server,p.id,p.secret,size);
  }

  
  private static Photos getPhotos(String method, String urlSuffix, String ... params) {
    
    JsonElement e = WS.url(prepareRequestUrl(method)+urlSuffix, params).get().getJson();
    
    if (e == null) return null;
    
    Gson g = new Gson();
    PhotosWrapper wrapper = g.fromJson(
        e.getAsJsonObject(),
        PhotosWrapper.class); 
    
    return wrapper != null ? wrapper.photos : null; 
    
  }
  
  
  private static String prepareRequestUrl(String method) {
    String result = API_ENDPOINT;
    result += "api_key=" + API_KEY;
    result += "&method=" + method;
    result += "&format=json";
    result += "&nojsoncallback=1";
    return result;
  }

}

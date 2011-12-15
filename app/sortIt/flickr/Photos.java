package sortIt.flickr;

public class Photos {
  public int page;
  public int pages;
  public int perpage;
  public int total;
  public Photo[] photo;    
  
  public static class Photo {
    public String id;
    public String owner;
    public String secret;
    public String server;
    public String farm;
    public String title;
    boolean ispublic;
  }
}

package jobs;

import java.util.List;

import models.DataSet;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;

@Every("60s")
public class OrderJob extends Job {
  
  
  public void doJob() {
    
    
    List<DataSet> dataSets = DataSet.all().fetch();
    
    for (DataSet ds : dataSets) {
      Logger.info("reordering dataset " + ds.name);
      ds.reorder();
      Logger.info("reordering dataset " + ds.name+" done");
    }
    
  }

}

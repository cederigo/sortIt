package jobs;

import java.util.List;

import models.DataSet;
import models.Element;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import sortIt.DataSorter;
import sortIt.InsertionSort;

@Every("10s")
public class OrderJob extends Job {
  
  public void doJob() {
    
    DataSorter sorter = new InsertionSort();
    
    List<DataSet> dataSets = DataSet.all().fetch();
    
    for (DataSet ds : dataSets) {
      Logger.info("reordering dataset " + ds.name);
      
      ds.doSort(sorter);
      
      Logger.info("reordering dataset " + ds.name+" done");
    }
    
  }

}

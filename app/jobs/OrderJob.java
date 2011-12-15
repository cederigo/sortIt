package jobs;

import java.util.List;

import models.DataSet;
import models.Element;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import sortIt.DataSorter;
import sortIt.InsertionSort;

@Every("60s")
public class OrderJob extends Job {
  
  private DataSorter sorter = new InsertionSort();
  
  public void doJob() {
    
    List<DataSet> dataSets = DataSet.all().fetch();
    
    for (DataSet ds : dataSets) {
      ds.doSort(sorter);
    }
    
  }

}

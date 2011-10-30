package controllers.admin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import models.Attribute;
import models.DataSet;

import play.Logger;
import play.Play;
import play.libs.Files;

import controllers.CRUD;

public class DataSets extends CRUD {

  public static void importZip(File zip, long setId, long[] attributeIds) throws IOException {
    
    DataSet dataSet = DataSet.findById(setId);
    Set<Attribute> attributes = new HashSet<Attribute>();
    
    if (zip == null || dataSet == null) {
      String msg = "invalid parameters!";
      flash.error(msg);
      CRUD.index();
      return;
    }
    
    if (attributeIds != null) {
      for(long aId : attributeIds) {
        Attribute a = Attribute.findById(aId);
        if (a != null) {
          attributes.add(a);        
        }
      }      
    }
      
    File unzipDest = new File(Play.tmpDir,zip.getName());
    Files.unzip(zip, unzipDest);
    
    boolean success = dataSet.addElements(FileUtils.listFiles(unzipDest, null, true)
         , attributes, "image/jpeg");
    FileUtils.deleteDirectory(unzipDest);
    
    if (success) {
      flash.success("import into %s successful", dataSet.name);
    } else {
      flash.error("faild to import into %s", dataSet);
    }
    
    CRUD.index();
    
  }
  
  
  
}

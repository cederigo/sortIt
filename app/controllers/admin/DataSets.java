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
import play.data.validation.Required;
import play.libs.Files;
import play.mvc.After;
import sortIt.Importer;

import controllers.CRUD;

public class DataSets extends CRUD {

  public static void doImport(File zip, long setId, long[] attributeIds, String tag)
      throws IOException {

    DataSet dataSet = DataSet.findById(setId);

    if (dataSet == null) {
      String msg = "DataSet with id: " + setId + "not found";
      flash.error(msg);
      CRUD.index();
      return;
    }

    boolean success = true;
    if (zip != null) {
      success &= Importer.fromZip(dataSet, zip, attributeIds);
    }
    if (tag != null) {
      success &= Importer.taggedWith(dataSet, tag);
    }

    if (success) {
      flash.success("import into %s successful", dataSet.name);
    } else {
      flash.error("faild to import into %s", dataSet);
    }

    CRUD.index();

  }

}

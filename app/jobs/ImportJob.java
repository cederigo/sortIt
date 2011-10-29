package jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import models.Attribute;
import models.DataSet;
import models.Element;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import play.Logger;
import play.Play;
import play.db.jpa.Blob;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class ImportJob extends Job {

  private boolean checkEnabled() {
    return Play.configuration.getProperty("importjob.enabled", "false").equals(
        "true");
  }

  public void doJob() {

    if (!checkEnabled()) {
      return;
    }

    Logger.info("starting Import");

    String[] attributeNames = Play.configuration.getProperty("importjob.attributes", "")
        .split(",");
    String type = Play.configuration.getProperty("importjob.type");
    String importPath = Play.configuration.getProperty("importjob.path");
    String setName = Play.configuration.getProperty("importjob.set");

    FileInputStream fis = null;
    File importFolder;

    if (new File(importPath).isAbsolute()) {
      importFolder = new File(importPath);
    } else {
      importFolder = Play.getFile(importPath);
    }
    
    Collection<File> filesToImport;

    try {
      filesToImport = FileUtils.listFiles(importFolder, null, true);
    } catch (Exception e) {
      Logger.error(e, "Could not list files from %s",importFolder.getAbsolutePath());
      return;
    }

    /* DataSet */
    DataSet newSet = DataSet.find("byName", setName).first();

    if (newSet != null) {
      Logger.warn("Set %s already exists. Aborting import!", setName);
      return;
    } else {
      Logger.info("Creating Set %s for import", setName);
      newSet = new DataSet();
      newSet.name = setName;
      newSet.elements = new LinkedList<Element>();
      if (!newSet.validateAndCreate()) {
        Logger.error("could not create set for import");
        return;
      }
    }

    /* Attributes */
    Set<Attribute> attributes = new HashSet<Attribute>();
    for (String attributeName : attributeNames) {
      Attribute a = Attribute.find("byName", attributeName).first();
      if (a != null) {
        attributes.add(a);
      } else {
        a = new Attribute();
        a.name = attributeName;
        a.description = attributeName;
        if (!a.validateAndSave()) {
          Logger.warn("could not create Attribute %s", attributeName);
        } else {
          attributes.add(a);
        }
      }
    }

    Logger
        .info(
            "importing %s files from %s into set %s with the following attributes %s",
            filesToImport.size(), importFolder.getAbsolutePath(), setName,
            attributes.toString());

    for (File f : filesToImport) {
      try {

        fis = new FileInputStream(f);
        Blob b = new Blob();
        b.set(fis, type);
        Element e = new Element();
        e.blob = b;
        e.attributes = attributes;
        e.set = newSet;
        newSet.elements.add(e);

      } catch (FileNotFoundException e) {
        Logger.warn(e, "could not import file");
      } catch (Exception e) {
        Logger.error(e, "strange error, breaking loop");
        break;
      } finally {
        IOUtils.closeQuietly(fis);
      }
    }

    if (!newSet.validateAndSave()) {
      Logger.error("Could not save Set");
    }

    Logger.info("Import completed");

  }

}

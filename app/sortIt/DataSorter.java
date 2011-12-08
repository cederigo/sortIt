package sortIt;

import java.util.List;

import models.Element;
import models.Relation;

public interface DataSorter {
  void doSort(List<Element> elements, List<Relation> relations);
}

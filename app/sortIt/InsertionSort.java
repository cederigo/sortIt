package sortIt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import play.Logger;
import models.DataSet;
import models.Element;
import models.Relation;

public class InsertionSort implements DataSorter {

  @Override
  public void doSort(List<Element> elements, List<Relation> relations) {
    /* copy of unordered list */

    /* Insertion sort. O(n^2) */
    for (int p = 1; p < elements.size(); p++) {
      Element pe = elements.get(p); /* pointer element */
      Map<Integer, List<Relation>> candidates = new HashMap<Integer, List<Relation>>();
      for (int i : new int[] { -1, 0, 1 })
        candidates.put(i, new LinkedList<Relation>());

      for (int i = 0; i < p; i++) {
        /* i to p is sorted, do we have a relation saying where to insert */
        Element me = elements.get(i);
        Relation r = Relation.findIn(relations,pe, me);
        if (r != null) {
          candidates.get(r.valueFor(pe)).add(r);
        }
      }

      Relation decision = resolveCandidates(candidates);

      if (decision != null) {
        int oi = elements.indexOf(decision.getOther(pe));
        if (decision.valueFor(pe) >= 0) {
          addWithCheck(elements, oi + 1, pe);
        } else {
          addWithCheck(elements, oi, pe);
        }
        /* we inserted before, so the index has shifted */
        elements.remove(p + 1);
      } else {
        Logger.debug("no decision for %s", pe);
      }
    }
    
    /*update positions*/
    int i = 0;
    for (Element e : elements) {
      e.pos = i++;
    }
    
  }
  
  /*helpers*/
  private void addWithCheck(List dest, int idx, Object el) {
    if (idx < dest.size() - 1 && idx >= 0) {
      dest.add(idx, el);
    } else {
      dest.add(el);
    }
  }

  private static Relation resolveCandidates(Map<Integer, List<Relation>> candidates) {

    Relation result = null;

    /**
     * Test for transitivity. not ok: 0 -> 1, -1 -> 1, -1 -> 0
     */
    // 0 -> 1
    if (!candidates.get(0).isEmpty() && !candidates.get(1).isEmpty()) {
      if (sumVotes(candidates.get(0)) > sumVotes(candidates.get(1))) {
        result = candidates.get(0).get(0);
      } else {
        result = candidates.get(1).get(0);
      }
    } else if (!candidates.get(-1).isEmpty() && !candidates.get(1).isEmpty()) {
      if (sumVotes(candidates.get(-1)) > sumVotes(candidates.get(1))) {
        result = candidates.get(-1).get(0);
      } else {
        result = candidates.get(1).get(0);
      }
    } else if (!candidates.get(-1).isEmpty() && !candidates.get(0).isEmpty()) {
      if (sumVotes(candidates.get(-1)) > sumVotes(candidates.get(0))) {
        result = candidates.get(-1).get(0);
      } else {
        result = candidates.get(0).get(0);
      }
    } else {
      if (!candidates.get(-1).isEmpty()) {
        result = candidates.get(-1).get(0);
      } else if (!candidates.get(0).isEmpty()) {
        result = candidates.get(0).get(0);
      } else if (!candidates.get(1).isEmpty()) {
        result = candidates.get(1).get(candidates.get(1).size() - 1);
      }
    }

    return result;

  }

  private static int sumVotes(List<Relation> rs) {

    int res = 0;

    for (Relation r : rs) {
      res += r.getVotes();
    }

    return res;
  }

}

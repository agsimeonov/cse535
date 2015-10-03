import java.util.Comparator;

/**
 * Comparator used to sort Entries by ascending document IDs.
 * 
 * @author Alexander Simeonov
 */
public class DocIdComparator implements Comparator<Entry> {
  @Override
  public int compare(Entry first, Entry second) {
    return first.getDocId().compareTo(second.getDocId());
  }
}

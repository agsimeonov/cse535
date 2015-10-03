import java.util.Comparator;

/**
 * Comparator used to sort Entries by descending document frequency.
 * 
 * @author Alexander Simeonov
 */
public class TermFrequencyComparator implements Comparator<Entry> {
  @Override
  public int compare(Entry first, Entry second) {
    if (first.getTermFrequency() < second.getTermFrequency()) return 1;
    else if (first.getTermFrequency() == second.getTermFrequency()) return 0;
    else return -1;
  }
}

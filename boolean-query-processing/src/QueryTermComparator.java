import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Used for query optimization, reorders query terms from one with smallest to largest posting list.
 * 
 * @author Alexander Simeonov
 */
public class QueryTermComparator implements Comparator<String> {
  private final Map<String, List<Entry>> dictionary;
  
  /**
   * Used for query optimization, reorders queries from one with smallest to largest posting list.
   * 
   * @param dictionary - used to find the posting list for a given query
   */
  public QueryTermComparator(Map<String, List<Entry>> dictionary) {
    this.dictionary = dictionary;
  }

  @Override
  public int compare(String first, String second) {
    List<Entry> a = dictionary.get(first);
    List<Entry> b = dictionary.get(second);
    if (a == null && b == null) return 0;
    else if (a == null) return -1;
    else if (b == null) return 1;
    else if (a.size() < b.size()) return -1;
    else if (a.size() > b.size()) return 1;
    else return 0;
  }
}

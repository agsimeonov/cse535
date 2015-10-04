import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single query.
 * 
 * @author Alexander Simeonov
 */
public class Query {
  private final List<String> terms;
  
  /**
   * Initializes the query from a single query line of terms.
   * 
   * @param line - a line of terms
   */
  public Query(String line) {
    String[] split = line.split(" ");
    terms = new ArrayList<String>(split.length);
    for (String term : split)
      terms.add(term);
  }
  
  /**
   * Acquires the query terms.
   * 
   * @return the query terms
   */
  public List<String> getTerms() {
    return terms;
  }
  
  @Override
  public String toString() {
    return terms.toString();
  }
}

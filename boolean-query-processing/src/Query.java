import java.util.Arrays;

/**
 * Represents a single query.
 * 
 * @author Alexander Simeonov
 */
public class Query {
  private final String[] terms;
  
  /**
   * Initializes the query from a single query line of terms.
   * 
   * @param line - a line of terms
   */
  public Query(String line) {
    terms = line.split(" ");
  }
  
  /**
   * Acquires the query terms.
   * 
   * @return the query terms
   */
  public String[] getTerms() {
    return terms;
  }
  
  @Override
  public String toString() {
    return Arrays.toString(terms);
  }
}

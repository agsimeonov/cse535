import java.util.List;

/**
 * Contains the results of a query and the number of comparisons.
 * 
 * @author Alexander Simeonov
 */
public class Result {
  private final List<Entry> results;
  private final int comparisons;
  
  /**
   * The results of a query and number of comparisons.
   * 
   * @param results - results of a query
   * @param comparisons - number of comparisons
   */
  public Result(List<Entry> results, int comparisons){
    this.results = results;
    this.comparisons = comparisons;
  }
  
  /**
   * Acquires the results of a query.
   * 
   * @return the results of a query
   */
  public List<Entry> getResults() {
    return results;
  }
  
  /**
   * Acquires the number of comparisons.
   * 
   * @return the number of comparisons
   */
  public int getComparisons() {
    return comparisons;
  }
  
  @Override
  public String toString() {
    return getResults().toString().replaceAll("\\[|\\]", "");
  }
}

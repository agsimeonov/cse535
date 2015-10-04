/**
 * An iterator over the queries in a given query file.
 * 
 * @author Alexander Simeonov
 */
public class QueryIterator extends LineIterator<Query> {
  /**
   * Creates an iterator for the queries in a given file.
   * 
   * @param indexFile - path to the index file
   */
  public QueryIterator(String indexFile) {
    super(indexFile);
  }

  @Override
  public Query next() {
    String line = getLine();
    return line == null ? null : new Query(line);
  }
}

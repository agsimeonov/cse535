import java.util.Iterator;

/**
 * Provides an iterable over the queries in the query file.
 * 
 * @author Alexander Simeonov
 */
public class Queries implements Iterable<Query> {
  private final String indexFile;

  /**
   * Provides an iterable over the queries in the query file.
   * 
   * @param indexFile - path to the queries file
   */
  public Queries(String indexFile) {
    this.indexFile = indexFile;
  }

  @Override
  public Iterator<Query> iterator() {
    return new QueryIterator(indexFile);
  }
}

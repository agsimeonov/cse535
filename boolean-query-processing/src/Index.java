import java.util.Iterator;

/**
 * Provides an iterable over the postings given in an index file.
 * 
 * @author Alexander Simeonov
 */
public class Index implements Iterable<Posting> {
  private final String indexFile;

  /**
   * Provides an iterable over the postings given in an index file.
   * 
   * @param indexFile - path to the index file
   */
  public Index(String indexFile) {
    this.indexFile = indexFile;
  }

  @Override
  public Iterator<Posting> iterator() {
    return new IndexIterator(indexFile);
  }
}

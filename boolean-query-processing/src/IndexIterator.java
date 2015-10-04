/**
 * An iterator over the postings in a given index file.
 * 
 * @author Alexander Simeonov
 */
public class IndexIterator extends LineIterator<Posting> {
  /**
   * Creates an iterator for the postings in a given index file.
   * 
   * @param indexFile - path to the index file
   */
  public IndexIterator(String indexFile) {
    super(indexFile);
  }

  @Override
  public Posting next() {
    String line = getLine();
    return line == null ? null : new Posting(line);
  }
}

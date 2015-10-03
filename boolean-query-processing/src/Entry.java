/**
 * Represents an entry in a postings list.
 * 
 * @author Alexander Simeonov
 */
public class Entry {
  private final String docId;
  private final int count;
  
  
  public Entry(String entry) {
    String[] split = entry.split("/");
    docId = split[0];
    count = Integer.parseInt(split[1]);
  }
  
  /**
   * Acquires the document ID.
   * 
   * @return the document ID.
   */
  public String getDocId() {
    return docId;
  }
  
  /**
   * Acquires the number of occurrences for a term.
   * 
   * @return the number of occurrences for a term
   */
  public int getCount() {
    return count;
  }
  
  @Override
  public String toString() {
    return docId + "/" + count;
  }
}

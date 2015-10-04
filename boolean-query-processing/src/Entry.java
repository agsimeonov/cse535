/**
 * Represents an entry in a postings list.
 * 
 * @author Alexander Simeonov
 */
public class Entry {
  private final String docId;
  private final int count;
  private static boolean showCount = false;
  
  /**
   * Initializes the entry, toString() will display only docIDs.
   * 
   * @param entry
   */
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
  public int getTermFrequency() {
    return count;
  }
  
  /**
   * Determines whether the toString() representations displays the term frequency.
   * 
   * @param showCount - true to display the term frequency otherwise false
   */
  public static void showCount(boolean showCount) {
    Entry.showCount = true;
  }
  
  @Override
  public String toString() {
    return showCount ? docId + "/" + count : docId;
  }
}

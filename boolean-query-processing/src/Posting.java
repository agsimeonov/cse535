import java.util.ArrayList;


/**
 * Represents a posting in the index. 
 * 
 * @author Alexander Simeonov
 */
public class Posting {
  private final String term;
  private final int size;
  private final ArrayList<Entry> postingList = new ArrayList<Entry>();
  
  /**
   * Creates a posting from a line in the index file.
   * 
   * @param line - a line in the index file
   */
  public Posting(String line) {
    String[] split = line.split("\\\\[cm]");
    term = split[0];
    size = Integer.parseInt(split[1]);
    String list = split[2].substring(1, split[2].length() - 1);
    for (String entry : list.split(", "))
      postingList.add(new Entry(entry));
  }
  
  /**
   * Acquires the term.
   * 
   * @return the term
   */
  public String getTerm() {
    return term;
  }
  
  /**
   * Acquires the size of the posting list.
   * 
   * @return size of the posting list
   */
  public int getSize() {
    return size;
  }
  
  
  /**
   * Acquires the posting list.
   * 
   * @return the posting list
   */
  public ArrayList<Entry> getPostingList() {
    return postingList;
  }
  
  @Override
  public String toString() {
    return term + "\\c" + size + "\\m" + postingList;
  }
} 

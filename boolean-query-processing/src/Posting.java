import java.util.LinkedList;
import java.util.List;

/**
 * Represents a posting in the index. 
 * 
 * @author Alexander Simeonov
 */
public class Posting implements Comparable<Posting> {
  private final String term;
  private final List<Entry> postingList;
  
  /**
   * Creates a posting from a line in the index file.
   * 
   * @param line - a line in the index file
   */
  public Posting(String line) {
    String[] split = line.split("\\\\[cm]");
    term = split[0];
    postingList = new LinkedList<Entry>();
    String entries = split[2].substring(1, split[2].length() - 1);
    for (String entry : entries.split(", "))
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
    return postingList.size();
  }
  
  
  /**
   * Acquires the posting list.
   * 
   * @return the posting list
   */
  public List<Entry> getPostingList() {
    return postingList;
  }
  
  @Override
  public String toString() {
    return getTerm() + "\\c" + getSize() + "\\m" + getPostingList();
  }

  @Override
  public int compareTo(Posting posting) {
    if (this.getSize() < posting.getSize()) return 1;
    else if (this.getSize() == posting.getSize()) return 0;
    else return -1;
  }
} 

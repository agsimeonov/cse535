import java.util.Arrays;

/**
 * Represents a posting in the index. 
 * 
 * @author Alexander Simeonov
 */
public class Posting implements Comparable<Posting> {
  private final String term;
  private final Entry postingList[];
  
  /**
   * Creates a posting from a line in the index file.
   * 
   * @param line - a line in the index file
   */
  public Posting(String line) {
    String[] split = line.split("\\\\[cm]");
    term = split[0];
    postingList = new Entry[Integer.parseInt(split[1])];
    String list = split[2].substring(1, split[2].length() - 1);
    String[] entries = list.split(", ");
    for (int i = 0; i < getSize(); i++)
      postingList[i] = new Entry(entries[i]);
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
    return postingList.length;
  }
  
  
  /**
   * Acquires the posting list.
   * 
   * @return the posting list
   */
  public Entry[] getPostingList() {
    return postingList;
  }
  
  @Override
  public String toString() {
    return getTerm() + "\\c" + getSize() + "\\m" + Arrays.toString(getPostingList());
  }

  @Override
  public int compareTo(Posting posting) {
    if (this.getSize() < posting.getSize()) return 1;
    else if (this.getSize() == posting.getSize()) return 0;
    else return -1;
  }
} 

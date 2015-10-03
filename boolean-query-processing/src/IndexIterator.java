import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * An iterator over the postings in a given index file.
 * 
 * @author Alexander Simeonov
 */
public class IndexIterator implements Iterator<Posting> {
  private BufferedReader reader;

  /**
   * Creates an iterator for the postings in a given index file.
   * 
   * @param indexFile - path to the index file
   */
  public IndexIterator(String indexFile) {
    try {
      FileReader fileReader = new FileReader(indexFile);
      reader = new BufferedReader(fileReader);
    } catch (FileNotFoundException e) {
      System.out.println("Index file not found: " + indexFile);
      System.exit(0);
    }
  }

  @Override
  public boolean hasNext() {
    if (reader == null) return false;
    
    try {
      if (reader.ready()) {
        return true;
      } else {
        reader.close();
        reader = null;
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Posting next() {
    if (!this.hasNext()) return null;
    String line = null;

    try {
      line = reader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return line == null ? null : new Posting(line);
  }
}

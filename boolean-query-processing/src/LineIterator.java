import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * An iterator over the lines in a given file.
 * 
 * @author Alexander Simeonov
 */
public abstract class LineIterator<T> implements Iterator<T> {
  protected BufferedReader reader;

  /**
   * Creates an iterator for the lines in a given file.
   * 
   * @param file - path to the given file
   */
  public LineIterator(String file) {
    try {
      FileReader fileReader = new FileReader(file);
      reader = new BufferedReader(fileReader);
    } catch (FileNotFoundException e) {
      System.out.println("File not found: " + file);
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
  
  /**
   * Acquires the next line in the file, null if none available.
   * 
   * @return the next line in the file, null if none available.
   */
  protected String getLine() {
    if (!this.hasNext()) return null;
    String line = null;

    try {
      line = reader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return line;
  }
  
  @Override
  public abstract T next();
}

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Used to log the output of the boolean query processor.
 * 
 * @author Alexander Simeonov
 */
public class Log {
  private BufferedWriter writer;
  private BufferedWriter system;
  
  /**
   * Initializes the log file.
   * 
   * @param file - log file location
   */
  public Log(String file) {
    try {
      FileOutputStream stream = new FileOutputStream(file, false);
      this.writer = new BufferedWriter(new OutputStreamWriter(stream));
      this.system = new BufferedWriter(new OutputStreamWriter(System.out));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  /**
   * Logs a given string.
   * 
   * @param string - the given string
   */
  public void log(String string) {
    try {
      writer.append(string);
      writer.append("\n");
      system.append(string);
      system.append("\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    System.out.println(string);
  }
  
  /** Closes out the log file. */
  public void close() {
    try {
      writer.close();
      system.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

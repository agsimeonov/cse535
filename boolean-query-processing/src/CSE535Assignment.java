import java.util.ArrayList;
import java.util.Collections;


/**
 * The driver for the Boolean Query Processor based on Posting Lists.
 * 
 * @author Alexander Simeonov
 */
public final class CSE535Assignment {
  private static final ArrayList<Posting> postings = new ArrayList<Posting>();
  
  /**
   * Processes the command line arguments.
   * 
   * @param args - command line arguments
   */
  private static void setArguments(String[] args) {
    if (args.length != 4) {
      System.out.println("Expected format:");
      System.out.println("java CSE535Assignment term.idx output.log 10 query_file.txt");
      System.exit(0);
    }
    
    for (Posting posting : new Index(args[0])) {
      postings.add(posting);
    }
    
    Collections.sort(postings);
  }
  
  /**
   * This returns the key dictionary terms that have the K largest postings lists. The result is 
   * expected to be an ordered string in the descending order of result postings, i.e., largest in 
   * the first position, and so on. The output should be formatted as follows (K=10 for an example)
   * 
   * FUNCTION: getTopK 10
   * Result: term1, term2, term3..., term10 (list the terms)
   * 
   * @param K - number of largest postings list to get
   * @return the key dictionary terms that have the K largest postings lists
   */
  public static String getTopK(int K) {
    String out = "FUNCTION: getTopK " + K + "\n";
    
    if (K > postings.size()) K = postings.size();
    
    for (int i = 0; i < K; i++) {
      out += postings.get(i).getTerm();
      if (i + 1 < K) out += ", ";
    }
    
    return out + "\n";
  }
  
	/**
	 * Main entry point.
	 * 
	 * @param args - command line arguments
	 */
	public static void main(String[] args) {
	  setArguments(args);
	  System.out.println(getTopK(20));
	}
}

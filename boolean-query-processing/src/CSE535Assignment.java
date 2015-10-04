import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * The driver for the Boolean Query Processor based on Posting Lists.
 * 
 * @author Alexander Simeonov
 */
public final class CSE535Assignment {
  private static final ArrayList<Posting> postings = new ArrayList<Posting>();
  private static final ArrayList<Query> queries = new ArrayList<Query>();
  private static final HashMap<String, Entry[]> dictionary = new HashMap<String, Entry[]>();
  private static Log log;
  private static int K;
  
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
      dictionary.put(posting.getTerm(), posting.getPostingList());
    }
    
    Collections.sort(postings);
    
    log = new Log(args[1]);
    
    K = Integer.parseInt(args[2]);
    
    for (Query query : new Queries(args[3])) {
      queries.add(query);
    }
  }
  
  /**
   * This returns the key dictionary terms that have the K largest postings lists. The result is 
   * expected to be an ordered string in the descending order of result postings, i.e., largest in 
   * the first position, and so on. The output should be formatted as follows (K=10 for an example)
   * <blockquote><pre>
   * FUNCTION: getTopK 10
   * Result: term1, term2, term3..., term10 (list the terms)
   * </pre></blockquote>
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
    
    return out;
  }
  
  /**
   * Retrieve the postings list for the given query. Since we have N input query terms, this 
   * function should be executed N times, and output the postings for each term from both two 
   * different ordered postings list. The corresponding posting list should be displayed in the 
   * following format:
   * <blockquote><pre>
   * FUNCTION: getPostings query_term
   * Ordered by doc IDs: 100, 200, 300... (list the document IDs ordered by increasing document IDs)
   * Ordered by TF: 300, 100, 200... (list the document IDs ordered by decreasing term frequencies)
   * </pre></blockquote>
   * Should display “term not found” if it is not in the index.
   * 
   * @param query_term - given query
   * @return documents IDs by document ID order and by term frequency order
   */
  public static String getPostings(String query_term) {
    String out = "FUNCTION: getPostings " + query_term + "\n";
    Entry[] entries = dictionary.get(query_term);
    if (entries != null) {
      out += "Ordered by doc IDs: ";
      Arrays.sort(entries, new DocIdComparator());
      String arrayString = Arrays.toString(entries);
      out += arrayString.substring(1, arrayString.length() - 1) + "\n";
      out += "Ordered by TF: ";
      Arrays.sort(entries, new TermFrequencyComparator());
      arrayString = Arrays.toString(entries);
      out += arrayString.substring(1, arrayString.length() - 1);   
    } else {
      out += "term not found";
    }
    
    return out;
  }
  
	/**
	 * Main entry point.
	 * 
	 * @param args - command line arguments
	 */
	public static void main(String[] args) {
	  setArguments(args);
	  log.log(getTopK(K));
	  for (Query query : queries) {
	    for (String term : query.getTerms()) {
	      log.log(getPostings(term));
	    }
	  }
	}
}

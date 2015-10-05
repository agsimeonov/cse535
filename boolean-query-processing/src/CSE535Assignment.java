import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * The driver for the Boolean Query Processor based on Posting Lists.
 * 
 * @author Alexander Simeonov
 */
public final class CSE535Assignment {
  private static final String NOT_FOUND = "terms not found";
  private static final Comparator<Entry> ID_COMP = new DocIdComparator();
  private static final Comparator<Entry> TF_COMP = new TermFrequencyComparator();
  
  private static final List<Posting> postings = new ArrayList<Posting>();
  private static final List<Query> queries = new ArrayList<Query>();
  private static final HashMap<String, List<Entry>> dictionary = new HashMap<String, List<Entry>>();
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
    List<Entry> entries = dictionary.get(query_term);
    if (entries != null) {
      out += "Ordered by doc IDs: ";
      entries.sort(ID_COMP);
      out += entries.toString().replaceAll("\\[|\\]", "") + "\n";
      out += "Ordered by TF: ";
      entries.sort(TF_COMP);
      out += entries.toString().replaceAll("\\[|\\]", "");   
    } else {
      out += NOT_FOUND;
    }
    
    return out;
  }
  
  public static String termAtATimeQueryAnd(Query query) {
    long startTime = System.nanoTime();
    List<String> terms = query.getTerms();
    String out = "FUNCTION: termAtATimeQueryAnd ";
    out += terms.toString().replaceAll("\\[|\\]", "") + "\n";
    List<Entry> results = new ArrayList<Entry>();
    int comparisons = 0;
    
    for (String term : terms) {
      List<Entry> entries = dictionary.get(term);
      if (entries == null) return NOT_FOUND;
      entries.sort(TF_COMP);
      if (results.isEmpty()) {
        for (Entry entry : entries) results.add(entry);
      } else {
        List<Entry> intermediate = new ArrayList<Entry>();
        for (Entry entry : entries) {
          for (Entry result : results) {
            comparisons += 1;
            if (entry.getDocId().equals(result.getDocId())) {
              intermediate.add(entry);
              break;
            }
          }
        }
        results = intermediate;
      }
    }
    
    results.sort(ID_COMP); 
    
    out += results.size() + " documents are found\n";
    out += comparisons + " comparisons are made\n";
    out += ((double)(System.nanoTime() - startTime)) / 1000000000.0 + " seconds are used\n";
    out += "Result: " + results.toString().replaceAll("\\[|\\]", "");
    
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
	    log.log(termAtATimeQueryAnd(query));
	  }
	}
}

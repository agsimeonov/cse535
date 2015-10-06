import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The driver for the Boolean Query Processor based on Posting Lists.
 * 
 * @author Alexander Simeonov
 */
public final class CSE535Assignment {
  private static final Comparator<Entry> ID_COMP = new DocIdComparator();
  private static final Comparator<Entry> TF_COMP = new TermFrequencyComparator();
  
  private static final List<Posting> postings = new ArrayList<Posting>();
  private static final List<Query> queries = new ArrayList<Query>();
  private static final Map<String, List<Entry>> dictionary = new HashMap<String, List<Entry>>();
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
    List<Entry> postingList = dictionary.get(query_term);
    if (postingList != null) {
      out += "Ordered by doc IDs: ";
      postingList.sort(ID_COMP);
      out += postingList.toString().replaceAll("\\[|\\]", "") + "\n";
      out += "Ordered by TF: ";
      postingList.sort(TF_COMP);
      out += postingList.toString().replaceAll("\\[|\\]", "");   
    } else {
      out += "term not found";
    }
    
    return out;
  }
  
  /**
   * Performs term-at-a-time query and, used as helper do be done for optimized terms as well.
   * 
   * @param terms - the query terms
   * @return a pair of results and number of comparisons, null if a term could not be found
   */
  private static Result termAtATimeQueryAnd(List<String> terms) {
    List<Entry> results = new ArrayList<Entry>();
    int comparisons = 0;
    
    for (String term : terms) {
      List<Entry> postingList = dictionary.get(term);
      if (postingList == null) return null; //TODO Modify to return null results show comparisons and everything
      postingList.sort(TF_COMP);
      if (results.isEmpty()) {
        for (Entry entry : postingList) results.add(entry);
      } else {
        List<Entry> intermediate = new ArrayList<Entry>();
        for (Entry entry : postingList) {
          for (Entry result : results) {
            comparisons += 1;
            if (ID_COMP.compare(entry, result) == 0) {
              intermediate.add(entry);
              break;
            }
          }
        }
        results = intermediate;
      }
    }
    
    return new Result(results, comparisons);
  }
  
  public static String termAtATimeQueryAnd(Query query) {
    long startTime = System.nanoTime();
    List<String> terms = query.getTerms();
    String out = "FUNCTION: termAtATimeQueryAnd ";
    out += terms.toString().replaceAll("\\[|\\]", "") + "\n";
    
    Result result = termAtATimeQueryAnd(terms);
    if (result == null) return "terms not found";
    
    terms.sort(new QueryTermComparator(dictionary));
    Result optResult = termAtATimeQueryAnd(terms);
    
    result.getResults().sort(ID_COMP);
    
    out += result.getResults().size() + " documents are found\n";
    out += result.getComparisons() + " comparisons are made\n";
    out += ((double)(System.nanoTime() - startTime)) / 1000000000.0 + " seconds are used\n";
    out += optResult.getComparisons() + " are made with optimization (optional bonus part)\n";
    out += "Result: " + result;
    
    return out;
  }
  
  public static String docAtATimeQueryAnd(Query query) {
    List<String> terms = query.getTerms();
    List<List<Entry>> postingLists = new ArrayList<List<Entry>>(terms.size());
    
    for (String term : terms) {
      List<Entry> postingList = dictionary.get(term);
      if (postingList == null) return "terms not found";
      postingList.sort(ID_COMP);
      postingLists.add(postingList);
    }
    
    List<Integer> indexes = new ArrayList<Integer>(postingLists.size());
    int index = 0;
    
    while (true) {
      
    }
    
    return null;
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

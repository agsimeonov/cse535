import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The driver for the Boolean Query Processor based on Posting Lists.
 * 
 * @author Alexander Simeonov
 */
public final class CSE535Assignment {
  private static final Comparator<Entry> BY_ID = new DocIdComparator();
  private static final Comparator<Entry> BY_TF = new TermFrequencyComparator();
  
  private static final List<Posting> postings = new LinkedList<Posting>();
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
      postingList.sort(BY_ID);
      out += postingList.toString().replaceAll("\\[|\\]", "") + "\n";
      out += "Ordered by TF: ";
      postingList.sort(BY_TF);
      out += postingList.toString().replaceAll("\\[|\\]", "");   
    } else {
      out += "term not found";
    }
    
    return out;
  }
  
  /**
   * Acquires the result string for DAAT and TAAT queries.
   * 
   * @param query - the given query
   * @param result - regular result
   * @param optimal - optimized query result
   * @param startTime - start time of computation
   * @return the result string in the expected format
   */
  public static String getResultString(Query query, Result result, Result optimal, long startTime) {
    String out = "FUNCTION: " + Thread.currentThread().getStackTrace()[2].getMethodName() + " ";
    out += query.getTerms().toString().replaceAll("\\[|\\]", "") + "\n";
    
    if (result == null) {
      out += "terms not found";
    } else {
      out += result.getResults().size() + " documents are found\n";
      out += result.getComparisons() + " comparisons are made\n";
      out += String.format("%f", ((double)(System.nanoTime() - startTime)) / 1000000000.0);
      out += " seconds are used\n";
      
      if (optimal != null) {
        out += optimal.getComparisons();
        out += " comparisons are made with optimization (optional bonus part)\n";
      }
      
      out += "Result: " + result;
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
    List<Entry> results = new LinkedList<Entry>();
    int comparisons = 0;
    
    for (String term : terms) {
      List<Entry> postingList = dictionary.get(term);
      if (postingList == null) return null; // a term wasn't found
      postingList.sort(BY_TF); // make sure we order by decreasing TF
      if (results.isEmpty()) {
        results.addAll(postingList);
        if (results.isEmpty()) break;
      } else {
        List<Entry> intermediate = new LinkedList<Entry>();
        for (Entry entry : postingList) {
          for (Entry result : results) {
            comparisons += 1;
            // only care if their IDs equal
            if (BY_ID.compare(entry, result) == 0) {
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
  
  /**
   * Performs regular and optimized TAAT AND.
   * 
   * @param query - given query
   * @return result formatted string
   */
  public static String termAtATimeQueryAnd(Query query) {
    long startTime = System.nanoTime();
    
    List<String> terms = query.getTerms();
    
    Result result = termAtATimeQueryAnd(terms);
    if (result == null) return getResultString(query, null, null, startTime);
    
    terms.sort(new QueryTermComparator(dictionary));
    Result optResult = termAtATimeQueryAnd(terms); // optional bonus
    
    result.getResults().sort(BY_ID);
    return getResultString(query, result, optResult, startTime);
  }
  
  /**
   * Helper function for TAAT OR.  Used so that one can perform both regular and optimized.
   * 
   * @param terms - the query terms
   * @return a pair of results and number of comparisons, null if no terms could not be 
   */
  private static Result termAtATimeQueryOr(List<String> terms) {
    List<Entry> results = new LinkedList<Entry>();
    int comparisons = 0;
    
    List<List<Entry>> postingLists = new LinkedList<List<Entry>>();
    
    for (String term : terms) {
      List<Entry> postingList = dictionary.get(term);
      if (postingList == null) continue;
      postingList.sort(BY_TF); // make sure we order by decreasing TF
      postingLists.add(postingList);
    }
    
    if (postingLists.isEmpty()) return null; // no terms were found
    
    for (List<Entry> postingList : postingLists) {
      if (results.isEmpty()) {
        results.addAll(postingList);
        continue;
      }

      List<Entry> intermediate = new LinkedList<Entry>();
      
      for (Entry entry : postingList) {
        boolean found = false;
        
        for (Entry result : results) {
          int equality = BY_ID.compare(entry, result);
          comparisons = comparisons + 1;
          
          // make sure we have no duplicates
          if (equality == 0) {
            found = true;
            break;
          }
        }
        
        if (found) continue;
        intermediate.add(entry);
      }
      
      results.addAll(intermediate);
    }

    return new Result(results, comparisons);
  }
  
  /**
   * Performs regular and optimized TAAT OR.
   * 
   * @param query - given query
   * @return result formatted string
   */
  public static String termAtATimeQueryOr(Query query) {
    long startTime = System.nanoTime();
    
    List<String> terms = query.getTerms();
    Result result = termAtATimeQueryOr(terms);
    if (result == null) return getResultString(query, null, null, startTime);
    
    terms.sort(new QueryTermComparator(dictionary));
    Result optResult = termAtATimeQueryOr(terms); // optional bonus
    
    result.getResults().sort(BY_ID);
    return getResultString(query, result, optResult, startTime);
  }
  
  /**
   * Performs DAAT AND, modified version of Figure 1.6.
   * 
   * @param query - given query
   * @return result formatted string
   */
  public static String docAtATimeQueryAnd(Query query) {
    long startTime = System.nanoTime();
    int comparisons = 0;
    
    List<String> terms = query.getTerms();
    List<Iterator<Entry>> iterators = new ArrayList<Iterator<Entry>>(terms.size());
    
    for (String term : terms) {
      List<Entry> postingList = dictionary.get(term);
      if (postingList == null) return getResultString(query, null, null, startTime); // missing term
      postingList.sort(BY_ID); // sort by increasing doc id
      iterators.add(postingList.iterator());
    }
    
    List<Entry> results = new LinkedList<Entry>();
    int total = iterators.size();
    int current = 0;
    int skip = -1;
    Entry max = null;
    
    while (current < total && iterators.get(current).hasNext()) {
      if (current == skip) {
        current = current + 1;
        continue;
      }
      
      Entry entry = iterators.get(current).next();
      
      int equality;
      
      if (max == null) {
        max = entry;
        equality = 0;
      } else {
        equality = BY_ID.compare(entry, max);
        comparisons = comparisons + 1;
      }
      
      if (equality == 0) { // add result if everything equals
        if (total == current + 1 || (total == current + 2 && total == skip + 1)) {
          results.add(max);
          current = 0;
          skip = -1;
          max = null;
        } else {
          current = current + 1;
          continue;
        }
      } else if (equality > 0) { // if we find a new max entry use this one
        max = entry;
        skip = current;
        current = 0;
      } else { // keep going this entry is lower then the current max
        continue;
      }
    }

    return getResultString(query, new Result(results, comparisons), null, startTime);
  }
  
  /**
   * Performs DAAT OR, modified version of Figure 1.6.
   * 
   * @param query - given query
   * @return result formatted string
   */
  public static String docAtATimeQueryOr(Query query) {
    long startTime = System.nanoTime();
    int comparisons = 0;
    
    List<String> terms = query.getTerms();
    List<List<Entry>> postingLists = new ArrayList<List<Entry>>(terms.size());
    
    for (String term : terms) {
      List<Entry> postingList = dictionary.get(term);
      if (postingList == null) continue;
      postingList.sort(BY_ID); // sort by increasing doc id
      postingLists.add(postingList);
    }
    
    // all terms are not found
    if (postingLists.isEmpty()) return getResultString(query, null, null, startTime);
    
    List<Entry> results = new LinkedList<Entry>();
    int[] indexes = new int[postingLists.size()];
    Arrays.fill(indexes, 0);
    
    while(true) {
      Entry min = null;
      List<Integer> positions = new LinkedList<Integer>();
      
      for (int i = 0; i < postingLists.size(); i++) {
        List<Entry> postingList = postingLists.get(i);
        int index = indexes[i];

        if (index < postingList.size()) {
          Entry entry = postingList.get(index);
          
          if (min == null) { // no min yet get one
            min = entry;
            positions.add(i);
          } else { // see if your current min changes
            int equality = BY_ID.compare(entry, min);
            comparisons = comparisons + 1;
            
            if (equality == 0) { // your current min doesn't change note position
              positions.add(i);
            } else if (equality < 0) { // your current min changes wipe previous note this position
              min = entry;
              positions.clear();
              positions.add(i);
            }
          }
        }
      }
      
      // you finished a single pass through all docs
      if (min != null) { // add min found and increment pointers of docs that match the min
        results.add(min);
        for (Integer position : positions) indexes[position] = indexes[position] + 1;
      } else { // no more entries
        break;
      }
    }
    
    return getResultString(query, new Result(results, comparisons), null, startTime);
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
      log.log(termAtATimeQueryOr(query));
      log.log(docAtATimeQueryAnd(query));
      log.log(docAtATimeQueryOr(query));
	  }
	  log.close();
	}
}

import java.util.ArrayList;
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
  private static final Comparator<Entry> ID_COMP = new DocIdComparator();
  private static final Comparator<Entry> TF_COMP = new TermFrequencyComparator();
  
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
      if (postingList == null) return null;
      postingList.sort(TF_COMP);
      if (results.isEmpty()) {
        results.addAll(postingList);
        if (results.isEmpty()) break;
      } else {
        List<Entry> intermediate = new LinkedList<Entry>();
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
    
    Result result = termAtATimeQueryAnd(terms);
    if (result == null) return getResultString(query, null, null, startTime);
    
    terms.sort(new QueryTermComparator(dictionary));
    Result optResult = termAtATimeQueryAnd(terms);
    
    result.getResults().sort(ID_COMP);
    return getResultString(query, result, optResult, startTime);
  }
  
  private static Result termAtATimeQueryOr(List<String> terms) {
    List<Entry> results = new LinkedList<Entry>();
    int comparisons = 0;
    
    List<List<Entry>> postingLists = new LinkedList<List<Entry>>();
    
    for (String term : terms) {
      List<Entry> postingList = dictionary.get(term);
      if (postingList == null) continue;
      postingList.sort(TF_COMP);
      postingLists.add(postingList);
    }
    
    if (postingLists.isEmpty()) return null;
    
    for (List<Entry> postingList : postingLists) {
      if (results.isEmpty()) {
        results.addAll(postingList);
        continue;
      }

      List<Entry> intermediate = new LinkedList<Entry>();
      
      for (Entry entry : postingList) {
        boolean found = false;
        
        for (Entry result : results) {
          int equality = ID_COMP.compare(entry, result);
          comparisons = comparisons + 1;
          
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
  
  public static String termAtATimeQueryOr(Query query) {
    long startTime = System.nanoTime();
    
    List<String> terms = query.getTerms();
    Result result = termAtATimeQueryOr(terms);
    if (result == null) return getResultString(query, null, null, startTime);
    
    terms.sort(new QueryTermComparator(dictionary));
    Result optResult = termAtATimeQueryOr(terms);
    
    result.getResults().sort(ID_COMP);
    return getResultString(query, result, optResult, startTime);
  }
  
  public static String docAtATimeQueryAnd(Query query) {
    long startTime = System.nanoTime();
    int comparisons = 0;
    
    List<String> terms = query.getTerms();
    List<Iterator<Entry>> iterators = new ArrayList<Iterator<Entry>>(terms.size());
    
    for (String term : terms) {
      List<Entry> postingList = dictionary.get(term);
      if (postingList == null) return getResultString(query, null, null, startTime);
      postingList.sort(ID_COMP);
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
        equality = ID_COMP.compare(entry, max);
        comparisons = comparisons + 1;
      }
      
      if (equality == 0) {
        if (total == current + 1 || (total == current + 2 && total == skip + 1)) {
          results.add(max);
          current = 0;
          skip = -1;
          max = null;
        } else {
          current = current + 1;
          continue;
        }
      } else if (equality > 0) {
        max = entry;
        skip = current;
        current = 0;
      } else {
        continue;
      }
    }

    return getResultString(query, new Result(results, comparisons), null, startTime);
  }
  
  public static String docAtATimeQueryOr(Query query) {
    long startTime = System.nanoTime();
    int comparisons = 0;
    
    List<String> terms = query.getTerms();
    List<Iterator<Entry>> iterators = new ArrayList<Iterator<Entry>>(terms.size());
    
    for (String term : terms) {
      List<Entry> postingList = dictionary.get(term);
      if (postingList == null) continue;
      postingList.sort(ID_COMP);
      iterators.add(postingList.iterator());
    }
    
    if (iterators.isEmpty()) return getResultString(query, null, null, startTime);
    
    List<Entry> results = new LinkedList<Entry>();
    
    while (true) {
      int complete = 0;
      
      for (Iterator<Entry> iterator : iterators) {     
        Entry entry;
        
        if (iterator.hasNext()) {
          entry = iterator.next();
        } else {
          complete = complete + 1;
          continue;
        }
        
        if (results.isEmpty()) {
          results.add(entry);
          continue;
        }
        
        for (int i = 0; i < results.size(); i++) {
          Entry result = results.get(i);
          int equality = ID_COMP.compare(entry, result);
          comparisons = comparisons + 1;
          
          if (equality < 0) {
            results.add(i, entry);
            break;
          } else if (equality == 0) {
            break;
          } else {
            if (i + 1 == results.size()) results.add(entry);
          }
        }
      }
      
      if (complete == iterators.size()) break;
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
	}
}

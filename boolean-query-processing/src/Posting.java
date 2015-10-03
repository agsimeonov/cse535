import java.util.ArrayList;


public class Posting {
  private final String term;
  private final int size;
  private final ArrayList<Entry> postingList = new ArrayList<Entry>();
  
  public Posting(String line) {
    String[] split = line.split("\\\\[cm]");
    term = split[0];
    size = Integer.parseInt(split[1]);
    String list = split[2].substring(1, split[2].length() - 1);
    for (String entry : list.split(", "))
      postingList.add(new Entry(entry));
    System.out.println(postingList);
  }
} 

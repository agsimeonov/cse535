package edu.buffalo.cse535;

import org.apache.solr.common.params.DisMaxParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.ExtendedDismaxQParser;
import org.apache.solr.search.ExtendedDismaxQParserPlugin;
import org.apache.solr.search.QParser;

public class AgsimeonQParserPlugin extends ExtendedDismaxQParserPlugin {
  public static final String NAME = "agsimeon";
  private static final String[] CUSTOM_QF = {"text_en", "text_de", "text_ru", "tweet_hashtags"};
  
  @Override
  public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
    ModifiableSolrParams customParams = new ModifiableSolrParams();
    customParams.add(DisMaxParams.QF, CUSTOM_QF);
    params = SolrParams.wrapAppended(params, customParams);
    return new ExtendedDismaxQParser(qstr, localParams, params, req);
  }
}

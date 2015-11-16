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
  private static final String LANG_EN = "lang:en";
  private static final String LANG_DE = "lang:de";
  private static final String LANG_RU = "lang:ru";
  
  @Override
  public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
    ModifiableSolrParams customParams = new ModifiableSolrParams();
    
    if (qstr.contains("#")) {
      CUSTOM_QF[3] = "tweet_hashtags^1.5";
    }
    else {
      CUSTOM_QF[3] = "tweet_hashtags^0.5";
    }
    
    if (qstr.contains(LANG_EN)) {
      CUSTOM_QF[0] = "text_en^1.25";
      qstr = qstr.replace(LANG_EN, "");
    }
    
    if (qstr.contains(LANG_DE)) {
      CUSTOM_QF[1] = "text_de^1.25";
      qstr = qstr.replace(LANG_DE, "");
    }
    
    if (qstr.contains(LANG_RU)) {
      CUSTOM_QF[2] = "text_ru^1.25";
      qstr = qstr.replace(LANG_RU, "");
    }
    
    customParams.add(DisMaxParams.QF, CUSTOM_QF);
    params = SolrParams.wrapAppended(params, customParams);
    return new ExtendedDismaxQParser(qstr, localParams, params, req);
  }
}

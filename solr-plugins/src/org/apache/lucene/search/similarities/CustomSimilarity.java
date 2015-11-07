package org.apache.lucene.search.similarities;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;

public class CustomSimilarity extends DefaultSimilarity {
  @Override
  public float coord(int overlap, int maxOverlap) {
    return super.coord(overlap, maxOverlap);
  }

  @Override
  public float idf(long docFreq, long numDocs) {
    return super.idf(docFreq, numDocs);
  }

  @Override
  public float lengthNorm(FieldInvertState state) {
    return super.lengthNorm(state);
  }

  @Override
  public float tf(float freq) {
    return super.tf(freq);
  }
}

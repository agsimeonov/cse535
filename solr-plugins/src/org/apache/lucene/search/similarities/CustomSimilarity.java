package org.apache.lucene.search.similarities;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;

public class CustomSimilarity extends DefaultSimilarity {
  @Override
  public float coord(int overlap, int maxOverlap) {
    return 1.0f;
  }

  @Override
  public float idf(long docFreq, long numDocs) {
    return 1.0f;
  }

  @Override
  public float lengthNorm(FieldInvertState arg0) {
    return 1.0f;
  }

  @Override
  public float tf(float freq) {
    return 1.0f;
  }
}

package com.okurahn.smoothie.filter;

public interface Updatable<Observation> extends Predictable, Correctable<Observation>
{
  default void update( final long time, final Observation observation )
  {
    predict( time );
    correct( observation );
  }
}

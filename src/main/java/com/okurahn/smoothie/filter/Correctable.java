package com.okurahn.smoothie.filter;

public interface Correctable<Observation>
{
  void correct( Observation observation );
}

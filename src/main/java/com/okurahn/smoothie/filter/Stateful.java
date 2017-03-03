package com.okurahn.smoothie.filter;

public interface Stateful<State, Covariance>
{
  long getTime();
  State getState();
  Covariance getCovariance();
}

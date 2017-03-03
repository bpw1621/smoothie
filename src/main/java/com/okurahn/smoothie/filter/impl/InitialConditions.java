package com.okurahn.smoothie.filter.impl;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.okurahn.smoothie.filter.Stateful;

public class InitialConditions implements Stateful<RealVector, RealMatrix>
{
  final long time;
  final RealVector state;
  final RealMatrix covariance;

  public InitialConditions( final long time, final RealVector state, final RealMatrix covariance )
  {
    this.time = time;
    this.state = state;
    this.covariance = covariance;
  }

  @Override
  public long getTime()
  {
    return time;
  }

  @Override
  public RealVector getState()
  {
    return state;
  }

  @Override
  public RealMatrix getCovariance()
  {
    return covariance;
  }
}

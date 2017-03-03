package com.okurahn.smoothie.filter.impl;

import org.apache.commons.math3.linear.RealMatrix;

public class ProcessModel
{
  private RealMatrix transitionMatrix;
  private RealMatrix processNoiseMatrix;

  public ProcessModel( final RealMatrix transitionMatrix, final RealMatrix processNoiseMatrix )
  {
    this.transitionMatrix = transitionMatrix;
    this.processNoiseMatrix = processNoiseMatrix;
  }

  public RealMatrix getTransitionMatrix()
  {
    return transitionMatrix;
  }

  public RealMatrix getProcessNoiseMatrix()
  {
    return processNoiseMatrix;
  }
}

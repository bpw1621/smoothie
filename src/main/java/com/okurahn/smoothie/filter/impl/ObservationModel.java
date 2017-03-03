package com.okurahn.smoothie.filter.impl;

import org.apache.commons.math3.linear.RealMatrix;

public class ObservationModel
{
  private RealMatrix observationMatrix;
  private RealMatrix observationNoiseMatrix;

  public ObservationModel( final RealMatrix observationMatrix, final RealMatrix observationNoiseMatrix )
  {
    this.observationMatrix = observationMatrix;
    this.observationNoiseMatrix = observationNoiseMatrix;
  }

  public RealMatrix getObservationMatrix()
  {
    return observationMatrix;
  }

  public RealMatrix getObservationNoiseMatrix()
  {
    return observationNoiseMatrix;
  }
}

package com.okurahn.smoothie.filter.impl;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

import com.okurahn.smoothie.filter.impl.InitialConditions;
import com.okurahn.smoothie.filter.impl.KalmanFilter;
import com.okurahn.smoothie.filter.impl.ObservationModel;
import com.okurahn.smoothie.filter.impl.ProcessModel;

public class TestKalmanFilter
{
  private KalmanFilter filter;

  @Test
  public void testKalmanFilter() throws Exception
  {
    final long initialTime = 0;
    final RealVector initialState = new ArrayRealVector( new double[] { 0.0, 0.0, 0.0, 0.0 } );
    final RealMatrix initialCovariance = MatrixUtils.createRealIdentityMatrix( 4 );
    final InitialConditions initialConditions = new InitialConditions( initialTime, initialState, initialCovariance );

    final RealMatrix transitionMatrix = new Array2DRowRealMatrix( new double[][] { { 1.0, 1.0, 0.0, 0.0 },
                                                                                   { 0.0, 1.0, 0.0, 0.0 },
                                                                                   { 0.0, 0.0, 1.0, 1.0 },
                                                                                   { 0.0, 0.0, 0.0, 1.0 } } );
//    final RealMatrix processNoiseMatrix = new DiagonalMatrix( new double[] { 1.0, 1.0, 1.0, 1.0 } );
    final RealMatrix processNoiseMatrix = MatrixUtils.createRealIdentityMatrix( 4 );
    final ProcessModel processModel = new ProcessModel( transitionMatrix, processNoiseMatrix );

    final RealMatrix observationMatrix = new Array2DRowRealMatrix( new double[][] { { 1.0, 0.0, 0.0, 0.0 },
                                                                                    { 0.0, 0.0, 1.0, 0.0 } } );
//    final RealMatrix observationNoiseMatrix = new DiagonalMatrix( new double[] { 1.0, 1.0 } );
    final RealMatrix observationNoiseMatrix = MatrixUtils.createRealIdentityMatrix( 2 );
    final ObservationModel observationModel = new ObservationModel( observationMatrix, observationNoiseMatrix );

    filter = new KalmanFilter( initialConditions, processModel, observationModel );

    filter.update( 1, new ArrayRealVector( new double[] { 1.0, 0.0 } ) );
    System.out.println( "filter.getTime() = " + filter.getTime() + ", filter.getState() = " + filter.getState() + ", filter.getCovariance() = " + filter.getCovariance() );

    filter.update( 2, new ArrayRealVector( new double[] { 1.3, 0.2 } ) );
    System.out.println( "filter.getTime() = " + filter.getTime() + ", filter.getState() = " + filter.getState() + ", filter.getCovariance() = " + filter.getCovariance() );
  }
}

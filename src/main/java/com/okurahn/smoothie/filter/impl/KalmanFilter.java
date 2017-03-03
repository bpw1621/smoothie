package com.okurahn.smoothie.filter.impl;

import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.okurahn.smoothie.filter.Stateful;
import com.okurahn.smoothie.filter.Updatable;

public class KalmanFilter implements Updatable<RealVector>, Stateful<RealVector, RealMatrix>
{
  private long time;
  private RealVector state;
  private RealMatrix covariance;

  private RealMatrix transitionMatrix;
  private RealMatrix processNoiseMatrix;

  private RealMatrix observationMatrix;
  private RealMatrix observationNoiseMatrix;


  public KalmanFilter( final InitialConditions initialConditions, final ProcessModel processModel, final ObservationModel observationModel )
  {
    time = initialConditions.getTime();
    state = initialConditions.getState();
    covariance = initialConditions.getCovariance();

    transitionMatrix = processModel.getTransitionMatrix();
    processNoiseMatrix = processModel.getProcessNoiseMatrix();

    observationMatrix = observationModel.getObservationMatrix();
    observationNoiseMatrix = observationModel.getObservationNoiseMatrix();
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

  @Override
  public void predict( final long time )
  {
    // TODO transitionMatrix( dt )
    // final long dt = time - this.time;

    state = transitionMatrix.operate( state );

    covariance = transitionMatrix.multiply( covariance )
                                 .multiply( transitionMatrix.transpose() )
                                 .add( processNoiseMatrix );

    this.time = time;
  }
  
  @Override
  public void correct( final RealVector observation )
  {
    RealMatrix s = observationMatrix.multiply( covariance )
                                    .multiply( observationMatrix.transpose() )
                                    .add( observationNoiseMatrix );

    RealVector innovation = observation.subtract( observationMatrix.operate( state ) );

    RealMatrix kalmanGain = new CholeskyDecomposition( s ).getSolver()
                                                          .solve( observationMatrix.multiply( covariance.transpose() ) )
                                                          .transpose();

    state = state.add( kalmanGain.operate( innovation ) );

    RealMatrix identity = MatrixUtils.createRealIdentityMatrix( kalmanGain.getRowDimension() );
    covariance = identity.subtract( kalmanGain.multiply( observationMatrix ) ).multiply( covariance );
  }
}

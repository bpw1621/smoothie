package com.okurahn.smoothie.gui;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.okurahn.smoothie.filter.impl.InitialConditions;
import com.okurahn.smoothie.filter.impl.KalmanFilter;
import com.okurahn.smoothie.filter.impl.ObservationModel;
import com.okurahn.smoothie.filter.impl.ProcessModel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class FilterButtonPressedHandler implements EventHandler<ActionEvent>
{
  private static int FILTER_ESTIMATE_SIZE = 5;
  private static Color FILTER_ESTIMATE_COLOR = Color.BLUE;

  private final Pane canvas;
  private final GuiState guiState;

  private KalmanFilter filter;

  private Group filterEstimates;

  DoubleProperty measurementSigmaProperty = new SimpleDoubleProperty();
  DoubleProperty processNoiseVarianceProperty = new SimpleDoubleProperty();

  public FilterButtonPressedHandler( final Pane canvas, final DoubleProperty measurementSigmaSliderValueProperty, final DoubleProperty processNoiseVarianceSliderValueProperty, final GuiState guiState )
  {
    this.canvas = canvas;
    this.guiState = guiState;

    measurementSigmaProperty.bind( measurementSigmaSliderValueProperty );
    processNoiseVarianceProperty.bind( processNoiseVarianceSliderValueProperty );
  }

  @Override
  public void handle( final ActionEvent event )
  {
    System.out.println( "event = " + event );
    guiState.filter.clear();
    filterObservations();
    drawFilterEstimates();
  }

  private void drawFilterEstimates()
  {
    canvas.getChildren().remove( filterEstimates );
    filterEstimates = new Group();

    guiState.filter.stream().forEach( this::addFilterPositions );
    canvas.getChildren().addAll( filterEstimates );
  }

  private void filterObservations()
  {
    final List<Point2D> observations = Collections.unmodifiableList( guiState.observations );
    final int observationsSize = observations.size();  
    if( observationsSize < 3 )
      return;

    final InitialConditions initialConditions = buildInitialConditions( observations.get( 0 ), observations.get( 1 ) );
    final ProcessModel processModel = buildProcessModel();
    final ObservationModel observationModel = buildObservationModel();
    filter = new KalmanFilter( initialConditions, processModel, observationModel );

    final List<ArrayRealVector> observationVectors = observations.stream().skip( 2 )
                                                                          .map( p -> new ArrayRealVector( new double[] { p.getX(), p.getY() } ) )
                                                                          .collect( Collectors.toList() );

    long filterTime = filter.getTime();
    RealVector filterState = filter.getState();
    RealMatrix filterCovariance = filter.getCovariance();
    System.out.println( "filter .getTime() = " + filterTime + ", .getState() = " + filterState + ", .getCovariance() = " + filterCovariance );

    Point2D filterPosition = new Point2D( filterState.getEntry( 0 ), filterState.getEntry( 2 ) );
    guiState.filter.add( filterPosition );

    long time = 2;
    for( final ArrayRealVector vector : observationVectors )
    {
      filter.update( time++, vector );
      filterTime = filter.getTime();
      filterState = filter.getState();
      filterCovariance = filter.getCovariance();
      System.out.println( "filter .getTime() = " + filterTime + ", .getState() = " + filterState + ", .getCovariance() = " + filterCovariance );
      
      filterPosition = new Point2D( filterState.getEntry( 0 ), filterState.getEntry( 2 ) );
      guiState.filter.add( filterPosition ); 
    }
  }

  private void addFilterPositions( final Point2D position )
  {
    final Circle filterEstimate = new Circle( position.getX(), position.getY(), FILTER_ESTIMATE_SIZE );
    filterEstimate.setFill( Color.TRANSPARENT ); 
    filterEstimate.setStroke( FILTER_ESTIMATE_COLOR ); 

    filterEstimates.getChildren().add( filterEstimate ); 
  }

  private InitialConditions buildInitialConditions( final Point2D first, final Point2D second )
  {
    final long initialTime = 1;
    final Point2D deltaPosition = second.subtract( first );
    final RealVector initialState = new ArrayRealVector( new double[] { second.getX(), deltaPosition.getX(), second.getY(), deltaPosition.getY() } );

    final RealMatrix initialCovariance = MatrixUtils.createRealIdentityMatrix( 4 ).scalarMultiply( 100.0 );

    return new InitialConditions( initialTime, initialState, initialCovariance );
  }

  private ProcessModel buildProcessModel()
  {
    // TODO use real dt when incorporating real time changes
    final double dt = 1.0;

    final RealMatrix transitionMatrix = new Array2DRowRealMatrix( new double[][] { { 1, dt, 0,  0 },
                                                                                   { 0,  1, 0,  0 },
                                                                                   { 0,  0, 1, dt },
                                                                                   { 0,  0, 0,  1 } } );

    final double processNoiseVariance = processNoiseVarianceProperty.get();

    // Constant-Velocity White Noise Model
    final double dt2 = Math.pow( dt, 2 ),
                 dt3 = Math.pow( dt, 3 );
    final RealMatrix processNoiseMatrix = new Array2DRowRealMatrix( new double[][] { { dt3 / 3, dt2 / 2,       0,       0 },
                                                                                     { dt2 / 2,       1,       0,       0 },
                                                                                     {       0,       0, dt3 / 3, dt2 / 2 },
                                                                                     {       0,       0, dt2 / 2,      dt } } ).scalarMultiply( processNoiseVariance * dt );

    return new ProcessModel( transitionMatrix, processNoiseMatrix );
  }

  private ObservationModel buildObservationModel()
  {
    final RealMatrix observationMatrix = new Array2DRowRealMatrix( new double[][] { { 1, 0, 0, 0 },
                                                                                    { 0, 0, 1, 0 } } );

    final double measurementVariance = Math.pow( measurementSigmaProperty.get(), 2 );
    final RealMatrix observationNoiseMatrix = new DiagonalMatrix( new double[] { measurementVariance, measurementVariance } );

    return new ObservationModel( observationMatrix, observationNoiseMatrix );
  }
}

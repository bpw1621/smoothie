package com.okurahn.smoothie.gui;

import java.util.Random;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ObservationsButtonPressedHandler implements EventHandler<ActionEvent>
{
  private static final Color OBSERVATION_COLOR = Color.RED;
  private static final int MEASUREMENT_SIZE = 4;

  final Pane canvas;
  final GuiState guiState;

  final Random rng = new Random();
  Group observations;

  DoubleProperty measurementSigmaProperty = new SimpleDoubleProperty();

  public ObservationsButtonPressedHandler( final Pane canvas, final DoubleProperty measurementSigmaSliderValueProperty, final GuiState guiState )
  {
    this.canvas = canvas;
    this.guiState = guiState;

    measurementSigmaProperty.bind( measurementSigmaSliderValueProperty );
  }

  @Override
  public void handle( final ActionEvent event )
  {
    System.out.println( "event = " + event );
    drawObservations();
  }

  private void drawObservations()
  {
    canvas.getChildren().remove( observations );
    observations = new Group();
    guiState.observations.clear();

    canvas.getChildren().stream().filter( node -> node instanceof Group )
                                 .map( node -> (Group) node )
                                 .flatMap( group -> group.getChildren().stream() )
                                 .filter( node -> node instanceof Circle )
                                 .map( circle -> (Circle) circle )
                                 .filter( circle -> circle.getStroke().equals( Color.BLACK ) )
                                 .forEach( this::addObservation );
    canvas.getChildren().addAll( observations );
  }
  
  private void addObservation( final Circle circle ) 
  {
    System.out.println( "circle = " + circle );

    final double x = circle.getCenterX(),
                 y = circle.getCenterY();

    final double measurementSigma = measurementSigmaProperty.get();
    final double observationX = x + measurementSigma * rng.nextGaussian(),
                 observationY = y + measurementSigma * rng.nextGaussian();

    final Circle observation = new Circle( observationX, observationY, MEASUREMENT_SIZE, OBSERVATION_COLOR );
    observations.getChildren().add( observation ); 

    guiState.observations.add( new Point2D( observationX, observationY ) );
  }
}

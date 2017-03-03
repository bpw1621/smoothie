package com.okurahn.smoothie.gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class JFXFilter extends Application
{
  private static final int SCENE_WIDTH = 600;
  private static final int SCENE_HEIGHT = 700;

  final GuiState guiState;
  final VBox root;
  final HBox buttonHolder;
  final HBox parameterControlContainer;
  final Pane canvas;
  final Scene scene;

  DoubleProperty measurementSigmaSliderValueProperty;
  DoubleProperty processNoiseVarianceSliderValueProperty;

  public JFXFilter()
  {
    guiState = new GuiState();
    root = new VBox();
    canvas = new Pane();
    canvas.setPrefSize( SCENE_WIDTH, SCENE_HEIGHT );
    canvas.setStyle( "-fx-border-color: #F00;" );

    buttonHolder = new HBox();
    buttonHolder.setPadding( new Insets( 15, 12, 15, 12 ) );
    buttonHolder.setSpacing( 10 );
    buttonHolder.setStyle( "-fx-background-color: #336699;" );
    buttonHolder.setAlignment( Pos.CENTER );
    
    parameterControlContainer = new HBox();
    parameterControlContainer.setPadding( new Insets( 15, 12, 15, 12 ) );
    parameterControlContainer.setSpacing( 10 );
    parameterControlContainer.setStyle( "-fx-background-color: #336699;" );
    parameterControlContainer.setAlignment( Pos.CENTER ); 

    root.getChildren().addAll( buttonHolder, canvas, parameterControlContainer );
    scene = new Scene( root, SCENE_WIDTH, SCENE_HEIGHT );
  }

  @Override
  public void start( final Stage primaryStage )
  {
    canvas.addEventHandler( MouseEvent.MOUSE_CLICKED, new MouseHandler( canvas, guiState ) );
    scene.addEventHandler( KeyEvent.KEY_TYPED, new KeyHandler( canvas, guiState ) );

    addParameterControls();
    addButtons();

    primaryStage.setScene( scene );
    primaryStage.sizeToScene();
    primaryStage.setResizable( false );
    primaryStage.setTitle( "JFXFilter" ); 
    primaryStage.show();
  }

  private void addParameterControls()
  {
    final Color defaultPaneBackgroundColor = Color.web( "#F1F1F1" );

    final Label measurementSigmaSliderLabel = new Label( "Meas Sigma" ); 
    measurementSigmaSliderLabel.setTextFill( defaultPaneBackgroundColor );
    final Slider measurementSigmaSlider = new Slider( 1e-1, 1e2, GuiState.DEFAULT_MEASUREMENT_SIGMA );
    measurementSigmaSliderValueProperty = measurementSigmaSlider.valueProperty(); 
    final Label measurementSigmaSliderReadout = new Label();
    final StringExpression measurementSigmaBindingFormat = Bindings.format( "%.2f", measurementSigmaSliderValueProperty );
    measurementSigmaSliderReadout.textProperty().bind( measurementSigmaBindingFormat );

    final Label processNoiseVarianceSliderLabel = new Label( "PN Var" ); 
    processNoiseVarianceSliderLabel.setTextFill( defaultPaneBackgroundColor );
    final Slider processNoiseVarianceSlider = new Slider( 1e-1, 1e2, GuiState.DEFAULT_PROCESS_NOISE_VARIANCE );
    processNoiseVarianceSliderValueProperty = processNoiseVarianceSlider.valueProperty(); 
    final Label processNoiseVarianceSliderReadout = new Label(); 
    final StringExpression processNoiseVarianceBindingFormat = Bindings.format( "%.2f", processNoiseVarianceSliderValueProperty );
    processNoiseVarianceSliderReadout.textProperty().bind( processNoiseVarianceBindingFormat );

    parameterControlContainer.getChildren().addAll( measurementSigmaSliderLabel, measurementSigmaSlider, measurementSigmaSliderReadout );
    parameterControlContainer.getChildren().addAll( processNoiseVarianceSliderLabel, processNoiseVarianceSlider, processNoiseVarianceSliderReadout );
  }

  private void addButtons()
  {
    final Button measurementsButton = new Button( "Obs" );
    measurementsButton.addEventHandler( ActionEvent.ACTION, new ObservationsButtonPressedHandler( canvas, measurementSigmaSliderValueProperty, guiState ) );

    final Button filterButton = new Button( "Filter" );
    filterButton.addEventHandler( ActionEvent.ACTION, new FilterButtonPressedHandler( canvas, measurementSigmaSliderValueProperty, processNoiseVarianceSliderValueProperty, guiState ) ); 

    buttonHolder.getChildren().addAll( measurementsButton, filterButton );
  }

  public static void main( String[] args )
  {
    launch( args );
  }
}

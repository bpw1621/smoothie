package com.okurahn.smoothie.gui;

import java.util.Optional;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class MouseHandler implements EventHandler<MouseEvent>
{
  private static double DEFAULT_CIRCLE_RADIUS = 10.0;

  private final Pane parent;
  private final GuiState guiState;

  private Point2D mouseClickPoint;

  public MouseHandler( final Pane parent, final GuiState guiState )
  {
    this.parent = parent;
    this.guiState = guiState;
  }

  @Override
  public void handle( final MouseEvent event )
  {
    System.out.println( "event = " + event );
    mouseClickPoint = new Point2D( event.getX(), event.getY() );
    addCircleAndNumberNodes();
    guiState.maybeLastMouseClickPoint.ifPresent( this::addLineBetweenCurrentAndLastNodes );
    guiState.maybeLastMouseClickPoint = Optional.of( mouseClickPoint );
  }

  private void addCircleAndNumberNodes()
  {
    final double x = mouseClickPoint.getX();
    final double y = mouseClickPoint.getY();

    final Circle circle = new Circle( x, y, DEFAULT_CIRCLE_RADIUS );
    circle.setStroke( Color.BLACK );
    circle.setFill( Color.TRANSPARENT );

    final String mouseClickedCounterText = String.valueOf( ++guiState.mouseClickedCounter );
    final int mouseClickedCounterTextLength = mouseClickedCounterText.length(); 
    final double horizontalTextOffsetFactor = 2.3;
    final Text text = new Text( x - horizontalTextOffsetFactor * mouseClickedCounterTextLength, y, mouseClickedCounterText );
    text.setFont( new Font( 9 ) );
    text.setTextAlignment( TextAlignment.CENTER );
    text.setTextOrigin( VPos.CENTER );

    final Group numberCircle = new Group( circle, text );
    parent.getChildren().add( numberCircle );

    guiState.truth.add( new Point2D( x, y ) );
  }

  private void addLineBetweenCurrentAndLastNodes( final Point2D lastMouseClickPoint )
  {
    final double x = mouseClickPoint.getX();
    final double y = mouseClickPoint.getY();

    final double dx = x - lastMouseClickPoint.getX();
    final double dy = y - lastMouseClickPoint.getY();
    final double hypot = Math.hypot( dx, dy );

    final double xOffset = DEFAULT_CIRCLE_RADIUS * dx / hypot;
    final double yOffset = DEFAULT_CIRCLE_RADIUS * dy / hypot;

    final MoveTo pathStart = new MoveTo( lastMouseClickPoint.getX() + xOffset, lastMouseClickPoint.getY() + yOffset );
    final LineTo lineTo = new LineTo( x - xOffset, y - yOffset );
    final Path path = new Path( pathStart, lineTo );

    parent.getChildren().add( path );
  }
}

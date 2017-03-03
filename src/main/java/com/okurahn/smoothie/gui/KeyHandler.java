package com.okurahn.smoothie.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public class KeyHandler implements EventHandler<KeyEvent>
{
  private final Pane parent;
  private final GuiState state;

  public KeyHandler( final Pane parent, final GuiState state )
  {
    this.parent = parent;
    this.state = state;
  }

  @Override
  public void handle( final KeyEvent event )
  {
    final String c = event.getCharacter();
    switch( c )
    {
      case "q":
        Platform.exit();
        System.exit( 0 );
        break;
      case "c":
        parent.getChildren().clear();
        state.reset();
        break;
      default:
       System.out.println( "event = " + event );   
    }
  }
}

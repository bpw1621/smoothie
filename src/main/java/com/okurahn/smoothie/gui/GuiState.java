package com.okurahn.smoothie.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.geometry.Point2D;

public class GuiState
{
  public static final double DEFAULT_MEASUREMENT_SIGMA = 10;
  public static final double DEFAULT_PROCESS_NOISE_VARIANCE = 100;

  public int mouseClickedCounter = 0;
  public Optional<Point2D> maybeLastMouseClickPoint = Optional.empty();
  
  public List<Point2D> truth = new ArrayList<>();
  public List<Point2D> observations = new ArrayList<>();
  public List<Point2D> filter = new ArrayList<>();

  public void reset()
  {
    mouseClickedCounter = 0;
    maybeLastMouseClickPoint = Optional.empty();

    truth = new ArrayList<>();
    observations = new ArrayList<>();
    filter = new ArrayList<>();
  }
}

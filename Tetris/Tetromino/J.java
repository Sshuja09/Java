package swen221.tetris.tetromino;

import swen221.tetris.logic.Color;

public class J extends Tetromino{
	
	private int rotationState;
	
  public J(int x, int y, Color color) {
    super(x, y, color);/*TODO: complete this code*/
    rotationState = 0; // start in initial orientation
  }
  
  @Override
  public int x(int i) {
    int[][][] coords = getCoords();
    return centerX() + coords[rotationState][i][0];
  }

  @Override
  public int y(int i) {
    int[][][] coords = getCoords();
    return centerY() + coords[rotationState][i][1];
  }

  @Override
  public void rotateRight() {
    rotationState = (rotationState + 1) % 4;
  }

  public int[][][] getCoords() {
    return new int[][][] {
    	{ {0, 0}, {-1, 0}, {1, -1}, {1, 0} },
    	{ {0, 0}, {0, 1}, {-1, -1}, {0, -1} },
    	{ {0, 0}, {1, 0}, {-1, 1}, {-1, 0} },
    	{ {0, 0}, {0, -1}, {1, 1}, {0, 1} },
      };
  }
  
  //TODO: add more methods if needed
}
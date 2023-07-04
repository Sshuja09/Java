package swen221.tetris.tetromino;

import swen221.tetris.logic.Color;

public class L extends J{
  public L(int x, int y, Color color) {
    super(x, y, color);/*TODO: complete this code*/
  }
  
  @Override
  public int[][][] getCoords() {
    return new int[][][] {
    	{ {0, 0}, {1, 0}, {-1, -1}, {-1, 0} },
    	{ {0, 0}, {0, -1}, {-1, 1}, {0, 1} },
    	{ {0, 0}, {-1, 0}, {1, 1}, {1, 0} },   
        { {0, 0}, {0, 1}, {1, -1}, {0, -1} }
    };
  }
  
  //TODO: add more methods if needed
}

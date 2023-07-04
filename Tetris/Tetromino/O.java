package swen221.tetris.tetromino;

import swen221.tetris.logic.Color;

public class O extends Tetromino{
  public O(int x, int y, Color color) {
    super(x, y, color);/*TODO: complete this code*/    
  }

  @Override
  public int x(int i) {/*TODO: complete this code*/
	  if (i == 0 || i == 2) {
	      return centerX();
	    } else {
	      return centerX() + 1;
	    }
  //HINT you may want to use some paper and
  //make diagrams like those for all the tetrominos
  /*i x y
    0 0 0
    1 1 0
    2 0 1
    3 1 1*/    
  }

  @Override
  public int y(int i) {/*TODO: complete this code*/
	  if (i == 0 || i == 1) {
	      return centerY();
	    } else {
	      return centerY() + 1;
	    }
  }

  @Override
  public void rotateRight() {/*TODO: complete this code*/
	  // O tetromino has no orientations, so it should stay the same when rotated
  }
  //TODO: add more methods if needed
}

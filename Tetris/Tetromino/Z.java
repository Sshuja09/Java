package swen221.tetris.tetromino;

import swen221.tetris.logic.Color;

public class Z extends S{
	
	boolean horizontal = true;
	
  public Z(int x, int y, Color color) {
    super(x, y, color);/*TODO: complete this code*/
    
  }
  

  @Override
  public int yOffset(int i) {
    if (i < 2) {
      return 1;
    }
    return 0;
  }
  
  //TODO: add more methods if needed
}

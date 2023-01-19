package libs.characters;

import libs.gfx.*;
import libs.math.*;

public class elf extends player {
  final int ULT_DMG = 25;
  public elf(String loc, int px, int py, int w_c, int h_c, neo nn) {
    super(loc, px, py, w_c, h_c, nn);
  }

  // shoots multiple bullets at the same time
  public void ult() {
  }
};

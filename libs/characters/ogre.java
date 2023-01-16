package libs.characters;

import libs.gfx.*;
import libs.math.*;
import libs.functionality.*;

public class ogre extends player {
  final int ULT_DMG = 100;
  public ogre(String loc, int px, int py, int w_c, int h_c, neo nn) {
    super(loc, px, py, w_c, h_c, nn);
    this.attacks.add(new weapon("BOULDER", ULT_DMG, nn));
  }

  public void ult() {
    // TODO: add gravity
    bullet boulder = new bullet("./sprites/rock.bmp", ULT_DMG, 64, 64, 1, 1, nn); 
    this.attacks.get(1).shoot(this.pos, this.forward, boulder);
  }
};

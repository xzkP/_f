package libs.characters;

import libs.gfx.*;
import libs.math.*;
import libs.functionality.*;

public class ogre extends player {
  final int ULT_DMG = 100;
  weapon boulder;
  public ogre(String loc, int px, int py, int w_c, int h_c, neo nn) {
    super(loc, px, py, w_c, h_c, nn);
    boulder = new weapon("HEAVY", ULT_DMG, nn);
    this.attacks.add(boulder);
  }

  public void ult() {
    // TODO: add gravity
    bullet rock = new bullet("sprites/rock.bmp", ULT_DMG, 64, 64, 1, 1, nn);
    boulder.shoot(this.pos, this.forward, rock);
  }
};


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
		/*
    bullet boulder = new bullet("./sprites/rock.bmp", ULT_DMG, 64, 64, 1, 1, this.forward, this.nn);
		boulder.collide = false;
		boulder.setPosition(nn.new Vec2(this.pos.x, this.pos.y-128));
		boulder.setVelocity(nn.new Vec2((this.forward?1:-1)*6, -5));
    boulder.setGravity(nn.new Vec2(0, 0.15));
		this.attacks.get(1).shoot(boulder);*/
  }
};


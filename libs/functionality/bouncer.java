package libs.functionality;

import libs.functionality.*;
import libs.gfx.*;
import libs.math.*;

// bounce pad.
public class bouncer extends platform {
  final double FORCE=10.0;
  // all bouncepads have the same width (avoid unecessary confusion)
  public bouncer(int x, int y, int w, int h, neo nn) {
    super(x, y, 100, 10, "white", nn, "./sprites/trampoline.bmp");
  }
  public bouncer(platform p, int w, int h, neo nn) {
    super((int) (p.getPos().x+p.getDimensions().x/2-w), (int) (p.getPos().y-h), w, h, "white", nn, "./sprites/trampoline.bmp");
  }
};

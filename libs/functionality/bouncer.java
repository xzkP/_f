package libs.functionality;

import libs.functionality.*;
import libs.gfx.*;
import libs.math.*;

// bounce pad.
public class bouncer extends platform {
  final double FORCE=10.0;
  // all bouncepads have the same width (avoid unecessary confusion)
  public bouncer(int x, int y, int w, int h, neo nn) {
    super(x, y, 100, 10, "white", nn);
  }
  public bouncer(platform p, int w, int h, neo nn) {
    super((int) (p.get_pos().x+p.get_dimensions().x/2)-w/2, (int) p.get_pos().y-(h/2), w, h, "white", nn);
  }
};

package libs.functionality;

import libs.math.*;
import libs.gfx.*;
import javax.imageio.*;
import java.io.*;

public class bullet {
  public static sprite img;
  int cticks = 0;
  double dmg;
  boolean shot = false, forward = false;
  neo.Vec2 position;
  public bullet(String fn, double d, int dx, int dy, int cx, int cy, neo nn) {
    img = new sprite(fn, dx, dy, cx, cy, nn);
    dmg = d;
    dmg = critical();
  }
  public double critical() {
    return (Math.random()<=0.15?(Math.random()+0.5)*(1+Math.random())*dmg : dmg);
  }
  public void update() {
    this.position.x += (forward?1:-1)*7.5;
    this.img.img_update(2);
    cticks++;
  }
  public String toString() {
    return String.format("BULLET (%f, %f): %f", position.x, position.y, dmg);
  }
  public double get_dmg() {
    return dmg;
  }
};

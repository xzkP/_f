package libs.functionality;

import libs.math.*;
import libs.gfx.*;
import javax.imageio.*;
import java.io.*;

public class bullet {
  public static sprite img;
  int survivalTicks = 0;
  double dmg, baseDmg;
  boolean shot = false, forward = false, hasGravity = false;
  neo.Vec2 position, velocity, gravity = null;
  public neo.Vec2 knockback;
  public bullet(String fn, double d, int dx, int dy, int cx, int cy, boolean f, neo nn) {
    this.img = new sprite(fn, dx, dy, cx, cy, nn);
    this.baseDmg = d;
    this.dmg = critical();
    this.forward = f;
    this.velocity = nn.new Vec2((this.forward?1:-1)*7.5, 0);
    /* create knockback proportionate to damage
      30 dmg --> knockback(3, -5) */
    if (this.baseDmg <= 30) {
      this.knockback = nn.new Vec2(3, -5);
    } else {
      this.knockback = nn.new Vec2(4.5, -7.5);
    }
  }
  public double critical() {
    return (Math.random()<=0.15?(Math.random()+0.5)*(1+Math.random())*baseDmg: baseDmg);
  }
  public void setPosition(neo.Vec2 p) {
    this.position = p;
  }
  public void setVelocity(neo.Vec2 v) {
    if (v != null) {
      this.velocity = v;
    }
  }
  public void setGravity(neo.Vec2 g) {
    if (g != null) {
      this.hasGravity = true;
      this.gravity = g;
    }
  }
  public void updatePos() {
    this.position.x += this.velocity.x;
    this.position.y += this.velocity.y;
    if (this.hasGravity && gravity != null) {
      this.velocity = this.velocity.add(gravity);
    }
    this.img.imgUpdate(2);
    ++survivalTicks;
  }
  public String toString() {
    return String.format("BULLET (%f, %f): %f", position.x, position.y, dmg);
  }
  public double[] dmgInfo() {
    double info[] = {this.dmg, this.baseDmg};
    return info;
  }
  public double getDmg() {
    return this.dmg;
  }
  public boolean isForward() {
    return this.forward;
  }
};

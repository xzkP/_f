package libs.gfx;

import libs.gfx.*;
import libs.functionality.*;
import libs.math.*;
import java.util.ArrayList;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;

abstract public class player extends sprite {
  HashMap<Character, Integer> movement;
  final int DOUBLE_JUMP = 15, ULTABLE = 500;
  protected ArrayList<weapon> attacks = new ArrayList<weapon>();
  public int ddt = 0, shootable = 0;
  // jumps[2] = double jump
  // dash[2] -> dash, direction
  public boolean directions[] = { false, false, false, false }, jumps[] = { true, false, true, false}, dash[] = { false, false };
  public text criticalText;
  double crit = 0.0, JUMP_FORCE = 8, DOUBLE_JUMP_FORCE=15;
  neo.Vec2 vel;
  // melee, weapon.
  boolean permeate = false;
  platform pt;

  public player(String fn, double px, double py, int w_count, int h_count, neo nn) {
    super(fn, px, py, w_count, h_count, nn);
    this.attacks.add(new weapon("fireballs", 30, this.nn));
    vel = nn.new Vec2(0, 0);
  }

  public void criticalTextInit(int x, int y) {
    this.criticalText = new text("0.00%", x, y, "0xFFFFFF", 48);
  }

  void move(ArrayList<platform> platforms, double TICK_SCALE) {
    double movement_scalar = 5.0;
    if (this.directions[0] == this.directions[1] == this.directions[2] == this.directions[3] == false) {
      this.source_dim.y=0;
    }
    for (int i = 0; i < this.directions.length; i++) {
      if (this.directions[i]) {
        neo.Vec2 move_vector;
        if (i%2==0) {
          neo.Vec2 dim = this.dimensions();
          this.forward = (i==2);
          this.source_dim.y = (dim.x*(1+(i>0?0:1)));
          move_vector = nn.new Vec2((i-(i%2)>0)?1:-1, 0).scale(movement_scalar);
          if (!this.collide(platforms, this.pos.add(move_vector), true)) {
            this.modPos(move_vector);
          }
        } else {
          if (i%2==i) {
            this.source_dim.y = 192;
            boolean surface = this.onSurface(platforms, this.pos);
            if (surface && this.jumps[0]) {
              this.vel.y = -this.JUMP_FORCE;
              this.jump_tick = 0;
              this.jumps[0] = false;
              this.jumps[1] = true;
            } else if (!surface && this.jumps[1] && this.jumps[2] && this.ddt >= DOUBLE_JUMP) {
              this.vel.y = -this.DOUBLE_JUMP_FORCE;
              this.jump_tick = 0;
              this.jumps = new boolean[]{this.jumps[0], true, false, true};
            }
          } else {
            boolean surface = this.onSurface(platforms, this.pos);
            if (!surface && (this.jumps[0] || this.jumps[1])) {
              this.vel.y += 0.35;
            } else {
              platform g = getSurface(platforms);
              if (g.permeable) {
                this.permeate = true;
                this.pt = g;
                this.modVel(0, 0.375);
              }
            }
          }
        }
      }
      this.imgUpdate(TICK_SCALE);
    }
  }

  public void respawn(double px, double py) {
    this.pos = this.nn.new Vec2(px, py);
    this.vel = this.nn.new Vec2(0, 0);
    for (weapon w : this.attacks) {
      w.erase();
    }
    this.crit = 0;
  }

  public platform getSurface(ArrayList<platform> platforms) {
    for (platform p : platforms) {
      if (p.under(this, this.pos)) return p;
    }
    return null;
  }

  public void modPos(double x, double y) {
    this.pos = this.pos.add(x,y);
  }

  public void modPos(neo.Vec2 v) {
    this.pos = this.pos.add(v);
  }

  public void modVel(double x, double y) {
    this.vel = this.vel.add(x,y);
    this.modPos(this.vel);
  }

  public void modVel(neo.Vec2 v) {
    this.vel = this.vel.add(v);
    this.modPos(this.vel);
  }

  public void bounce(ArrayList<bouncer> bouncers) {
    ArrayList<platform> tc = new ArrayList<platform>(bouncers);
    if (this.collide(tc, this.pos, false)) {
      this.vel.y = (Math.abs(this.vel.y)*-0.25)-10.0;
      this.jumps = new boolean[]{this.jumps[0], true, true, false };
      this.ddt = 0;
      this.modPos(this.vel);
    }
  }

  // check horizontal collision with all objects
  boolean onSurface(ArrayList<platform> platforms, neo.Vec2 position) {
    for (platform p : platforms) {
      if (p.under(this, position)) {
        return true;
      }
    }
    return false;
  }

  boolean collide(platform p, neo.Vec2 position) {
    neo.Vec2 dim = this.dimensions();
    Shape boundary = new Rectangle2D.Double(position.x, position.y, dim.x, dim.y);
    if (p.collide) {
      Shape pbounds = new Rectangle2D.Double(p.pos.x, p.pos.y, p.dimensions.x, p.dimensions.y);
      return boundary.intersects((Rectangle2D) pbounds);
    }
    return false;
  }

  boolean collide(ArrayList<platform> platforms, neo.Vec2 position, boolean ub) {
    neo.Vec2 dim = this.dimensions();
    Shape boundary = new Rectangle2D.Double(position.x, position.y, dim.x, dim.y);
    for (platform p : platforms) {
      if (this.collide(p, position) && (ub?!p.under(this, position):true)) {
        return true;
      }
    }
    return false;
  }

  public void updateCritical(bullet b) {
    this.crit += ((b.getDmg()/b.dmgInfo()[1])*Math.random()*5+5);
    this.criticalText.updateMsg(String.format("%.2f", this.crit)+"%");
    boolean critical = Double.compare(Math.random()*Math.random(), (1-this.crit/100)) >= 0;
    double scalar = (critical?Math.random()*0.5+1:1);
    this.modVel((b.isForward()?1:-1)*b.knockback.x*scalar, b.knockback.y*scalar);
  }

  abstract public void ult();
};

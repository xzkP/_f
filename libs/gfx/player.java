package libs.gfx;

import libs.gfx.*;
import libs.functionality.*;
import libs.math.*;
import java.util.ArrayList;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.awt.*;
import java.util.HashMap;

public class player extends sprite {
  HashMap<Character, Integer> movement;
  final int DOUBLE_JUMP = 15;
  public int ddt = 0; 
  // jumps[2] = double jump
  public boolean directions[] = { false, false, false, false }, jumps[] = { true, false, true, false};
  double crit = 0.0, JUMP_FORCE = 8, DOUBLE_JUMP_FORCE=10;
  neo.Vec2 vel;
  // melee, weapon.
  ArrayList<weapon> attacks = new ArrayList<weapon>();

  public player(String fn, double px, double py, int w_count, int h_count, neo nn) {
    super(fn, px, py, w_count, h_count, nn);
    this.attacks.add(new weapon("f", 30, this.nn));
    vel = nn.new Vec2(0, 0);
  }

  void move(ArrayList<platform> platforms, neo.Vec2 end, double TICK_SCALE) {
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
          if (Double.compare(this.pos.x+move_vector.x, 0) >= 0 
              && Double.compare(this.pos.x+move_vector.x, end.x-dim.x) <= 0  
              && !this.collide(platforms, this.pos.add(move_vector), true)) {
            this.mod_pos(move_vector);
          }
        } else {
          if (i==1) {
            boolean surface = this.on_surface(platforms, this.pos);
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
          }
        }
        this.img_update(TICK_SCALE);
      }
    }
  }

  public void mod_pos(double x, double y) {
    this.pos = this.pos.add(x,y);
  }
  public void mod_pos(neo.Vec2 v) {
    this.pos = this.pos.add(v);
  }
  public void mod_vel(double x, double y) {
    this.vel = this.vel.add(x,y);
    this.mod_pos(this.vel);
  }
  public void mod_vel(neo.Vec2 v) {
    this.vel = this.vel.add(v);
    this.mod_pos(this.vel);
  }

  public void bounce(ArrayList<bouncer> bouncers) {
    ArrayList<platform> tc = new ArrayList<platform>(bouncers);
    if (this.collide(tc, this.pos, false)) {
      this.vel.y = -10.0;
      this.jumps = new boolean[]{this.jumps[0], true, true, false };
      this.ddt = 0;
      this.mod_pos(this.vel);
    }
  }

  // check horizontal collision with all objects
  boolean on_surface(ArrayList<platform> platforms, neo.Vec2 position) {
    for (platform p : platforms) {
      if (p.under(this, position)) {
        return true;
      }
    }
    return false;
  }

  boolean collide(ArrayList<platform> platforms, neo.Vec2 position, boolean ub) {
    neo.Vec2 dim = this.dimensions();
    Shape boundary = new Rectangle2D.Double(position.x, position.y, dim.x, dim.y);
    for (platform p : platforms) {
      if (p.collide) {
        Shape pbounds = new Rectangle2D.Double(p.pos.x, p.pos.y, p.dimensions.x, p.dimensions.y);
        if (boundary.intersects((Rectangle2D) pbounds) && (ub?!p.under(this, position):true)) {
          return true;
        }
      }
    }
    return false;
  }

  public void action(int index) {
    switch (index) {
      case (4):
        this.attacks.get(0).shoot(pos, forward);
        break;
      case (5):
        this.attacks.get(1).shoot(pos, forward);
    }
  }
};

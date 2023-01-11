package libs.gfx;

import libs.gfx.*;
import libs.functionality.*;
import libs.math.*;
import java.util.ArrayList;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.awt.*;

public class player extends sprite {
  final int DOUBLE_JUMP = 20;
  public int ddt = 0;
  // jumps[2] = double jump
  public boolean directions[] = { false, false, false, false }, jumps[] = { true, false, true, false};
  neo.Vec2 vel;
  ArrayList<weapon> inventory = new ArrayList<weapon>();

  public player(String fn, double px, double py, int w_count, int h_count, neo nn) {
    super(fn, px, py, w_count, h_count, nn);
    vel = nn.new Vec2(0, 0);
  }

  void move(ArrayList<platform> platforms, neo.Vec2 end, double TICK_SCALE) {
    System.out.println(end);
    double movement_scalar = 5.0;
    if (this.directions[0] == this.directions[1] == this.directions[2] == this.directions[3] == false) {
      this.source_dim.y=0;
    } 
    for (int i = 0; i < this.directions.length; i++) {
      if (this.directions[i]) {
        neo.Vec2 move_vector;
        if (i%2==0) {
          this.forward = (i==2);
          this.source_dim.y = (this.dimensions().x*(1+(i>0?0:1)));
          move_vector = nn.new Vec2((i-(i%2)>0)?1:-1, 0).scale(movement_scalar).add(this.vel);
          if (Double.compare(this.pos.x+move_vector.x, 0) >= 0 && Double.compare(this.pos.x+move_vector.x, end.x) <= 0  
              && !this.collide(platforms, this.pos.add(move_vector))) {
            this.mod_pos(move_vector);
          }
        } else {
          if (i==1) {
            boolean surface = this.on_surface(platforms, this.pos);
            if (surface && this.jumps[0]) {
              this.jumps[1] = true;
              this.jumps[0] = false;
              this.jump_tick = 0;
            } else if (
                !surface && !this.jumps[1] && this.jumps[2] && this.ddt >= DOUBLE_JUMP) {
              this.jumps[2] = false;
              this.jumps[3] = true;
            }
          }
        }
        this.img_update(TICK_SCALE);
      }
    }
  }

  public void mod_pos(double x, double y) {
    this.pos = this.pos.add(this.nn.new Vec2(x,y));
    this.relative_pos = this.relative_pos.add(this.nn.new Vec2(x,y));
  }

  public void mod_pos(neo.Vec2 v) {
    this.pos = this.pos.add(v);
    this.relative_pos = this.relative_pos.add(v);
  }

  public void bounce(ArrayList<bouncer> bouncers) {
    ArrayList<platform> tc = new ArrayList<platform>(bouncers);
    if (this.collide(tc, this.pos)) {
      this.jumps[2] = true;
      this.ddt = 0;
      this.mod_pos(0, -1000);
    }
  }

  /*boolean above(platform p, neo.Vec2 position) {
        return (((position.x+this.dimensions().x >= p.pos.x && position.x+this.dimensions().x <= p.pos.x+p.dimensions.x) || (position.x >= p.pos.x && position.x <= p.pos.x+p.dimensions.x) || p.infinite) && position.y+this.dimensions().y >= p.pos.y && position.y+this.dimensions().y <= p.pos.y+5);
    }*/

  // check horizontal collision with all objects
  boolean on_surface(ArrayList<platform> platforms, neo.Vec2 position) {
    for (platform p : platforms) {
      if (p.under(this, position)) return true;
    }
    return false;
  }

  boolean collide(ArrayList<platform> platforms, neo.Vec2 position) {
    Shape boundary = new Rectangle2D.Double(position.x, position.y, this.width/this.sprite_values.x, this.height/this.sprite_values.y);
    for (platform p : platforms) {
      if (p.collide) {
        Shape pbounds = new Rectangle2D.Double(p.pos.x, p.pos.y, p.dimensions.x, p.dimensions.y);
        if (boundary.intersects((Rectangle2D) pbounds) && !p.under(this, position)) {
          return true;
        }
      }
    }
    return false;
  }
};



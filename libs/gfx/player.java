package libs.gfx;

import libs.gfx.*;
import libs.functionality.*;
import libs.math.*;
import java.util.ArrayList;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.awt.*;

public class player extends sprite {
  public boolean directions[] = { false, false, false, false }, jumps[] = { true, false };
  ArrayList<weapon> inventory = new ArrayList<weapon>();

  public player(String fn, double px, double py, int w_count, int h_count) {
    super(fn, px, py, w_count, h_count);
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
          this.forward = !(i==0);
          this.source_dim.y = (this.dimensions().x*(1+(i>0?0:1)));
          move_vector = nn.new Vec2((i-(i%2)>0)?1:-1, 0).scale(movement_scalar);
          if (!(Double.compare(this.pos.x+move_vector.x, 0) < 0 && Double.compare(this.pos.x, 0) <= 0) && !this.collide(platforms, this.pos.add(move_vector))) {
            this.pos = this.pos.add(move_vector);
            this.relative_pos = this.relative_pos.add(move_vector);
          }
        } else {
          if (i==1) {
            if (this.jumps[0] && this.on_surface(platforms, this.pos)) {
              this.jumps[1] = true;
              this.jumps[0] = false;
              this.jump_tick = 0;
            }
          }
        }
        this.img_update(TICK_SCALE);
      }
    }
  }

  boolean above(platform p, neo.Vec2 position) {
        return (((position.x+this.dimensions().x >= p.pos.x && position.x+this.dimensions().x <= p.pos.x+p.dimensions.x) || (position.x >= p.pos.x && position.x <= p.pos.x+p.dimensions.x) || p.infinite) && 
            position.y+this.dimensions().y >= p.pos.y && position.y+this.dimensions().y <= p.pos.y+5);
    }

    // check horizontal collision with all objects
    boolean on_surface(ArrayList<platform> platforms, neo.Vec2 position) {
      for (platform p : platforms) {
        if (this.above(p, position)) return true;
      }
      return false;
    }

    boolean collide(ArrayList<platform> platforms, neo.Vec2 position) {
      Shape boundary = new Rectangle2D.Double(position.x, position.y, this.width/this.sprite_values.x, this.height/this.sprite_values.y);
      for (platform p : platforms) {
        if (p.collide) {
          Shape pbounds = new Rectangle2D.Double(p.pos.x, p.pos.y, p.dimensions.x, p.dimensions.y);
          if (boundary.intersects((Rectangle2D) pbounds) && !this.above(p, position)) {
            return true;
          }
        }
      }
      return false;
    }
};



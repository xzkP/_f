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
	// remapping keycodes to more succint movement keys
	HashMap<Integer, Integer> movement;
  final int DOUBLE_JUMP = 15, ULTABLE = 500;
  protected ArrayList<weapon> attacks = new ArrayList<weapon>();
  public int ddt = 0, shootable = 0, walk_tick = 0, score = 0;
  // jumps[2] = double jump
  // dash[2] -> dash, direction
  public boolean directions[] = { false, false, false, false }, jumps[] = { true, false, true, false}, dash[] = { false, false };
  public text criticalText;
  double crit = 0.0, JUMP_VELOCITY = 8, DOUBLE_JUMP_VELOCITY =15;
  neo.Vec2 vel;
  // melee, weapon.
  boolean permeate = false;
  platform pt;
	sound walk = new sound("./src/sounds/walking.wav"), bounce = new sound("./src/sounds/boing.wav");

  public player(String fn, double px, double py, int w_count, int h_count, neo nn) {
    super(fn, px, py, w_count, h_count, nn);
    this.attacks.add(new weapon("fireballs", 30, this.nn));
    this.vel = nn.new Vec2(0, 0);
		this.walk.setVolume(0.05);
		this.bounce.setVolume(0.05);
  }


  public void criticalTextInit(int x, int y) {
    this.criticalText = new text("0.00%  " + String.format("[%d]", this.score), x, y, "0xFFFFFF", 48);
  }

  void move(ArrayList<platform> platforms, double FRAME_SCALING) {
    double movement_scalar = 5.0;
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
					if (this.walk_tick >= 75 && this.onSurface(platforms, this.pos)) {
						this.walk.playBeginning();
						this.walk_tick = 0;
					}
        } else {
          if (i%2==i) {
            this.source_dim.y = 64*3;
            boolean surface = this.onSurface(platforms, this.pos);
            if (surface && this.jumps[0]) {
              this.vel.y = -this.JUMP_VELOCITY;
              this.jump_tick = 0;
              this.jumps[0] = false;
              this.jumps[1] = true;
            } else if (!surface && this.jumps[1] && this.jumps[2] && this.ddt >= DOUBLE_JUMP) {
              this.vel.y = -this.DOUBLE_JUMP_VELOCITY;
              this.jump_tick = 0;
              this.jumps = new boolean[]{this.jumps[0], true, false, true};
            }
          } else {
            boolean surface = this.onSurface(platforms, this.pos);
            if (!surface && (this.jumps[0] || this.jumps[1])) {
              this.vel.y += 0.35;
            } else {
              platform g = getSurface(platforms);
              if (g != null && g.permeable) {
                this.permeate = true;
                this.pt = g;
                this.modVel(0, 0.375);
              }
            }
          }
        }
      }
      this.imgUpdate(FRAME_SCALING);
    }
  }

  public void respawn(double px, double py) {
    this.pos = this.nn.new Vec2(px, py);
    this.vel = this.nn.new Vec2(0, 0);
    this.crit = 0;
		this.criticalText.updateMsg("0.00%  " + String.format("[%d]", this.score));
    for (weapon w : this.attacks) {
      w.erase();
    }
  }

	public void addScore() {
		++this.score;
		this.criticalText.updateMsg("0.00%  " + String.format("[%d]", this.score));
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
			this.bounce.playBeginning();
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
		int prevState = (int) Math.log10(this.crit);
    this.crit += ((b.getDmg()/b.dmgInfo()[1])*Math.random()*5+5);
    this.criticalText.updateMsg(String.format("%.2f", this.crit)+"%  " + String.format("[%d]", this.score));
    boolean critical = Double.compare(Math.random(), (1-this.crit/100)) >= 0;
    double scalar = (critical?Math.random()*(this.crit/100)+1:1);
    this.modVel((b.isForward()?1:-1)*b.knockback.x*scalar, b.knockback.y*scalar);
  }

	public void updateCritical(double dmg) {
		this.crit += (Math.random()*5+5);
		this.criticalText.updateMsg(String.format("%.2f", this.crit)+"%  " + String.format("[%d]", this.score));
	}

	// check if the second player is within the range, and push him back
	public void melee(player p2, double RANGE, ArrayList<text> queue) {
		Rectangle2D p1_boundary, p2_boundary;
		neo.Vec2 p1_dimensions = this.dimensions(), p2_dimensions = p2.dimensions(), p2_position = p2.position();
		p2_boundary = new Rectangle2D.Double(p2_position.x, p2_position.y, p2_dimensions.x, p2_dimensions.y);

		// p1_boundary must be according to whether the character is facing forward or not
		p1_boundary = new Rectangle2D.Double(this.pos.x-(this.forward?0:RANGE), this.pos.y-RANGE, p1_dimensions.x+RANGE, p1_dimensions.y+RANGE);

		boolean inRange = (p1_boundary.intersects(p2_boundary));

		if (inRange) {
			Rectangle2D intersection = p1_boundary.createIntersection(p2_boundary);
			neo.Vec2 POI = nn.new Vec2(intersection.getX()+intersection.getWidth()/2, intersection.getY()+intersection.getHeight()/2), midpoint = nn.new Vec2(this.pos.x+this.dimensions().x/2, this.pos.y+this.dimensions().y/2);
			neo.Vec2 t = POI.subtract(midpoint);
			double distance = Math.sqrt(Math.pow(t.x, 2)+Math.pow(t.y,2));
			double angle = Math.acos((distance != 0) ? t.x/distance : 0);
			// TODO: make dmg according to location hit --> most damage is done from the bottom & the top [sin(angle) will resolve the problem]
			double rate = Math.sin(angle), dmg = Math.max(0.5, rate)*10;
			queue.add(new text(String.format("%.2f", dmg), (int) this.pos.x, (int) this.pos.y, "0xFFFFFF"));
			p2.modVel(Math.max(this.crit/100, 0.75)*1.5*(1+Math.cos(angle)), Math.max(this.crit/100, 0.75)*-2.75*(1+Math.sin(angle)));
			p2.updateCritical(dmg);
		}
	}

  abstract public void ult();
};

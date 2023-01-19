package libs.functionality;

import libs.math.*;
import libs.gfx.*;
import java.awt.geom.Rectangle2D;
import libs.gfx.sprite;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.*;

public class weapon {
  ArrayList<bullet> bullets = new ArrayList<bullet>();
  String name, description;
  String bullet_fn;
  double dmg;
  neo nn;

  public weapon(String nm, double damage, neo n) {
    this.name = nm;
    this.dmg = damage;
    this.nn = n;
  }

  public void updateBullets(int TICKS) {
    for (Iterator<bullet> i=bullets.iterator(); i.hasNext();) {
			bullet bullet = i.next();
			bullet.updatePos();
      try {
        if (bullet.survivalTicks >= TICKS) { i.remove(); }
      } catch (Exception e) { System.out.println(e); }
    }
  }

  public void collide(ArrayList<platform> platforms) {
    for (Iterator<bullet> i=bullets.iterator(); i.hasNext();) {
      bullet b = i.next();
      neo.Vec2 bdim = b.img.dimensions();
      Shape boundary = new Rectangle2D.Double(b.position.x, b.position.y, bdim.x, bdim.y);
      for (platform p : platforms) {
        if (p.collide) {
          neo.Vec2 pdim = p.getDimensions();
          Shape pb = new Rectangle2D.Double(p.getPos().x, p.getPos().y, pdim.x, pdim.y);
          if (boundary.intersects((Rectangle2D) pb) && b.canCollide()) {
						p.shot(b.getDmg());
            try {
              i.remove();
            } catch (Exception e) { System.out.println(e); }
          }
        }
      }
    }
  }
  public void render(Graphics g, neo.Vec2 scope, neo.Vec2 dimensions) {
		int pos_x = (int) (scope.x), pos_y = (int) (scope.y), dx = (int) (dimensions.x), dy = (int) (dimensions.y);
		int lower_x = (pos_x/dx)*dx, upper_x = lower_x+dx;
		for (bullet bullet : bullets) {
			if (bullet.position.x > lower_x && bullet.position.x < upper_x) {
				sprite img = bullet.img;
				int display_x = (int) (bullet.position.x), display_y = (int) (bullet.position.y);
				neo.Vec2 bullet_dimensions = img.dimensions();
				try {
					g.drawImage(img.getImg(), display_x, display_y, display_x+(int) (bullet_dimensions.x), display_y+(int) (bullet_dimensions.y), 0, 0, (int) (img.dimensions().x), (int) (img.dimensions().y), null);
				} catch (Exception e) { System.out.println(e); }
			}
		}
	}

	public void hit(ArrayList<player> players, int skip, ArrayList<text> q) {
		int count = 0;
		for (Iterator<bullet> i = bullets.iterator(); i.hasNext();) {
			bullet bullet = i.next();
			neo.Vec2 bullet_dimensions = bullet.img.dimensions();
			Shape bullet_boundary = new Rectangle2D.Double(bullet.position.x, bullet.position.y, bullet_dimensions.x, bullet_dimensions.y);
			for (int n = 0; n < players.size(); n++) {
				neo.Vec2 player_dimensions, player_position;
				if (n == skip) continue;
				player player = players.get(n);
				player_dimensions = player.dimensions();
				player_position = player.position();
				Shape player_hitbox = new Rectangle2D.Double(player.position().x, player.position().y, player_dimensions.x, player_dimensions.y);
				if (bullet_boundary.intersects((Rectangle2D) player_hitbox)) {
					text dmg = new text(String.format("%.2f", bullet.dmg), (int) (player_position.x), (int) (player_position.y), "0xFFFFFF");
					q.add(dmg);
					player.updateCritical(bullet);
					try {
						i.remove();
					} catch (Exception e) { System.out.println(e);}
				}
			}
		}
	}

  public void shoot(neo.Vec2 p, boolean f) {
    bullet b = new bullet("./src/sprites/fireball.bmp", dmg, 32, 32, 1, 1, f, this.nn);
    b.shot = true;
    b.position = nn.new Vec2(p.x, p.y);
    b.position.x += (f?1:-1)*b.img.dimensions().x;
    bullets.add(b);
  }
  public void shoot(neo.Vec2 p, boolean f, bullet b) {
    b.shot = true;
    b.position = nn.new Vec2(p.x, p.y);
    b.position.x += (f?1:-1)*b.img.dimensions().x;
    bullets.add(b);
  }
	public void shoot(bullet b) {
		bullets.add(b);
	}
  public void erase() {
    try {
      this.bullets.clear();
    } catch (Exception e) { System.out.println(e); }
  }
  @Override
  public String toString() {
    return String.format("WEAPON: %s\nDESCRIPTION: %s\nDAMAGE: %f", name, description, dmg);
  }
};

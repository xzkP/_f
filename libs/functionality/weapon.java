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
  public weapon(String nn, double damage) {
    name = nn;
    dmg = damage;
  }
  public void update(int TICKS) {
    for (Iterator<bullet> i=bullets.iterator(); i.hasNext();) {
      bullet b = i.next();
      b.update();
      if (b.cticks >= TICKS) {
        try { i.remove(); } catch (Exception e) {}
      }
    } 
  }
  public void collide(ArrayList<platform> platforms) {
    for (Iterator<bullet> i=bullets.iterator(); i.hasNext();) {
      bullet b = i.next();
      Shape boundary = new Rectangle2D.Double(b.position.x, b.position.y, b.img.dimensions().x, b.img.dimensions().y);
      for (platform p : platforms) {
        if (p.collide) {
          Shape pb = new Rectangle2D.Double(p.get_pos().x, p.get_pos().y, p.get_dimensions().x, p.get_dimensions().y);
          if (boundary.intersects((Rectangle2D) pb)) {
            try { 
              i.remove(); p.shoot(b);
            } catch (Exception e) {}
          }
        }
      }
    }
  }

  public void render(Graphics g, neo.Vec2 scope, neo.Vec2 dimensions) {
    int px = (int) scope.x, py = (int) scope.y, dx = (int) dimensions.x, dy = (int) dimensions.y;
    int lb_x = (px/dx)*dx, ub_x = (px/dx+1)*dx;
    for (bullet b : bullets) {
      if (b.position.x > lb_x && b.position.x < ub_x) {
        int dpx = ((int)(b.position.x))%((int)(dimensions.x)), dpy = ((int)(b.position.y))%((int)(dimensions.y));
        g.drawImage(b.img.get_img(), dpx, dpy, dpx+(int)(b.img.dimensions().x), dpy+(int)(b.img.dimensions().y), 0, 0, 32, 32, null);
      }
    }
  }

  public void shoot(neo.Vec2 p, boolean f) {
    neo nn = new neo();
    bullet b = new bullet("sprites/bullet.bmp", dmg, 32, 32, 1, 1);
    b.shot = true;
    b.position = nn.new Vec2(p.x, p.y);
    b.position.x += (f?1:-1)*b.img.dimensions().x;
    b.forward = f;
    bullets.add(b);
  }

  @Override
  public String toString() {
    return String.format("WEAPON: %s\nDESCRIPTION: %s\nDAMAGE: %f", name, description, dmg);
  }
};

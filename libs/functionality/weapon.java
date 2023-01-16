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
  public void update(int TICKS) {
    for (Iterator<bullet> i=bullets.iterator(); i.hasNext();) {
      try {
        bullet b = i.next();
        b.update();
        if (b.cticks >= TICKS) { i.remove(); }
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
          if (boundary.intersects((Rectangle2D) pb)) {
            try { 
              p.shoot(b);
              i.remove();
            } catch (Exception e) { System.out.println(e); }
          }
        }
      }
    }
  }
  public void render(Graphics g, neo.Vec2 scope, neo.Vec2 dimensions) {
    int px = (int) scope.x, py = (int) scope.y, dx = (int) dimensions.x, dy = (int) dimensions.y;
    int lb_x = (px/dx)*dx, ub_x = (px/dx+1)*dx;

    try {
      for (bullet b : bullets) {
        neo.Vec2 dim = b.img.dimensions();
        if (b.position.x > lb_x && b.position.x < ub_x) {
          int dpx = ((int)(b.position.x))%((int)(dimensions.x)), dpy = ((int)(b.position.y))%((int)(dimensions.y));
          g.drawImage(b.img.getImg(), dpx, dpy, dpx+(int)(dim.x), dpy+(int)(dim.y), 0, 0, (int) b.img.dimensions().x, (int) b.img.dimensions().y, null);
        }
      }
    } catch (Exception e) { System.out.println(e); }
  }
  public void hit(ArrayList<player> players, int index, Graphics g, ArrayList<text> q)  {
    int count = 0;
    try {
      for (Iterator<bullet> i=bullets.iterator(); i.hasNext();) {
        bullet b = i.next();
        neo.Vec2 bdim = b.img.dimensions(), pdim;
        Shape bullet_bound = new Rectangle2D.Double(b.position.x, b.position.y, bdim.x, bdim.y);
        for (int n = 0; n < players.size(); n++) {
          if (n == index) continue;
          player p = players.get(n);
          pdim = p.dimensions();
          Shape hitbox = new Rectangle2D.Double(p.position().x, p.position().y, pdim.x, pdim.y);
          if (bullet_bound.intersects((Rectangle2D) hitbox)) {
            neo.Vec2 position = p.position();
            text dmg = new text(String.format("%.2f", b.dmg), (int) position.x, (int) (position.y-p.dimensions().y), "0xFFFFFF");
            q.add(dmg);
            //p.modVel(b.forward?1:-1*3, -5);
            p.updateCritical((b.dmg/b.baseDmg)*Math.random()*10+5, b.forward);
            i.remove(); 
          }
        }
      }
    } catch (Exception e) { System.out.println(e); }
  }
  public void shoot(neo.Vec2 p, boolean f) {
    bullet b = new bullet("sprites/fireball.bmp", dmg, 32, 32, 1, 1, this.nn);
    b.shot = true;
    b.position = nn.new Vec2(p.x, p.y);
    b.position.x += (f?1:-1)*b.img.dimensions().x;
    b.forward = f;
    bullets.add(b);
  }
  public void shoot(neo.Vec2 p, boolean f, bullet b) {
    b.shot = true;
    b.position = nn.new Vec2(p.x, p.y);
    b.position.x += (f?1:-1)*b.img.dimensions().x;
    b.forward = f;
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

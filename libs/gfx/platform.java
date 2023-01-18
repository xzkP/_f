package libs.gfx;
import libs.math.*;
import libs.functionality.*;
import java.awt.*;
import java.util.HashMap;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

/* platforms:
 - size & shape --> probably mostly rectangular 
 - width & height
*/
public class platform {
  String title;
	public boolean infinite = false, collide = true, damageable = false, permeable = true, loaded = true;
  double health = Integer.MAX_VALUE;
  neo nn;
	neo.Vec2 dimensions, pos, tile_dimensions;
	Color pc;
  BufferedImage tile;
	public platform(int x, int y, int w, int h, neo n) {
    try {
      this.tile = ImageIO.read(new File("./sprites/wall.bmp"));
      int tw = tile.getWidth(), th = tile.getHeight();
      this.nn = n;
      this.dimensions = this.nn.new Vec2(Math.max(1, w/tw)*tw, Math.max(1, h/th)*th);
      this.pos = this.nn.new Vec2(x, y);
      this.tile_dimensions = this.nn.new Vec2(tw, th);
    } catch (Exception e) {
      this.loaded = false;
    }
	}
	public platform(int x, int y, int w, int h, String c, neo n) {
    try {
      tile = ImageIO.read(new File("./sprites/wall.bmp"));
      int tw = tile.getWidth(), th = tile.getHeight();
      this.nn = n;
      this.dimensions = this.nn.new Vec2(Math.max(1, w/tw)*tw, Math.max(1, h/th)*th);
      this.pos = nn.new Vec2(x, y);
      this.assignColor(c);
      this.tile_dimensions = this.nn.new Vec2(tw, th);
    } catch (Exception e) {
      this.loaded = false;
    }
	}
	public platform(int x, int y, int w, int h, String c, String t, neo n) {
    try {
      tile = ImageIO.read(new File("./sprites/wall.bmp"));
      int tw = tile.getWidth(), th = tile.getHeight();
      this.nn = n;
      this.dimensions = this.nn.new Vec2(Math.max(1, w/tw)*tw, Math.max(1, h/th)*th);
      this.pos = nn.new Vec2(x, y);
      this.assignColor(c);
      this.title = t;
      this.tile_dimensions = this.nn.new Vec2(tw, th);
    } catch (Exception e) {
      this.loaded = false;
    }
	}
  public platform(int x, int y, int w, int h, String c, neo n, String fn) {
    try {
      tile = ImageIO.read(new File(fn));
      int tw = tile.getWidth(), th = tile.getHeight();
      this.nn = n;
      this.dimensions = this.nn.new Vec2(Math.max(1, w/tw)*tw, Math.max(1, h/th)*th);
      this.pos = nn.new Vec2(x, y);
      this.tile_dimensions = this.nn.new Vec2(tw, th);
      this.assignColor(c);
    } catch (Exception e) {
      this.loaded = false;
    }
  }
  public void modify(int w, int h) {
    this.dimensions = this.nn.new Vec2(w,h);
  }
  public void setHealth(double h) {
    this.health = h;
    this.damageable = true;
  }
	public void shot(double dmg) {
		if (damageable) this.health -= dmg;
	}
	public void assignColor(String cname) {
		HashMap<String, String> colors = new HashMap<String, String>() {{
			put("black", "000000");
			put("white", "FFFFFF");
			put("red", "FF0000");
			put("green", "00FF00");
			put("blue", "0000FF");
			put("grey", "222222");
			put("cyan", "00FFFF");
		}};
		int color = Integer.parseInt((colors.containsKey(cname.toLowerCase()) ? colors.get(cname.toLowerCase()) : colors.get("grey")), 16);
    this.assignColor(color);
	}
  public neo.Vec2 getPos() {
    return nn.new Vec2(pos.x, pos.y);
  }
  public neo.Vec2 getDimensions() {
    return nn.new Vec2(dimensions.x, dimensions.y);
  }
  public void assignColor(int H) {
    pc = new Color(H>>16&0xFF, H>>8&0xFF, H&0xFF, 255);
  }
	// hex codes 0xRRGGBB + 0xAA (alpha --> should be defaulted at 255)
	public void assignColor(int R, int G, int B, int A) {
		pc = new Color(R, G, B, A);
	}
  public void render(Graphics g) {
    for (int x = 0; x < this.dimensions.x/tile_dimensions.x; x++) {
      for (int y = 0; y < this.dimensions.y/tile_dimensions.y; y++) {
        g.drawImage(this.tile, (int) (x*tile_dimensions.x+this.pos.x), (int) (y*tile_dimensions.y+this.pos.y), null);
      }
    }

  }
  public boolean under(player main, neo.Vec2 position) {
    neo.Vec2 dim = main.dimensions();
    boolean ret =  (((position.x+dim.x >= this.pos.x && position.x+dim.x <= this.pos.x+this.dimensions.x) || (position.x >= this.pos.x && position.x <= this.pos.x+this.dimensions.x) || this.infinite) && position.y+dim.y >= this.pos.y && position.y+dim.y <= this.pos.y+this.dimensions.y);
    // adjust main so that it lies on the platform.
		main.modPos(0, (ret&&!main.permeate)?this.pos.y-(main.pos.y+dim.y):0);
		return ret;
  }
  @Override
  public String toString() {
    return String.format("(%.2f, %.2f)", this.getPos().x, this.getPos().y);
  }
};

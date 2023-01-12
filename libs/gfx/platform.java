package libs.gfx;
import libs.math.*;
import libs.functionality.*;
import java.awt.*;
import java.util.HashMap;

/* platforms:
 - size & shape --> probably mostly rectangular 
 - width & height
 */
public class platform {
  String title;
	public boolean infinite = false, collide = true, damageable = false;
  double health = Integer.MAX_VALUE;
  neo nn;
	neo.Vec2 dimensions, pos;
	Color pc;
	public platform(int x, int y, int w, int h, neo n) {
    this.nn = n;
		this.dimensions = this.nn.new Vec2(w, h);
		this.pos = this.nn.new Vec2(x, y);
	}
	public platform(int x, int y, int w, int h, String c, neo n) {
    this.nn = n;
		this.dimensions = nn.new Vec2(w, h);
		this.pos = nn.new Vec2(x, y);
		this.assign_color(c);
	}
	public platform(int x, int y, int w, int h, String c, String t, neo n) {
    this.nn = n;
		this.dimensions = nn.new Vec2(w, h);
		this.pos = nn.new Vec2(x, y);
		this.assign_color(c);
    this.title = t;
	}
  public boolean visible(neo.Vec2 pos, int scope) {
    int px = (int) pos.x, py = (int) pos.y;
    return (this.pos.x > (px/scope)*scope && this.pos.x < (px/scope+1)*scope);
  }
  public void modify(int w, int h) {
    this.dimensions = this.nn.new Vec2(w,h);
  }
  public void setHealth(double h) {
    this.health = h;
    this.damageable = true;
  }
  public void shoot(bullet bb) {
    if (damageable) this.health -= bb.get_dmg();
  }
	public void assign_color(String cname) {
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
    this.assign_color(color);
	}
  public neo.Vec2 get_pos() {
    return nn.new Vec2(pos.x, pos.y);
  }
  public neo.Vec2 get_dimensions() {
    return nn.new Vec2(dimensions.x, dimensions.y);
  }
  public void assign_color(int H) {
    pc = new Color(H>>16&0xFF, H>>8&0xFF, H&0xFF, 255);
  }
	// hex codes 0xRRGGBB + 0xAA (alpha --> should be defaulted at 255)
	public void assign_color(int R, int G, int B, int A) {
		pc = new Color(R, G, B, A);
	}
  public void render(Graphics g, int width, int height) {
    g.setColor(this.pc);
    g.fillRect(nn.mod((int) this.pos.x, width), nn.mod((int) this.pos.y, height), (int) this.dimensions.x, (int) this.dimensions.y);
  }
  public boolean under(player main, neo.Vec2 position) {
    neo.Vec2 dim = main.dimensions();
    return (((position.x+dim.x >= this.pos.x && position.x+dim.x <= this.pos.x+this.dimensions.x) || (position.x >= this.pos.x && position.x <= this.pos.x+this.dimensions.x) || this.infinite) && position.y+dim.y >= this.pos.y && position.y+dim.y <= this.pos.y+this.dimensions.y);
  }
  @Override
  public String toString() {
    return String.format("(%.2f, %.2f)", this.get_pos().x, this.get_pos().y);
  }
};

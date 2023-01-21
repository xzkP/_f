package libs.functionality;

import libs.math.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.Font;

public class text extends JComponent {
  public int limit = 100, ticks = 0;
  public String message;
  public boolean hasLimit = true;
  neo nn = new neo();
  neo.Vec2 position;
  Color c;
  Font f = new Font("04b03", Font.PLAIN, 24);

  public text(String msg, int px, int py) {
    this.message = msg;
    this.position = nn.new Vec2(px, py);
    this.assignColor("0xFFFFFF");
  }

  public text(String msg, int px, int py, String color) {
    this.message = msg;
    this.position = nn.new Vec2(px, py);
    this.assignColor(color);
  }

  public text(String msg, int px, int py, String color, int fontSize) {
    this.message = msg;
    this.position = nn.new Vec2(px, py);
    this.assignColor(color);
    this.f = new Font("04b03", Font.PLAIN, fontSize);
  }

  public void updateMsg(String new_msg) {
    this.message = new_msg;
  }

  void assignColor(String hex) {
    int H = Integer.decode(hex);
    this.assignColor(H);
  }

  void assignColor(int H) {
    c = new Color(H>>16&0xFF, H>>8&0xFF, H&0xFF);
  }

  public void renderText(Graphics g) {
    this.setOpaque(false);
    g.setFont(f);
    g.setColor(this.c);
    g.drawString(this.message, (int) (this.position.x), (int) (this.position.y));
  }

	public void changePosition(int x, int y) {
		this.position = nn.new Vec2(x, y);
	}

	public void shiftPosition(int x, int y) {
		this.position = this.position.add(nn.new Vec2(x,y));
	}
};

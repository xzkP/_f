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
  public String message;
  neo.Vec2 position;
  Color c;
  Font f = new Font("C&C Red Alert [INET]", Font.PLAIN, 24);
  public text(String msg, int px, int py) {
    neo nn = new neo();
    this.message = msg;
    this.position = nn.new Vec2(px, py);
    this.assign_color("0xFFFFFF");
  }
  public text(String msg, int px, int py, String color) {
    neo nn = new neo();
    this.message = msg;
    this.position = nn.new Vec2(px, py);
    this.assign_color(color);
  }

  public void update_msg(String new_msg) {
    this.message = new_msg;
  }

  void assign_color(String hex) {
    int H = Integer.decode(hex);
    this.assign_color(H);
  }
  void assign_color(int H) {
    c = new Color(H>>16&0xFF, H>>8&0xFF, H&0xFF);
  }

  public void renderText(Graphics g) {
    this.setOpaque(false);
    g.setFont(f);
    g.setColor(this.c);
    g.drawString(this.message, (int) this.position.x, (int) this.position.y);
  }
};


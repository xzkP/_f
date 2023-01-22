package libs.gfx;

import libs.gfx.Button;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Arrays;

public class menu {
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	int height = (int) screen.getHeight(), width = (int) screen.getWidth();
	BufferedImage background = null, resized = null;
	Color primary = new Color(0, 79, 207), secondary = new Color(0, 0, 0), highlight = new Color(236, 231, 95);
	Font button = new Font("04b03", Font.BOLD, 50), title = new Font("04b03", Font.BOLD, 100);
	ArrayList<Button> buttons = new ArrayList<Button>(Arrays.asList(new Button(width/2-50, 350, 100, 50, "play"), new Button(width/2-50, 425, 100, 50, "help"), new Button(width/2-50, 500, 100, 50, "exit")));
	public menu() {
		try {
			this.background = ImageIO.read(new File("./src/sprites/background.png"));
			this.resized = new BufferedImage(this.width,this.height,this.background.getType());
			Graphics2D graphics = resized.createGraphics();
			graphics.drawImage(this.background,0,0,this.width,this.height, null);
		} catch (IOException e) { System.out.println(e); }
	}
	public void render(Graphics g) {
		g.drawImage(this.resized,0,0,null);
		// drawing title
		g.setColor(this.primary);
		g.setFont(this.title);
		g.drawString("dungeon", width/2-175, 170);

		g.setFont(this.button);
		for (int i = 0; i < buttons.size(); i++) {
			Button button = buttons.get(i);
			g.setColor(button.highlighted?this.secondary:this.highlight);
			g.drawString(button.getTitle(), button.x-2, button.y+35+2);
			g.setColor(this.primary);
			g.drawString(button.getTitle(), button.x, button.y+35);
		}
	}
	public ArrayList<Button> getButtons() {
		return this.buttons;
	}
};


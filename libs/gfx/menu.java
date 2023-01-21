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

public class menu extends JPanel {
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int height = (int) screenSize.getHeight(), width = (int) screenSize.getWidth();
	BufferedImage background = null, resized = null;
	Color color = new Color(0, 79, 207), secondary = new Color(0, 0, 0), highlight = new Color(236, 231, 95);
	Font button_font = new Font("04b03", Font.BOLD, 50), title_font = new Font("04b03", Font.BOLD, 100);

	String options[] = { "play", "help", "exit" };

	// 0: play, 1: help, 2: exit
	ArrayList<Button> buttons = new ArrayList<Button>(Arrays.asList(new Button(width/2-50, 350, 100, 50, "play"), new Button(width/2-50, 425, 100, 50, "help"), new Button(width/2-50, 500, 100, 50, "exit")));

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		try {
			this.background = ImageIO.read(new File("./src/sprites/background.png"));
			this.resized = new BufferedImage(width,height,background.getType());
			Graphics2D graphics = resized.createGraphics();
			graphics.drawImage(this.background, 0, 0, this.width, this.height, null);
		} catch (IOException ex) { ex.printStackTrace(); }

		g2d.drawImage(this.resized, 0, 0, null);
		g.setFont(this.title_font);

		g.setColor(this.color);
		g.drawString("dungeon", width/2-175, 170);
		g.setColor(this.color);
		g.drawString("dungeon", width/2-173, 170);

		g.setFont(this.button_font);

		// Drawing strings
		for (int i = 0; i < buttons.size(); i++) {
			Button button = buttons.get(i);
			g.setColor(button.highlighted?secondary:highlight);
			g.drawString(options[i], button.x-2, button.y+35+2);
			g.setColor(color);
			g.drawString(options[i], button.x, button.y+35);
		}
	}
	public ArrayList<Button> getButtons() {
		return this.buttons;
	}
}

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

public class Menu {
	String info = "Dungeon, a game made by Josh Souphanthong and Henry Wang.";
	String p1 = "Player 1. Melee: T. Fireballs: Y. Movement: W-A-S-D";
	String p2 = "Player 2. Melee: N. Fireballs: M. Movement: Arrow Keys";
	int height, width;
	BufferedImage background = null, resized = null;
	Color primary = new Color(255, 255, 255), secondary = new Color(0, 0, 0), highlight = new Color(218,112,214);
	Font button = new Font("04b03", Font.BOLD, 100), title = new Font("04b03", Font.BOLD, 200), regular = new Font("04b03", Font.BOLD, 35);
	ArrayList<Button> buttons;
	public Menu(int w, int h) {
		this.width = w;
		this.height = h;
		this.buttons = new ArrayList<Button>(Arrays.asList(new Button(width/2-125, 400, 250, 100, "PLAY"), new Button(width/2-125, 500, 250, 100, "HELP"), new Button(width/2-125, 600, 250, 100, "EXIT")));
		try {
			this.background = ImageIO.read(new File("./src/sprites/background.png"));
			this.resized = new BufferedImage(this.width,this.height,this.background.getType());
			Graphics2D graphics = resized.createGraphics();
			graphics.drawImage(this.background,0,0,this.width,this.height, null);
		} catch (IOException e) { System.out.println(e); }
	}
	public void render(Graphics g) {
		g.drawImage(this.resized,0,0,null);
		if (window.state == window.STATE.Menu) {
			g.setFont(this.title);
			// drawing title
			for (int i = 0; i < 5; i++) {
				g.setColor(i%2==0?this.secondary:this.highlight);
				g.drawString("dungeon", width/2-450-(4*i), 170+(4*i));
			}

			g.setFont(this.button);
			for (int i = 0; i < buttons.size(); i++) {
				Button button = buttons.get(i);
				g.setColor(button.highlighted?this.highlight:this.secondary);
				g.drawString(button.getTitle(), button.x-2, button.y+35+2);
				g.setColor(this.primary);
				g.drawString(button.getTitle(), button.x, button.y+35);
			}
		} else if (window.state == window.STATE.Help) {
			g.setFont(this.regular);
			g.setColor(this.primary);
			g.drawString(info, 100, 100);
			g.drawString(p1, 100, 200);
			g.drawString(p2, 100, 300);
		}
	}
	public ArrayList<Button> getButtons() {
		return this.buttons;
	}
};



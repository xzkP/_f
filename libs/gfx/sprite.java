package libs.gfx;

import libs.functionality.*;
import libs.math.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.ArrayList;

public class sprite {
  final private int CAPACITY = 5;
  protected boolean loaded = true, forward = true;
	BufferedImage img;
	int width, height, counter = 0, jump_tick = 500; 
  protected neo nn;
  protected neo.Vec2 pos, sprite_values, source_dim;

  public sprite(String fn, double px, double py, int w_count, int h_count, neo n) {
		try {
      File f = new File(fn);
      this.nn = n;
			this.pos = this.nn.new Vec2(px, py);
			this.sprite_values= this.nn.new Vec2(w_count, h_count);
			this.source_dim = this.nn.new Vec2(0, 0);
			this.img = ImageIO.read(f);
			this.width = img.getWidth();
			this.height = img.getHeight();
		} catch (Exception e) {
			System.out.println(String.format("Could not load file: %s", fn));
			this.loaded = false;
		}
	}

  public neo.Vec2 position() {
    return this.nn.new Vec2(this.pos.x, this.pos.y);
  }

	public neo.Vec2 dimensions() {
		return this.nn.new Vec2(this.width/this.sprite_values.x, this.height/this.sprite_values.y);
	}

  public BufferedImage getImg() {
    return this.img;
  }

  public void imgUpdate(double scale) {
		this.counter = (counter+1)%((int)(60*scale));
		if (this.counter%((int)(20*scale))==0) {
			this.source_dim.x += this.dimensions().x;
			this.source_dim.x %= (this.width);
		}
	}
};

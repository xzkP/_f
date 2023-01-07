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
  boolean loaded = true, forward = true;
	BufferedImage img;
	int width, height, counter = 0, jump_tick = 500, shot_tick=0;
  neo nn;
  neo.Vec2 pos, relative_pos, sprite_values, source_dim;
  weapon equipped;

  public sprite(String fn, double px, double py, int w_count, int h_count, neo n) {
		try {
      File f = new File(fn);
      this.nn = n;
			this.pos = this.nn.new Vec2(px, py);
			this.relative_pos = this.nn.new Vec2(px, py);
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

	public neo.Vec2 dimensions() {
		return this.nn.new Vec2(this.width/this.sprite_values.x, this.height/this.sprite_values.y);
	}

  public BufferedImage get_img() {
    return this.img;
  }

  public void img_update(double scale) {
		this.counter = (counter+1)%((int)(60*scale));
		if (this.counter%((int)(4*scale))==0) {
			this.source_dim.x += this.dimensions().x;
			this.source_dim.x %= (this.width);
		}
	}

  void shoot() {
    /* TODO: 
     - probably make a weapons class (for inventory) + parser (for reading weapons from file)
     - inventory --> upto 3 weapons?
     - currently selected weapon? --> melee fists
     */
    this.equipped.shoot(pos, forward);
  }

};

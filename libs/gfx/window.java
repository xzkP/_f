package libs.gfx;

import libs.math.*;
import libs.functionality.*;
import javax.script.*;
import java.io.*;
import libs.gfx.sprite;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class window {
  // all tick rates are based on 60.
	private final int 
    FRAME_PERIOD = 120, BULLET_TICKS = 300, BOUNCER_HEIGHT = 5, BOUNCER_WIDTH = 100;
  private double fps, TICK_SCALE = FRAME_PERIOD/60.0, JUMP_GRAVITY=0.7/TICK_SCALE, FALL_GRAVITY=0.15/TICK_SCALE, LAST=0, JUMP_FORCE=15.0;
  int jf=0, djf=0;
  neo nn = new neo();
  neo.Vec2 end;
	JFrame frame;
	Panel p;
	InputKey keys;
	int width, height;
	player p1, p2;
  platform ground;
  ArrayList<player> players = new ArrayList<player>();
	ArrayList<platform> platforms = new ArrayList<platform>();
  ArrayList<bouncer> bouncers = new ArrayList<bouncer>();
	public window(String w_title, int w, int h) {
		width = w;
		height = h;
		p1 = new player("sprites/animated.bmp", w/2, 0, 4, 4, this.nn);
    p1.equipped = new weapon("fists", 30, this.nn);
    p1.inventory.add(p1.equipped);
    p1.movement = new HashMap<Character, Integer>() {{
      put('←', 0);
      put('↑', 1);
      put('→', 2);
      put('↓', 3);
      put('Z', 4);
    }};

    p2 = new player("sprites/animated.bmp", w/2, 0, 4, 4, this.nn);
    p2.equipped = new weapon("fists", 30, this.nn);
    p2.inventory.add(p2.equipped);

    p2.movement = new HashMap<Character, Integer>() {{
      put('A', 0);
      put('W', 1);
      put('D', 2);
      put('S', 3);
      put('J', 4);
    }};


    players.add(p1);
    players.add(p2);

		frame = new JFrame(w_title);
		p = new Panel();
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(p);
		keys = new InputKey();
		p.addKeyListener(keys);
		frame.setVisible(true);

    frame.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        width = frame.getWidth();
        height = frame.getHeight();
        end = nn.new Vec2(width, height);
      }
    });

    this.level("./levels/1.txt");


    this.ground = new platform(100, 800, width-200, 100, "grey", this.nn);
    this.platforms.add(ground);
    this.bouncers.add(new bouncer(ground, BOUNCER_WIDTH, BOUNCER_HEIGHT, this.nn));
	}

  void level(String fn) {
    try (BufferedReader f = new BufferedReader(new FileReader(fn))) {
      boolean additional = false;
      int nested = 0;
      String l, key="";
      HashMap<String, ArrayList<String>> metadata = new HashMap<String, ArrayList<String>>();
      ArrayList<String> tmp = new ArrayList<String>();
      while ((l = f.readLine()) != null) {
        l = l.strip();
        if (nested == 0) {
          tmp.clear();
          key = l.substring(0, l.indexOf(":")).strip().toLowerCase();
          additional = (l.contains("{"));
          if (!additional) {
            tmp.add(l.substring(l.indexOf(":")+1).strip()); 
          }
          else { nested++; continue; }
        } 

        if (additional && !(l.equals("{") || l.equals("}"))) {
          tmp.add(l);
        }

        nested += (l.contains("{") && l.contains("}") ? 0 : l.contains("{") ? 1 : l.contains("}") ? -1 : 0 );

        if (nested == 0 && !metadata.containsKey(key)) {
          ArrayList<String> unreferenced = new ArrayList<String>(tmp);
          metadata.put(key, unreferenced);
        }
      }
      parse_platform(metadata.get("platforms"));
      if (metadata.containsKey("end")) {
        String endl = metadata.get("end").get(0).replace("{","").replace("}","").replace("[","").replace("]","").strip();
        int px = Integer.parseInt(endl.substring(0, endl.indexOf(",")).strip()), 
            py = Integer.parseInt(endl.substring(endl.indexOf(",")+1).strip());
      } 
      this.end = this.nn.new Vec2(this.width, this.height);
    } catch (Exception e) {
      System.out.println(e);
      System.out.println(String.format("Can't read file: %s", fn));
    }
  }

  void push_HM(ArrayList<HashMap<String, String>> A, HashMap<String, String> B) {
    HashMap<String, String> tmp = new HashMap<String, String>(B);
    A.add(tmp);
  }

  void parse_platform(ArrayList<String> data) {
    HashMap<String, String> tmp = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> split = new ArrayList<HashMap<String, String>>();
    for (String dat : data) {
      String key = dat.substring(0, dat.indexOf(":")).strip(), value = dat.substring(dat.indexOf(":")+1).strip();
      if (key.equals("position") && tmp.size() != 0) {
        push_HM(split, tmp);
        tmp.clear();
      }
      tmp.put(key, value);
    }
    push_HM(split, tmp);

    for (HashMap<String, String> info : split) {
      if (info.containsKey("position") && info.containsKey("dimensions")) {
        neo.Vec2 d_v, p_v;
        String pos = info.get("position").replace("[","").replace("]","").replace("{","").replace("}","").strip(), 
               dim = info.get("dimensions").replace("[","").replace("]","").replace("{","").replace("}","").strip();
        String p1 = pos.substring(0, pos.indexOf(",")).strip(),
               p2 = pos.substring(pos.indexOf(",")+1).strip(),
               d1 = dim.substring(0, dim.indexOf(",")).strip(),
               d2 = dim.substring(dim.indexOf(",")+1).strip();
        p_v = this.nn.new Vec2(Integer.parseInt(p1), Integer.parseInt(p2));
        d_v = this.nn.new Vec2(Integer.parseInt(d1), Integer.parseInt(d2));
        String hex = (info.containsKey("hex")?info.get("hex"):"222222"), title=(info.containsKey("title")?info.get("title"):String.format("P%d", this.platforms.size()));
        // assuming that platform is square (TODO: add other shapes)
        platform p = new platform((int) p_v.x, (int) p_v.y, (int) d_v.x, (int) d_v.y, this.nn);
        p.assign_color(Integer.parseInt(hex, 16));
        if (info.containsKey("health")) {
          p.setHealth(Double.parseDouble(info.get("health")));
        }
        this.platforms.add(p);
      }
    }
  }

	public void adjust() {
    for (Iterator<platform> p=platforms.iterator(); p.hasNext();) {
      platform pp = p.next();
      if (pp.health <= 0) {
        try { 
          p.remove();
        } catch (Exception e) {}
      }
    }

    for (player player : players) {
      player.pos.x = this.nn.mod((int) player.pos.x, width);
      player.pos.y = this.nn.mod((int) player.pos.y, height);
      player.pos.x = this.nn.mod((int) player.pos.x, width);
      player.pos.y = this.nn.mod((int) player.pos.y, height);
    }
	}

	public void exec() {
    int delay = 35;
		while (true) {
			frame.repaint();
      for (player player : players) {
        player.bounce(bouncers);
        player.jump_tick++;
        player.ddt++;
        player.jumps[0] = (player.jump_tick >= delay*TICK_SCALE);
        boolean surface = player.on_surface(platforms, player.pos);
        if (!surface) {
          if (player.jumps[1]) {
            player.vel.y = (-player.JUMP_FORCE+(player.jump_tick)*JUMP_GRAVITY);
          } else {
            player.vel.y += FALL_GRAVITY*2;
            player.vel.y = Math.min(100, player.vel.y);
          }
        } else {
          player.jumps = new boolean[]{player.jumps[0], false, true, false };
          player.ddt = 0;
          player.vel.x = 0;
          player.vel.y = 0;
        }

        player.move(platforms, this.end, TICK_SCALE);
        player.mod_pos(player.vel);
        


        for (weapon w : player.inventory) {
          w.update((int) (BULLET_TICKS*TICK_SCALE));
          w.collide(platforms);
        }
      }

			this.adjust();
			try { Thread.sleep(1000/FRAME_PERIOD); } catch (Exception e) {}; 
		}
	}

	class Panel extends JPanel {
    private long last=0;
		public Panel() {
			setFocusable(true);
			requestFocusInWindow();
		}
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			/* boolean Graphics.drawImage(Image img,
       int dstx1, int dsty1, int dstx2, int dsty2,
       int srcx1, int srcy1, int srcx2, int srcy2,
       ImageObserver observer); */
			p.setBackground(Color.black);

      text fps_display = new text("", 15, 30, "0xFFFFFF"), pos_display = new text("", 15, 60, "0xFFFFFF"), velocity = new text("", 15, 90, "0xFFFFFF");
      pos_display.update_msg(String.format("(%.2f, %.2f)", p1.pos.x, p1.pos.y));
      fps_display.update_msg(String.format("FPS: %f", ((double) 1e9)/(System.nanoTime()-last)));
      velocity.update_msg(String.format("(%.2f, %.2f)", p1.vel.x, p1.vel.y));
      last = System.nanoTime();
      pos_display.renderText(g);
      velocity.renderText(g);
      fps_display.renderText(g);

			// check if platform is in viewing distance + render
			for (platform p : platforms) {
				p.pos.x += (p.pos.x%50==0?1:0);
				if ((p.infinite || p.visible(p1.pos, width)) && p.health > 0) {
          p.render(g, width, height);
				}
			}

      for (bouncer b : bouncers) {
        if (b.visible(p1.pos, width)) { 
          b.render(g, width, height);
        }
      }

      for (int i = 0;  i < players.size(); i++) {
        player x = players.get(i);
        if (x.loaded) {
          neo.Vec2 dim = x.dimensions();
          g.drawImage(x.img, (int) x.pos.x, (int) x.pos.y,
              (int) (x.pos.x+dim.x), (int) (x.pos.y+dim.y),
              (int) (x.source_dim.x), (int) (x.source_dim.y), 
              (int) (x.source_dim.x+dim.x), (int) (x.source_dim.y+dim.y), null);
          for (weapon wp : x.inventory) {
            wp.render(g, x.pos, nn.new Vec2(width, height));
            wp.hit(players, i);
          }
        } else {
          System.out.println("Unable to open sprite, not loaded");
        }
      }
		}
	};

	public class InputKey implements KeyListener {
		public void keyPressed(KeyEvent e) {
			/* left , up, right, down 
			 	 [ 37, 38, 39, 40 ] */
      char key = KeyEvent.getKeyText(e.getKeyCode()).charAt(0);
      for (player p : players) {
        if (p.movement.containsKey(key)) {
          int index = p.movement.get(key);
          if (index >= 0 && index <= 3) {
            p.directions[index] = true;
          } else {
            p.action(index);
          }
        }
      }
		}

		public void keyReleased(KeyEvent e) {
      char key = KeyEvent.getKeyText(e.getKeyCode()).charAt(0);
      for (player p : players) {
        if (p.movement.containsKey(key)) {
          int index = p.movement.get(key);
          if (index >= 0 && index <= 3) {
            p.directions[index] = false;
          } 
        }
      }
		}
		public void keyTyped(KeyEvent e) {}
	};
};

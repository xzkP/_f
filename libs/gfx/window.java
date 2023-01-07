package libs.gfx;

import libs.math.*;
import libs.functionality.*;
import java.io.*;
import libs.gfx.sprite;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class window {
  // all tick rates are based on 60...
	private final int 
    FRAME_PERIOD = 120, BASE_HEIGHT = 500, JUMP_HEIGHT = 100, BULLET_TICKS = 300, SHOOT_TICK = 20, JUMP_TICKS=10;
  private long last=0;
  private double fps, TICK_SCALE = FRAME_PERIOD/60.0, GRAVITY=4.0/TICK_SCALE;
  neo nn = new neo();
  neo.Vec2 end;
	JFrame frame;
	Panel p;
	InputKey keys;
	int width, height;
	ArrayList<sprite> sprites = new ArrayList<sprite>();
	ArrayList<platform> platforms = new ArrayList<platform>();
  ArrayList<mob> mobs = new ArrayList<mob>();
	player main;
	public window(String w_title, int w, int h) {
		main = new player("sprites/animated.bmp", w/2, h-BASE_HEIGHT, 4, 4, this.nn);
    main.equipped = new weapon("fists", 30, this.nn);
    main.shot_tick = SHOOT_TICK*FRAME_PERIOD;
    main.inventory.add(main.equipped);
		platform ground = new platform(0, h-BASE_HEIGHT+(int) main.dimensions().x-1, w, h, "grey", this.nn);
		ground.infinite = true;
		ground.collide = false;
		platforms.add(ground);

    this.level("./levels/1.txt");

		sprites.add(main);
		frame = new JFrame(w_title);
		p = new Panel();
		width = w;
		height = h;
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(p);
		keys = new InputKey();
		p.addKeyListener(keys);
		frame.setVisible(true);
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
        this.end = this.nn.new Vec2(px, py);
      } else { // ?
        int max_x = 0;
        for (platform p : platforms) {
          max_x = Math.max((int) p.pos.x, max_x);
        }
        this.end = this.nn.new Vec2(max_x, 1e3);
      }
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
        platforms.add(p);
      }
    }
  }



  double eval(String expr) {
    System.out.println(expr);
    return 1.0;
  }

	public void adjust() {
		/* relative_pos: relative position on screen
			 mainpos: distance from starting point (0, h/2) */
    for (Iterator<platform> p=platforms.iterator(); p.hasNext();) {
      platform pp = p.next();
      if (pp.health <= 0) {
        try { 
          p.remove();
        } catch (Exception e) {}
      }
    }
		main.relative_pos.x = this.nn.mod((int) main.relative_pos.x, width);
		main.relative_pos.y = this.nn.mod((int) main.relative_pos.y, width);
	}

	public void exec() {
    int delay = 35, jf = 0;
		while (true) {
			frame.repaint();
			main.move(platforms, end, TICK_SCALE);
      main.shot_tick++;
      main.jump_tick++;
      main.jumps[0]= (main.jump_tick >= delay*TICK_SCALE);
      if (main.jumps[1]) {
        main.pos = main.pos.add(this.nn.new Vec2(0, -JUMP_HEIGHT/JUMP_TICKS));
        main.relative_pos = main.relative_pos.add(this.nn.new Vec2(0, -JUMP_HEIGHT/JUMP_TICKS));
        jf++;
        if (jf >= JUMP_TICKS) { jf = 0; main.jumps[1] = false; };
      }
			if (!main.on_surface(platforms, main.pos) && !main.jumps[1]) {
				main.pos.y += GRAVITY;
				main.relative_pos.y += GRAVITY;
			}

      for (weapon w : main.inventory) {
        w.update((int) (BULLET_TICKS*TICK_SCALE));
        w.collide(platforms);
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

      text fps_display = new text("", 15, 30, "0xFFFFFF"), pos_display = new text("", 15, 60, "0xFFFFFF");
      pos_display.update_msg(String.format("(%.2f, %.2f)", main.pos.x, main.pos.y));
      fps_display.update_msg(String.format("FPS: %f", ((double) 1e9)/(System.nanoTime()-last)));
      last = System.nanoTime();
      pos_display.renderText(g);
      fps_display.renderText(g);

			// check if platform is in viewing distance + render
			for (platform p : platforms) {
				p.pos.x += (p.pos.x%50==0?1:0);
				if ((p.infinite || p.visible(main.pos, width)) && p.health > 0) {
					g.setColor(p.pc);
					g.fillRect(nn.mod((int) p.pos.x, width), nn.mod((int) p.pos.y, height), (int) p.dimensions.x, (int) p.dimensions.y);
				}
			}

			for (sprite x : sprites) {
				if (x.loaded) {
					g.drawImage(x.img, (int) x.relative_pos.x, (int) x.relative_pos.y, 
							(int) (x.relative_pos.x+x.dimensions().x), (int) (x.relative_pos.y+x.dimensions().y),
              (int) x.source_dim.x, (int) x.source_dim.y,
							(int) (x.source_dim.x+x.dimensions().x), (int) (x.source_dim.y+x.dimensions().y),
							null);

          if (x instanceof player) {
            player p = (player) x;
            for (weapon w : p.inventory) w.render(g, p.pos, nn.new Vec2(width, height));
          } else {
            x.equipped.render(g, x.pos, nn.new Vec2(width, height));
          }
				} else {
					System.out.println("Unable to open sprite");
				}
			}
		}
	};

	public class InputKey implements KeyListener {

		public void keyPressed(KeyEvent e) {
			/* left , up, right, down 
			 	 [ 37, 38, 39, 40 ] */
      char key = KeyEvent.getKeyText(e.getKeyCode()).charAt(0);
      int k_t = e.getKeyCode();

      if (k_t == 32 && main.shot_tick >= SHOOT_TICK*TICK_SCALE) {
        main.shot_tick = 0;
        main.shoot();
      }
			if (k_t >= 37 && k_t <= 40) {
				main.directions[k_t-37] = true;
			}
		}

		public void keyReleased(KeyEvent e) {
			int k_t = e.getKeyCode();
			if (k_t >= 37 && k_t <= 40) {
				main.directions[k_t-37] = false;
			}
		}
		public void keyTyped(KeyEvent e) {}
	};
};

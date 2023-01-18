package libs.gfx;

import libs.math.*;
import libs.functionality.*;
import libs.characters.*;
import javax.script.*;
import java.io.*;
import libs.gfx.sprite;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.imageio.ImageIO;

public class window {
	// constants
	private final int FRAMES = 120, BULLET_SPAN = 300, SHOOT_DELAY = 20, DEATH_RANGE = 600;
	private double
		FPS, FRAME_SCALING = FRAMES/60.0, GRAVITY = 0.15/FRAME_SCALING, JUMPING_GRAVITY = 0.7/FRAME_SCALING, JUMP_VELOCITY = 15.0, MELEE_RANGE = 5;
  neo nn = new neo();
  JFrame frame;
  Panel p;
  InputKey keys;
  int width, height;
	sprite ogre_headshot, elf_headshot;
  player p1, p2;
  platform ground;
  ArrayList<player> players = new ArrayList<player>();
	ArrayList<sprite> headshots = new ArrayList<sprite>();
  ArrayList<platform> platforms = new ArrayList<platform>();
  ArrayList<bouncer> bouncers = new ArrayList<bouncer>();
  ArrayList<text> textQueue = new ArrayList<text>();
  int hex_bg = Integer.valueOf("000000", 16);
  Color bg;
  public window(String w_title, int w, int h) {
    bg = new Color(hex_bg>>16&0xFF, hex_bg>>8&0xFF, hex_bg&0xFF);

    this.width = w;
    this.height = h;

		headshots.add(new sprite("./sprites/elf_headshot.bmp", 16, this.height-150, 1, 1, this.nn));
		headshots.add(new sprite("./sprites/ogre_headshot.bmp", this.width-96, this.height-150, 1, 1, this.nn));


    p1 = new elf("./sprites/spritesheet_f.bmp", w/2, 0, 4, 4, this.nn);
    // bottom left
    p1.criticalTextInit(100, this.height-100);
    p1.shootable = (int) (SHOOT_DELAY*FRAME_SCALING);
    p1.movement = new HashMap<Character, Integer>() {{
      put('←', 0);
      put('↑', 1);
      put('→', 2);
      put('↓', 3);
      put('N', 4);
      put('M', 5);
    }};

    p2 = new ogre("./sprites/spritesheet_ogre.bmp", w/2, 0, 4, 4, this.nn);
    p2.shootable = (int) (SHOOT_DELAY*FRAME_SCALING);
    // bottom right
    p2.criticalTextInit(this.width-100-3*48, this.height-100);
    p2.movement = new HashMap<Character, Integer>() {{
      put('A', 0);
      put('W', 1);
      put('D', 2);
      put('S', 3);
      put('T', 4);
      put('Y', 5);
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
      }
    });

    this.level("./levels/1.txt");

    this.ground = new platform(100, 800, width-200, 100, "grey", this.nn);
    this.ground.permeable = false;
    this.platforms.add(ground);
    this.bouncers.add(new bouncer(ground, 64, 28, this.nn));

		sound test = new sound("./sounds/clashing.wav");
  }

	// read in platforms from file --> fn is filename
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
      parsePlatform(metadata.get("platforms"));
    } catch (Exception e) {
      System.out.println(e);
      System.out.println(String.format("Can't read file: %s", fn));
    }
  }

  void pushHM(ArrayList<HashMap<String, String>> A, HashMap<String, String> B) {
    HashMap<String, String> tmp = new HashMap<String, String>(B);
    A.add(tmp);
  }

  void parsePlatform(ArrayList<String> data) {
    HashMap<String, String> tmp = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> split = new ArrayList<HashMap<String, String>>();
    for (String dat : data) {
      String key = dat.substring(0, dat.indexOf(":")).strip(), value = dat.substring(dat.indexOf(":")+1).strip();
      if (key.equals("position") && tmp.size() != 0) {
        pushHM(split, tmp);
        tmp.clear();
      }
      tmp.put(key, value);
    }
    pushHM(split, tmp);

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
        // assuming that platform is rectangular (TODO: add other shapes)
        platform p = new platform((int) (p_v.x), (int) (p_v.y), (int) (d_v.x), (int) (d_v.y), this.nn);
        p.assignColor(Integer.parseInt(hex, 16));
        if (info.containsKey("health")) {
          p.setHealth(Double.parseDouble(info.get("health")));
        }
        this.platforms.add(p);
      }
    }
  }

  public void updateObjects() {
    for (Iterator<platform> p=platforms.iterator(); p.hasNext();) {
      platform pp = p.next();
      if (pp.health <= 0) {
        try {
          p.remove();
        } catch (Exception e) {
          System.out.println(e);
        }
      }
    }
  }

  public void exec() {
    int delay = 35;
    while (true) {
      frame.repaint();
      frame.getContentPane().setBackground(Color.YELLOW);
      for (player player : players) {
        if (player.permeate && !player.collide(player.pt, player.pos)) {
          player.permeate = false;
        }
        player.bounce(bouncers);
        player.jump_tick++;
        player.ddt++;
        player.jumps[0] = (player.jump_tick >= delay*FRAME_SCALING);


        if (player.dash[0]) {
          player.vel.x = (player.dash[1]?Math.max(0, player.vel.x-0.25):Math.min(0, player.vel.x+0.25));
          if (Double.compare(player.vel.x, 0.25) <= 0 && Double.compare(player.vel.x, -0.25) >= 0) {
            player.vel.x = 0;
          }
        }

        boolean surface = player.onSurface(platforms, player.pos);
        if (!surface) {
          if (player.jumps[1] && !player.jumps[0]) {
            player.vel.y = (-player.JUMP_VELOCITY+(player.jump_tick)*JUMPING_GRAVITY);
          } else {
            player.vel.y += GRAVITY*2;
            player.vel.y = Math.min(100, player.vel.y);
          }
        } else {
          if (!player.permeate) {
            player.jumps = new boolean[]{player.jumps[0], false, true, false };
            player.ddt = 0;
            player.vel = nn.new Vec2(0, 0);
            player.dash[0] = false;
          }
        }

        player.move(platforms, FRAME_SCALING);
        player.modPos(player.vel);

        for (weapon w : player.attacks) {
          w.updateBullets((int) (BULLET_SPAN*FRAME_SCALING));
          w.collide(platforms);
        }

        neo.Vec2 pos = player.position();

        if (pos.x < -DEATH_RANGE || pos.x > this.width+DEATH_RANGE ||
            pos.y < -DEATH_RANGE || pos.y > this.height+DEATH_RANGE) {
          player.respawn(this.width/2, 0);
        }
        player.shootable++;
      }
      this.updateObjects();
      try { Thread.sleep(1000/FRAMES); } catch (Exception e) { System.out.println(e); };
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
      p.setBackground(bg);

      text FPS = new text("", 15, 30, "0xFFFFFF");
      FPS.updateMsg(String.format("FPS: %.2f", ((double)1e9/(System.nanoTime()-last))));
      FPS.renderText(g);
      last = System.nanoTime();

      // check if platform is in viewing distance + render
      for (platform p : platforms) {
        p.pos.x += (p.pos.x%50==0?1:0);
        if (p.health > 0 || p.infinite) {
          p.render(g);
        }
      }

      for (bouncer b : bouncers) {
        b.render(g);
      }

			for (sprite headshot : headshots) {
				headshot.render(g);
			}

      /* boolean Graphics.drawImage(Image img,
       int dstx1, int dsty1, int dstx2, int dsty2,
       int srcx1, int srcy1, int srcx2, int srcy2,
       ImageObserver observer); */
      for (int i = 0;  i < players.size(); i++) {
        player x = players.get(i);
        x.criticalText.renderText(g);
        if (x.loaded) {
					x.render(g);
          for (weapon wp : x.attacks) {
            wp.render(g, x.pos, nn.new Vec2(width, height));
            wp.hit(players, i, textQueue);
          }
        } else {
          System.out.println("Unable to open sprite, not loaded");
        }
      }
      for (Iterator<text> i=textQueue.iterator(); i.hasNext();) {
        text t = i.next();
        t.ticks++;
        if (t.hasLimit && t.ticks >= t.limit) {
          try {
            i.remove();
          } catch (Exception e) {
            System.out.println(e);
          }
        } else {
          t.renderText(g);
        }
      }
    }
  };

  public class InputKey implements KeyListener {
    public void keyPressed(KeyEvent e) {
      /* left , up, right, down
         [ 37, 38, 39, 40 ] */
      char key = KeyEvent.getKeyText(e.getKeyCode()).charAt(0);
			for (int i = 0; i < players.size(); i++) {
				player p = players.get(i);
        if (p.movement.containsKey(key)) {
          int index = p.movement.get(key);
          if (index >= 0 && index <= 3) {
            p.directions[index] = true;
          } else {
            switch (index) {
              case (4):
                p.melee(players.get(1-i), MELEE_RANGE, textQueue);
                break;
              case (5):
                if (p.shootable >= SHOOT_DELAY*FRAME_SCALING) {
                  p.attacks.get(0).shoot(p.position(), p.forward);
                  p.shootable = 0;
                }
                break;
              default:
                break;
            }
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

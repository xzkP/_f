package libs.gfx;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.Image.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.imageio.ImageIO;

public class menu extends JPanel{
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int h = (int)screenSize.getHeight();
    int w = (int)screenSize.getWidth();
    public Rectangle playButton = new Rectangle(w/2 - 50, 350, 100, 50);
    public Rectangle helpButton = new Rectangle(w/2 - 50, 425, 100, 50);
    public Rectangle exitButton = new Rectangle(w/2 - 50, 500, 100, 50);


    public void render(Graphics g){
        Graphics2D g2d = (Graphics2D) g;        
        BufferedImage bground = null;
        BufferedImage resized = null;

        try{
            bground = ImageIO.read(new File("./src/sprites/backgroundfinal.png"));
            resized = new BufferedImage(w, h, bground.getType());
            Graphics2D g2 = resized.createGraphics();
            g2.drawImage(bground, 0, 0,w, h, null);
            g2.dispose();
        }catch (IOException ex) {
            ex.printStackTrace();
        }
        Color clr = null;
        g2d.drawImage(resized, 0, 0, null);
        Font font = new Font("04b03", Font.BOLD, 100);
        g.setFont(font);
        clr = new Color(111, 196, 110);
        g.setColor(Color.white);
        g.drawString("balls", w/2 - 120, 170);
        clr = new Color(2, 112, 0);
        g.setColor(Color.gray);
        g.drawString("balls", w/2 - 118, 167);


        Font fnt1 = new Font("04b03", Font.BOLD, 50);
        clr = new Color(0, 79, 207);
        g.setFont(fnt1);
        g.setColor(Color.black);
        g.drawString("play",playButton.x-2, playButton.y+35+2);
        g.drawString("help",helpButton.x-2, helpButton.y+35+2);
        g.drawString("exit",exitButton.x + 4 -2 , exitButton.y+35+2);
        g.setColor(clr);
        g.drawString("play",playButton.x, playButton.y+35);
        g.drawString("help",helpButton.x, helpButton.y+35);
        g.drawString("exit",exitButton.x + 4, exitButton.y+35);
        // g2d.draw(playButton);
        // g2d.draw(helpButton);
        // g2d.draw(exitButton);

    }
}

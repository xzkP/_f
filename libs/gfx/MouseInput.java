package libs.gfx;
import libs.gfx.window;
import java.awt.*;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

public class MouseInput implements MouseInputListener {

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int h = (int)screenSize.getHeight();
    int w = (int)screenSize.getWidth();


    @Override
    public void mouseClicked(MouseEvent e) {

        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
        int mx = e.getX();
        int my = e.getY();
        /**
         * public Rectangle playButton = new Rectangle(w/2 - 50, 350, 100, 50);
    public Rectangle helpButton = new Rectangle(w/2 - 50, 425, 100, 50);
    public Rectangle exitButton = new Rectangle(w/2 - 50, 500, 100, 50);
         */
        if(mx >= w/2 - 50 && mx <= w/2 + 50){
            if(my >= 350 && my <= 400){
                window.state = window.STATE.Game;
            }
        }

        if(mx >= w/2- 50 && my <= w/2 + 50){
            if(my >= 500 && my <= 550){
                System.exit(0);
            }
        }
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        
    }

    @Override
    public void mouseEntered(MouseEvent e) {

        
    }

    @Override
    public void mouseExited(MouseEvent e) {

        
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

        
    }

}

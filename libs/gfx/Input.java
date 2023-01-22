package libs.gfx;

import libs.gfx.Button;
import libs.gfx.window;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.MouseInputListener;
import java.util.ArrayList;

public class Input implements MouseInputListener {
	ArrayList<Button> buttons;
	public Input(ArrayList<Button> b) {
		this.buttons = b;
	}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {
		int pos_x = e.getX(), pos_y = e.getY();
		for (int i = 0; i < buttons.size(); i++) {
			Button button = buttons.get(i);
			boolean clicked = (pos_x >= button.getX() && pos_x <= (button.getX() + button.getWidth()) && pos_y >= button.getY() && pos_y <= (button.getY() + button.getHeight()));
			if (clicked) {
				switch (i) {
					case (0): window.state = window.STATE.Game; break;
					case (1): window.state = window.STATE.Help; break;
					case (2): System.exit(0); break;
					default: break;
				}
				break;
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseDragged(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}
}

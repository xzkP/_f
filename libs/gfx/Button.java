package libs.gfx;

import java.awt.*;

public class Button extends Rectangle {
	String title;
	boolean highlighted = false;
	public Button(int x, int y, int width, int height, String t) {
		super(x, y, width, height);
		this.title = t;
	}
}

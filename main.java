import libs.math.*;
import libs.gfx.*;
import libs.functionality.*;
import java.awt.Toolkit;
import java.awt.Dimension;

public class main {
    public static void main(String[] args) {
      Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
      double width = size.getWidth(), height = size.getHeight();
			window w = new window("balls", (int) Math.round(width), (int) Math.round(height));
			w.exec();
    }
};

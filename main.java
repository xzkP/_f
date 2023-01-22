import libs.math.*;
import libs.gfx.*;
import libs.functionality.*;
import java.awt.Toolkit;
import java.awt.Dimension;

public class main {
    public static void main(String[] args) {
      Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
      double width = size.getWidth(), height = size.getHeight();
			window w = new window("balls", (int) width, (int) height);
			w.exec();
    }
};

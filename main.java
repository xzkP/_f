import libs.math.*;
import libs.gfx.*;
import libs.functionality.*;
import java.awt.Toolkit;
import java.awt.Dimension;

/* Define good constants so that the math yields no gaps:
 - movement scalar
 - position of obstacles
*/

public class main {
    public static void main(String[] args) {
      Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
      double width = size.getWidth(), height = size.getHeight();
			window w = new window("balls", (int) width, (int) height);
			w.exec();
    }
};

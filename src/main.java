import javax.swing.*;
import java.awt.*;

/**
 * Created by mghan on 2017-03-31.
 */
public class main {
    public static void main(String[] args){
        Map testMap = new Map();
        JFrame frame = new JFrame();
        JLabel label = new JLabel();

        testMap.CheckForObstacle();
        label.setIcon(new ImageIcon(testMap.getImage()));
        frame.add(label);
        frame.setVisible(true);
    }
}

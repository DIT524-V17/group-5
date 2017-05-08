import javax.swing.*;

/**
 * Created by mghan on 2017-03-31.
 */
public class main {
    public static void main(String[] args){
        Map testMap = new Map();
        testMap.mapParser.parse(120 , 120, 30);
        Map testMap2 = new Map();
        testMap2.updateCarPosition(Constants.SENSOR_MAX_DISTANCE, 23);
        testMap2.mapParser.parse(3, 21, 90);
        testMap.Overlay(testMap2);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JLabel label = new JLabel();

        label.setIcon(new ImageIcon(testMap.getImage()));
        frame.add(label);
        frame.setVisible(true);

    }
}

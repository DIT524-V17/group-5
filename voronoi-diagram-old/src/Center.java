import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Vin on 03/04/2017.
 */
public class Center {
    JLabel icon;
    int x;
    int y;

    public Center(int x, int y, Plane p){
        this.x = x;
        this.y = y;
        this.icon = new JLabel(png("star"));
        p.centers.add(this);
    }


    public Center belongsTo(Plane p){
        double minDistance = Double.MAX_VALUE;
        double tempDistance;
        Center tempCenter = null;

        for(Center c : p.centers){
            tempDistance = Math.sqrt(Math.pow((c.x-this.x),2)+Math.pow((c.y-this.y),2));
            if(tempDistance<minDistance && c != this){
                minDistance = tempDistance;
                tempCenter = c;
            }
        }
        return tempCenter;
    }

    public static ImageIcon png(String name){
        ImageIcon icon = null;
        icon = new ImageIcon("res/" +name+".png");
        return icon;
    }
}

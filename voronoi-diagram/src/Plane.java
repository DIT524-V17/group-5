import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Vin on 03/04/2017.
 */
public class Plane {
    //TODO: add centers and edges arraylists
    ArrayList<Center> centers;
    ArrayList<Edge> edges;
    JFrame owner;
    public Plane(JFrame owner){
        //TODO:centers and edges
        this.owner = owner;

        centers = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void add(int x, int y){
        //TODO:add center initialization here
    }

}

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

/**
 * Created by Vin on 03/04/2017.
 */
public class MainClass {
    public static void main(String[] args){

        JFrame frame = new JFrame();
        frame.setSize(800,700);
        frame.setBackground(Color.WHITE);

        JPanel contentPane = new JPanel();
        frame.setContentPane(contentPane);
        contentPane.setLayout(null);
        contentPane.setBackground(Color.WHITE);

        Plane p = new Plane(frame);

        frame.setVisible(true);
        Graphics2D graphics2D = (Graphics2D) frame.getGraphics();

        contentPane.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                frame.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                //draw image
                int x = e.getX();
                int y = e.getY();
                Center c = new Center(x, y, p);
                JLabel temp = c.icon;
                contentPane.add(temp);
                temp.setBounds(x - 15, y - 15, 30, 30);
                temp.setVisible(true);

                //test edge between two centers - TEMP
                if(p.centers.size()==2){
                    Edge e1 = new Edge(p.centers.get(0), p.centers.get(1), p);
                    p.edges.add(e1);
                }
                else if(p.centers.size()>2){
                    Edge e1 = new Edge(c, c.belongsTo(p), p);
                    Edge middle = e1;
                    for (Edge edge: p.edges){
                        edge.c1.toString();
                        edge.c2.toString();
                        c.belongsTo(p).toString();
                        if (!e1.isParallel(edge) && (edge.sharesCenter(c,p))){
                            Point pointt = e1.findIntersection(edge);
                            e1.trim(pointt);
                            edge.trim(pointt);
                            middle = new Edge(e1,edge,p);
                            middle.trim(pointt);
                        }
                    }
                    p.edges.add(e1);
                    p.edges.add(middle);
                }


                for(Edge edge : p.edges){
                    if (edge.leftLine!=null)
                        graphics2D.draw(edge.leftLine);
                    if (edge.rightLine!=null)
                        graphics2D.draw(edge.rightLine);
                }



            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

    }


}

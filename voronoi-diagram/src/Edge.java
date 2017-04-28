import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Created by Vin on 03/04/2017.
 */
public class Edge {
    Line2D.Double leftLine;
    Line2D.Double rightLine;
    Center c1;
    Center c2;

    private double a;   //slope
    private double b;   //increment thing
    private Point2D middle;

    /*
        edges can be two-sided or one-sided kinds
        two sided ones don't touch the plane borders


        the function for carving out a pane is as follows:
        1. edge is drawn with the nearest center, cut at both intersections
        2. from both ends of this edge, a new one is drawn with the neighbouring center
        3. the "starting" intersection is trimmed
        4. 2-3 is repeated until the end of current edge 1 reaches the border or == curredge2

        the first one will call the edge drawing function in both directions (if edge is two-sided)
        @param: intersect - used as the "start"of the edge
                            i.e. the intersected edge it's on will be trimmed at the end of the function
        */
    public Edge(Center a, Center b, Plane p){
        this.c1 = a;
        this.c2 = b;
        //first: draw one long edge between two centers
        //1. find center of a-b
        double x = (a.x+b.x)/2 + 5;
        double y = (a.y+b.y)/2 + 30;
        this.middle = new Point2D.Double(x,y);
        System.out.println(x+"    "+y);

        //2. find slope (a) for a-b
        double t1=(b.y-a.y);
        double t2=(b.x-a.x);
        double slope = -t2/t1;
        this.a = slope;
        System.out.println("a - "+this.a);

        //4. find the increment thingy (b)
        //b = y - ax
        double increment = y - (slope * x);
        this.b = increment;
        System.out.println("b - "+increment);

        //5a. find start of edge x1y1 (x1=0 or y1=0)
        double y1=0;
        double x1 = (y1 - increment) / slope;
        System.out.println("x1= "+x1+" y1= "+y1);
        System.out.println("x= "+x+" y= "+y);

        //5b. find end of edge x2y2 (x2 = max or y2 = max)
        double y2 = p.owner.getHeight();
        double x2 = (y2 - increment) /slope;
        System.out.println("x2= "+x2+" y2= "+y2);

        //6. initialize both lines
        this.leftLine = new Line2D.Double(x1, y1, x, y);
        this.rightLine = new Line2D.Double(x, y,x2,y2);
        System.out.println();
        System.out.println();
    }

    public Edge(Edge a, Edge b, Plane p){
        Center center1 = a.c1!=b.c1 && a.c1!=b.c2 ? a.c1 : a.c2;
        Center center2 = b.c1!=center1 ? b.c1 : b.c2;
        Edge temp = new Edge (center1,center2,p);
        this.a = temp.a;
        this.b = temp.b;
        this.c1 = temp.c1;
        this.c2 = temp.c2;
        this.middle = temp.middle;
        this.leftLine = temp.leftLine;
        this.rightLine = temp.rightLine;
    }

    public Point findIntersection(Edge e){
        //TODO: find an intersection point between two edges
        //1. solve ax + b = cx + d
        //   x = (d - b)/(a - c)
        int x = (int) ((this.b - e.b) / (e.a - this.a));

        //2. find y by just substituting either linear equation's x
        int y = (int) ((this.a * x) + this.b);

        //3. return values
        if (this.rightLine.contains(new Point2D.Double(x,y)) || this.leftLine.contains(new Point2D.Double(x,y)))
            System.out.println("SUCCESSSSSSSSSSSSSSSSSSSSSSSSSSSS1");
        if (e.rightLine.contains(new Point2D.Double(x,y)) || e.leftLine.contains(new Point2D.Double(x,y)))
            System.out.println("SUCCESSSSSSSSSSSSSSSSSSSSSSSSSSSS2");
        System.out.println();
        System.out.println("INTERSECTION POINT:    X="+x+"  Y="+y);
        System.out.println();
        return new Point(x, y);
    }

    public void trim(Edge other){
        //- this edge is owned by center c1 and center c2     c1|c2
        //  it intersects with an edge, owned by c1 and c3    c1|c3
        //* detect center c3 (center != common center)
        //* detect which side of the intersection is towards c3
        //* trim this edge so the part between c3 and the intersection is GONE
        //- <-----c3----------x------------------->
        //-       c3          x------------------->
        //* call the same actions on A|C
        //- check which side of the intersection is towards B
        //- trim accordingly
        //- <-----c2----------x------------------->
        //-       c2          x------------------->
        Center c1 = (other.c1==this.c1 || other.c2==this.c1) ? this.c1 : this.c2;
        Center c2 = (c1==this.c1) ? this.c2 : this.c1;
        Center c3 = (c1==other.c1) ? other.c2 : other.c1;

        //detect intersection
        Point2D intersection = this.findIntersection(other);



    }

    public Edge trim(Point point){
        //TODO: trim an edge at an intersection point
        System.out.println("LALALALA");

        if ((point.getX() > this.leftLine.getX1() &&
                point.getX() < this.leftLine.getX2()) ||
                ( point.getX() < this.leftLine.getX1() &&
                        point.getX() > this.leftLine.getX2())) {

            this.leftLine = new Line2D.Double(point, this.leftLine.getP2());
        }
        else if((point.getX() > this.rightLine.getX1() &&
                point.getX() < this.rightLine.getX2()) ||
                ( point.getX() < this.rightLine.getX1() &&
                point.getX() > this.rightLine.getX2())) {
            this.rightLine = new Line2D.Double(this.rightLine.getP1(), point);
        }
        else if((point.getY() > this.rightLine.getY1() &&
                point.getY() < this.rightLine.getY2()) ||
                ( point.getY() < this.rightLine.getY1() &&
                        point.getY() > this.rightLine.getY2())) {
            this.rightLine = new Line2D.Double(this.rightLine.getP1(), point);
        }
        else {
            this.leftLine = new Line2D.Double(point, this.leftLine.getP2());
        }
        return this;
    }
    public void trimTwo(Point2D p, Center c){

        double y1 = (p.getX()+1)*this.a + this.b;
        double y2 = (p.getX()-1)*this.a + this.b;

        Point2D left = new Point2D.Double(p.getX()+1, y1);
        Point2D right = new Point2D.Double(p.getX()-1, y2);

        //check which one is the closest to center
        double leftDist = Math.sqrt(Math.pow(c.x-left.getX(),2)+Math.pow(c.y-left.getY(),2));
        double rightDist = Math.sqrt(Math.pow(c.x-right.getX(),2)+Math.pow(c.y-right.getY(),2));

        if (leftDist<rightDist){
            if (left.getX()>this.leftLine.getX2()){
                this.rightLine = null;
                this.leftLine = new Line2D.Double(this.leftLine.getP1(), left);
            }
            else if (left.getX() == this.leftLine.getX2()){
                this.rightLine = null;
            }
            else {
                this.rightLine = new Line2D.Double(this.rightLine.getP1(),left);
            }
        }
        else {
            if (right.getX()<this.rightLine.getX1()){
                this.leftLine = null;
                this.rightLine = new Line2D.Double(right, this.rightLine.getP2());
            }
            else if (right.getX() == this.rightLine.getX1()){
                this.leftLine = null;
            }
            else {
                this.leftLine = new Line2D.Double(right,this.leftLine.getP2());
            }
        }



    }

    boolean isParallel(Edge e){
        return this.a==e.a;
    }

    boolean sharesCenter(Center c,  Plane p){
        return (this.c1==c.belongsTo(p)) || (this.c2==c.belongsTo(p));
    }

    public void rotate(Plane p, Point next, Point end){
        //TODO: trim edge at next intersection
        //stop if next == end
        //OR stop if x or y are out of or at the bounds
        //  ⤷ in that case, call rotate on end

        //before every recursive call, trim the intersected edge at the old next
    }

}

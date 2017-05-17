/**
 * Created by mghan on 2017-05-17.
 */
public class LinearFunction {
    // General formula y = mx + b

    public LinearFunction(){

    }

    public String getLinearFunction(Vertex v1, Vertex v2){
            double m = getSlope(v1, v2);
            double b = getB(v1, m);
            if(b > 0) {
                return "y = " + m + "x + " + b;
            } else if(b < 0){
                return "y = " + m + "x - " + Math.abs(b);
            } else if(b == 0){
                return "y = " + m +"x";
            }else{
                return "Could not compute function";
            }

    }

    public double getSlope(Vertex v1, Vertex v2){
        //m = (y2 - y1)/(x2 - x1)
        double x1 = v1.x; double y1 = v1.y;
        double x2 = v2.x; double y2 = v2.y;

       return (y2 - y1)/(x2 - x1);

    }

    public Double getB(Vertex v, Double m){
        // b = y - mx

        return (v.y - ((m)*(v.x)));
    }
}

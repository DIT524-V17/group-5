package com.example.mghan.implementationvoronidijkstra;
import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;

public class LinearFunction {
    // General formula y = mx + b
    int MAPMAX = 200;
    int MAPMIN = 0;
    public LinearFunction(){

    }

    /**
     *
     * @param v1 first Node / node
     * @param v2 second Node / node
     * @return A string is returned containing the function of the line as a string
     */
    public String getLinearFunction(Node v1, Node v2){
        double m = getSlope(v1, v2);
        double b = getB(v1, m);

        return "y = " + m + "x " + b;


    }

    public String getLinearFunction(Coordinate c1, Coordinate c2){
        double m = getSlope(c1, c2);
        double b = getB(c1, m);

        return "y = " + m + "x " + b;


    }

    /**
     *
     * @param v1 first Node / node
     * @param v2 second Node / node
     * @return returns a Node array with the new nodes such that they start and end within the boundaries of the map
     */
    public Node[] getTrimmedCoordinates(Node v1, Node v2){
        Node[] mapCooridnates = new Node[2];
        double m = getSlope(v1, v2);
        double b = getB(v1, m);
        double v1x = v1.x; double v1y = v1.y;
        double v2x = v2.x; double v2y = v2.y;

        //Working with the first point
        //If the x coordinate is outside of the map from the left side find y value such that x = 0;
        // y = b
        if(v1x < MAPMIN){
            v1y = b;
            v1x = MAPMIN;
        } else{}

        //If the x coordinate is outside of the map from the right side find the y value such that x = 200;
        // y = 200m + b
        if(v1x > MAPMAX){
            v1x = MAPMAX;
            v1y = (MAPMAX * m) + b;
        } else{}
        //If the y coordinate is outside of the map from the top of the map find the x value such that y = 0;
        //x = -b/m
        if (v1y < MAPMIN){
            v1x = -b/m;
            v1y = MAPMIN;
        } else{}

        //If the y coordinate is outside of the map from the bottom of the map find the x value such that y = 200;
        //x = (200 - b) / m
        if(v1y > MAPMAX){
            v1x = (MAPMAX - b)/m;
            v1y = MAPMAX;

        } else{}

        //Working with the second point
        //If the x coordinate is outside of the map from the left side find y value such that x = 0;
        // y = b
        if(v2x < MAPMIN){
            v2y = b;
            v2x = MAPMIN;
        } else{}

        //If the x coordinate is outside of the map from the right side find the y value such that x = 200;
        // y = 200m + b
        if(v2x > MAPMAX){
            v2x = MAPMAX;
            v2y = (MAPMAX * m) + b;
        } else{}
        //If the y coordinate is outside of the map from the top of the map find the x value such that y = 0;
        //x = -b/m
        if (v2y < MAPMIN){
            v2x = -b/m;
            v2y = MAPMIN;
        } else{}

        //If the y coordinate is outside of the map from the bottom of the map find the x value such that y = 200;
        //x = (200 - b) / m
        if(v2y > MAPMAX){
            v2x = (MAPMAX - b)/m;
            v2y = MAPMAX;

        } else{}

        mapCooridnates[0] = new Node(v1x,v1y, v1.identifier);
        mapCooridnates[1] = new Node(v2x,v2y,v2.identifier);

        return mapCooridnates;
    }

    public Coordinate[] getTrimmedCoordinates(Coordinate c1, Coordinate c2){
        Coordinate[] mapCooridnates = new Coordinate[2];
        double m = getSlope(c1, c2);
        double b = getB(c1, m);
        double v1x = c1.x;
        double v1y = c1.y;
        double v2x = c2.x;
        double v2y = c2.y;


        if(m == 0){
            if(b > MAPMAX || b < MAPMIN){
                return null;
            }
            v1y = b;
            v2y = b;
            if(v1x < MAPMIN){
                v1x = MAPMIN;
            } else {}
            if(v1x > MAPMAX){
                v1x = MAPMAX;
            } else {}
            if(v2x < MAPMIN){
                v2x = MAPMIN;
            } else {}
            if(v2x > MAPMAX){
                v2x = MAPMAX;
            } else {}
        } else if(Double.isInfinite(m) || Double.isNaN(m)) {
            if(v1x > MAPMAX || v1x < MAPMIN){
                return null;
            } else{
                if(v1y > MAPMAX){
                    v1y = MAPMAX;
                } else{}

                if(v1y < MAPMIN){
                    v1y = MAPMIN;
                } else{}

                if(v2y > MAPMAX){
                    v2y = MAPMAX;
                } else{}

                if(v2y < MAPMIN){
                    v2y = MAPMIN;
                } else {}
            }

        } else {
            boolean bool = false;
            int boolCounter = 0;
            while(bool != true) {
                //Working with the first point
                //If the x coordinate is outside of the map from the left side find y value such that x = 0;
                // y = b
                if (v1x < MAPMIN) {
                    v1y = b;
                    v1x = MAPMIN;
                }

                //If the x coordinate is outside of the map from the right side find the y value such that x = 200;
                // y = 200m + b
                else if (v1x > MAPMAX) {
                    v1x = MAPMAX;
                    v1y = (MAPMAX * m) + b;
                }

                //If the y coordinate is outside of the map from the top of the map find the x value such that y = 0;
                //x = -b/m
                else if (v1y < MAPMIN) {
                    v1x = -b / m;
                    v1y = MAPMIN;
                }

                //If the y coordinate is outside of the map from the bottom of the map find the x value such that y = 200;
                //x = (200 - b) / m
                else if (v1y > MAPMAX) {
                    v1x = (MAPMAX - b) / m;
                    v1y = MAPMAX;

                }

                //Working with the second point
                //If the x coordinate is outside of the map from the left side find y value such that x = 0;
                // y = b
                if (v2x < MAPMIN) {
                    v2y = b;
                    v2x = MAPMIN;
                }

                //If the x coordinate is outside of the map from the right side find the y value such that x = 200;
                // y = 200m + b
                else if (v2x > MAPMAX) {
                    v2x = MAPMAX;
                    v2y = (MAPMAX * m) + b;
                }

                //If the y coordinate is outside of the map from the top of the map find the x value such that y = 0;
                //x = -b/m
                else if (v2y < MAPMIN) {
                    v2x = -b / m;
                    v2y = MAPMIN;
                }

                //If the y coordinate is outside of the map from the bottom of the map find the x value such that y = 200;
                //x = (200 - b) / m
                else if (v2y > MAPMAX) {
                    v2x = (MAPMAX - b) / m;
                    v2y = MAPMAX;

                }

                if(!(v1x > MAPMAX|| v1x < MAPMIN|| v2x > MAPMAX || v2x < MAPMIN || v1y > MAPMAX || v1y < MAPMIN || v2y > MAPMAX || v2y < MAPMIN)){
                    bool = true;
                } else if( boolCounter++ > 10){
                    return null;
                } else{}
            }
        }

        if (v1x == v2x && v1y == v2y) return null;
        else {
            mapCooridnates[0] = new Coordinate(v1x, v1y);
            mapCooridnates[1] = new Coordinate(v2x, v2y);
            return mapCooridnates;
        }
    }

    public double getSlope(Node v1, Node v2){
        //m = (y2 - y1)/(x2 - x1)
        double x1 = v1.x; double y1 = v1.y;
        double x2 = v2.x; double y2 = v2.y;

        return (y2 - y1)/(x2 - x1);

    }

    public double getSlope(Coordinate c1, Coordinate c2){
        //m = (y2 - y1)/(x2 - x1)
        double x1 = c1.x; double y1 = c1.y;
        double x2 = c2.x; double y2 = c2.y;
        return (y2 - y1)/(x2 - x1);

    }

    public double getB(Node v, Double m){
        // b = y - mx

        return (v.y - ((m)*(v.x)));
    }

    public double getB(Coordinate c, Double m){
        // b = y - mx

        return (c.y - ((m)*(c.x)));
    }

    /**
     * @param function
     * @return index 0 is slope index 1 of double array is the b value
     */
    public double[] parseFunction(String function){
        int index = 0;

        ArrayList<Character> charsM = new ArrayList();
        ArrayList<Character> charsB = new ArrayList();


        for(int i = 0; i < function.length(); i++){
            if(Character.isDigit(function.charAt(i)) || function.charAt(i) == '.' ){
                charsM.add(function.charAt(i));
                if(function.charAt(i + 1) == 'x'){
                    index = i + 1;
                    break;
                } else{

                }
            }
        }

        for(int i = index; i < function.length(); i++){
            if(Character.isDigit(function.charAt(i)) || function.charAt(i) == '.' ) {
                charsB.add(function.charAt(i));
            }
        }
        char[] charsMM = new char[charsM.size()];
        char[] charsBB = new char[charsB.size()];
        for(int i = 0; i < charsMM.length; i++){
            charsMM[i] = charsM.get(i);
        }
        for(int i = 0; i < charsBB.length; i++){
            charsBB[i] = charsB.get(i);
        }

        StringBuilder builder = new StringBuilder();
        double m = Double.parseDouble(new String(charsMM));
        double b = Double.parseDouble(new String(charsBB));
        double[] arr = new double[]{m,b};
        return arr;
    }
}


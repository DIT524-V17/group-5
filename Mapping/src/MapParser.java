import java.awt.image.BufferedImage;

/**
 * Created by mghan on 2017-03-31.
 */
public class MapParser {
    Coordinate coordinateFront, coordinateBack;
    int distanceFront, distanceBack;
    int angle, backAngle;
    Map map;

    public MapParser(Map map){
        this.angle = 0;
        this.backAngle = 0 + Constants.FRONT_SENSOR_TO_BACK_ANGLE;
        this.distanceFront = 0;
        this.distanceBack = 0;
        this.map = map;

    }

    public void parse(int distanceFront, int distanceBack, int angle){
        this.angle = angle;
        this.backAngle = angle + Constants.FRONT_SENSOR_TO_BACK_ANGLE;
        this.distanceFront = distanceFront;
        this.distanceBack = distanceBack;


        if(this.distanceFront < Constants.SENSOR_MAX_DISTANCE && this.distanceFront != 0){
           setAsObstacle(new Coordinate((int) (this.distanceFront * Math.cos(this.angle * (Math.PI / 180))) + this.map.carX,
                   (int) (this.distanceFront * Math.sin(this.angle * (Math.PI / 180)))+ this.map.carY));
        } else{
        }

        if(this. distanceBack < Constants.SENSOR_MAX_DISTANCE && this.distanceBack != 0) {
            setAsObstacle(new Coordinate((int) (this.distanceBack * Math.cos(this.backAngle * (Math.PI / 180))) + this.map.carX,
                    (int) (this.distanceBack * Math.sin(this.backAngle * (Math.PI / 180))) + this.map.carY));
        } else{
        }
    }
    public void setAsObstacle(Coordinate coord){
        if(coord != null) {
            this.map.getImage().setRGB((int) coord.getX() /*+ this.map.carX*/, (int) coord.getY() /*+ this.map.carY*/, 0);
        }
    }
}

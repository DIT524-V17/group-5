/**
 * Created by mghan on 2017-03-31.
 */
public class MapSensorParser {
    Coordinate coordinateFront, coordinateBack;
    int distanceFront, distanceBack;
    int angle, backAngle;

    public MapSensorParser(){
        this.angle = 0;
        this.backAngle = 0 + Constants.FRONT_SENSOR_TO_BACK_ANGLE;
        this.distanceFront = 0;
        this.distanceBack = 0;
        this.coordinateFront = null;
        this.coordinateBack = null;
    }

    public void parse(int distanceFront, int distanceBack, int angle){
        this.angle = angle;
        this.backAngle = angle + Constants.FRONT_SENSOR_TO_BACK_ANGLE;
        this.distanceFront = distanceFront;
        this.distanceBack = distanceBack;


        if(this.distanceFront < Constants.SENSOR_MAX_DISTANCE && this.distanceFront != 0){
            this.coordinateFront = new Coordinate((int) (this.distanceFront * Math.cos(this.angle * (Math.PI / 180))),(int) (this.distanceFront * Math.sin(this.angle * (Math.PI / 180))));
        } else{
        }

        if(this. distanceBack < Constants.SENSOR_MAX_DISTANCE && this.distanceBack != 0) {
            this.coordinateBack = new Coordinate((int) (this.distanceBack * Math.cos(this.backAngle * (Math.PI / 180))),(int) (this.distanceBack * Math.sin(this.backAngle * (Math.PI / 180))));
        } else{
        }
    }
    public Coordinate returnCoordinateFront(){
        return this.coordinateFront;
    }
    public Coordinate returnCoordinateBack(){
        return this.coordinateBack;
    }
    public void resetCoordinates(){
        this.coordinateFront = null;
        this.coordinateBack = null;
    }
}

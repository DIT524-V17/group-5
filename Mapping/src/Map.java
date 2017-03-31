import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

/**
 * Created by mghan on 2017-03-31.
 */
public class Map {
    int carX = Constants.SENSOR_MAX_DISTANCE;
    int carY = Constants.SENSOR_MAX_DISTANCE;
    byte[] byteArr = {(byte)0xff, (byte)0};
    ColorModel colorModel = new IndexColorModel(1,2,byteArr,byteArr,byteArr);
    BufferedImage image;
    MapSensorParser mapSensorParser;

    public Map() {

        this.image = new BufferedImage(2 * Constants.SENSOR_MAX_DISTANCE, 2 * Constants.SENSOR_MAX_DISTANCE, BufferedImage.TYPE_BYTE_BINARY, (IndexColorModel) colorModel);
        this.mapSensorParser = new MapSensorParser();
    }

    public Map(int width, int height){
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY ,(IndexColorModel) colorModel);
    }

    public BufferedImage getImage(){
        return this.image;
    }

    public void setAsObstacle(Coordinate coord){
       if(coord != null) {
           this.image.setRGB((int) coord.getX() + carX, (int) coord.getY() + carY, 0);
       }
    }

    public void CheckForObstacle(){
        this.mapSensorParser.parse(0, 100, 0);
        setAsObstacle(mapSensorParser.returnCoordinateFront());
        setAsObstacle(mapSensorParser.returnCoordinateBack());
        this.mapSensorParser.resetCoordinates();

    }
}
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
    MapParser mapParser = new MapParser(this);

    public Map() {

        this.image = new BufferedImage(2 * Constants.SENSOR_MAX_DISTANCE, 2 * Constants.SENSOR_MAX_DISTANCE, BufferedImage.TYPE_BYTE_BINARY, (IndexColorModel) colorModel);
    }

    public Map(int width, int height){
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY ,(IndexColorModel) colorModel);
    }

    public BufferedImage getImage(){
        return this.image;
    }

    public void Overlay(Map map){
        BufferedImage combined = new BufferedImage(this.getImage().getWidth() + map.getImage().getWidth(),
                 this.getImage().getHeight() + map.getImage().getHeight(),BufferedImage.TYPE_BYTE_BINARY,
                (IndexColorModel) colorModel);
        Graphics g = combined.getGraphics();

        g.drawImage(this.getImage(), this.carX, this.carY, null);
        g.drawImage(map.getImage(), map.carX, map.carY, null);
        this.image = combined;
    }

    public void updateCarPosition(int x, int y){
        this.carX = x;
        this.carY = y;
    }
}
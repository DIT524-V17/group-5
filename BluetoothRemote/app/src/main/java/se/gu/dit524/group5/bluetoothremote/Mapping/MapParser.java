package se.gu.dit524.group5.bluetoothremote.Mapping;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import static se.gu.dit524.group5.bluetoothremote.Mapping.Constants.*;

/**
 * Created by mghan on 2017-03-31.
 * Modified by julian.bock on 2017-04-03, 2017-05-12 (and following).
 */
public class MapParser {
    private Map map;

    MapParser(Map map){
        this.map = map;
    }

    void parse(int distanceFront, int distanceBack, int angle) {
        this.parse(distanceFront, distanceBack, angle, false);
    }

    void clean(int distanceFront, int distanceBack, int angle) {
        this.parse(distanceFront, distanceBack, angle, true);
    }

    private void parse(int distanceFront, int distanceBack, int angle, boolean cleanup) {
        int frontAngle = (int) -this.map.getLastCar().front() -angle;
        int backAngle  = frontAngle -180;

        double cos = Math.cos(Math.toRadians(frontAngle));
        double sin = Math.sin(Math.toRadians(frontAngle));

        if (distanceFront <= SENSOR_MAX_DISTANCE && distanceFront > 0) {
           setAsObstacle(new Point((int) (distanceFront *cos +this.map.getCar().servo().x),
                                   (int) (distanceFront *sin +this.map.getCar().servo().y)), frontAngle, cleanup);
        }
        else {
            setAsObstacle(new Point((int) (SENSOR_MAX_DISTANCE *cos +this.map.getCar().servo().x),
                                    (int) (SENSOR_MAX_DISTANCE *sin +this.map.getCar().servo().y)), frontAngle, true);
        }

        cos = Math.cos(Math.toRadians(backAngle));
        sin = Math.sin(Math.toRadians(backAngle));

        if (distanceBack <= SENSOR_MAX_DISTANCE && distanceBack > 0) {
            setAsObstacle(new Point((int) (distanceBack *cos +this.map.getCar().servo().x),
                                    (int) (distanceBack *sin +this.map.getCar().servo().y)), backAngle, cleanup);
        }
        else {
            setAsObstacle(new Point((int) (SENSOR_MAX_DISTANCE *cos +this.map.getCar().servo().x),
                                    (int) (SENSOR_MAX_DISTANCE *sin +this.map.getCar().servo().y)), backAngle, true);
        }
    }

    private void setAsObstacle(Point coord, int angle, boolean cleanUp){
        if (coord != null) {
            Canvas c = new Canvas(map.getMap());
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setStyle(Paint.Style.FILL_AND_STROKE);

            double AN = Math.sqrt(Math.pow(map.getCar().servo().x -coord.x, 2) +Math.pow(map.getCar().servo().y -coord.y, 2));
            double GK = Math.tan(Math.toRadians(-7.5)) *AN;
            double HY = Math.sqrt(Math.pow(AN, 2) +Math.pow(GK, 2));

            if (cleanUp) HY -= 1;
            Point A = new Point((int)map.getCar().servo().x, (int)map.getCar().servo().y),
                  D = new Point(), E = new Point();

            Point B = new Point((int)(map.getCar().servo().x +HY *Math.sin(Math.toRadians(angle -7.5))),
                                (int)(map.getCar().servo().y -HY *Math.cos(Math.toRadians(angle -7.5))));
            Point C = new Point((int)(map.getCar().servo().x +HY *Math.sin(Math.toRadians(angle +7.5))),
                                (int)(map.getCar().servo().y -HY *Math.cos(Math.toRadians(angle +7.5))));

            if (!cleanUp) {
                  D = new Point((int)(map.getCar().servo().x +(HY +1) *Math.sin(Math.toRadians(angle -7.5))),
                                (int)(map.getCar().servo().y -(HY +1) *Math.cos(Math.toRadians(angle -7.5))));
                  E = new Point((int)(map.getCar().servo().x +(HY +1) *Math.sin(Math.toRadians(angle +7.5))),
                                (int)(map.getCar().servo().y -(HY +1) *Math.cos(Math.toRadians(angle +7.5))));
            }

            if (cleanUp) {
                /*
                p.setColor(Color.WHITE);
                Path path = new Path();
                path.moveTo(A.x, A.y);
                path.lineTo(B.x, B.y);
                path.lineTo(C.x, C.y);
                path.close();
                c.drawPath(path, p); */
            }
            else {
                p.setColor(Color.argb(255, 0x33, 0x33, 0x33));
                p.setStrokeWidth(1);
                c.drawLine(B.x, B.y, C.x, C.y, p);
            }
        }
    }
}

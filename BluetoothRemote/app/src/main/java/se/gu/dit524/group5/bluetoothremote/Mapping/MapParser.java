package se.gu.dit524.group5.bluetoothremote.Mapping;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import static se.gu.dit524.group5.bluetoothremote.Mapping.Constants.*;

/**
 * Created by mghan on 2017-03-31.
 * Modified by julian.bock on 2017-04-03.
 */
public class MapParser {
    private int distanceFront, distanceBack;
    private int angle, backAngle;
    private Map map;

    public MapParser(Map map){
        this.angle = 0;
        this.backAngle = FRONT_SENSOR_TO_BACK_ANGLE;
        this.distanceFront = 0;
        this.distanceBack = 0;
        this.map = map;
    }

    public void parse(int distanceFront, int distanceBack, int angle) {
        this.parse(distanceFront, distanceBack, angle, false);
    }

    public void clean(int distanceFront, int distanceBack, int angle) {
        this.parse(distanceFront, distanceBack, angle, true);
    }

    public void parse(int distanceFront, int distanceBack, int angle, boolean cleanup) {
        this.angle = angle;
        this.backAngle = angle - FRONT_SENSOR_TO_BACK_ANGLE;
        this.distanceFront = distanceFront;
        this.distanceBack = distanceBack;

        if (this.distanceFront <= SENSOR_MAX_DISTANCE && this.distanceFront > 0) {
           setAsObstacle(new Point((int) (this.distanceFront * Math.cos((CAR_FRONT + this.angle) * (Math.PI / 180))) +this.map.getCar().x,
                   (int) (this.distanceFront * Math.sin((CAR_FRONT +this.angle) * (Math.PI / 180))) +this.map.getCar().y), angle, cleanup);
        }
        else {
            setAsObstacle(new Point((int) (SENSOR_MAX_DISTANCE * Math.cos((CAR_FRONT + this.angle) * (Math.PI / 180))) +this.map.getCar().x,
                    (int) (SENSOR_MAX_DISTANCE * Math.sin((CAR_FRONT +this.angle) * (Math.PI / 180))) +this.map.getCar().y), angle, true);
        }

        if (this. distanceBack <= SENSOR_MAX_DISTANCE && this.distanceBack > 0) {
            setAsObstacle(new Point((int) (this.distanceBack * Math.cos((CAR_FRONT +this.backAngle) * (Math.PI / 180))) +this.map.getCar().x,
                    (int) (this.distanceBack * Math.sin((CAR_FRONT +this.backAngle) * (Math.PI / 180))) +this.map.getCar().y), backAngle, cleanup);
        }
        else {
            setAsObstacle(new Point((int) (SENSOR_MAX_DISTANCE * Math.cos((CAR_FRONT +this.backAngle) * (Math.PI / 180))) +this.map.getCar().x,
                    (int) (SENSOR_MAX_DISTANCE * Math.sin((CAR_FRONT +this.backAngle) * (Math.PI / 180))) +this.map.getCar().y), backAngle, true);
        }
    }

    public void setAsObstacle(Point coord, int angle, boolean cleanUp){
        if(coord != null) {
            Canvas c = new Canvas(map.getImage());
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setStyle(Paint.Style.FILL_AND_STROKE);

            double AN = Math.sqrt(Math.pow(map.getCar().x -coord.x, 2) +Math.pow(map.getCar().y -coord.y, 2));
            double GK = Math.tan(Math.toRadians(-7.5)) *AN;
            double HY = Math.sqrt(Math.pow(AN, 2) +Math.pow(GK, 2));

            if (cleanUp) HY -= 1;
            Point A = new Point(map.getCar().x, map.getCar().y), D = new Point(), E = new Point();
            Point B = new Point(
                    (int)(map.getCar().x +HY *Math.sin(Math.toRadians(angle -7.5))),
                    (int)(map.getCar().y -HY *Math.cos(Math.toRadians(angle -7.5))));
            Point C = new Point(
                    (int)(map.getCar().x +HY *Math.sin(Math.toRadians(angle +7.5))),
                    (int)(map.getCar().y -HY *Math.cos(Math.toRadians(angle +7.5))));

            if (!cleanUp) {
                D = new Point((int)(map.getCar().x +(HY +1) *Math.sin(Math.toRadians(angle -7.5))),
                              (int)(map.getCar().y -(HY +1) *Math.cos(Math.toRadians(angle -7.5))));
                E = new Point((int)(map.getCar().x +(HY +1) *Math.sin(Math.toRadians(angle +7.5))),
                              (int)(map.getCar().y -(HY +1) *Math.cos(Math.toRadians(angle +7.5))));
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

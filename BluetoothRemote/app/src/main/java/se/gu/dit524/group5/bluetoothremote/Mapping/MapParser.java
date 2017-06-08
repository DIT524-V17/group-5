package se.gu.dit524.group5.bluetoothremote.Mapping;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static se.gu.dit524.group5.bluetoothremote.Mapping.Constants.*;

/**
 * Created by mghan on 2017-03-31.
 * Modified by julian.bock on 2017-04-03, 2017-05-12 (and following).
 */
public class MapParser {
    private Canvas canvas;
    private Car car;

    MapParser(Canvas canvas, Car car){
        this.canvas = canvas;
        this.car = car;
    }

    ArrayList<PointF> parseToList(int distanceFront, int distanceBack, int angle) {
        return this.parse(distanceFront, distanceBack, angle, false, false);
    }

    void parse(int distanceFront, int distanceBack, int angle) {
        this.parse(distanceFront, distanceBack, angle, true, false);
    }

    void clean(int distanceFront, int distanceBack, int angle) {
        this.parse(distanceFront, distanceBack, angle, true, true);
    }

    private ArrayList<PointF> parse(int distanceFront, int distanceBack, int angle, boolean draw, boolean cleanup) {
        int frontAngle = (int) -this.car.front() -angle;
        int backAngle  = frontAngle -180;

        ArrayList<PointF> obstacleCenters = new ArrayList<>();
        double cos = Math.cos(Math.toRadians(frontAngle));
        double sin = Math.sin(Math.toRadians(frontAngle));

        if (distanceFront <= SENSOR_MAX_DISTANCE && distanceFront > 0) {
            obstacleCenters.add(
                setAsObstacle(new Point((int) (distanceFront *cos +this.car.servo().x),
                                        (int) (distanceFront *sin +this.car.servo().y)), frontAngle, cleanup, draw));
        }
        else {
            obstacleCenters.add(
                setAsObstacle(new Point((int) (SENSOR_MAX_DISTANCE *cos +this.car.servo().x),
                                        (int) (SENSOR_MAX_DISTANCE *sin +this.car.servo().y)), frontAngle, true, draw));
        }

        cos = Math.cos(Math.toRadians(backAngle));
        sin = Math.sin(Math.toRadians(backAngle));

        if (distanceBack <= SENSOR_MAX_DISTANCE && distanceBack > 0) {
            obstacleCenters.add(
                setAsObstacle(new Point((int) (distanceBack *cos +this.car.servo().x),
                                        (int) (distanceBack *sin +this.car.servo().y)), backAngle, cleanup, draw));
        }
        else {
            obstacleCenters.add(
                setAsObstacle(new Point((int) (SENSOR_MAX_DISTANCE *cos +this.car.servo().x),
                                        (int) (SENSOR_MAX_DISTANCE *sin +this.car.servo().y)), backAngle, true, draw));
        }
        return obstacleCenters;
    }

    private void setAsObstacle(Point coord, int angle, boolean cleanUp) {
        this.setAsObstacle(coord, angle, cleanUp, true);
    }

    private PointF setAsObstacle(Point coord, int angle, boolean cleanUp, boolean draw){
        if (coord != null) {
            Paint p = new Paint();
            p.setStrokeWidth(1.0f);
            p.setAntiAlias(true);
            p.setStyle(Paint.Style.FILL_AND_STROKE);

            double AN = Math.sqrt(Math.pow(this.car.servo().x -coord.x, 2) +Math.pow(this.car.servo().y -coord.y, 2));
            double GK = Math.tan(Math.toRadians(-7.5)) *AN;
            double HY = Math.sqrt(Math.pow(AN, 2) +Math.pow(GK, 2));

            if (cleanUp) HY -= 1.5f;
            PointF A = new PointF(this.car.servo().x, car.servo().y);

            PointF B = new PointF((float)(this.car.servo().x +HY *Math.sin(Math.toRadians(angle -7.5))),
                                  (float)(this.car.servo().y -HY *Math.cos(Math.toRadians(angle -7.5))));
            PointF C = new PointF((float)(this.car.servo().x +HY *Math.sin(Math.toRadians(angle +7.5))),
                                  (float)(this.car.servo().y -HY *Math.cos(Math.toRadians(angle +7.5))));

            if (draw) {
                if (cleanUp) {
                    p.setColor(Color.argb(FREE_SPACE_INTENSITY, 0xff, 0xff, 0xff));
                    Path path = new Path();
                    path.moveTo(A.x, A.y);
                    path.lineTo(B.x, B.y);
                    path.lineTo(C.x, C.y);
                    path.close();
                    this.canvas.drawPath(path, p);
                } else {
                    p.setColor(Color.argb(OBSTACLE_INTENSITY, 0x00, 0x00, 0x00));
                    this.canvas.drawLine(B.x, B.y, C.x, C.y, p);
                }
            }
            return new PointF((B.x +C.x) /2, (B.y +C.y) /2);
        }
        else return null;
    }
}

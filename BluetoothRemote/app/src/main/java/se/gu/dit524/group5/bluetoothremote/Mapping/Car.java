package se.gu.dit524.group5.bluetoothremote.Mapping;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import static se.gu.dit524.group5.bluetoothremote.Mapping.Constants.*;

/**
 * Created by julian.bock on 2017-05-08.
 * (and before that some day around Easter...)
 */

public class Car {
    private Path   car, cupholder;
    private Path[] wheels;
    private double front;
    private PointF center, servo;
    private PointF flWheel, frWheel, rlWheel, rrWheel;
    private PointF flCorner, frCorner, rlCorner, rrCorner;
    private PointF flCHCorner, frCHCorner, rlCHCorner, rrCHCorner;

    private final float flWAngle, frWAngle, rlWAngle, rrWAngle;
    private final float flCAngle, frCAngle, rlCAngle, rrCAngle;
    private final float flCHAngle, frCHAngle, rlCHAngle, rrCHAngle;
    private final float innerWflCAngle, innerWfrCAngle, innerWrlCAngle, innerWrrCAngle;

    private final float frontWRad, rearWRad, innerWRad;
    private final float frontCRad, rearCRad, frontCHRad, rearCHRad;

    public Car(int x, int y, int frontAngle) {
        this.flWheel = new PointF(WHEEL_WIDTH /2, WHEEL_FRONT_OFFSET +WHEEL_HEIGHT /2);
        this.frWheel = new PointF(CAR_WIDTH -WHEEL_WIDTH /2, WHEEL_FRONT_OFFSET +WHEEL_HEIGHT /2);
        this.rlWheel = new PointF(WHEEL_WIDTH /2, CAR_HEIGHT -WHEEL_REAR_OFFSET -WHEEL_HEIGHT /2);
        this.rrWheel = new PointF(CAR_WIDTH -WHEEL_WIDTH /2, CAR_HEIGHT -WHEEL_REAR_OFFSET -WHEEL_HEIGHT /2);

        this.flCorner = new PointF(0f, 0f);
        this.frCorner = new PointF(CAR_WIDTH, 0f);
        this.rlCorner = new PointF(0f, CAR_HEIGHT);
        this.rrCorner = new PointF(CAR_WIDTH, CAR_HEIGHT);

        this.flCHCorner = new PointF(CUPHOLDER_OFFSET, CAR_HEIGHT);
        this.frCHCorner = new PointF(CAR_WIDTH -CUPHOLDER_OFFSET, CAR_HEIGHT);
        this.rlCHCorner = new PointF(CUPHOLDER_OFFSET, CAR_HEIGHT +CUPHOLDER_HEIGHT);
        this.rrCHCorner = new PointF(CAR_WIDTH -CUPHOLDER_OFFSET, CAR_HEIGHT +CUPHOLDER_HEIGHT);

        this.center = new PointF(CAR_WIDTH /2, CAR_HEIGHT /2);
        this.servo =  new PointF(CAR_WIDTH /2, CAR_HEIGHT -WHEEL_REAR_OFFSET -WHEEL_HEIGHT /2);

        this.flWAngle = (float) (-180 +Math.toDegrees(Math.atan((flWheel.x -center.x) /(flWheel.y -center.y))));
        this.frWAngle = (float) (-180 +Math.toDegrees(Math.atan((frWheel.x -center.x) /(frWheel.y -center.y))));
        this.rlWAngle = (float) Math.toDegrees(Math.atan((rlWheel.x -center.x) /(rlWheel.y -center.y)));
        this.rrWAngle = (float) Math.toDegrees(Math.atan((rrWheel.x -center.x) /(rrWheel.y -center.y)));

        this.flCAngle = (float) (-180 +Math.toDegrees(Math.atan((flCorner.x -center.x) /(flCorner.y -center.y))));
        this.frCAngle = (float) Math.toDegrees(Math.atan((frCorner.x -center.x) /(frCorner.y -center.y)));
        this.rlCAngle = (float) (180 +Math.toDegrees(Math.atan((rlCorner.x -center.x) /(rlCorner.y -center.y))));
        this.rrCAngle = (float) Math.toDegrees(Math.atan((rrCorner.x -center.x) /(rrCorner.y -center.y)));

        this.flCHAngle = (float) (90 +Math.toDegrees(Math.atan((flCHCorner.x -center.x) /(flCHCorner.y -center.y))));
        this.frCHAngle = (float) (90 +Math.toDegrees(Math.atan((frCHCorner.x -center.x) /(frCHCorner.y -center.y))));
        this.rlCHAngle = (float) (90 +Math.toDegrees(Math.atan((rlCHCorner.x -center.x) /(rlCHCorner.y -center.y))));
        this.rrCHAngle = (float) (90 +Math.toDegrees(Math.atan((rrCHCorner.x -center.x) /(rrCHCorner.y -center.y))));

        this.frontWRad = (float) Math.sqrt(Math.pow(frWheel.x -center.x, 2) +Math.pow(frWheel.y -center.y, 2));
        this.rearWRad =  (float) Math.sqrt(Math.pow(rrWheel.x -center.x, 2) +Math.pow(rrWheel.y -center.y, 2));
        this.frontCRad = (float) Math.sqrt(Math.pow(frCorner.x -center.x, 2) +Math.pow(frCorner.y -center.y, 2));
        this.rearCRad =  (float) Math.sqrt(Math.pow(rrCorner.x -center.x, 2) +Math.pow(rrCorner.y -center.y, 2));
        this.frontCHRad = (float) Math.sqrt(Math.pow(frCHCorner.x -center.x, 2) +Math.pow(frCHCorner.y -center.y, 2));
        this.rearCHRad =  (float) Math.sqrt(Math.pow(rrCHCorner.x -center.x, 2) +Math.pow(rrCHCorner.y -center.y, 2));

        this.innerWRad = (float) (Math.sqrt(Math.pow(WHEEL_WIDTH, 2) +Math.pow(WHEEL_HEIGHT, 2)) /2);
        this.innerWflCAngle = (float) (90 +Math.toDegrees(Math.atan((WHEEL_WIDTH /2) /(WHEEL_HEIGHT /2))));
        this.innerWfrCAngle = (float) (90 -Math.toDegrees(Math.atan((WHEEL_WIDTH /2) /(WHEEL_HEIGHT /2))));
        this.innerWrlCAngle = (float) (-90 -Math.toDegrees(Math.atan((WHEEL_WIDTH /2) /(WHEEL_HEIGHT /2))));
        this.innerWrrCAngle = (float) (-90 +Math.toDegrees(Math.atan((WHEEL_WIDTH /2) /(WHEEL_HEIGHT /2))));

        this.center = new PointF(x, y);
        this.front = frontAngle;
        this.relocate();
        this.reshape();
    }

    private void relocate() {
        double sin, cos;

        sin = Math.sin(Math.toRadians(flWAngle +this.front));
        cos = Math.cos(Math.toRadians(flWAngle +this.front));
        this.flWheel = new PointF((float) (center.x +sin *frontWRad), (float) (center.y +cos *frontWRad));
        sin = Math.sin(Math.toRadians(frWAngle +this.front));
        cos = Math.cos(Math.toRadians(frWAngle +this.front));
        this.frWheel = new PointF((float) (center.x +sin *frontWRad), (float) (center.y +cos *frontWRad));
        sin = Math.sin(Math.toRadians(rlWAngle +this.front));
        cos = Math.cos(Math.toRadians(rlWAngle +this.front));
        this.rlWheel = new PointF((float) (center.x +sin *rearWRad), (float) (center.y +cos *rearWRad));
        sin = Math.sin(Math.toRadians(rrWAngle +this.front));
        cos = Math.cos(Math.toRadians(rrWAngle +this.front));
        this.rrWheel = new PointF((float) (center.x +sin *rearWRad), (float) (center.y +cos *rearWRad));

        sin = Math.sin(Math.toRadians(flCAngle +this.front));
        cos = Math.cos(Math.toRadians(flCAngle +this.front));
        this.flCorner = new PointF((float) (center.x +sin *frontCRad), (float) (center.y +cos *frontCRad));
        sin = Math.sin(Math.toRadians(frCAngle +this.front));
        cos = Math.cos(Math.toRadians(frCAngle +this.front));
        this.frCorner = new PointF((float) (center.x +sin *frontCRad), (float) (center.y +cos *frontCRad));
        sin = Math.sin(Math.toRadians(rlCAngle +this.front));
        cos = Math.cos(Math.toRadians(rlCAngle +this.front));
        this.rlCorner = new PointF((float) (center.x +sin *rearCRad), (float) (center.y +cos *rearCRad));
        sin = Math.sin(Math.toRadians(rrCAngle +this.front));
        cos = Math.cos(Math.toRadians(rrCAngle +this.front));
        this.rrCorner = new PointF((float) (center.x +sin *rearCRad), (float) (center.y +cos *rearCRad));

        sin = Math.sin(Math.toRadians(flCHAngle -this.front));
        cos = Math.cos(Math.toRadians(flCHAngle -this.front));
        this.flCHCorner = new PointF((float) (center.x +cos *frontCHRad), (float) (center.y +sin *frontCHRad));
        sin = Math.sin(Math.toRadians(frCHAngle -this.front));
        cos = Math.cos(Math.toRadians(frCHAngle -this.front));
        this.frCHCorner = new PointF((float) (center.x +cos *frontCHRad), (float) (center.y +sin *frontCHRad));
        sin = Math.sin(Math.toRadians(rlCHAngle -this.front));
        cos = Math.cos(Math.toRadians(rlCHAngle -this.front));
        this.rlCHCorner = new PointF((float) (center.x +cos *rearCHRad), (float) (center.y +sin *rearCHRad));
        sin = Math.sin(Math.toRadians(rrCHAngle -this.front));
        cos = Math.cos(Math.toRadians(rrCHAngle -this.front));
        this.rrCHCorner = new PointF((float) (center.x +cos *rearCHRad), (float) (center.y +sin *rearCHRad));

        this.servo =  new PointF((rlWheel.x +rrWheel.x) /2, (rlWheel.y +rrWheel.y) /2);
    }

    private void reshape() {
        this.car = new Path();
        this.car.moveTo(flCorner.x, flCorner.y);
        this.car.lineTo(frCorner.x, frCorner.y);
        this.car.lineTo(rrCorner.x, rrCorner.y);
        this.car.lineTo(rlCorner.x, rlCorner.y);
        this.car.lineTo(flCorner.x, flCorner.y);

        this.cupholder = new Path();
        this.cupholder.moveTo(flCHCorner.x, flCHCorner.y);
        this.cupholder.lineTo(frCHCorner.x, frCHCorner.y);
        this.cupholder.lineTo(rrCHCorner.x, rrCHCorner.y);
        this.cupholder.lineTo(rlCHCorner.x, rlCHCorner.y);
        this.cupholder.lineTo(flCHCorner.x, flCHCorner.y);

        this.wheels = new Path[4];
        for (int i = 0; i < this.wheels.length; i++) {
            PointF wheel, fl, fr, rl, rr;
            double sin, cos;
            switch (i) {
                case 0: wheel = flWheel; break;
                case 1: wheel = frWheel; break;
                case 2: wheel = rlWheel; break;
                case 3: wheel = rrWheel; break;
                default: wheel = new PointF();
            }
            sin = Math.sin(Math.toRadians(innerWflCAngle -this.front));
            cos = Math.cos(Math.toRadians(innerWflCAngle -this.front));
            fl = new PointF((float) (wheel.x +cos *innerWRad), (float) (wheel.y +sin *innerWRad));
            sin = Math.sin(Math.toRadians(innerWfrCAngle -this.front));
            cos = Math.cos(Math.toRadians(innerWfrCAngle -this.front));
            fr = new PointF((float) (wheel.x +cos *innerWRad), (float) (wheel.y +sin *innerWRad));
            sin = Math.sin(Math.toRadians(innerWrlCAngle -this.front));
            cos = Math.cos(Math.toRadians(innerWrlCAngle -this.front));
            rl = new PointF((float) (wheel.x +cos *innerWRad), (float) (wheel.y +sin *innerWRad));
            sin = Math.sin(Math.toRadians(innerWrrCAngle -this.front));
            cos = Math.cos(Math.toRadians(innerWrrCAngle -this.front));
            rr = new PointF((float) (wheel.x +cos *innerWRad), (float) (wheel.y +sin *innerWRad));

            this.wheels[i] = new Path();
            this.wheels[i].moveTo(fl.x, fl.y);
            this.wheels[i].lineTo(fr.x, fr.y);
            this.wheels[i].lineTo(rr.x, rr.y);
            this.wheels[i].lineTo(rl.x, rl.y);
            this.wheels[i].lineTo(fl.x, fl.y);
        }
    }

    public double front() {
        return front;
    }

    public PointF center() {
        return center;
    }

    public PointF servo() {
        return servo;
    }

    public void rotate(int degrees) {

    }

    public void move(int centimeters) {

    }

    public void draw(Canvas c) {
        Paint p = new Paint();
        p.setStrokeWidth(1);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL_AND_STROKE);

        p.setColor(Color.argb(0xff, 0xff, 0xb9, 0x41));
        c.drawPath(this.car, p);

        p.setColor(Color.argb(0xff, 0x71, 0xb9, 0x60));
        c.drawCircle(center.x, center.y, 4.0f, p);

        p.setColor(Color.argb(0xff, 0x6b, 0x85, 0xff));
        c.drawCircle(servo.x, servo.y, 2.5f, p);

        p.setColor(Color.argb(0xff, 0x66, 0x66, 0x66));
        c.drawPath(this.cupholder, p);

        p.setColor(Color.argb(0xff, 0x33, 0x33, 0x33));
        for (Path wheel : this.wheels) c.drawPath(wheel, p);
    }

    public void drawParticles(Canvas c) {
        Paint p = new Paint();
        p.setStrokeWidth(1);
        p.setAntiAlias(true);
        p.setColor(Color.MAGENTA);
        p.setStyle(Paint.Style.STROKE);

        c.drawCircle(center.x, center.y, 0.5f, p);
        c.drawCircle(servo.x, servo.y, 0.5f, p);

        c.drawCircle(flCorner.x, flCorner.y, 0.5f, p);
        c.drawCircle(frCorner.x, frCorner.y, 0.5f, p);
        c.drawCircle(rlCorner.x, rlCorner.y, 0.5f, p);
        c.drawCircle(rrCorner.x, rrCorner.y, 0.5f, p);

        c.drawCircle(flCHCorner.x, flCHCorner.y, 0.5f, p);
        c.drawCircle(frCHCorner.x, frCHCorner.y, 0.5f, p);
        c.drawCircle(rlCHCorner.x, rlCHCorner.y, 0.5f, p);
        c.drawCircle(rrCHCorner.x, rrCHCorner.y, 0.5f, p);

        c.drawCircle(flWheel.x, flWheel.y, 0.5f, p);
        c.drawCircle(frWheel.x, frWheel.y, 0.5f, p);
        c.drawCircle(rlWheel.x, rlWheel.y, 0.5f, p);
        c.drawCircle(rrWheel.x, rrWheel.y, 0.5f, p);

        // c.drawCircle(center.x, center.y, frontWRad, p);
        // c.drawCircle(center.x, center.y, rearWRad, p);
    }
}

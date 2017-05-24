package se.gu.dit524.group5.bluetoothremote.Mapping;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import static se.gu.dit524.group5.bluetoothremote.Mapping.Constants.*;

/**
 * Created by julian.bock on 2017-05-08 (and before that some day around Easter).
 */

public class Car {
    private boolean shaped;
    private Path    car, cupholder;
    private Path[]  wheels;
    private float   front, rotationCircleRadius;
    private PointF  rotationCenter;
    private PointF  center, servo;
    private PointF  flWheel, frWheel, rlWheel, rrWheel;
    private PointF  flCorner, frCorner, rlCorner, rrCorner;
    private PointF  flCHCorner, frCHCorner, rlCHCorner, rrCHCorner;

    private final float flWAngle, frWAngle, rlWAngle, rrWAngle;
    private final float flCAngle, frCAngle, rlCAngle, rrCAngle;
    private final float flCHAngle, frCHAngle, rlCHAngle, rrCHAngle;
    private final float innerWflCAngle, innerWfrCAngle, innerWrlCAngle, innerWrrCAngle;

    private final float frontWRad, rearWRad, innerWRad;
    private final float frontCRad, rearCRad, frontCHRad, rearCHRad;

    private static final int FWD_RIGHT_ROTATION = 1;
    private static final int FWD_LEFT_ROTATION  = 2;
    private static final int REV_RIGHT_ROTATION = 4;
    private static final int REV_LEFT_ROTATION  = 8;

    public Car(Car source) {
        this(source.center().x, source.center().y, source.front());
        this.reshape();

        this.rotationCenter = source.lastRotationCenter();
    }

    public Car(float x, float y, float frontAngle) {
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

        this.flWAngle = (float) (-180 +Math.toDegrees(Math.atan((this.flWheel.x -this.center.x)
                                                               /(this.flWheel.y -this.center.y))));
        this.frWAngle = (float) (-180 +Math.toDegrees(Math.atan((this.frWheel.x -this.center.x)
                                                               /(this.frWheel.y -this.center.y))));
        this.rlWAngle = (float) Math.toDegrees(Math.atan((this.rlWheel.x -this.center.x)
                                                        /(this.rlWheel.y -this.center.y)));
        this.rrWAngle = (float) Math.toDegrees(Math.atan((this.rrWheel.x -this.center.x)
                                                        /(this.rrWheel.y -this.center.y)));

        this.flCAngle = (float) (-180 +Math.toDegrees(Math.atan((this.flCorner.x -this.center.x)
                                                               /(this.flCorner.y -this.center.y))));
        this.frCAngle = (float) Math.toDegrees(Math.atan((this.frCorner.x -this.center.x)
                                                        /(this.frCorner.y -this.center.y)));
        this.rlCAngle = (float) (180 +Math.toDegrees(Math.atan((this.rlCorner.x -this.center.x)
                                                              /(this.rlCorner.y -this.center.y))));
        this.rrCAngle = (float) Math.toDegrees(Math.atan((this.rrCorner.x -this.center.x)
                                                        /(this.rrCorner.y -this.center.y)));

        this.flCHAngle = (float) (90 +Math.toDegrees(Math.atan((this.flCHCorner.x -this.center.x)
                                                              /(this.flCHCorner.y -this.center.y))));
        this.frCHAngle = (float) (90 +Math.toDegrees(Math.atan((this.frCHCorner.x -this.center.x)
                                                              /(this.frCHCorner.y -this.center.y))));
        this.rlCHAngle = (float) (90 +Math.toDegrees(Math.atan((this.rlCHCorner.x -this.center.x)
                                                              /(this.rlCHCorner.y -this.center.y))));
        this.rrCHAngle = (float) (90 +Math.toDegrees(Math.atan((this.rrCHCorner.x -this.center.x)
                                                              /(this.rrCHCorner.y -this.center.y))));

        this.frontWRad = (float) Math.sqrt(Math.pow(this.frWheel.x -this.center.x, 2)
                                          +Math.pow(this.frWheel.y -this.center.y, 2));
        this.rearWRad =  (float) Math.sqrt(Math.pow(this.rrWheel.x -this.center.x, 2)
                                          +Math.pow(this.rrWheel.y -this.center.y, 2));
        this.frontCRad = (float) Math.sqrt(Math.pow(this.frCorner.x -this.center.x, 2)
                                          +Math.pow(this.frCorner.y -this.center.y, 2));
        this.rearCRad =  (float) Math.sqrt(Math.pow(this.rrCorner.x -this.center.x, 2)
                                          +Math.pow(this.rrCorner.y -this.center.y, 2));
        this.frontCHRad = (float) Math.sqrt(Math.pow(this.frCHCorner.x -this.center.x, 2)
                                           +Math.pow(this.frCHCorner.y -this.center.y, 2));
        this.rearCHRad =  (float) Math.sqrt(Math.pow(this.rrCHCorner.x -this.center.x, 2)
                                           +Math.pow(this.rrCHCorner.y -this.center.y, 2));

        this.innerWRad      = (float)(Math.sqrt(Math.pow(WHEEL_WIDTH, 2) +Math.pow(WHEEL_HEIGHT, 2)) /2);
        this.innerWflCAngle = (float)(90 +Math.toDegrees(Math.atan((WHEEL_WIDTH /2) /(WHEEL_HEIGHT /2))));
        this.innerWfrCAngle = (float)(90 -Math.toDegrees(Math.atan((WHEEL_WIDTH /2) /(WHEEL_HEIGHT /2))));
        this.innerWrlCAngle = (float)(-90 -Math.toDegrees(Math.atan((WHEEL_WIDTH /2) /(WHEEL_HEIGHT /2))));
        this.innerWrrCAngle = (float)(-90 +Math.toDegrees(Math.atan((WHEEL_WIDTH /2) /(WHEEL_HEIGHT /2))));

        this.rotationCircleRadius = (float) Math.sqrt(
                Math.pow(CAR_HEIGHT -WHEEL_FRONT_OFFSET -WHEEL_HEIGHT /2 +CUPHOLDER_HEIGHT, 2)+ Math.pow(CAR_WIDTH, 2));

        this.center = new PointF(x, y);
        this.front = frontAngle;
        this.relocate();
    }

    private void relocate() {
        double sin, cos;

        sin = Math.sin(Math.toRadians(this.flWAngle +this.front));
        cos = Math.cos(Math.toRadians(this.flWAngle +this.front));
        this.flWheel = new PointF((float) (this.center.x +sin *this.frontWRad),
                                  (float) (this.center.y +cos *this.frontWRad));
        sin = Math.sin(Math.toRadians(this.frWAngle +this.front));
        cos = Math.cos(Math.toRadians(this.frWAngle +this.front));
        this.frWheel = new PointF((float) (this.center.x +sin *this.frontWRad),
                                  (float) (this.center.y +cos *this.frontWRad));
        sin = Math.sin(Math.toRadians(this.rlWAngle +this.front));
        cos = Math.cos(Math.toRadians(this.rlWAngle +this.front));
        this.rlWheel = new PointF((float) (this.center.x +sin *this.rearWRad),
                                  (float) (this.center.y +cos *this.rearWRad));
        sin = Math.sin(Math.toRadians(this.rrWAngle +this.front));
        cos = Math.cos(Math.toRadians(this.rrWAngle +this.front));
        this.rrWheel = new PointF((float) (this.center.x +sin *this.rearWRad),
                                  (float) (this.center.y +cos *this.rearWRad));

        sin = Math.sin(Math.toRadians(this.flCAngle +this.front));
        cos = Math.cos(Math.toRadians(this.flCAngle +this.front));
        this.flCorner = new PointF((float) (this.center.x +sin *this.frontCRad),
                                   (float) (this.center.y +cos *this.frontCRad));
        sin = Math.sin(Math.toRadians(this.frCAngle +this.front));
        cos = Math.cos(Math.toRadians(this.frCAngle +this.front));
        this.frCorner = new PointF((float) (this.center.x +sin *this.frontCRad),
                                   (float) (this.center.y +cos *this.frontCRad));
        sin = Math.sin(Math.toRadians(this.rlCAngle +this.front));
        cos = Math.cos(Math.toRadians(this.rlCAngle +this.front));
        this.rlCorner = new PointF((float) (this.center.x +sin *this.rearCRad),
                                   (float) (this.center.y +cos *this.rearCRad));
        sin = Math.sin(Math.toRadians(this.rrCAngle +this.front));
        cos = Math.cos(Math.toRadians(this.rrCAngle +this.front));
        this.rrCorner = new PointF((float) (this.center.x +sin *this.rearCRad),
                                   (float) (this.center.y +cos *this.rearCRad));

        sin = Math.sin(Math.toRadians(this.flCHAngle -this.front));
        cos = Math.cos(Math.toRadians(this.flCHAngle -this.front));
        this.flCHCorner = new PointF((float) (this.center.x +cos *this.frontCHRad),
                                     (float) (this.center.y +sin *this.frontCHRad));
        sin = Math.sin(Math.toRadians(this.frCHAngle -this.front));
        cos = Math.cos(Math.toRadians(this.frCHAngle -this.front));
        this.frCHCorner = new PointF((float) (this.center.x +cos *this.frontCHRad),
                                     (float) (this.center.y +sin *this.frontCHRad));
        sin = Math.sin(Math.toRadians(this.rlCHAngle -this.front));
        cos = Math.cos(Math.toRadians(this.rlCHAngle -this.front));
        this.rlCHCorner = new PointF((float) (this.center.x +cos *this.rearCHRad),
                                     (float) (this.center.y +sin *this.rearCHRad));
        sin = Math.sin(Math.toRadians(this.rrCHAngle -this.front));
        cos = Math.cos(Math.toRadians(this.rrCHAngle -this.front));
        this.rrCHCorner = new PointF((float) (this.center.x +cos *this.rearCHRad),
                                     (float) (this.center.y +sin *this.rearCHRad));

        this.servo =  new PointF((this.rlWheel.x +this.rrWheel.x) /2,
                                 (this.rlWheel.y +this.rrWheel.y) /2);
        this.shaped = false;
    }

    private void reshape() {
        if (shaped) return;

        this.car = new Path();
        this.car.moveTo(this.flCorner.x, this.flCorner.y);
        this.car.lineTo(this.frCorner.x, this.frCorner.y);
        this.car.lineTo(this.rrCorner.x, this.rrCorner.y);
        this.car.lineTo(this.rlCorner.x, this.rlCorner.y);
        this.car.lineTo(this.flCorner.x, this.flCorner.y);

        this.cupholder = new Path();
        this.cupholder.moveTo(this.flCHCorner.x, this.flCHCorner.y);
        this.cupholder.lineTo(this.frCHCorner.x, this.frCHCorner.y);
        this.cupholder.lineTo(this.rrCHCorner.x, this.rrCHCorner.y);
        this.cupholder.lineTo(this.rlCHCorner.x, this.rlCHCorner.y);
        this.cupholder.lineTo(this.flCHCorner.x, this.flCHCorner.y);

        this.wheels = new Path[4];
        for (int i = 0; i < this.wheels.length; i++) {
            PointF wheel, fl, fr, rl, rr;
            double sin, cos;
            switch (i) {
                case 0: wheel = this.flWheel; break;
                case 1: wheel = this.frWheel; break;
                case 2: wheel = this.rlWheel; break;
                case 3: wheel = this.rrWheel; break;
                default: wheel = new PointF();
            }
            sin = Math.sin(Math.toRadians(this.innerWflCAngle -this.front));
            cos = Math.cos(Math.toRadians(this.innerWflCAngle -this.front));
            fl = new PointF((float) (wheel.x +cos *innerWRad),
                            (float) (wheel.y +sin *this.innerWRad));
            sin = Math.sin(Math.toRadians(this.innerWfrCAngle -this.front));
            cos = Math.cos(Math.toRadians(this.innerWfrCAngle -this.front));
            fr = new PointF((float) (wheel.x +cos *innerWRad),
                            (float) (wheel.y +sin *this.innerWRad));
            sin = Math.sin(Math.toRadians(this.innerWrlCAngle -this.front));
            cos = Math.cos(Math.toRadians(this.innerWrlCAngle -this.front));
            rl = new PointF((float) (wheel.x +cos *innerWRad),
                            (float) (wheel.y +sin *this.innerWRad));
            sin = Math.sin(Math.toRadians(this.innerWrrCAngle -this.front));
            cos = Math.cos(Math.toRadians(this.innerWrrCAngle -this.front));
            rr = new PointF((float) (wheel.x +cos *innerWRad),
                            (float) (wheel.y +sin *this.innerWRad));

            this.wheels[i] = new Path();
            this.wheels[i].moveTo(fl.x, fl.y);
            this.wheels[i].lineTo(fr.x, fr.y);
            this.wheels[i].lineTo(rr.x, rr.y);
            this.wheels[i].lineTo(rl.x, rl.y);
            this.wheels[i].lineTo(fl.x, fl.y);
        }

        this.shaped = true;
    }

    public float front() {
        return this.front;
    }

    public PointF center() {
        return this.center;
    }

    public PointF servo() {
        return this.servo;
    }

    public PointF lastRotationCenter() {
        return this.rotationCenter;
    }

    public float rotationCircleRadius() {
        return this.rotationCircleRadius;
    }

    public void rotate(int degrees) {
        this.rotate(degrees, degrees >= 0 ? FWD_LEFT_ROTATION : FWD_RIGHT_ROTATION, true);
    }

    private void rotate(int degrees, boolean relocate) {
        this.rotate(degrees, degrees >= 0 ? FWD_LEFT_ROTATION : FWD_RIGHT_ROTATION, relocate);
    }

    private void rotate(int degrees, int direction, boolean relocate) {
        double sin, cos;

        switch (direction) {
            case FWD_LEFT_ROTATION: this.rotationCenter = this.flWheel; break;
            case FWD_RIGHT_ROTATION: this.rotationCenter = this.frWheel; break;
            case REV_LEFT_ROTATION: this.rotationCenter = this.rlWheel; break;
            case REV_RIGHT_ROTATION: this.rotationCenter = this.rrWheel; break;
            default: return;
        }

        // TODO: the following should be adapted in case rotating and
        //       going reverse at the same time becomes a thing...

        sin = Math.sin(Math.toRadians(-degrees));
        cos = Math.cos(Math.toRadians(-degrees));

        this.center = new PointF(
            (float)(cos *(this.center.x -this.rotationCenter.x) -sin
                        *(this.center.y -this.rotationCenter.y) +this.rotationCenter.x),
            (float)(sin *(this.center.x -this.rotationCenter.x) +cos
                        *(this.center.y -this.rotationCenter.y) +this.rotationCenter.y));

        this.front += degrees;
        if (this.front > 180) this.front -= 360;
        else if (this.front < -180) this.front += 360;
        if (relocate) this.relocate();
    }

    public void move(int centimeters) {
        this.move(centimeters, true);
    }

    private void move(int centimeters, boolean relocate) {
        double sin, cos;

        sin = Math.sin(Math.toRadians(this.front));
        cos = Math.cos(Math.toRadians(this.front));

        this.center = new PointF((float)(center.x -sin *centimeters), (float)(center.y -cos *centimeters));
        if (relocate) this.relocate();
    }

    public void alterPosition(float x, float y) {
        this.center = new PointF(this.center.x +x, this.center.y +y);
        this.relocate();
    }

    public int[] findPath(PointF dest, float radius) {
        int directions[] = new int[2];
        for (int angle = -180; angle <= 180; angle++) {
            Car c = new Car(this.center.x, this.center.y, this.front);
            c.rotate(angle, false);
            for (int distance = 0; distance < radius; distance++) {
                c.move(1, false);
                if (Math.abs(Math.round(c.center().x) -Math.round(dest.x)) <= 1 &&
                        Math.abs(Math.round(c.center().y) -Math.round(dest.y)) <= 1) {
                    directions[0] = angle;
                    directions[1] = distance;
                    return directions;
                }
            }
        }
        return directions;
    }

    public void draw(Canvas c) {
        this.reshape();

        Paint p = new Paint();
        p.setStrokeWidth(1.0f);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL_AND_STROKE);

        p.setColor(Color.argb(0xff, 0xff, 0xb9, 0x41));
        c.drawPath(this.car, p);

        p.setColor(Color.argb(0xff, 0x71, 0xb9, 0x60));
        c.drawCircle(this.center.x, this.center.y, 4.0f, p);

        p.setColor(Color.argb(0xff, 0x6b, 0x85, 0xff));
        c.drawCircle(this.servo.x, this.servo.y, 2.5f, p);

        p.setColor(Color.argb(0xff, 0x66, 0x66, 0x66));
        c.drawPath(this.cupholder, p);

        p.setColor(Color.argb(0xff, 0x33, 0x33, 0x33));
        for (Path wheel : this.wheels) c.drawPath(wheel, p);
    }

    public void drawShade(Canvas c) {
        this.reshape();

        Paint p = new Paint();
        p.setStrokeWidth(1.0f);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL_AND_STROKE);

        p.setColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));
        c.drawPath(this.car, p);

        p.setColor(Color.argb(0xff, 0x99, 0x99, 0x99));
        c.drawPath(this.cupholder, p);

        p.setColor(Color.argb(0xff, 0x66, 0x66, 0x66));
        for (Path wheel : this.wheels) c.drawPath(wheel, p);
    }

    public void erase(Canvas c) {
        if (this.car == null || this.cupholder == null || this.wheels == null) return;
        if (!this.shaped) this.reshape();

        Paint p = new Paint();
        p.setStrokeWidth(3.0f);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        c.drawPath(this.car, p);
        c.drawPath(this.cupholder, p);
    }

    public void drawParticles(Canvas c) {
        Paint p = new Paint();
        p.setStrokeWidth(1);
        p.setAntiAlias(true);
        p.setColor(Color.MAGENTA);
        p.setStyle(Paint.Style.STROKE);

        c.drawCircle(this.center.x, this.center.y, 0.5f, p);
        c.drawCircle(this.servo.x, this.servo.y, 0.5f, p);

        c.drawCircle(this.flCorner.x, this.flCorner.y, 0.5f, p);
        c.drawCircle(this.frCorner.x, this.frCorner.y, 0.5f, p);
        c.drawCircle(this.rlCorner.x, this.rlCorner.y, 0.5f, p);
        c.drawCircle(this.rrCorner.x, this.rrCorner.y, 0.5f, p);

        c.drawCircle(this.flCHCorner.x, this.flCHCorner.y, 0.5f, p);
        c.drawCircle(this.frCHCorner.x, this.frCHCorner.y, 0.5f, p);
        c.drawCircle(this.rlCHCorner.x, this.rlCHCorner.y, 0.5f, p);
        c.drawCircle(this.rrCHCorner.x, this.rrCHCorner.y, 0.5f, p);

        c.drawCircle(this.flWheel.x, this.flWheel.y, 0.5f, p);
        c.drawCircle(this.frWheel.x, this.frWheel.y, 0.5f, p);
        c.drawCircle(this.rlWheel.x, this.rlWheel.y, 0.5f, p);
        c.drawCircle(this.rrWheel.x, this.rrWheel.y, 0.5f, p);

        c.drawCircle(this.flWheel.x, this.flWheel.y, this.frontWRad, p);
        c.drawCircle(this.frWheel.x, this.frWheel.y, this.frontWRad, p);
    }
}

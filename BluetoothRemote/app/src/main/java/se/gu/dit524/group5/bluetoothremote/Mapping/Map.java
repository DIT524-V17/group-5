package se.gu.dit524.group5.bluetoothremote.Mapping;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import se.gu.dit524.group5.bluetoothremote.BluetoothService;
import se.gu.dit524.group5.bluetoothremote.Instruction;
import se.gu.dit524.group5.bluetoothremote.ScanResult;

import static se.gu.dit524.group5.bluetoothremote.Mapping.Constants.CAR_WIDTH;
import static se.gu.dit524.group5.bluetoothremote.Mapping.Constants.SENSOR_MAX_DISTANCE;

/**
 * Created by mghan on 2017-03-31.
 * Modified by julian.bock on 2017-04-03, 2017-05-12 (and following).
 */

public class Map {
    private BluetoothService btInterface;
    private MapParser mapParser = new MapParser(this);
    private Bitmap map, carOverlay, shadeOverlay, routeOverlay;

    private Car car = new Car(SENSOR_MAX_DISTANCE, SENSOR_MAX_DISTANCE, 0);
    private Car lastCar = new Car(SENSOR_MAX_DISTANCE, SENSOR_MAX_DISTANCE, 0);
    private Car lastScanCar = new Car(SENSOR_MAX_DISTANCE, SENSOR_MAX_DISTANCE, 0);

    public Map() {
        this(SENSOR_MAX_DISTANCE *2, SENSOR_MAX_DISTANCE *2);
    }

    public Map(BluetoothService btInterface) {
        this(SENSOR_MAX_DISTANCE *2, SENSOR_MAX_DISTANCE *2, btInterface);
    }

    public Map(int width, int height) {
        this(width, height, null);
    }

    public Map(int width, int height, BluetoothService btInterface) {
        this.btInterface = btInterface;

        this.map = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        this.carOverlay = Bitmap.createBitmap(this.map.getWidth(), this.map.getHeight(), Bitmap.Config.ARGB_4444);
        this.shadeOverlay = Bitmap.createBitmap(this.map.getWidth(), this.map.getHeight(), Bitmap.Config.ARGB_4444);
        this.routeOverlay = Bitmap.createBitmap(this.map.getWidth(), this.map.getHeight(), Bitmap.Config.ARGB_4444);

        this.drawCar();
    }

    public void setBluetoothInterface(BluetoothService btInterface) {
        this.btInterface = btInterface;
    }

    public void processMeasurement(ScanResult.SingleScan scan) {
        if (this.car.servo().x -SENSOR_MAX_DISTANCE < 0 ||
            this.car.servo().y -SENSOR_MAX_DISTANCE < 0 ||
            this.car.servo().x +SENSOR_MAX_DISTANCE > this.map.getWidth() ||
            this.car.servo().y +SENSOR_MAX_DISTANCE > this.map.getHeight()) resize();

        this.mapParser.parse(scan.getDistanceA(), scan.getDistanceB(), scan.getAngle());
    }

    public void removeCollidingObstacles(ScanResult.SingleScan scan) {
        this.mapParser.clean(scan.getDistanceA(), scan.getDistanceB(), scan.getAngle());
    }

    public Bitmap getMap(){
        return this.map;
    }

    public Bitmap getCarOverlay() {
        return this.carOverlay;
    }

    public Bitmap getShadeOverlay() {
        return this.shadeOverlay;
    }

    public Bitmap getRouteOverlay() {
        return this.routeOverlay;
    }

    private static final int NO_RESIZE = 0;
    private static final int ADVANCE_LEFT= 1;
    private static final int ADVANCE_RIGHT = 2;
    private static final int ADVANCE_TOP = 4;
    private static final int ADVANCE_BOTTOM = 8;

    private void resize() {
        int rFlag = NO_RESIZE;
        if (this.car.servo().x +SENSOR_MAX_DISTANCE >= this.map.getWidth()) rFlag += ADVANCE_RIGHT;
        if (this.car.servo().y +SENSOR_MAX_DISTANCE >= this.map.getHeight()) rFlag += ADVANCE_BOTTOM;
        if (this.car.servo().x -SENSOR_MAX_DISTANCE < 0) rFlag += ADVANCE_LEFT;
        if (this.car.servo().y -SENSOR_MAX_DISTANCE < 0) rFlag += ADVANCE_TOP;
        if (rFlag == NO_RESIZE) return;

        Bitmap bmp;
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0: bmp = this.map; break;
                case 1: bmp = this.carOverlay; break;
                case 2: bmp = this.shadeOverlay; break;
                case 3: bmp = this.routeOverlay; break;
                default: return;
            }

            Bitmap combined = Bitmap.createBitmap(
                    (int) (bmp.getWidth() +(((rFlag &ADVANCE_LEFT) >= 1 || (rFlag &ADVANCE_RIGHT) >= 1) ?
                            Math.abs(SENSOR_MAX_DISTANCE -this.car.servo().x) : 0)),
                    (int) (bmp.getHeight() +(((rFlag &ADVANCE_TOP) >= 1 || (rFlag &ADVANCE_BOTTOM) >= 1) ?
                            Math.abs(SENSOR_MAX_DISTANCE -this.car.servo().y) : 0)),
                    Bitmap.Config.ARGB_4444);

            int[] pixels = new int[bmp.getWidth() *bmp.getHeight()];
            bmp.getPixels(pixels, 0,
                    bmp.getWidth(), 0, 0,
                    bmp.getWidth(),
                    bmp.getHeight());

            combined.setPixels(pixels, 0,
                    bmp.getWidth(),
                    (int) ((rFlag &ADVANCE_LEFT) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -this.car.servo().x) : 0),
                    (int) ((rFlag &ADVANCE_TOP) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -this.car.servo().y) : 0),
                    bmp.getWidth(),
                    bmp.getHeight());

            switch (i) {
                case 0: this.map = combined; break;
                case 1: this.carOverlay = combined; break;
                case 2: this.shadeOverlay = combined; break;
                case 3: this.routeOverlay = combined; break;
                default: return;
            }
        }

        updateCarPosition(
                (rFlag &ADVANCE_LEFT) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -this.car.center().x) : 0,
                (rFlag &ADVANCE_TOP) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -this.car.center().y) : 0, false, false);
        updateLastCarPosition(
                (rFlag &ADVANCE_LEFT) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -this.lastCar.center().x) : 0,
                (rFlag &ADVANCE_TOP) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -this.lastCar.center().y) : 0);
        updateLastScanCarPosition(
                (rFlag &ADVANCE_LEFT) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -this.lastScanCar.center().x) : 0,
                (rFlag &ADVANCE_TOP) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -this.lastScanCar.center().y) : 0);
    }

    public void drawCar() {
        Canvas carCanvas = new Canvas(this.carOverlay);
        Canvas shadeCanvas = new Canvas(this.shadeOverlay);
        Canvas routeCanvas = new Canvas(this.routeOverlay);

        this.lastCar.erase(carCanvas);
        this.lastCar.drawShade(shadeCanvas);
        this.car.draw(carCanvas);

        Paint p = new Paint();
        p.setColor(Color.argb(0xff, 0xdd, 0xdd, 0xdd));
        p.setAntiAlias(true);
        p.setStrokeWidth(CAR_WIDTH);

        if (this.lastCar.front() != this.car.front()) routeCanvas.drawCircle(
                this.car.lastRotationCenter().x, this.car.lastRotationCenter().y,
                this.car.rotationCircleRadius(), p);

        else routeCanvas.drawLine(
                this.lastCar.center().x, this.lastCar.center().y,
                this.car.center().x, this.car.center().y, p);
    }

    public Car getCar() {
        return this.car;
    }

    public void setCar(Car c) {
        this.car = new Car(c.center().x, c.center().y, c.front());
    }

    public boolean updateCarPosition(float x, float y, boolean move, boolean draw) {
        if (move) {
            int[] directions = this.car.findPath(new PointF(this.car.center().x +x, this.car.center().y +y),
                    (float) (Math.sqrt(Math.pow(this.map.getWidth() /2, 2) + Math.pow(this.map.getHeight() /2, 2))));

            if (directions[0] != 0 || directions[1] != 0) {
                if (btInterface != null) {
                    if (btInterface.busy()) return false;

                    byte deg = (byte)((Math.abs(directions[0]) &0x7F) |(directions[0] > 0 ? 0b10000000 : 0x00));
                    byte cm  = (byte)((Math.abs(directions[1]) &0x7F) |(directions[1] < 0 ? 0b10000000 : 0x00));

                    btInterface.send(new Instruction(new byte[]{ 0x31, deg }, 3, BluetoothService.AWAITING_STEERING_CALLBACK), true);
                    btInterface.send(new Instruction(new byte[]{ 0x41, cm }, 2, BluetoothService.AWAITING_STEERING_CALLBACK), true);
                }

                this.setLastCar(this.getCar());
                this.car.rotate(directions[0]);
                if (draw) this.drawCar();

                this.setLastCar(this.getCar());
                this.car.move(directions[1]);
                if (draw) this.drawCar();

                return true;
            }
        }
        else this.car.alterPosition(x, y);
        return false;
    }

    public Car getLastScanCar() {
        return this.lastScanCar;
    }

    public void setLastScanCar(Car c) {
        this.lastScanCar = new Car(c);
    }

    public void updateLastScanCarPosition(float x, float y) {
        this.lastScanCar.alterPosition(x, y);
    }

    public Car getLastCar() {
        return this.lastCar;
    }

    public void setLastCar(Car c) {
        this.lastCar = new Car(c);
    }

    public void updateLastCarPosition(float x, float y) {
        this.lastCar.alterPosition(x, y);
    }
}
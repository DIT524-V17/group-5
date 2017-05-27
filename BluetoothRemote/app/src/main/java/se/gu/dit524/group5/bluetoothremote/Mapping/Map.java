package se.gu.dit524.group5.bluetoothremote.Mapping;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import java.util.ArrayList;

import se.gu.dit524.group5.bluetoothremote.BluetoothService;
import se.gu.dit524.group5.bluetoothremote.Instruction;
import se.gu.dit524.group5.bluetoothremote.ScanResult;

import static se.gu.dit524.group5.bluetoothremote.Mapping.Constants.*;

/**
 * Created by mghan on 2017-03-31.
 * Modified by julian.bock on 2017-04-03, 2017-05-12 (and following).
 */

public class Map {
    private BluetoothService btInterface;
    private ArrayList<ScanResult> sensorReadings;
    private MapParser mapParser;
    private Bitmap rawMap, concreteMap, carOverlay, shadeOverlay, routeOverlay;

    private int lastConcreteObstacleThreshold;

    private Car car = new Car(SENSOR_MAX_DISTANCE, SENSOR_MAX_DISTANCE, 0);
    private Car lastCar = new Car(this.car);
    private Car lastScanCar = new Car(this.car);

    public Map() {
        this(SENSOR_MAX_DISTANCE *2, SENSOR_MAX_DISTANCE *2);
    }

    public Map(BluetoothService btInterface) {
        this(SENSOR_MAX_DISTANCE *2, SENSOR_MAX_DISTANCE *2, btInterface);
    }

    public Map(int width, int height) {
        this(width, height, null);
    }

    public Map(Bitmap map, BluetoothService btInterface) {
        this(map.getWidth(), map.getHeight(), btInterface);
        this.rawMap = map;

        // TODO: parse this.car ...?
        this.car = new Car(0, 0, 0);
        this.lastCar = new Car(this.car);
        this.lastScanCar = new Car(this.car);

    }

    public Map(int width, int height, BluetoothService btInterface) {
        this.btInterface = btInterface;
        this.sensorReadings = new ArrayList<>();

        this.rawMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        this.carOverlay = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);
        this.shadeOverlay = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);
        this.routeOverlay = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);

        this.drawCar();
    }

    public void parsePreparedMap() {

    }

    public void setBluetoothInterface(BluetoothService btInterface) {
        this.btInterface = btInterface;
    }

    private void processMeasurement(ScanResult.SingleScan scan, Canvas canvas, Car car) {
        this.mapParser = new MapParser(canvas, car);
        this.mapParser.parse(scan.getDistanceA(), scan.getDistanceB(), scan.getAngle());
    }

    private void removeCollidingObstacles(ScanResult.SingleScan scan, Canvas canvas, Car car) {
        this.mapParser = new MapParser(canvas, car);
        this.mapParser.clean(scan.getDistanceA(), scan.getDistanceB(), scan.getAngle());
    }

    public Bitmap getMap(){
        return this.rawMap;
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

    public void resize() {
        float addR = this.car.servo().x +SENSOR_MAX_DISTANCE +SERVO_OFFSET -this.rawMap.getWidth();
        float addB = this.car.servo().y +SENSOR_MAX_DISTANCE +SERVO_OFFSET -this.rawMap.getHeight();
        float addL = -(this.car.servo().x -SENSOR_MAX_DISTANCE -SERVO_OFFSET);
        float addT = -(this.car.servo().y -SENSOR_MAX_DISTANCE -SERVO_OFFSET);

        if (addR <= 0 && addB <= 0 && addL <= 0 && addT <= 0) return;
        else {
            if (addR < 0) addR = 0;
            if (addB < 0) addB = 0;
            if (addL < 0) addL = 0;
            if (addT < 0) addT = 0;
            this.concreteMap = null;
        }

        for (int i = 0; i < 4; i++) {
            Bitmap bmp;
            switch (i) {
                case 0: bmp = this.rawMap; break;
                case 1: bmp = this.carOverlay; break;
                case 2: bmp = this.shadeOverlay; break;
                case 3: bmp = this.routeOverlay; break;
                default: return;
            }

            Bitmap combined = Bitmap.createBitmap(
                    (int) (bmp.getWidth() +addL +addR),
                    (int) (bmp.getHeight() +addT +addB), Bitmap.Config.ARGB_4444);

            int[] pixels = new int[bmp.getWidth() *bmp.getHeight()];
            bmp.getPixels(pixels, 0,
                    bmp.getWidth(), 0, 0,
                    bmp.getWidth(),
                    bmp.getHeight());

            combined.setPixels(pixels, 0,
                    bmp.getWidth(),
                    (int) (addL),
                    (int) (addT),
                    bmp.getWidth(),
                    bmp.getHeight());

            switch (i) {
                case 0: this.rawMap = combined; break;
                case 1: this.carOverlay = combined; break;
                case 2: this.shadeOverlay = combined; break;
                case 3: this.routeOverlay = combined; break;
                default: return;
            }
        }

        updateCarPosition(addL, addT, false, false);
        updateLastCarPosition(addL, addT);
        updateLastScanCarPosition(addL, addT);

        for (ScanResult scan : this.sensorReadings) scan.car().alterPosition(addL, addT);
    }

    public void drawCar() {
        this.carOverlay = Bitmap.createBitmap(this.carOverlay.getWidth(), this.carOverlay.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas carCanvas = new Canvas(this.carOverlay);
        Canvas shadeCanvas = new Canvas(this.shadeOverlay);
        Canvas routeCanvas = new Canvas(this.routeOverlay);

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
                    (float) (Math.sqrt(Math.pow(this.rawMap.getWidth() /2, 2) + Math.pow(this.rawMap.getHeight() /2, 2))));

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

    public void processScanResult(ScanResult scanResult) {
        this.resize();
        this.concreteMap = null;
        if (scanResult.car() == null) {
            scanResult.setCar(this.car);
            this.setLastScanCar(this.car);
        }
        this.sensorReadings.add(scanResult);

        Bitmap out = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);
        Bitmap poi = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);

        Canvas outCanvas = new Canvas(out);
        Canvas poiCanvas = new Canvas(poi);
        Canvas mapCanvas = new Canvas(this.rawMap);

        for (ScanResult.SingleScan scan : scanResult.scans()) this.processMeasurement(scan, outCanvas, scanResult.car());
        for (ScanResult prevResult : this.sensorReadings) {
            if (Math.abs(scanResult.car().servo().x -prevResult.car().servo().x) <= SENSOR_MAX_DISTANCE ||
                    Math.abs(scanResult.car().servo().y -prevResult.car().servo().y) <= SENSOR_MAX_DISTANCE) {
                for (ScanResult.SingleScan scan : prevResult.scans())
                    this.removeCollidingObstacles(scan, outCanvas, prevResult.car());
            }
        }

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.WHITE);
        poiCanvas.drawCircle(scanResult.car().servo().x -0.5f, scanResult.car().servo().y +0.5f, SENSOR_MAX_DISTANCE +1.0f, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        outCanvas.drawBitmap(poi, 0, 0, p);
        mapCanvas.drawBitmap(out, 0, 0, null);
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

    public Bitmap generateConcreteMap(int obstacleThreshold) {
        if (this.concreteMap != null && obstacleThreshold == this.lastConcreteObstacleThreshold) return this.concreteMap;
        this.concreteMap = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);
        for (int i = 0; i < this.rawMap.getWidth(); i++) {
            for (int j = 0; j < this.rawMap.getHeight(); j++) {
                if ((this.rawMap.getPixel(i, j) &0xff) <= obstacleThreshold)
                    this.concreteMap.setPixel(i, j, Color.BLACK);
                else this.concreteMap.setPixel(i, j, Color.WHITE);
            }
        }
        this.lastConcreteObstacleThreshold = obstacleThreshold;
        return this.concreteMap;
    }
}
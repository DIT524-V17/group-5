package se.gu.dit524.group5.bluetoothremote.Mapping;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import se.gu.dit524.group5.bluetoothremote.BluetoothService;
import se.gu.dit524.group5.bluetoothremote.Dijkstra.FastFinder;
import se.gu.dit524.group5.bluetoothremote.Instruction;
import se.gu.dit524.group5.bluetoothremote.ScanResult;
import se.gu.dit524.group5.bluetoothremote.Voronoi.Graph;
import se.gu.dit524.group5.bluetoothremote.Voronoi.Node;

import static se.gu.dit524.group5.bluetoothremote.Mapping.Constants.*;

/**
 * Created by mghan on 2017-03-31.
 * Modified by julian.bock on 2017-04-03, 2017-05-12 (and following).
 */

public class Map {
    public Object mainActivity;
    public Method drawCallback;

    private BluetoothService btInterface;
    private ArrayList<ScanResult> sensorReadings;
    private MapParser mapParser;
    private Bitmap rawMap, concreteMap, carOverlay, shadeOverlay, routeOverlay, instructionOverlay;
    private int lastConcreteObstacleThreshold;

    public boolean processingSteeringInstructions, steeringCallbackReceived;

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
        this.btInterface = btInterface;
        this.sensorReadings = new ArrayList<>();

        this.rawMap = map;
        for (int x = 0; x < this.rawMap.getWidth(); x++)
            for (int y = 0; y < this.rawMap.getHeight(); y++)
                if (this.rawMap.getPixel(x, y) == Color.argb(0xFF, 0x71, 0xB9, 0x60)) {
                    this.rawMap.setPixel(x, y, Color.WHITE);
                    this.car = new Car(x, y, 0);
                    this.lastCar = new Car(this.car);
                    this.lastScanCar = new Car(this.car);
                    break;
                }

        this.carOverlay = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);
        this.shadeOverlay = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);
        this.routeOverlay = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);
        this.instructionOverlay = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);

        this.drawCar();
    }

    public Map(int width, int height, BluetoothService btInterface) {
        this.btInterface = btInterface;
        this.sensorReadings = new ArrayList<>();

        this.rawMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        this.carOverlay = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);
        this.shadeOverlay = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);
        this.routeOverlay = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);
        this.instructionOverlay = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);

        this.drawCar();
    }

    public void setBluetoothInterface(BluetoothService btInterface) {
        this.btInterface = btInterface;
    }

    public ArrayList<Point> getObstacleCenters() {
        if (this.sensorReadings == null || this.sensorReadings.size() == 0) return null;
        else {
            ArrayList<Point> obstacleCenters = new ArrayList<>();
            for (ScanResult scanResult : this.sensorReadings)
                for (ScanResult.SingleScan scan : scanResult.scans())
                    obstacleCenters.addAll(this.processMeasurementToList(scan, null, scanResult.car()));

            return obstacleCenters;
        }
    }

    private ArrayList<Point> processMeasurementToList(ScanResult.SingleScan scan, @Nullable Canvas canvas, Car car) {
        this.mapParser = new MapParser(canvas, car);
        return this.mapParser.parseToList(scan.getDistanceA(), scan.getDistanceB(), scan.getAngle());
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

    public Bitmap getInstructionOverlay() {
        return this.instructionOverlay;
    }

    public Bitmap exportMap() {
        Bitmap bmp = this.rawMap.copy(Bitmap.Config.ARGB_8888, true);

        Paint p = new Paint();
        p.setAntiAlias(false);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setStrokeWidth(1.0f);
        p.setColor(Color.argb(0xFF, 0x71, 0xB9, 0x60));

        Canvas c = new Canvas(bmp);
        c.drawPoint(this.car.center().x, this.car.center().y, p);

        bmp.setConfig(Bitmap.Config.ARGB_4444);
        return bmp;
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

        for (int i = 0; i < 5; i++) {
            Bitmap bmp;
            switch (i) {
                case 0: bmp = this.rawMap; break;
                case 1: bmp = this.carOverlay; break;
                case 2: bmp = this.shadeOverlay; break;
                case 3: bmp = this.routeOverlay; break;
                case 4: bmp = this.instructionOverlay; break;
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
                case 4: this.instructionOverlay = combined; break;
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

    public boolean updateCarPosition(final Context ctx, Graph nw, PointF src, PointF dst) {
        if (this.processingSteeringInstructions) return false;
        final Node[] route =  FastFinder.findRoute(nw, new Node(src.x, src.y, -1), new Node(dst.x, dst.y, -1));
        if (route != null) {

            PointF[] stops = new PointF[route.length];
            for (int i = 0; i < route.length; i++) stops[i] = route[i].getLoc();

            this.visualizeRoute(stops);
            processingSteeringInstructions = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (btInterface != null) {
                        while (btInterface.busy()) try { Thread.sleep(500); }
                        catch (InterruptedException e) { e.printStackTrace(); }

                        for (int i = 0; i < route.length; i++) {
                            steeringCallbackReceived = false;

                            int[] directions = car.findPath(route[i].getLoc(), rawMap.getWidth(), rawMap.getHeight());
                            if (directions[0] != -1 && directions[1] != -1) {
                                byte deg = (byte) ((Math.abs(directions[0]) & 0x7F) | (directions[0] > 0 ? 0b10000000 : 0x00));
                                byte cm = (byte) ((Math.abs(directions[1]) & 0x7F) | (directions[1] < 0 ? 0b10000000 : 0x00));

                                btInterface.send(new Instruction(new byte[]{0x31, deg}, 3, BluetoothService.IDLE), true);
                                btInterface.send(new Instruction(new byte[]{0x41, cm}, 2, BluetoothService.AWAITING_STEERING_CALLBACK), true);

                                while (!steeringCallbackReceived) try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                setLastCar(getCar());
                                car.rotate(directions[0]);
                                drawCar();

                                setLastCar(getCar());
                                car.move(directions[1]);
                                drawCar();

                                if (mainActivity != null && drawCallback != null) try {
                                    drawCallback.invoke(mainActivity);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    processingSteeringInstructions = false;

                    /*
                    instructionOverlay = Bitmap.createBitmap(rawMap.getWidth(), rawMap.getHeight(), Bitmap.Config.ARGB_4444);
                    if (mainActivity != null && drawCallback != null) try {
                        drawCallback.invoke(mainActivity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } */
                }
            }).start();
            return true;
        }
        return false;
    }

    public boolean updateCarPosition(float x, float y, boolean move, boolean draw) {
        if (move) {
            if (this.processingSteeringInstructions) return false;
            PointF dest = new PointF(this.car.center().x +x, this.car.center().y +y);
            int[] directions = this.car.findPath(dest, this.rawMap.getWidth(), this.rawMap.getHeight());

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

    private void visualizeRoute(PointF[] stops) {
        this.instructionOverlay = Bitmap.createBitmap(
                this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);

        Path path = new Path();
        for (int i = 0; i < stops.length; i++) {
            if (i == 0) path.moveTo(stops[i].x, stops[i].y);
            else path.lineTo(stops[i].x, stops[i].y);
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(0xff, 0xab, 0x47, 0xcb));

        Canvas canvas = new Canvas(this.instructionOverlay);
        canvas.drawPath(path, paint);

        if (mainActivity != null && drawCallback != null) try { drawCallback.invoke(mainActivity); }
        catch (Exception e) { e.printStackTrace(); }
    }

    public void processScanResult(ScanResult scanResult) {
        this.processScanResult(scanResult, true);
    }

    private ArrayList<Point> processScanResult(ScanResult scanResult, boolean draw) {
        Canvas outCanvas = null, poiCanvas = null, mapCanvas = null;
        Bitmap out = null, poi = null;
        ArrayList<Point> obstacleCenters = new ArrayList<>();

        if (draw) {
            this.resize();
            this.concreteMap = null;
            if (scanResult.car() == null) {
                scanResult.setCar(this.car);
                this.setLastScanCar(this.car);
            }
            this.sensorReadings.add(scanResult);

            out = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);
            poi = Bitmap.createBitmap(this.rawMap.getWidth(), this.rawMap.getHeight(), Bitmap.Config.ARGB_4444);

            outCanvas = new Canvas(out);
            poiCanvas = new Canvas(poi);
            mapCanvas = new Canvas(this.rawMap);
        }


        if (draw) {
            for (ScanResult.SingleScan scan : scanResult.scans()) this.processMeasurement(scan, outCanvas, scanResult.car());
            for (ScanResult prevResult : this.sensorReadings) {
                if (Math.abs(scanResult.car().servo().x - prevResult.car().servo().x) <= SENSOR_MAX_DISTANCE ||
                        Math.abs(scanResult.car().servo().y - prevResult.car().servo().y) <= SENSOR_MAX_DISTANCE) {
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
        else for (ScanResult.SingleScan scan : scanResult.scans())
            obstacleCenters.addAll(this.processMeasurementToList(scan, outCanvas, scanResult.car()));

        return draw ? null : obstacleCenters;
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
package se.gu.dit524.group5.bluetoothremote.Mapping;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import se.gu.dit524.group5.bluetoothremote.ScanResult;

import static se.gu.dit524.group5.bluetoothremote.Mapping.Constants.SENSOR_MAX_DISTANCE;

/**
 * Created by mghan on 2017-03-31.
 * Modified by julian.bock on 2017-04-03.
 */
public class Map {
    private Bitmap image;
    private MapParser mapParser = new MapParser(this);
    private Point car = new Point(SENSOR_MAX_DISTANCE, SENSOR_MAX_DISTANCE);
    private Point lastCar = new Point(SENSOR_MAX_DISTANCE, SENSOR_MAX_DISTANCE);
    private Point lastScanCar = new Point(SENSOR_MAX_DISTANCE, SENSOR_MAX_DISTANCE);

    public Map() {
        this.image = Bitmap.createBitmap(
                2* SENSOR_MAX_DISTANCE,
                2* SENSOR_MAX_DISTANCE,
                Bitmap.Config.ARGB_4444);
        this.drawCar();
    }

    public Map(int width, int height){
        this.image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
    }

    public void processMeasurement(ScanResult.SingleScan scan) {
        if (this.car.x - SENSOR_MAX_DISTANCE < 0 ||
            this.car.y - SENSOR_MAX_DISTANCE < 0 ||
            this.car.x + SENSOR_MAX_DISTANCE > this.getImage().getWidth() ||
            this.car.y + SENSOR_MAX_DISTANCE > this.getImage().getHeight()) resize();
        this.mapParser.parse(scan.getDistanceA(), scan.getDistanceB(), -scan.getAngle());
    }

    public void removeCollidingObstacles(ScanResult.SingleScan scan) {
        this.mapParser.clean(scan.getDistanceA(), scan.getDistanceB(), -scan.getAngle());
    }

    public Bitmap getImage(){
        return this.image;
    }

    private static final int NO_RESIZE = 0;
    private static final int ADVANCE_LEFT= 1;
    private static final int ADVANCE_RIGHT = 2;
    private static final int ADVANCE_TOP = 4;
    private static final int ADVANCE_BOTTOM = 8;

    public void resize(){
        int rFlag = NO_RESIZE;
        if (this.car.x +SENSOR_MAX_DISTANCE >= this.getImage().getWidth()) rFlag += ADVANCE_RIGHT;
        if (this.car.y +SENSOR_MAX_DISTANCE >= this.getImage().getHeight()) rFlag += ADVANCE_BOTTOM;
        if (this.car.x - SENSOR_MAX_DISTANCE < 0) rFlag += ADVANCE_LEFT;
        if (this.car.y - SENSOR_MAX_DISTANCE < 0) rFlag += ADVANCE_TOP;
        if (rFlag == NO_RESIZE) return;

        Bitmap combined = Bitmap.createBitmap(
                this.image.getWidth() +(((rFlag &ADVANCE_LEFT) >= 1 || (rFlag &ADVANCE_RIGHT) >= 1) ?
                        Math.abs(SENSOR_MAX_DISTANCE -car.x) : 0),
                this.image.getHeight() +(((rFlag &ADVANCE_TOP) >= 1 || (rFlag &ADVANCE_BOTTOM) >= 1) ?
                        Math.abs(SENSOR_MAX_DISTANCE -car.y) : 0),
                Bitmap.Config.ARGB_4444);

        int[] pixels = new int[this.getImage().getWidth() *this.getImage().getHeight()];
        this.getImage().getPixels(pixels, 0,
                this.getImage().getWidth(), 0, 0,
                this.getImage().getWidth(),
                this.getImage().getHeight());

        combined.setPixels(pixels, 0,
                this.getImage().getWidth(),
                (rFlag &ADVANCE_LEFT) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -car.x) : 0,
                (rFlag &ADVANCE_TOP) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -car.y) : 0,
                this.getImage().getWidth(),
                this.getImage().getHeight());

        updateCarPosition(
                (rFlag &ADVANCE_LEFT) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -car.x) : 0,
                (rFlag &ADVANCE_TOP) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -car.y) : 0, false);
        updateLastCarPosition(
                (rFlag &ADVANCE_LEFT) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -lastCar.x) : 0,
                (rFlag &ADVANCE_TOP) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -lastCar.y) : 0);
        updateLastScanCarPosition(
                (rFlag &ADVANCE_LEFT) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -lastScanCar.x) : 0,
                (rFlag &ADVANCE_TOP) >= 1 ? Math.abs(SENSOR_MAX_DISTANCE -lastScanCar.y) : 0);

        this.image = combined;
    }

    public Point getCar() {
        return car;
    }

    public void setCar(Point car) {
        this.car = new Point(car.x, car.y);
    }

    public void updateCarPosition(int x, int y, boolean draw){
        this.car.x += x;
        this.car.y += y;
        if (draw) this.drawCar();
    }

    public void drawCar() {
        Canvas c = new Canvas(this.getImage());
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.WHITE);
        c.drawCircle(lastCar.x, lastCar.y, 9, p);

        p.setColor(Color.argb(155, 0xC6, 0x45, 0x6F));
        c.drawCircle(lastCar.x, lastCar.y, 6, p);

        p.setStrokeWidth(3.0f);
        c.drawLine(lastCar.x, lastCar.y, car.x, car.y, p);

        p.setAlpha(255);
        c.drawCircle(car.x, car.y, 8, p);
    }

    public Point getLastScanCar() {
        return lastScanCar;
    }

    public void setLastScanCar(Point lastScanCar) {
        this.lastScanCar = new Point(lastScanCar.x, lastScanCar.y);
    }

    public void updateLastScanCarPosition(int x, int y){
        this.lastScanCar.x += x;
        this.lastScanCar.y += y;
    }

    public Point getLastCar() {
        return lastCar;
    }

    public void setLastCar(Point lastCar) {
        this.lastCar = new Point(lastCar.x, lastCar.y);
    }

    public void updateLastCarPosition(int x, int y) {
        this.lastCar.x += x;
        this.lastCar.y += y;
    }
}
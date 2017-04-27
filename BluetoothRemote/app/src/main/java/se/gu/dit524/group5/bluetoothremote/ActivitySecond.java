package se.gu.dit524.group5.bluetoothremote;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import se.gu.dit524.group5.bluetoothremote.Mapping.Map;

import static se.gu.dit524.group5.bluetoothremote.Mapping.Constants.SERVO_TURN_DEGREES;

public class ActivitySecond extends AppCompatActivity {

    private ArrayList<ScanResult> previousReadings;
    private Button connect, scan, reset;
    private ToggleButton movement;
    private Map map;
    private ImageView mapView;
    private BluetoothService btService;
    private SeekBar throttleBar, angleBar;
    private Point lastSteeringDirection;
    private TextView posView, targetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        map = new Map();
        previousReadings = new ArrayList<>();

        posView = (TextView) this.findViewById(R.id.carPosText);
        targetView = (TextView) this.findViewById(R.id.targetPosText);

        reset = (Button) this.findViewById(R.id.resetButton);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSteeringDirection = null;
                map = new Map();
                redrawMap();
            }
        });

        movement = (ToggleButton) this.findViewById(R.id.movementToggle);
        movement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movement.isChecked()) {
                    reset.setVisibility(View.VISIBLE);
                    mapView.setVisibility(View.VISIBLE);
                    posView.setVisibility(View.VISIBLE);
                    throttleBar.setFocusable(false);
                    angleBar.setFocusable(false);
                    throttleBar.setVisibility(View.INVISIBLE);
                    angleBar.setVisibility(View.INVISIBLE);
                }
                else {
                    reset.setVisibility(View.INVISIBLE);
                    mapView.setVisibility(View.INVISIBLE);
                    posView.setVisibility(View.INVISIBLE);
                    throttleBar.setFocusable(true);
                    angleBar.setFocusable(true);
                    throttleBar.setVisibility(View.VISIBLE);
                    angleBar.setVisibility(View.VISIBLE);
                }
            }
        });

        mapView = (ImageView)this.findViewById(R.id.mapView);
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mapView.getVisibility() == View.INVISIBLE) return false;

                float mvWidth = mapView.getMeasuredWidth();
                float widthCR = mvWidth /map.getImage().getWidth();
                float mvHeight = mapView.getMeasuredHeight();
                float heightCR = mvHeight /map.getImage().getHeight();

                float x = event.getX();
                if (x < 0) x = 0;
                else if (x >= mapView.getMeasuredWidth()) x = mapView.getMeasuredWidth();
                float y = event.getY();
                if (y < 0) y = 0;
                else if (y >= mapView.getMeasuredHeight()) y = mapView.getMeasuredHeight();

                Point dest = new Point((int)(x /widthCR), (int)(y /heightCR));

                targetView.setVisibility(View.VISIBLE);
                updateTargetView(dest);

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    targetView.setVisibility(View.INVISIBLE);

                    lastSteeringDirection = new Point(dest.x -map.getCar().x, dest.y -map.getCar().y);
                    byte bx = (byte)(Math.abs(lastSteeringDirection.x) &0x7F);
                    bx = (byte)(lastSteeringDirection.x >= 0 ? bx : (bx |0b10000000));
                    byte by = (byte)(Math.abs(lastSteeringDirection.y) &0x7F);
                    by = (byte)(lastSteeringDirection.y >= 0 ? by : (by |0b10000000));

                    if (btService != null && !btService.awaitingSteeringCallback) {
                        btService.awaitingSteeringCallback = true;
                        btService.send(new Instruction(new byte[]{0x22, bx, by}, 0));

                        // System.out.println("X: " +(((bx & 0x80) >= 1) ? "-" : "+") +(bx & 0x7F));
                        // System.out.println("Y: " +(((by & 0x80) >= 1) ? "-" : "+") +(by & 0x7F));
                    }

                    // Is MARBLE out of order? Use the snippet below.
                    /*
                    lastSteeringDirection = new Point(dest.x -map.getCar().x, dest.y -map.getCar().y);
                    updateCarPosition(); */
                }
                return true;
            }
        });
        redrawMap();

        connect = (Button)this.findViewById(R.id.connectButton);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btService = new BluetoothService();
                try {
                    btService.mainActivity = ActivitySecond.this;
                    btService.scanCallback = ActivitySecond.this.getClass()
                            .getMethod("updateMap", new Class[]{ ScanResult.class });
                    btService.automaticSteeringCallback = ActivitySecond.this.getClass()
                            .getMethod("updateCarPosition", new Class[]{ });}
                catch (Exception e){ e.printStackTrace(); }
            }
        });

        scan = (Button)this.findViewById(R.id.scanButton);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btService != null) btService.send(new Instruction(new byte[]{ (byte)0xF1, SERVO_TURN_DEGREES }, -1000));

                // Is MARBLE out of order? Use the snippet below.
                /*
                byte[] fakeResults = new byte[180 /SERVO_TURN_DEGREES *3];
                Random rnd = new Random();
                int pos = 0;
                while (pos < fakeResults.length /3) {
                    fakeResults[pos *3 +0] = (byte)(pos *SERVO_TURN_DEGREES);
                    fakeResults[pos *3 +1] = (byte)(SENSOR_MAX_DISTANCE /2 +rnd.nextInt(SENSOR_MAX_DISTANCE /2));
                    fakeResults[pos *3 +2] = (byte)(SENSOR_MAX_DISTANCE /2 +rnd.nextInt(SENSOR_MAX_DISTANCE /2));
                    pos++;
                }
                updateMap(new ScanResult(fakeResults, 0, fakeResults.length)); */
            }
        });

        throttleBar = (SeekBar)this.findViewById(R.id.throttleBar);
        angleBar = (SeekBar)this.findViewById(R.id.angleBar);
        angleBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (angleBar.getVisibility() == View.INVISIBLE || throttleBar.getVisibility() == View.INVISIBLE) return false;

                byte speed = 0x00, angle = 0x00;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    throttleBar.setProgress(throttleBar.getMax() /2);
                    angleBar.setProgress((angleBar.getMax() /2));
                    if (btService != null) btService.send(new Instruction(new byte[]{0x12, speed, angle}, 1), true);
                }
                else {

                    int x = (int) event.getAxisValue(0), y = (int) event.getAxisValue(1);
                    int maxX = angleBar.getMeasuredWidth(), maxY = angleBar.getMeasuredHeight();

                    if (x <= 0) angleBar.setProgress(angleBar.getMax());
                    else if (x >= maxX) angleBar.setProgress(0);
                    else angleBar.setProgress(angleBar.getMax() - x / (maxX / angleBar.getMax()));

                    if (y <= 0) throttleBar.setProgress(throttleBar.getMax());
                    else if (y >= maxY) throttleBar.setProgress(0);
                    else throttleBar.setProgress(throttleBar.getMax() - y / (maxY / throttleBar.getMax()));

                    if (throttleBar.getProgress() < throttleBar.getMax() / 2)
                        speed = (byte) (1 << 7);
                    speed += Math.abs(throttleBar.getProgress() - throttleBar.getMax() / 2);

                    if (angleBar.getProgress() > angleBar.getMax() / 2) angle = (byte) (1 << 7);
                    angle += Math.abs(angleBar.getProgress() - angleBar.getMax() / 2);

                    if (btService != null) btService.send(new Instruction(new byte[]{0x12, speed, angle}, 0));
                }

                // System.out.println("speed: " +(((speed & 0x80) >= 1) ? "-" : "+") +(speed & 0x7F));
                // System.out.println("angle: " +(((angle & 0x80) >= 1) ? "-" : "+") +(angle & 0x7F));

                return true;
            }
        });
    }

    public void updateMap(ScanResult scanResult) {
        scanResult.carOffset = new Point(
                this.map.getCar().x - this.map.getLastScanCar().x,
                this.map.getCar().y - this.map.getLastScanCar().y);
        this.map.setLastScanCar(this.map.getCar());

        for (ScanResult.SingleScan scan : scanResult.scans) this.map.processMeasurement(scan);
        previousReadings.add(scanResult);

        this.map.setCar(this.map.getLastScanCar());
        for (ScanResult.SingleScan scan : scanResult.scans) this.map.removeCollidingObstacles(scan);
        redrawMap();
    }

    public void updateCarPosition() {
        this.map.setLastCar(this.map.getCar());
        this.map.updateCarPosition(this.lastSteeringDirection.x, this.lastSteeringDirection.y, true);
        this.lastSteeringDirection = null;
        redrawMap();
    }

    private void redrawMap() {
        Handler handler = new Handler(getBaseContext().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mapView.setImageBitmap(map.getImage());
                updatePosView(map.getCar());
            }
        });
    }

    public void updatePosView(final Point car) {
        Handler handler = new Handler(getBaseContext().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                posView.setText("MARBLE: <" +car.x +", " +car.y +">");
            }
        });
    }

    public void updateTargetView(final Point target) {
        Handler handler = new Handler(getBaseContext().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                targetView.setText("DSTN: <" +target.x +", " +target.y +">");
            }
        });
    }


}

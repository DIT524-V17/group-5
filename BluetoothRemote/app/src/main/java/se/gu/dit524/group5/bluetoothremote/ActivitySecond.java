package se.gu.dit524.group5.bluetoothremote;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
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

import com.vividsolutions.jts.geom.Coordinate;

import java.util.Random;

import se.gu.dit524.group5.bluetoothremote.Dijkstra.FastFinder;
import se.gu.dit524.group5.bluetoothremote.Mapping.Map;
import se.gu.dit524.group5.bluetoothremote.Voronoi.Node;
import se.gu.dit524.group5.bluetoothremote.Voronoi.Voronoi;

import static se.gu.dit524.group5.bluetoothremote.Mapping.Constants.*;

public class ActivitySecond extends AppCompatActivity {

    private Button connect, scan, reset;
    private Map map;
    private Voronoi voronoi;
    private ImageView mapView;
    private BluetoothService btService;
    private ToggleButton movement, shadeToggle, routeToggle, concreteToggle, voronoiToggle;
    private SeekBar throttleBar, angleBar, obstacleThreshold;
    private PointF lastSteeringDirection;
    private TextView posView, targetView, listOfMaps, saveMap;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        map = new Map();
        posView = (TextView) this.findViewById(R.id.carPosText);
        targetView = (TextView) this.findViewById(R.id.targetPosText);

        reset = (Button) this.findViewById(R.id.resetButton);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSteeringDirection = null;
                map = new Map(btService);
                redrawMap();

                /* VORO-ROUTE-NOI-THING-STUFF...
                 *
                System.out.println("GENERATING...");
                Voronoi voronoi = new Voronoi(SENSOR_MAX_DISTANCE *20, SENSOR_MAX_DISTANCE *20);
                Random rnd = new Random();
                for (int i = 0; i < 10000; i++)
                    voronoi.addSite(
                            new Coordinate(
                            rnd.nextInt(SENSOR_MAX_DISTANCE *20),
                            rnd.nextInt(SENSOR_MAX_DISTANCE *20)));

                Bitmap voronoiBitmap = voronoi.createVoronoi();
                voronoi.extractVoronoiToGraph();
                voronoi.drawNodesAndEdges(new Canvas(voronoiBitmap));

                mapView.setImageBitmap(voronoiBitmap);

                System.out.println("SEARCHING...");
                int src = rnd.nextInt(voronoi.voronoiGraph.getNodes().size());
                int dst = rnd.nextInt(voronoi.voronoiGraph.getNodes().size());
                Node[] route =
                        FastFinder.findRoute(
                            voronoi.voronoiGraph,
                            voronoi.voronoiGraph.getNodes().get(src),
                            voronoi.voronoiGraph.getNodes().get(dst));

                for (int i = 0; i < route.length; i++) {
                    if (i == 0) System.out.println("FROM " +src +" TO " +route[0].id());
                    else System.out.println("FROM " +route[i -1].id() +" TO " +route[i].id());
                    if (i == route.length -1 && route[i].id() == dst)
                        System.out.println("YOU'VE REACHED YOUR DESTINATION.");
                } */
            }
        });

        saveMap = (TextView) this.findViewById(R.id.saveMap);
        listOfMaps = (TextView) this.findViewById(R.id.listOfMaps);

        shadeToggle = (ToggleButton) this.findViewById(R.id.shadeToggle);
        routeToggle = (ToggleButton) this.findViewById(R.id.routeToggle);
        concreteToggle = (ToggleButton) this.findViewById(R.id.concreteMapToggle);
        voronoiToggle = (ToggleButton) this.findViewById(R.id.voronoiInputToggle);

        View.OnClickListener mapToggleOCL = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.equals(concreteToggle)) {
                    if (concreteToggle.isChecked()) obstacleThreshold.setVisibility(View.VISIBLE);
                    else obstacleThreshold.setVisibility(View.INVISIBLE);
                    redrawMap();
                }
                else if (voronoiToggle.isChecked()) {
                    if (voronoi == null) {
                        voronoi = new Voronoi(map.getMap().getWidth(), map.getMap().getHeight());
                    }
                }
                else redrawMap();
            }
        };
        shadeToggle.setOnClickListener(mapToggleOCL);
        routeToggle.setOnClickListener(mapToggleOCL);
        concreteToggle.setOnClickListener(mapToggleOCL);
        voronoiToggle.setOnClickListener(mapToggleOCL);

        obstacleThreshold = (SeekBar) this.findViewById(R.id.obsThreshold);
        obstacleThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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
                    shadeToggle.setVisibility(View.VISIBLE);
                    routeToggle.setVisibility(View.VISIBLE);
                    concreteToggle.setVisibility(View.VISIBLE);
                    voronoiToggle.setVisibility(View.VISIBLE);
                    listOfMaps.setVisibility(View.VISIBLE);
                    saveMap.setVisibility(View.VISIBLE);

                }
                else {
                    reset.setVisibility(View.INVISIBLE);
                    mapView.setVisibility(View.INVISIBLE);
                    posView.setVisibility(View.INVISIBLE);
                    throttleBar.setFocusable(true);
                    angleBar.setFocusable(true);
                    throttleBar.setVisibility(View.VISIBLE);
                    angleBar.setVisibility(View.VISIBLE);
                    shadeToggle.setVisibility(View.INVISIBLE);
                    routeToggle.setVisibility(View.INVISIBLE);
                    concreteToggle.setVisibility(View.INVISIBLE);
                    voronoiToggle.setVisibility(View.INVISIBLE);
                    obstacleThreshold.setVisibility(View.INVISIBLE);
                    listOfMaps.setVisibility(View.INVISIBLE);
                    saveMap.setVisibility(View.INVISIBLE);
                }
            }
        });

        mapView = (ImageView)this.findViewById(R.id.mapView);
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mapView.getVisibility() == View.INVISIBLE) return false;
                else {
                    float mvWidth = mapView.getMeasuredWidth();
                    float widthCR = mvWidth / map.getMap().getWidth();
                    float mvHeight = mapView.getMeasuredHeight();
                    float heightCR = mvHeight / map.getMap().getHeight();

                    float x = event.getX();
                    if (x < 0) x = 0;
                    else if (x >= mapView.getMeasuredWidth()) x = mapView.getMeasuredWidth();
                    float y = event.getY();
                    if (y < 0) y = 0;
                    else if (y >= mapView.getMeasuredHeight()) y = mapView.getMeasuredHeight();

                    PointF dest = new PointF(x / widthCR, y / heightCR);

                    if (!voronoiToggle.isChecked()) {
                        targetView.setVisibility(View.VISIBLE);
                        updateTargetView(dest);

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            targetView.setVisibility(View.INVISIBLE);

                            lastSteeringDirection = new PointF(dest.x - map.getCar().center().x, dest.y - map.getCar().center().y);
                            if (map.updateCarPosition(lastSteeringDirection.x, lastSteeringDirection.y, true, true)) {
                                updateCarPosition();
                            }
                        }
                    }
                    else {
                        voronoi.addSite(new Coordinate(dest.x, dest.y));
                    }
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
                            .getMethod("updateCarPosition", new Class[]{ });
                    map.setBluetoothInterface(btService);
                }
                catch (Exception e){ e.printStackTrace(); }
            }
        });

        scan = (Button)this.findViewById(R.id.scanButton);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btService != null && !btService.busy())
                    btService.send(new Instruction(new byte[]{ (byte)0xF1, SERVO_TURN_DEGREES }, 1, BluetoothService.SCANNING), true);

                // Is MARBLE out of order? Use the snippet below.
                /*
                byte[] fakeResults = new byte[180 /SERVO_TURN_DEGREES *3];
                Random rnd = new Random();
                int pos = 0;
                while (pos < fakeResults.length /3) {
                    fakeResults[pos *3 +0] = (byte)(pos *SERVO_TURN_DEGREES);
                    fakeResults[pos *3 +1] = (byte)(rnd.nextInt(SENSOR_MAX_DISTANCE));
                    fakeResults[pos *3 +2] = (byte)(rnd.nextInt(SENSOR_MAX_DISTANCE));
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
                    if (btService != null) btService.send(new Instruction(new byte[]{0x12, speed, angle}), true);
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

                    if (btService != null) btService.send(new Instruction(new byte[]{0x12, speed, angle}));
                }

                // System.out.println("speed: " +(((speed & 0x80) >= 1) ? "-" : "+") +(speed & 0x7F));
                // System.out.println("angle: " +(((angle & 0x80) >= 1) ? "-" : "+") +(angle & 0x7F));

                return true;
            }
        });
    }

    public void updateMap(ScanResult scanResult) {
        this.map.setLastScanCar(this.map.getCar());
        this.map.processScanResult(scanResult);
        redrawMap();
    }

    public void updateCarPosition() {
        // TODO: think about blocking any further map interaction until MARBLE is done.
        this.lastSteeringDirection = null;
        redrawMap();
    }

    private void redrawMap() {
        Handler handler = new Handler(getBaseContext().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp;

                if (!concreteToggle.isChecked()) bmp = Bitmap.createBitmap(map.getMap());
                else bmp = Bitmap.createBitmap(map.generateConcreteMap(obstacleThreshold.getProgress()));

                Canvas c = new Canvas(bmp);

                if (routeToggle.isChecked()) c.drawBitmap(map.getRouteOverlay(), 0, 0, null);
                if (shadeToggle.isChecked()) c.drawBitmap(map.getShadeOverlay(), 0, 0, null);
                c.drawBitmap(map.getCarOverlay(), 0, 0, null);

                mapView.setImageBitmap(bmp);
                updatePosView(map.getCar().center());
            }
        });
    }

    public void updatePosView(final PointF car) {
        Handler handler = new Handler(getBaseContext().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                posView.setText("MARBLE: <" +car.x +", " +car.y +">");
            }
        });
    }

    public void updateTargetView(final PointF target) {
        Handler handler = new Handler(getBaseContext().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                targetView.setText("DESTINATION: <" +target.x +", " +target.y +">");
            }
        });
    }

    public void sendMessageToMaps(View view){
        Intent intent = new Intent(this, ActivityThird.class);
        this.startActivity(intent);
    }

    public void saveMap(View view) {
        System.out.println("THIS FUNCTION IS YET TO BE IMPLEMENTED.");
    }
}

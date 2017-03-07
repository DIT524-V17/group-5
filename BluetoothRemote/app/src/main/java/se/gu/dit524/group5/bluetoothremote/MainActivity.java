package se.gu.dit524.group5.bluetoothremote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity  {

    private Button connect;
    private BluetoothService btService;
    private SeekBar throttleBar, angleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect = (Button)this.findViewById(R.id.connectButton);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btService = new BluetoothService();
            }
        });

        throttleBar = (SeekBar)this.findViewById(R.id.throttleBar);
        angleBar = (SeekBar)this.findViewById(R.id.angleBar);

        angleBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getAxisValue(0), y = (int) event.getAxisValue(1);
                int maxX = angleBar.getMeasuredWidth(), maxY = angleBar.getMeasuredHeight();

                if (x <= 0) angleBar.setProgress(angleBar.getMax());
                else if (x >= maxX) angleBar.setProgress(0);
                else angleBar.setProgress(angleBar.getMax() - x /(maxX /angleBar.getMax()));

                if (y <= 0) throttleBar.setProgress(throttleBar.getMax());
                else if (y >= maxY) throttleBar.setProgress(0);
                else throttleBar.setProgress(throttleBar.getMax() - y /(maxY /throttleBar.getMax()));

                byte speed = 0;
                if (throttleBar.getProgress() < throttleBar.getMax() /2) speed = (byte)(1 << 7);
                speed += Math.abs(throttleBar.getProgress() -throttleBar.getMax() /2);

                byte angle = 0;
                if (angleBar.getProgress() > angleBar.getMax() /2) angle = (byte)(1 << 7);
                angle += Math.abs(angleBar.getProgress() -angleBar.getMax() /2);

                // System.out.println("speed: " +(((speed & 0x80) >= 1) ? "-" : "+") +(speed & 0x7F));
                // System.out.println("angle: " +(((angle & 0x80) >= 1) ? "-" : "+") +(angle & 0x7F));

                btService.prepareConnection();
                if (btService.connectionEstablished()) {
                    btService.connectedThread.write(new byte[]{ 0x12, speed, angle });
                }
                return true;
            }
        });
    }
}

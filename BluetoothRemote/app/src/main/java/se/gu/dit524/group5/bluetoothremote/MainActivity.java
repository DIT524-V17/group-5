package se.gu.dit524.group5.bluetoothremote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity  {

    private MyBluetoothService btService;
    private Button connect;
    private Button w, a, s, d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect = (Button)this.findViewById(R.id.connectButton);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btService = new MyBluetoothService();
            }
        });

        View.OnTouchListener buttonListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) { buttonClicked(v.getId()); return true; }
                else if (event.getAction() == MotionEvent.ACTION_UP) { buttonClicked(-1); return true; }
                return false;
            }
        };

        w = (Button)this.findViewById(R.id.wButton);
        w.setOnTouchListener(buttonListener);
        a = (Button)this.findViewById(R.id.aButton);
        a.setOnTouchListener(buttonListener);
        s = (Button)this.findViewById(R.id.sButton);
        s.setOnTouchListener(buttonListener);
        d = (Button)this.findViewById(R.id.dButton);
        d.setOnTouchListener(buttonListener);
    }

    private void buttonClicked(int srcId) {
        btService.prepareConnection();
        if (btService.connectionEstablished()) {
            switch (srcId) {
                case R.id.wButton: btService.connectedThread.write(new byte[]{ 'w' } ); break;
                case R.id.aButton: btService.connectedThread.write(new byte[]{ 'a' } ); break;
                case R.id.sButton: btService.connectedThread.write(new byte[]{ 's' } ); break;
                case R.id.dButton: btService.connectedThread.write(new byte[]{ 'd' } ); break;
                default: btService.connectedThread.write(new byte[]{ 'x' } );
            }
        }
    }

    /*
        drive forward       0-100 percent
        drive backwards     0-100 percent
        turn left           0-90 degrees
        turn right          0-90 degrees

        7   6   5   4   3   2   1   0
        128 64  32  16  8   4   2   1

     */

}

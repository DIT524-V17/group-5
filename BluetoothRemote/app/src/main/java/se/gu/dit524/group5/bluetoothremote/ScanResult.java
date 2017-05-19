package se.gu.dit524.group5.bluetoothremote;

import android.graphics.Point;
import android.graphics.PointF;

import java.util.ArrayList;

/**
 * Created by julian.bock on 2017-03-31.
 */

public class ScanResult {
    ArrayList<SingleScan> scans;
    PointF offsetToPreviousScan;

    public ScanResult(byte[] data, int offset, int length) {
        if (offset < 0 || offset +length > data.length) return;
        scans = new ArrayList<>();
        for (int i = offset; i < offset +length; i += 3) {
            scans.add(new SingleScan(data[i], data[i+1], data[i+2]));
        }
    }

    public class SingleScan {
        int angle;
        int distanceA;
        int distanceB;

        public SingleScan(byte angle, byte distanceA, byte distanceB) {
            this.angle = angle & 0xFF;
            this.distanceA = distanceA & 0xFF;
            this.distanceB = distanceB & 0xFF;
        }

        public int getAngle() {
            return angle;
        }

        public int getDistanceA() {
            return distanceA;
        }

        public int getDistanceB() {
            return distanceB;
        }
    }
}

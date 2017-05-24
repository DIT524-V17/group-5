package se.gu.dit524.group5.bluetoothremote;

import java.util.ArrayList;

import se.gu.dit524.group5.bluetoothremote.Mapping.Car;

/**
 * Created by julian.bock on 2017-03-31.
 */

public class ScanResult {
    private ArrayList<SingleScan> scans;
    private Car car;

    public ScanResult(byte[] data, int offset, int length) {
        if (offset < 0 || offset +length > data.length) return;
        scans = new ArrayList<>();
        for (int i = offset; i < offset +length; i += 3) {
            scans.add(new SingleScan(data[i], data[i+1], data[i+2]));
        }
    }

    public void setCar(Car car) {
        this.car = new Car(car);
    }

    public Car car() {
        return this.car;
    }

    public ArrayList<SingleScan> scans() {
        return this.scans;
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

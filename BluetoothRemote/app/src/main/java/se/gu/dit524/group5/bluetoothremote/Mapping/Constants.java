package se.gu.dit524.group5.bluetoothremote.Mapping;

/**
 * Created by mghan on 2017-03-31.
 * Modified by julian.bock on 2017-05-08 (and following).
 */
public class Constants {

    public static final int SERVO_TURN_DEGREES = 4;
    public static final int SENSOR_MAX_DISTANCE = 150;

    public static final float CAR_WIDTH =           15.5f;
    public static final float CAR_HEIGHT =          26.5f;
    public static final float CUPHOLDER_WIDTH =     9.25f;
    public static final float CUPHOLDER_HEIGHT =    7.5f;
    public static final float WHEEL_WIDTH =         2.5f;
    public static final float WHEEL_HEIGHT =        6.5f;
    public static final float WHEEL_FRONT_OFFSET =  5.25f;
    public static final float WHEEL_REAR_OFFSET =   2.0f;
    public static final float CUPHOLDER_OFFSET =    (CAR_WIDTH -CUPHOLDER_WIDTH) /2;

    public static final int OBSTACLE_INTENSITY =    0xFF;
    public static final int FREE_SPACE_INTENSITY =  0x18;
}

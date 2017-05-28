#include <Smartcar.h>

const int IR_MIDDLE_PIN = A7;
const int MIN_SPEED = 35;
const int MIN_DISTANCE = 20;
const int MIN_OBSTACLE_DETECTIONS = 2;
const int US_READINGS = 5;
const int STATE_DEFAULT = 0;
const int STATE_AUTOMATIC_STEERING = 2;

Odometer encoderLeft, encoderRight;
Gyroscope gyro(-6);
Car car;
GP2Y0A21 middle;

int state = STATE_DEFAULT;
int middleDistance;
int obstacleDetections = 0;

void setup(){
    gyro.attach();
    gyro.begin(50);
    encoderLeft.attach(2);
    encoderRight.attach(3);
    encoderLeft.begin();
    encoderRight.begin();
    car.begin(encoderLeft, encoderRight, gyro);
    middle.attach(IR_MIDDLE_PIN);
    Serial.begin(9600);
    Serial3.begin(9600);
}

void loop(){
  
  /* SENSORS, ensure we're not crashing into something  */
  middleDistance = middle.getDistance();
  if (middleDistance <= MIN_DISTANCE && middleDistance != 0) {
    obstacleDetections++;
  } else obstacleDetections = 0;
}

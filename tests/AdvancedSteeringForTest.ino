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
     /* BLUETOOTH, ensukre proper transmissions */
  byte ins, cmd, len, crc;
  byte data[16];
  bool cmdReceived = false;
  
  if (Serial3.available()) {
    ins = Serial3.peek();
    cmd = (ins & 0xF0) >> 4;
    len = ins & 0x0F;
    Serial3.readBytes(data, len +2);
    crc = crc8(data, 0, len +1);
    
    Serial3.write(crc);
    if (crc == data[len +1]) cmdReceived = true;
  }

    middleDistance = middle.getDistance();
  if (middleDistance <= MIN_DISTANCE && middleDistance != 0) {
    obstacleDetections++;
  } else obstacleDetections = 0;

  /* INSTRUCTION PROCESSING, make sure to do whatever we're supposed to do */
  if (cmdReceived) {
    switch (cmd) {
      case 1:    /* MANUAL STEERING INSTRUCTION */  
        if ((data[1] & B10000000) == 0) {
          if (obstacleDetections < MIN_OBSTACLE_DETECTIONS) {
            if ((data[1] & B01111111) > MIN_SPEED) car.setSpeed(data[1] & B01111111);
            else car.setSpeed(0);
          } else car.setSpeed(0);
        } else car.setSpeed(-(data[1] & B01111111));
        if ((data[2] & B10000000) == 0) car.setAngle(data[2] & B01111111);
        else car.setAngle(-(data[2] & B01111111)); 
        break;
      case 2:    /* SEMI-AUTOMATIC STEERING INSTRUCTION */
        state = STATE_AUTOMATIC_STEERING;
        int x, y;
        double hyp, angle;
        x = (data[1] & B01111111);
        y = (data[2] & B01111111);
        hyp = sqrt(x*x + y*y);
        angle = 90 -(acos(x /hyp) *4068) /71;
        if ((data[1] & B10000000) == 0 && (data[2] & B10000000) >= 1) {
          car.rotate((int) angle);        /* X, -Y */
          car.go((int) hyp);
        }
        else if ((data[1] & B10000000) >= 1 && (data[2] & B10000000) >= 1) {
          car.rotate((int) -angle);       /* -X, -Y */
          car.go((int) hyp);
        }
        else if ((data[1] & B10000000) >= 1 && (data[2] & B10000000) == 0) {
          car.rotate((int) angle);        /* -X, Y */
          car.go((int) -hyp);
        }
        else if ((data[1] & B10000000) == 0 && (data[2] & B10000000) == 0) {
          car.rotate((int) -angle);       /* X, Y */
          car.go((int) -hyp);
        }
        Serial3.write(0x2F);
        state = STATE_DEFAULT;
        break;
      case 15:   /* MAPPING/SCANNING INSTRUCTION */
        state = STATE_MAPPING;
        //SERVO_TURN = data[1];
        //SERVO_TURN_PAUSE = SERVO_TURN *5;
        car.setSpeed(0);
        car.setAngle(0);
        break;
    }
  }
}

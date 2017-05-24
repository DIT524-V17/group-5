#include <Smartcar.h>
#include <Servo.h>
#include <TemperatureSensor.h>

const int IR_MIDDLE_PIN = A7;
const int TEMP_HUM_PIN = 26;
const int SERVO_PIN = 24;
const int US1_TRIGGER = 30;
const int US1_ECHO = 31;
const int US2_TRIGGER = 32;
const int US2_ECHO = 33;
const int SERVO_POWER = 50;

const int MIN_SPEED = 35;
const int MIN_DISTANCE = 20;
const int MIN_OBSTACLE_DETECTIONS = 2;
const int SERVO_DEFAULT = 0;
      int SERVO_TURN = 4;
      int SERVO_TURN_PAUSE = SERVO_TURN *10;
const int SERVO_FULL_TURN_PAUSE = 880;
const int SERVO_READING_DISTANCE = 150;
const int US_READINGS = 5;
const int STATE_DEFAULT = 0;
const int STATE_AUTOMATIC_STEERING = 2;
const int STATE_MAPPING = 15;

Odometer encoderLeft, encoderRight;
Gyroscope gyro(-6);
Car car;
Servo servo;
SR04 us1(SERVO_READING_DISTANCE), us2(SERVO_READING_DISTANCE);
GP2Y0A21 middle;
TemperatureSensor ambient(TEMP_HUM_PIN);

int state = STATE_DEFAULT;
int servo_deg = SERVO_DEFAULT;
int middleDistance;
int obstacleDetections = 0;

void setup() {
  pinMode(SERVO_POWER, OUTPUT);
  gyro.attach();
  gyro.begin(50);
  encoderLeft.attach(2);
  encoderRight.attach(3);
  encoderLeft.begin();
  encoderRight.begin();
  car.begin(encoderLeft, encoderRight, gyro);
  servo.attach(SERVO_PIN);
  servo.write(SERVO_DEFAULT);
  us1.attach(US1_TRIGGER, US1_ECHO);
  us2.attach(US2_TRIGGER, US2_ECHO);
  middle.attach(IR_MIDDLE_PIN);
  delay(SERVO_FULL_TURN_PAUSE);
  digitalWrite(SERVO_POWER, HIGH);
  Serial.begin(9600);
  Serial3.begin(9600);
}

void loop() { 
  /* MAPPING/SCANNING state, blocks all other instruction processing until done */
  if (state == STATE_MAPPING) {
    digitalWrite(SERVO_POWER, LOW);
    byte results[(180 /SERVO_TURN +1) *3 +4];
    int pos = 1;
    while (servo_deg <= 180) {
      ambient.update();
      int d1 = 0, d2 = 0;
      for (int i = 0; i < US_READINGS; i++) {
        d1 += us1.getDistance(/*ambient.temperature(), ambient.humidity()*/);
        d2 += us2.getDistance(/*ambient.temperature(), ambient.humidity()*/);
      }
      results[pos *3 +0] = servo_deg;
      results[pos *3 +1] = d1 /US_READINGS;
      results[pos *3 +2] = d2 /US_READINGS;
      pos++;
      servo.write(servo_deg += SERVO_TURN);
      delay(SERVO_TURN_PAUSE);
    }
    state = STATE_DEFAULT;
    servo_deg = SERVO_DEFAULT;
    servo.write(servo_deg);
    results[0] = 0xFF;
    results[1] = ((sizeof(results) -4) &0xFF00) >> 8;
    results[2] = ((sizeof(results) -4) &0x00FF);
    results[sizeof(results) -1] = crc8(results, 0, sizeof(results) -1);
    Serial3.write(results, sizeof(results));
    delay(SERVO_FULL_TURN_PAUSE);
    digitalWrite(SERVO_POWER, HIGH);
  }
  
  /* BLUETOOTH, ensure proper transmissions */
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

  /* SENSORS, ensure we're not crashing into something  */
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
        Serial3.write(crc);
        state = STATE_DEFAULT;
        break;
      case 3:    /* SEMI-AUTOMATIC ROTATE */
        state = STATE_AUTOMATIC_STEERING;
        if ((data[1] & B10000000) == 0) car.rotate((int)(data[1] & B01111111));
        else if ((data[1] & B10000000) >= 1) car.rotate(-(int)(data[1] & B01111111));
        Serial3.write(0x3F);
        Serial3.write(crc);
        state = STATE_DEFAULT;
        break;
      case 4:    /* SEMI-AUTOMATIC MOVE */
        state = STATE_AUTOMATIC_STEERING;
        if ((data[1] & B10000000) == 0) car.go((int)(data[1] & B01111111));
        else if ((data[1] & B10000000) >= 1) car.go(-(int)(data[1] & B01111111));
        Serial3.write(0x4F);
        Serial3.write(crc);
        state = STATE_DEFAULT;
        break;
      case 15:   /* MAPPING/SCANNING INSTRUCTION */
        state = STATE_MAPPING;
        SERVO_TURN = data[1];
        SERVO_TURN_PAUSE = SERVO_TURN *5;
        car.setSpeed(0);
        car.setAngle(0);
        break;
    }
  }
}

byte crc8(byte data[], int offset, int len) {
  int polynomial = 0xA7, crc = 0x63;
  for (int j = offset; j < offset +len; j++) {
    for (int i = 0; i < 8; i++) {
      boolean b = ((data[j] >> (7-i) & 1) == 1);
      boolean c7 = ((crc >> 7 & 1) == 1);
      crc <<= 1;
      if (c7 ^ b) crc ^= polynomial;
    }
  }
  return (byte)(crc &0xFF);
}

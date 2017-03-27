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
const int SERVO_TURN = 5;
const int SERVO_PAUSE = 65;
const int SERVO_FULL_TURN_PAUSE = 1200;
const int STATE_DEFAULT = 0;
const int STATE_MAPPING = 15;

Car car;
Servo servo;
SR04 us1(255), us2(255);
GP2Y0A21 middle;
TemperatureSensor ambient(TEMP_HUM_PIN);

int state = STATE_DEFAULT;
int servo_deg = SERVO_DEFAULT;
int middleDistance;
int obstacleDetections = 0;

void setup() {
  pinMode(SERVO_POWER, OUTPUT);
  car.begin();
  servo.attach(SERVO_PIN);
  servo.write(SERVO_DEFAULT);
  us1.attach(US1_TRIGGER, US1_ECHO);
  us2.attach(US2_TRIGGER, US2_ECHO);
  middle.attach(IR_MIDDLE_PIN);
  delay(SERVO_FULL_TURN_PAUSE);
  digitalWrite(SERVO_POWER, HIGH);
  Serial3.begin(9600);
  Serial.begin(9600);
}

void loop() { 
  /* MAPPING/SCANNING state, blocks all other instruction processing until done */
  if (state == STATE_MAPPING) {
    digitalWrite(SERVO_POWER, LOW);
    byte results[180 /SERVO_TURN *3 +3];
    int pos = 2;
    for (int repetitions = 0; repetitions < 1; repetitions++) {
      while (servo_deg <= 180) {
        ambient.update();
        int d1 = 0, d2 = 0;
        int d[10];
        for (int i = 0; i < (sizeof(d) /2); i++) {
          d[i *2 +0] = us1.getDistance();
          d[i *2 +1] = us2.getDistance();
        }
        for (int i = 0; i < (sizeof(d) /2); i++) {
          d1 += d[i *2 +0];
          d2 += d[i *2 +1];
        }
        d1 /= sizeof(d) /2;
        d2 /= sizeof(d) /2;
        results[pos *3 +0]   = servo_deg;
        results[pos *3 +1]   = d1;
        results[pos++ *3 +2] = d2;
        Serial.print(servo_deg);
        Serial.print(": ");
        Serial.print(d1);
        Serial.print("cm, ");
        Serial.print(d2);
        Serial.print("cm\n");
        servo.write(servo_deg += SERVO_TURN);
        delay(SERVO_PAUSE);
      }
      servo.write(servo_deg = SERVO_DEFAULT);
    }
    state = STATE_DEFAULT;
    results[0] = 0xFF;
    results[2] = sizeof(results) -3;
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
      case 1:   /* STEERING INSTRUCTIONS */  
        if ((data[1] & B10000000) == 0) {
          if (obstacleDetections < MIN_OBSTACLE_DETECTIONS) {
            if ((data[1] & B01111111) > MIN_SPEED) car.setSpeed(data[1] & B01111111);
            else car.setSpeed(0);
          } else car.setSpeed(0);
        } else car.setSpeed(-(data[1] & B01111111));
        if ((data[2] & B10000000) == 0) car.setAngle(data[2] & B01111111);
        else car.setAngle(-(data[2] & B01111111)); 
        break;
      case 15:   /* MAPPING/SCANNING INSTRUCTION */
        state = STATE_MAPPING;
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

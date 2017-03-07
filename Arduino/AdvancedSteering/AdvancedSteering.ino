#include <Smartcar.h>

const int IR_PIN = A7;
const int MIN_SPEED = 35;
const int MIN_DISTANCE = 20;
const int MIN_OBSTACLE_DETECTIONS = 2;

Car car;
GP2Y0A21 front;

int distance;
int obstacleDetections = 0;

void setup() {
  car.begin();
  front.attach(IR_PIN);
  Serial3.begin(9600);
}

void loop() {
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

  /* SENSORS, ensure not crashing into something  */
  distance = front.getDistance();
  if (distance <= MIN_DISTANCE && distance != 0) {
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

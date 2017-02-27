#include <Smartcar.h>

GP2D120 front;
const int IR_PIN = A7;

Car car;
const int cSpeed = 70;
const int turnDegrees = 80;

char input;
int distance;
int obstacleDetections = 0;

void setup() {
  Serial3.begin(9600);
  car.begin();
  front.attach(IR_PIN);
}

void loop() {
  distance = front.getDistance();
  if (distance <= 15 && distance != 0) {
    obstacleDetections++;
  }
  else obstacleDetections = 0;
  if (Serial3.available()) input = Serial3.read();
  
  /*
  if (obstacleDetections < 2) {
        if (input & B10000000 == 0) car.setSpeed(input & B01111111);
        else car.setSpeed(-(input & B01111111));
      }
      else input = B00000000; */
      
  switch (input) {
    case 'w': if (obstacleDetections < 2) {
        car.setSpeed(cSpeed);
        car.setAngle(0);
      }
      else input = 'x'; break;

    case 's': car.setSpeed(-cSpeed + 20);
      car.setAngle(0); break;
    case 'a':
      car.setSpeed(cSpeed);
      car.setAngle(turnDegrees); break;
    case 'd':
      car.setSpeed(cSpeed);
      car.setAngle(-turnDegrees); break;

    default:  car.setSpeed(0);
      car.setAngle(0);
  }
}

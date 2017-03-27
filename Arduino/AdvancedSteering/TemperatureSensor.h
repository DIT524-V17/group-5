//
// Created by Julian Bock on 2017-03-25.
//

#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>

#ifndef MARBLE_TEMPERATURESENSOR_H
#define MARBLE_TEMPERATURESENSOR_H

#define TEMP_FAULT_TOLERANCE  (2.5)
#define HUM_FAULT_TOLERANCE   (7.5)
#define IV                    (-99)
#define DHTTYPE               DHT11

class TemperatureSensor {
public:
    TemperatureSensor(int);
    void update();
    float temperature();
    float humidity();

private:
    DHT_Unified* sensor;
    int lTemp, lHum;
    float temp, hum;
};

#endif //MARBLE_TEMPERATURESENSOR_H

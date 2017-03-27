//
// Created by Julian Bock on 2017-03-25.
//

#include "TemperatureSensor.h"

TemperatureSensor::TemperatureSensor(int sensor_pin){
    sensor  = new DHT_Unified(sensor_pin, DHTTYPE);
    lTemp   = IV;
    temp    = IV;
    lHum    = IV;
    hum     = IV;
    sensor  ->begin();
}

void TemperatureSensor::update(){
    sensors_event_t event;
    sensor ->temperature().getEvent(&event);
    if (!isnan(event.temperature)) {
        if (lTemp == IV || abs(lTemp -event.temperature) < TEMP_FAULT_TOLERANCE) {
            lTemp = event.temperature;
            temp = ((temp == IV ? lTemp : temp) + lTemp) /2;
        }
    }
    sensor ->humidity().getEvent(&event);
    if (!isnan(event.relative_humidity)) {
        if (lHum == IV || abs(lHum -event.relative_humidity) < HUM_FAULT_TOLERANCE) {
            lHum = event.relative_humidity;
            hum = ((hum == IV ? lHum : hum) + lHum) /2;
        }
    }
}

float TemperatureSensor::temperature() {
    return temp;
}

float TemperatureSensor::humidity() {
    return hum;
}
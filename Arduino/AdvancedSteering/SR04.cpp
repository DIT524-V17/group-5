/*
*    SR04.cpp - Handles the ultra sound (HC-SR04 & SRF05) sensors of the Smartcar
*    Author: Dimitris Platis
*    SR04 class is essentially a stripped-down version of the NewPing library by Tim Eckel adjusted to Smartcar needs
*     Get original library at: http://code.google.com/p/arduino-new-ping/
*     License: GNU GPL v3 http://www.gnu.org/licenses/gpl-3.0.html
*/
#include "Smartcar.h"

/* ------ SR04 ------ */
const unsigned int SR04::DEFAULT_MAX_US_DISTANCE = 70; // Maximum usable sensor distance is around 70cm.
const unsigned int US_ROUNDTRIP_CM = 57;      // Microseconds (uS) it takes sound to travel round-trip 1cm (2cm total), uses integer to save compiled code space.

// Probably shoudln't change these values unless you really know what you're doing.
const int NO_ECHO = 0;               // Value returned if there's no ping echo within the specified MAX_SENSOR_DISTANCE
const unsigned int MAX_SENSOR_DELAY = 5800;  // Maximum uS it takes for sensor to start the ping.

// Constants for speed calculation
//Pressure in pascal
const float PRESSURE = 101325;
//Euler's number
const float EULERS_NUMBER = 2.7182818284590452353602874713527;
//Kelvin conversion
const float CELCIUS_KELVIN_CONVERSION = 273.15;

SR04::SR04(unsigned int maxDistance) {
    _sensorMedianDelay = 8;
    _maxDistance = maxDistance;
    _triggerBit = 0, _echoBit = 0, _triggerOutput = 0, _triggerMode = 0;
    _echoInput = 0, _maxEchoTime = 0, _max_time = 0;
}

void SR04::attach(unsigned short triggerPin, unsigned short echoPin){
    _triggerBit = digitalPinToBitMask(triggerPin); // Get the port register bitmask for the trigger pin.
    _echoBit = digitalPinToBitMask(echoPin);       // Get the port register bitmask for the echo pin.

    _triggerOutput = portOutputRegister(digitalPinToPort(triggerPin)); // Get the output port register for the trigger pin.
    _echoInput = portInputRegister(digitalPinToPort(echoPin));         // Get the input port register for the echo pin.

    _triggerMode = (uint8_t *) portModeRegister(digitalPinToPort(triggerPin)); // Get the port mode register for the trigger pin.

    _maxEchoTime = _maxDistance * US_ROUNDTRIP_CM + (US_ROUNDTRIP_CM / 2); // Calculate the maximum distance in uS.
    *_triggerMode |= _triggerBit; // Set trigger pin to output.
}

unsigned int SR04::ping() {
    if (!ping_trigger()) return NO_ECHO;                // Trigger a ping, if it returns false, return NO_ECHO to the calling function.
    while (*_echoInput & _echoBit)                      // Wait for the ping echo.
        if (micros() > _max_time) return NO_ECHO;       // Stop the loop and return NO_ECHO (false) if we're beyond the set maximum distance.
    return (micros() - (_max_time - _maxEchoTime) - 5); // Calculate ping time, 5uS of overhead.
}

unsigned int SR04::getDistance() {
    unsigned int echoTime = ping();          // Calls the ping method and returns with the ping echo distance in uS.
    return (max((echoTime + US_ROUNDTRIP_CM / 2) / US_ROUNDTRIP_CM, (unsigned int) (echoTime ? 1 : 0))); // Convert uS to centimeters.
}

unsigned int SR04::getDistance(float T, float Rh) {
    float P = PRESSURE;			// pressure
    float C;                    // speed
    float Xc, Xw;               // Mole fraction of carbon dioxide and water vapour respectively
    float H;                    // molecular concentration of water vapour
    float C1;                   // Intermediate calculations
    float C2;
    float C3;
    float ENH;
    float PSV;
    float PSV1;
    float PSV2;
    float T_kel;                // ambient temperature (Kelvin)
    float Kelvin = 273.15;      //For converting to Kelvin
    float e = 2.71828182845904523536;
    
    T_kel = Kelvin + T;         //Measured ambient temp
    
    //Molecular concentration of water vapour calculated from http://resource.npl.co.uk/acoustics/techguides/speedair/
    
    ENH = 3.14*pow(10,-8)*P + 1.00062 + sqrt(T)*5.6*pow(10,-7);
    PSV1 = sqrt(T_kel)*1.2378847*pow(10,-5)-1.9121316*pow(10,-2)*T_kel;
    PSV2 = 33.93711047-6.3431645*pow(10,3)/T_kel;
    PSV = pow(e,PSV1)*pow(e,PSV2);
    H = Rh*ENH*PSV/P;
    Xw = H/100.0;
    Xc = 400.0*pow(10,-6);

    //Speed calculated using the method of Cramer from http://resource.npl.co.uk/acoustics/techguides/speedair/
    
    C1 = 0.603055*T + 331.5024 - sqrt(T)*5.28*pow(10,-4) + (0.1495874*T + 51.471935 -sqrt(T)*7.82*pow(10,-4))*Xw;
    C2 = (-1.82*pow(10,-7)+3.73*pow(10,-8)*T-sqrt(T)*2.93*pow(10,-10))*P+(-85.20931-0.228525*T+sqrt(T)*5.91*pow(10,-5))*Xc;
    C3 = sqrt(Xw)*2.835149 + sqrt(P)*2.15*pow(10,-13) - sqrt(Xc)*29.179762 - 4.86*pow(10,-4)*Xw*P*Xc;
    C = C1 + C2 - C3;
    
    unsigned int echoTime = ping();
    return echoTime /(0.1*C);
}

/* Standard ping method helper functions */
boolean SR04::ping_trigger() {
    *_triggerOutput &= ~_triggerBit; // Set the trigger pin low, should already be low, but this will make sure it is.
    delayMicroseconds(4);            // Wait for pin to go low, testing shows it needs 4uS to work every time.
    *_triggerOutput |= _triggerBit;  // Set trigger pin high, this tells the sensor to send out a ping.
    delayMicroseconds(10);           // Wait long enough for the sensor to realize the trigger pin is high. Sensor specs say to wait 10uS.
    *_triggerOutput &= ~_triggerBit; // Set trigger pin back to low.
    _max_time =  micros() + MAX_SENSOR_DELAY;                  // Set a timeout for the ping to trigger.
    while (*_echoInput & _echoBit && micros() <= _max_time) {} // Wait for echo pin to clear.
    while (!(*_echoInput & _echoBit))                          // Wait for ping to start.
        if (micros() > _max_time) return false;                // Something went wrong, abort.
    _max_time = micros() + _maxEchoTime; // Ping started, set the timeout.
    return true;                         // Ping started successfully.
}

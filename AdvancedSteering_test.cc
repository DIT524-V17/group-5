#include "gtest/gtest.h"
#include "gmock/gmock.h"
#include "arduino-mock/Arduino.h"
#include "arduino-mock/Serial.h"
#include "Smartcar.h" // The Smartcar library mocks

#include "../../src/AdvancedSteering.ino" // Our production code

using ::testing::_;   
using ::testing::Return;  // is this needed?

class AdvancedSteeringFixture : public ::testing::Test
{
public:
    ArduinoMock* arduinoMock; // Necessary for delay()
    SerialMock* serialMock;
    GP2Y0A21Mock* GP2Y0A21_mock;
    OdometerMock* odometerMock;
    GyroscopeMock* gyroscopeMock;
    CarMock* carMock;
    //ServoMock* servoMock;
    SR04_Mock* SR04_mock
    // Run this before the tests
    virtual void SetUp()
    {
        arduinoMock = arduinoMockInstance();
        serialMock = serialMockInstance();
	    GP2Y0A21_mock = GP2Y0A21MockInstance();
	    odometerMock = odometerMockInstance();
	    gyroscopeMock = gyroscopeMockInstance();
        carMock = carMockInstance();
        //servoMock = servoMockInstance();
        SR04_mock = SR04MockInstance();
    }
    // Run this after the tests
    virtual void TearDown()
    {
        releaseArduinoMock();
        releaseSerialMock();
        releaseCarMock();
	    releaseGP2Y0A21Mock();
	    releaseOdometerMock();
	    releaseGyroscopeMock();
        //releaseServoMock();
        releaseSR04_Mock();

    }
};

TEST_F(AdvancedSteeringFixture, initsAreCalled) {
    EXPECT_CALL(*serialMock, begin(_));
    //EXPECT_CALL(*AdvancedSteering_mock, attach(TRIGGER_PIN, ECHO_PIN));
    EXPECT_CALL(*GP2Y0A21_mock, attach(SIDE_FRONT_PIN));
    EXPECT_CALL(*odometerMock, attach(encoderPin));
    EXPECT_CALL(*odometerMock, begin());
    EXPECT_CALL(*gyroscopeMock, attach());
    EXPECT_CALL(*gyroscopeMock, begin(_));
    EXPECT_CALL(*carMock,begin());
    EXPECT_CALL(*SR04_mock,attach(TRIGGER_PIN,ECHO_PIN));
    setup();
}

TEST_F(AdvancedSteeringFixture, expectGetDistanceCall) {
    EXPECT_CALL(*AdvancedSteering_mock, getMedianDistance(_));
    EXPECT_CALL(*odometerMock, getDistance());
    EXPECT_CALL(*GP2Y0A21_mock, getDistance());
    loop();
}

TEST_F(AdvancedSteeringFixture, updateCalledForAngularDisplacementToBeCalculated) {
    EXPECT_CALL(*gyroscopeMock, update());
    EXPECT_CALL(*gyroscopeMock, getAngularDisplacement());
    loop();
}



int main(int argc, char* argv[]) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}

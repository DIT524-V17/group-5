#include "gtest/gtest.h"
#include "gmock/gmock.h"
#include "arduino-mock/Arduino.h"
#include "arduino-mock/Serial.h"
#include "Smartcar.h" // The Smartcar library mocks

#include "../../src/AdvancedSteering.ino" // Our production code

using ::testing::_;
// is this needed?
using ::testing::Return;

class AdvancedSteeringFixture : public ::testing::Test
{
public:
    ArduinoMock* arduinoMock; // Necessary for delay()
    SerialMock* serialMock;
    AdvancedSteeringMock* AdvancedSteering_mock;
    // Run this before the tests
    virtual void SetUp()
    {
        arduinoMock = arduinoMockInstance();
        serialMock = serialMockInstance();
        AdvancedSteering_mock = AdvancedSteeringMockInstance();
	GP2D120_mock = GP2D120MockInstance();
	odometerMock = odometerMockInstance();
	gyroscopeMock = gyroscopeMockInstance();
    }
    // Run this after the tests
    virtual void TearDown()
    {
        releaseArduinoMock();
        releaseSerialMock();
        releaseAdvancedSteeringMock();
	releaseGP2D120Mock();
	releaseOdometerMock();
	releaseGyroscopeMock();

    }
};

TEST_F(AdvancedSteeringFixture, initsAreCalled) {
    EXPECT_CALL(*serialMock, begin(_));
    EXPECT_CALL(*AdvancedSteering_mock, attach(TRIGGER_PIN, ECHO_PIN));
    EXPECT_CALL(*GP2D120_mock, attach(SIDE_FRONT_PIN));
    EXPECT_CALL(*odometerMock, attach(encoderPin));
    EXPECT_CALL(*odometerMock, begin());
    EXPECT_CALL(*gyroscopeMock, begin(_));
    setup();
}

TEST_F(AdvancedSteeringFixture, expectGetDistanceCall) {
    EXPECT_CALL(*AdvancedSteering_mock, getMedianDistance(_));
    EXPECT_CALL(*odometerMock, getDistance());
    EXPECT_CALL(*GP2D120_mock, getDistance());
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

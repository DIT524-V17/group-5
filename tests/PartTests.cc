#include "gtest/gtest.h"
#include "gmock/gmock.h"
#include "arduino-mock/Arduino.h"
#include "arduino-mock/Serial.h"
#include "arduino-mock/serialHelper.h"
#include "Smartcar.h" //The smartcar library mocks

#include "../../src/AdvanceSteeringdForTest.ino" //Our production code

using ::testing::_;
using ::testing::Return; 

class AdvancedSteeringFixture : public ::testing::Test
{
public:
    ArduinoMock* arduinoMock; //necessary?
    SerialMock* serialMock;
    GP2Y0A21Mock* GP2Y0A021_mock;
    GyroscopeMock* gyroscopeMock;
    CarMock* carMock;
    OdometerMock* odometerMock;

    virtual void SetUp()
    {
        arduinoMock = arduinoMockInstance();
        serialMock = serialMockInstance();
        GP2Y0A21_mock = GP2Y0A21MockInstance();
        gyroscopeMock = gyroscopeMockInstance();
        carMock = carMockInstance();
        odometerMock = odometerMockInstance();
    }
    //Run this after the tests
    virtual void TearDown()
    {
        releaseArduinoMock();
        releaseSerialMock();
        releaseGP2Y0A21Mock();
        releaseGyroscopeMock();
        releaseCarMock();
        releaseOdometerMock();

    }
};

TEST_F(AdvancedSteeringFixture, initsAreCalled) {
    EXPECT_CALL(*carMock, begin());
    EXPECT_CALL(*gyroscopeMock, attach());
    EXPECT_CALL(*gyroscopeMock, begin(_));
//    EXPECT_CALL(*odometerMock, attach(_));
//    EXPECT_CALL(*odometerMock, begin());
    EXPECT_CALL(*GP2Y0A21_mock, attach(_));
    setup();      
}

TEST_F(AdvancedSteeringFixture, expectObstacleCall) {
//    EXPECT_CALL(*odometerMock, getDistance());
    EXPECT_CALL(*GP2Y0A21_mock, getDistance())
    .WillOnce(Return(15));
    loop();
}

TEST_F(AdvancedSteeringFixture, expectNoObstacle) {
    EXPECT_CALL(*GP2Y0A21_mock,getDistance())
    .WillOnce(Return(30));
    loop();
}
int main(int argc, char* argv[]) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}

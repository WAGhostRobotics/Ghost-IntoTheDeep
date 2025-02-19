package org.firstinspires.ftc.teamcode.component;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import javax.annotation.concurrent.GuardedBy;

public class Imu {
    Thread imuThread;
    private final Object imuSync = new Object();

    @GuardedBy("imuSync")
    private IMU imu;
    private double heading = 0;
    private double angularVelocity = 0;


    HardwareMap hwMap;

    public Imu(HardwareMap hwMap) {
        this.hwMap = hwMap;
        initIMU();
    }

    //gets angle from imu
    public double getCurrentHeading() {
        return heading;
    }

    public void initImuThread(LinearOpMode opMode) {
        imuThread = new Thread(() -> {
            synchronized (imuSync) {
                while (!opMode.isStarted() && !opMode.isStopRequested()) {
                    imu.resetYaw();
                }
                while (opMode.opModeIsActive() && !opMode.isStopRequested()) {
                    heading = imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
                }

            }
        });
        imuThread.start();
    }

    public IMU getImu() {
        return imu;
    }


    public double getAngularVelocity() {
        return angularVelocity;
    }


    public void initIMU() {
        imu = hwMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(
                        new RevHubOrientationOnRobot(
                                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                        )
                )
        );
        imu.resetYaw();
    }

    public double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }
}
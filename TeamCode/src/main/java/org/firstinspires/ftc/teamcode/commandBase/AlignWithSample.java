package org.firstinspires.ftc.teamcode.commandBase;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.normalizeDegrees;

import com.arcrobotics.ftclib.controller.PIDController;

import org.firstinspires.ftc.teamcode.commandSystem.Command;
import org.firstinspires.ftc.teamcode.component.OuttakeSlides;
import org.firstinspires.ftc.teamcode.component.localizer.Localizer;
import org.firstinspires.ftc.teamcode.core.Pika;
import org.firstinspires.ftc.teamcode.pathing.Bezier;
import org.firstinspires.ftc.teamcode.pathing.MotionPlannerEdit;
import org.firstinspires.ftc.teamcode.pathing.Point;

public class AlignWithSample extends Command {
    double targetHeading, targetX;
    double xError = 0, yError, headingError, targetArea;
    double angle = 180, slideError, tX, tY, currentHeading, xPower, yPower, driveTurn, theta;
    double targetAreaThreshold = 0.05;
    double angleMeasureTargetAreaThreshold = 0.110;
    // Decreased from 0.125 because did not detect orientation in one run


    double kStaticY = 0.2;
    boolean isFinished = false;
    PIDController yControl = new PIDController(0.0125, 0.0068, 0.000);
    PIDController xControl = new PIDController(0.02, 0.0015, 0.0065);
    PIDController headingControl = new PIDController(0.018, 0.0001, 0);
    double error;
    MotionPlannerEdit follower;
    public AlignWithSample(MotionPlannerEdit follower) {
        this.follower = follower;
    }

    @Override
    public void init() {
        isFinished = false;
        yControl.setIntegrationBounds(-10000000, 10000000);
        xControl.setIntegrationBounds(-10000000, 10000000);
        headingControl.setIntegrationBounds(-10000000, 10000000);
        this.targetX = Pika.localizer.getY();
        this.targetHeading = Pika.localizer.getHeading(Localizer.Angle.DEGREES);
        Pika.outtakeSlides.pause();
        Pika.outtakeSlides.resetAlignController();
        follower.pause();
        xError = 0;
    }
    @Override
    public void update() {
        if (isFinished) {
            Pika.outtakeSlides.freeMove();
            Pika.drivetrain.drive(0,0,0,0);
            return;
        }


        Pika.limelight.getSampleOrientation();
        tX = Pika.limelight.tX;
        tY = Pika.limelight.tY;
        targetArea = Pika.limelight.targetArea;
        currentHeading = Pika.localizer.getHeading(Localizer.Angle.DEGREES);

        if (targetArea>angleMeasureTargetAreaThreshold) {
            angle = Pika.limelight.angle;
        }

        slideError = tX+9;

        slideError = (Math.abs(slideError)>1.2) ? slideError : 0;
//        xError = targetX- Pika.localizer.getY();
        yError = tY;
        headingError = targetHeading - currentHeading;


        xPower = xControl.calculate(0, xError);
        xPower = (Math.abs(xError)>1) ? xPower + Math.signum(xPower)* MotionPlannerEdit.kStatic_X : 0;

        yPower = yControl.calculate(0, yError);
        yPower = (Math.abs(yError)>3) ? Math.signum(yPower)*kStaticY + yPower : 0;

        driveTurn = headingControl.calculate(0, headingError);

        theta = normalizeDegrees(Math.toDegrees(Math.atan2(yError, xError)));
        if (targetArea>targetAreaThreshold)
            Pika.outtakeSlides.alignWithSample(slideError);
        else
            Pika.outtakeSlides.extendForSample();

        if (targetArea>targetAreaThreshold && Math.abs(slideError)<1.2 && Math.abs(yError)<3) {
            Pika.newClaw.setPivotOrientation(angle);
            Pika.outtakeSlides.freeMove();
            isFinished = true;
        }

        Pika.drivetrain.drive(Math.hypot(yPower, xPower), theta, driveTurn, 0.725);

    }

    @Override
    public boolean isFinished() {
         return isFinished;
    }

    @Override
    public void stop() {
    }

    public String getTelemetry() {
        return "Arm: " + Pika.arm.getTelemetry() +
        "\nEndEffectorClaw: " + Pika.newClaw.getTelemetry() +
        "\nAngle: " + angle +
        "\nError: " + yError +
        "\ntX: " + tX +
        "\ntY: " + tY +
        "\nTarget Area: " + targetArea +
        "\nxError: " + xError +
        "\nHeadingError: " + headingError +
        "\nxPower: " + xPower +
        "\nyPower: " + yPower +
        "\nTheta: " + theta +
        "\nDriveTurn: " + driveTurn +
        "\nMovementPower: " + Pika.movementPower +

        "\nSlide Power: " + Pika.outtakeSlides.getTelemetry() +
        "\nSlideError: " + slideError +

        "\nReachedSlides: " + (Math.abs(slideError)<1.2) +
        "\nReached Y: " + (Math.abs(yError)<3) +
        "\nEnough Area: " + (targetArea>0.115) +
        "\nArm Finished: " + Pika.arm.isFinished();
    }
}

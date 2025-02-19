package org.firstinspires.ftc.teamcode.auto;

import androidx.loader.content.Loader;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.sun.tools.javac.code.Scope;

import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.commandBase.Grab;
import org.firstinspires.ftc.teamcode.commandBase.GrabSpec;
import org.firstinspires.ftc.teamcode.commandBase.IntakeSampleSpec;
import org.firstinspires.ftc.teamcode.commandBase.IntakeSampleTeleOp;
import org.firstinspires.ftc.teamcode.commandBase.PrepareSpecDeposit;
import org.firstinspires.ftc.teamcode.commandBase.RetractAll;
import org.firstinspires.ftc.teamcode.commandBase.SlidesMove;
import org.firstinspires.ftc.teamcode.commandSystem.FollowTrajectory;
import org.firstinspires.ftc.teamcode.commandSystem.ParallelCommand;
import org.firstinspires.ftc.teamcode.commandSystem.RunCommand;
import org.firstinspires.ftc.teamcode.commandSystem.SequentialCommand;
import org.firstinspires.ftc.teamcode.commandSystem.Wait;
import org.firstinspires.ftc.teamcode.component.FinalClaw;
import org.firstinspires.ftc.teamcode.component.OuttakeSlides;
import org.firstinspires.ftc.teamcode.core.Pika;
import org.firstinspires.ftc.teamcode.pathing.Bezier;
import org.firstinspires.ftc.teamcode.pathing.MergedBezier;
import org.firstinspires.ftc.teamcode.pathing.MotionPlannerEdit;
import org.firstinspires.ftc.teamcode.pathing.Point;

@Autonomous
public class SpecimenAuto extends LinearOpMode {
    MotionPlannerEdit follower; //-17.7, 14.65
    ElapsedTime timer;
    Point spike1 = new Point(-18.5, 42.27); // 19600
    Point spike2 = new Point(-18.5, 52.9); // 19600
    Point spike3 = new Point(-18.5, 56.31); // 164.8 19632
    Point obsZone =  new Point(-16, 17);
    Point chamber1 = new Point(-26.65, -1.5);
    Point chamber2 = new Point(-26.65, -1);
    Point chamber3 = new Point(-33.5, -2);
    Point chamber4 = new Point(-32.5, -3.5);
    Point chamber5 = new Point(-31.205, -4.5);

    @Override
    public void runOpMode() throws InterruptedException {
        Pika.init(hardwareMap, this, false);
        timer = new ElapsedTime();
        follower = new MotionPlannerEdit(Pika.drivetrain, Pika.localizer, hardwareMap);
        Bezier preloadPath = new Bezier(
                0,
                new Point(0,0),
                chamber1);
        Bezier spike1Path = new MergedBezier(
                new Bezier(
                        0,
                        new Point(5, 0)
                ),
                new Bezier(
                    180,
                    new Point(-20, 15),
                    new Point(-10, 30),
                    spike1
                )
        );
        Bezier spike2Path = new Bezier(
                180,
                spike1Path.getEndPoint(),
                spike2
        );
        Bezier spike3Path = new Bezier(
                164.8,
                spike2Path.getEndPoint(),
                spike3
        );

        Bezier spikeToObs = new MergedBezier(
                new Bezier(
                        180,
                        spike3,
                        spike1
                ),
                new Bezier(
                        -320,
                        spike1,
                        obsZone
                )
        );

        Bezier chamberToObs = new MergedBezier(
                40,
                new Bezier(
                        0,
                        new Point(-5, 0)
                ),
                new Bezier(
                        180,
                        new Point(-20, 15),
                        new Point(-10, 30),
                        obsZone
                )
        );
        Bezier obsToChamber = new Bezier(
                0,
                obsZone,
                new Point(2, 3),
                chamber2
        );

        SequentialCommand preloadAndSpikes = new SequentialCommand(
                new PrepareSpecDeposit(),
                new FollowTrajectory(follower, preloadPath),

                new Wait(200),
                new SlidesMove(OuttakeSlides.TurnValue.SPEC_DEPOSIT.getTicks()),
                new RunCommand(()-> Pika.newClaw.setClaw(FinalClaw.ClawPosition.OPEN.getPosition())),

                new ParallelCommand(
                        new FollowTrajectory(follower, spike1Path),
                        new SequentialCommand(
                                new Wait(500),
                                new IntakeSampleTeleOp()
                        )
                ),
                new ParallelCommand(
                        new SlidesMove(21000),
                        new RunCommand(()-> Pika.newClaw.setPivotOrientation(180))
                ),
                new Grab(),
                new PrepareSpecDeposit(),
                new RunCommand(()-> Pika.newClaw.setClaw(FinalClaw.ClawPosition.OPEN.getPosition())),


                new ParallelCommand(
                        new FollowTrajectory(follower, spike2Path),
                        new IntakeSampleTeleOp()
                ),
                new ParallelCommand(
                        new SlidesMove(21000),
                        new RunCommand(()-> Pika.newClaw.setPivotOrientation(180))
                ),
                new Grab(),
                new PrepareSpecDeposit(),
                new RunCommand(()-> Pika.newClaw.setClaw(FinalClaw.ClawPosition.OPEN.getPosition())),

                new ParallelCommand(
                        new FollowTrajectory(follower, spike3Path),
                        new IntakeSampleTeleOp()
                ),
                new ParallelCommand(
                        new SlidesMove(21000),
                        new RunCommand(()-> Pika.newClaw.setPivotOrientation(180))
                ),
                new Grab(),
                new PrepareSpecDeposit(),
                new RunCommand(()-> Pika.newClaw.setClaw(FinalClaw.ClawPosition.OPEN.getPosition())),

                new ParallelCommand(
                        new FollowTrajectory(follower, spikeToObs),
                        new IntakeSampleSpec()
                ),

                new SlidesMove(17000),
                new Wait(1000),
                new RunCommand(()->Pika.newClaw.setClaw(FinalClaw.ClawPosition.CLOSE.getPosition())),
                new ParallelCommand(
                        new PrepareSpecDeposit(),
                        new SequentialCommand(
                                new Wait(300),
                                new FollowTrajectory(follower, obsToChamber)
                        )
                ),
                new SlidesMove(OuttakeSlides.TurnValue.SPEC_DEPOSIT.getTicks()),
                new ParallelCommand(
                        new FollowTrajectory(follower, chamberToObs),
                        new IntakeSampleSpec()
                ),

                new SlidesMove(17000),
                new Wait(1000),
                new RunCommand(()->Pika.newClaw.setClaw(FinalClaw.ClawPosition.CLOSE.getPosition())),
                new ParallelCommand(
                        new PrepareSpecDeposit(),
                        new SequentialCommand(
                                new Wait(300),
                                new FollowTrajectory(follower, obsToChamber)
                        )
                ),
                new SlidesMove(OuttakeSlides.TurnValue.SPEC_DEPOSIT.getTicks()),
                new ParallelCommand(
                        new RunCommand(()-> Pika.newClaw.setClaw(FinalClaw.ClawPosition.OPEN.getPosition())),
                        new FollowTrajectory(follower, chamberToObs),
                        new IntakeSampleSpec()
                ),

                new SlidesMove(17000),
                new Wait(1000),
                new RunCommand(()->Pika.newClaw.setClaw(FinalClaw.ClawPosition.CLOSE.getPosition())),
                new ParallelCommand(
                        new PrepareSpecDeposit(),
                        new SequentialCommand(
                                new Wait(300),
                                new FollowTrajectory(follower, obsToChamber)
                        )
                ),
                new SlidesMove(OuttakeSlides.TurnValue.SPEC_DEPOSIT.getTicks()),
                new ParallelCommand(
                        new RunCommand(()-> Pika.newClaw.setClaw(FinalClaw.ClawPosition.OPEN.getPosition())),
                        new FollowTrajectory(follower, chamberToObs),
                        new IntakeSampleSpec()
                ),

                new SlidesMove(17000),
                new Wait(1000),
                new RunCommand(()->Pika.newClaw.setClaw(FinalClaw.ClawPosition.CLOSE.getPosition())),
                new ParallelCommand(
                        new PrepareSpecDeposit(),
                        new SequentialCommand(
                                new Wait(300),
                                new FollowTrajectory(follower, obsToChamber)
                        )
                ),
                new SlidesMove(OuttakeSlides.TurnValue.SPEC_DEPOSIT.getTicks()),
                new ParallelCommand(
                        new RunCommand(()-> Pika.newClaw.setClaw(FinalClaw.ClawPosition.OPEN.getPosition())),
                        new FollowTrajectory(follower, chamberToObs),
                        new IntakeSampleSpec()
                )
        );
        follower.pause();
        follower.setMovementPower(0.85);
        waitForStart();

        preloadAndSpikes.init();
        while (opModeIsActive() && !isStopRequested()) {
            Pika.localizer.update();
            if (Pika.arm.isFinished())
                Pika.outtakeSlides.update();
            preloadAndSpikes.update();
            Pika.arm.update();
            follower.update();

            telemetry.addData("MP: ", follower.getTelemetry());
            telemetry.addData("Loop speed: ", timer.milliseconds());
            telemetry.update();

            timer.reset();
        }
    }

}

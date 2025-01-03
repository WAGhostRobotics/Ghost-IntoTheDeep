package org.firstinspires.ftc.teamcode.commandBase;

import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.commandSystem.ParallelCommand;
import org.firstinspires.ftc.teamcode.commandSystem.RunCommand;
import org.firstinspires.ftc.teamcode.commandSystem.SequentialCommand;
import org.firstinspires.ftc.teamcode.commandSystem.Wait;
import org.firstinspires.ftc.teamcode.component.Arm;
import org.firstinspires.ftc.teamcode.component.FinalClaw;
import org.firstinspires.ftc.teamcode.component.OuttakeSlides;
import org.firstinspires.ftc.teamcode.core.Pika;
import org.firstinspires.ftc.teamcode.pathing.MotionPlannerEdit;

public class AutoIntakeFromSub extends SequentialCommand {
    public AutoIntakeFromSub(MotionPlannerEdit follower) {
        super(
                new SlidesMove(OuttakeSlides.TurnValue.RETRACTED.getTicks()),
                new RunCommand(()-> Pika.newClaw.setMiniPitch(FinalClaw.MiniPitch.BEFORE_GRAB.getPosition())),

                new ParallelCommand(
                        new ArmMove(Arm.ArmPos.INTAKE.getPosition()),
                        new RunCommand(()-> Pika.newClaw.setPivotOrientation(90)),
                        new RunCommand(()-> Pika.newClaw.setArmPitch(FinalClaw.ArmPitch.BEFORE_GRAB.getPosition())),
                        new RunCommand(()-> Pika.newClaw.setClaw(FinalClaw.ClawPosition.OPEN.getPosition()))
                ),
                new RunCommand(() ->Pika.outtakeSlides.resetEncoder()),
                new SlidesMove(3000),
                new AlignWithSample(follower),
                new Wait(500),
                new SpecialGrab(),
                new RunCommand(()->Pika.newClaw.setPivotOrientation(180)),
                new RunCommand(()->Pika.outtakeSlides.resume()),
                new RunCommand(follower::resume)
        );
    }

}

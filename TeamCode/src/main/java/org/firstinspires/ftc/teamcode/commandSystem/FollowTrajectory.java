package org.firstinspires.ftc.teamcode.commandSystem;


import org.firstinspires.ftc.teamcode.pathing.Bezier;
import org.firstinspires.ftc.teamcode.pathing.CatmullRom;
import org.firstinspires.ftc.teamcode.pathing.MotionPlanner;
import org.firstinspires.ftc.teamcode.pathing.MotionPlannerEdit;
import org.firstinspires.ftc.teamcode.pathing.Path;

public class FollowTrajectory extends Command {
    MotionPlannerEdit mp;
    Path traj;

    public FollowTrajectory(MotionPlannerEdit mp, Path traj) {
        this.mp = mp;
        this.traj = traj;
    }

    @Override
    public void init() {
        mp.resume();
        mp.startTrajectory(traj);
    }

    @Override
    public void update() {}

    @Override
    public boolean isFinished() {
        return mp.isFinished();
    }

    @Override
    public void stop() {}
}

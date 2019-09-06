package org.usfirst.frc.team5968.robot;

public class DisabledMode implements IRobotMode {

    /* This class is used when the robot is disabled.
       In this mode, the robot should not be able to do anything.
     */
    
    // Fields for robot peripherals
    private IHook hook;
    private ILauncher launcher;
    private ILineDetector lineDetector;

    // Sets fields to real robot peripherals
    public DisabledMode(IHook hook, ILauncher launcher, ILineDetector lineDetector) {
        this.hook = hook;
        this.launcher = launcher;
        this.lineDetector = lineDetector;
    }

    // This does nothing upon startup
    @Override
    public void init() {

    }

    // Debug printouts
    @Override
    public void periodic() {
        Debug.logPeriodic("Line Detector Raw Value: " + lineDetector.getRawValue());
        Debug.logPeriodic("Is Robot on Line? " + lineDetector.isOnLine());
    }

}
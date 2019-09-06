package org.usfirst.frc.team5968.robot;

public class HABLineAuto implements IRobotMode {

    /* This autonomous mode makes the robot cross the HAB line */

    // Field for drive base
    private IDrive drive;

    // Constants
    private static final double DRIVE_DISTANCE = 120.0; // inches
    private static final double DRIVE_SPEED = 0.5;

    // Sets drive field to real drive base
    public HABLineAuto(IDrive drive) {
        this.drive = drive;
    }

    // Makes the robot drive forward upon startup
    @Override
    public void init() {
        drive.driveDistance(DRIVE_DISTANCE, DRIVE_SPEED);
    }

    @Override
    public void periodic() {
    // Nothing to do
    }
}

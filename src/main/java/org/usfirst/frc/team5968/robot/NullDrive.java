package org.usfirst.frc.team5968.robot;

public class NullDrive implements IDrive {

    /* This class makes the Drive Base unable to do anything for testing */
    
    @Override
    public DriveMode getCurrentDriveMode() {
        return DriveMode.DRIVERCONTROL;
    }

     @Override
     public void driveDistance(double distanceInches, double yDirectionSpeed){

    }

     @Override
    public void rotateDegrees(double angle, double angularSpeed) {

    }

    /*
     * completionRoutine is called when the current action has been completed
     */

     @Override
     public void driveDistance(double distanceInches, double yDirectionSpeed, Runnable completionRoutine) {

    }

    @Override
    public void rotateDegrees(double relativeAngle, double angularSpeed, Runnable completionRoutine) {

    }

    /*
     * This is the method used to drive manually during teleoperated mode
     */

     @Override
    public void driveManual(double xDirectionSpeed, double yDirectionSpeed) {

    }

    @Override
    public void lookAt(double angle, double speed) {
        Debug.logPeriodic("" + angle);
    }

     /* Called if stick position is lower than threshold */
     @Override
     public void maintainHeading() {

     }

    @Override
    public void driveToLine(double strafeSpeed,Runnable completionRoutine) {

    }

    @Override
    public void driveToLine(double strafeSpeed) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void init() {

    }

    /*
     * Called periodically to actually execute the driving and rotating set by
     * the driveDistance() and rotateDegrees() methods
     */

    @Override
    public void periodic(){

    }
}

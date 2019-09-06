package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends RobotBase {

    /* This class is the heart of the robot. Initializes peripherals and starts various periodics */

    // Robot Modes and peripherals
    private IRobotMode disabledMode;
    private IRobotMode autonomousMode;
    private IRobotMode teleoperatedMode;

    private IDrive drive;
    private IHook hook;
    private ILauncher launcher;
    private ICargoGuide cargoGuide;
    private IGyroscopeSensor gyroscope;
    private ILineDetector lineDetector;
    private ICompressor compressor;

    // Instantiates the Modes and Peripherals
    public Robot() {
        gyroscope = new NavXMXP();
        //drive = new NullDrive();
        hook = new Hook();
        launcher = new Launcher();
        cargoGuide = new CargoGuide();
        lineDetector = new LineDetector();
        drive = new Drive(gyroscope, lineDetector);
        compressor = new Compressor();

        disabledMode = new DisabledMode(hook, launcher, lineDetector);
        autonomousMode = new HABLineAuto(drive);
        //autonomousMode = new HatchPanelAuto(drive, hook);
        teleoperatedMode = new TeleoperatedMode(drive, hook, launcher, cargoGuide, gyroscope, compressor);
        //teleoperatedMode = new MotorTest();
    }

    // Starts the robot and dashboard
    @Override
    public void startCompetition() {
        HAL.observeUserProgramStarting();

        IRobotMode currentMode = null;
        IRobotMode desiredMode = null;

        while (true) {
            desiredMode = getDesiredMode();

            if (desiredMode != currentMode) {
                LiveWindow.setEnabled(isTest());
                doPeripheralReinitialization();
            	desiredMode.init();
            	currentMode = desiredMode;
            }
            currentMode.periodic();
            doPeripheralPeriodicProcessing();
            SmartDashboard.updateValues();
            LiveWindow.updateValues();
        }
    }

    // Reinitializes peripherals
    private void doPeripheralReinitialization() {
        cargoGuide.init();
        drive.init();
        hook.init();
        launcher.init();
        compressor.init();
    }

    // Starts peripheral periodics
    private void doPeripheralPeriodicProcessing() {
        drive.periodic();
        launcher.periodic();

        Debug.periodic();
    }

    // Gets the desired mode based based off match events
    private IRobotMode getDesiredMode() {
        if (isDisabled()) {
            HAL.observeUserProgramDisabled();
            return disabledMode;
        } else if (isAutonomous()) {
            HAL.observeUserProgramAutonomous();
        return autonomousMode;
        } else if (isOperatorControl()) {
            HAL.observeUserProgramTeleop();
            return teleoperatedMode;
        } else if (isTest()) {
            HAL.observeUserProgramTest();
            return teleoperatedMode;
        } else {
            throw new IllegalStateException("Robot is in an invalid mode");
        }
    }

}

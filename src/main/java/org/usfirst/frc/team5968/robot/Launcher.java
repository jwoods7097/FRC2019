package org.usfirst.frc.team5968.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Launcher implements ILauncher
{

    /* This class controls the behavior of the Launcher */

    // Field for the motor
    private TalonSRX launcherMotor;

    // Variables and constants
    private double motorSpeed;
    private static final double HIGH = 0.9;
    private static final double LOW = 0.0;

    // Instantiates the launcher motor and stops the launcher
    public Launcher() {
        launcherMotor = new TalonSRX(PortMap.CAN.CONVEYER_MOTOR_CONTROLLER);
        launcherMotor.setInverted(true);

        stop();
    }

    // Stops the launcher motor
    @Override
    public void stop() {
        motorSpeed = LOW;
    }

    // Starts the launcher motor
    @Override
    public void start() {
        motorSpeed = HIGH;
    }

    // Stops the launcher motor upon startup
    @Override
    public void init() {
        stop();
    }
    
    // Periodically sets launcher motor to the appropriate speed
    @Override
    public void periodic() {
        launcherMotor.set(ControlMode.PercentOutput, motorSpeed);
    }
}

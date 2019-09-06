package org.usfirst.frc.team5968.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DriverStation;

public class Drive implements IDrive {

    /* This class controls the drive base of the robot */

    // Fields for robot peripherals
    private IGyroscopeSensor gyroscope;
    private ILineDetector lineDetector;
    private DriveMode driveMode;
    private IEncoder leftEncoder;
    private IEncoder rightEncoder;

    // Fields for drive base motors
    private TalonSRX leftMotorControllerLead;
    private TalonSRX leftMotorControllerFollow;
    private TalonSRX rightMotorControllerLead;
    private TalonSRX rightMotorControllerFollow;
    private TalonSRX middleMotorControllerLead;
    private TalonSRX middleMotorControllerFollow;

    // Field for the current completion routine
    private Runnable currentCompletionRoutine;

    // Variables for speed, angle, and distance
    private double xDirectionSpeed;
    private double yDirectionSpeed;
    private double desiredAngle;
    private double rotationSpeed;
    private double distanceInches;

    // Constants
    private static final double DELTA_ANGLE_SPEED_POWER = 1;
    private static final double MAINTAINING_HEADING_SPEED = 1.0;
    private static final double WHEEL_DIAMETER = 6.0; // inches
    private static final double ENCODER_RESOLUTION = 2048.0;

    public Drive(IGyroscopeSensor gyroscope, ILineDetector lineDetector){

        // Sets gyroscope and line detector fields to real peripherals
        this.gyroscope = gyroscope;
        this.lineDetector = lineDetector;

        // Sets the correct CAN port for each motor
        leftMotorControllerLead = new TalonSRX(PortMap.CAN.LEFT_MOTOR_CONTROLLER_LEAD);
        leftMotorControllerFollow = new TalonSRX(PortMap.CAN.LEFT_MOTOR_CONTROLLER_FOLLOW);
        rightMotorControllerLead = new TalonSRX(PortMap.CAN.RIGHT_MOTOR_CONTROLLER_LEAD);
        rightMotorControllerFollow = new TalonSRX(PortMap.CAN.RIGHT_MOTOR_CONTROLLER_FOLLOW);
        middleMotorControllerLead = new TalonSRX(PortMap.CAN.MIDDLE_MOTOR_CONTROLLER_LEAD);
        middleMotorControllerFollow = new TalonSRX(PortMap.CAN.MIDDLE_MOTOR_CONTROLLER_FOLLOW);

        // Sets motors to their default settings
        leftMotorControllerLead.configFactoryDefault();
        leftMotorControllerFollow.configFactoryDefault();
        middleMotorControllerLead.configFactoryDefault();
        middleMotorControllerFollow.configFactoryDefault();
        rightMotorControllerLead.configFactoryDefault();
        rightMotorControllerFollow.configFactoryDefault();

        // Sets motors to brake when doing nothing
        leftMotorControllerLead.setNeutralMode(NeutralMode.Brake);
        leftMotorControllerFollow.setNeutralMode(NeutralMode.Brake);
        middleMotorControllerLead.setNeutralMode(NeutralMode.Brake);
        middleMotorControllerFollow.setNeutralMode(NeutralMode.Brake);
        rightMotorControllerLead.setNeutralMode(NeutralMode.Brake);
        rightMotorControllerFollow.setNeutralMode(NeutralMode.Brake);

        // Inverts right and middle motors
        leftMotorControllerFollow.setInverted(false);
        leftMotorControllerLead.setInverted(false);
        middleMotorControllerFollow.setInverted(true);
        middleMotorControllerLead.setInverted(true);
        rightMotorControllerLead.setInverted(true);
        rightMotorControllerFollow.setInverted(true);

        // Sets follower motors to their leads
        leftMotorControllerFollow.follow(leftMotorControllerLead);
        rightMotorControllerFollow.follow(rightMotorControllerLead);
        middleMotorControllerFollow.follow(middleMotorControllerLead);

        // Instantiates motor encoders
        leftEncoder = new TalonEncoder(leftMotorControllerFollow);
        rightEncoder = new TalonEncoder(rightMotorControllerFollow);

        // Variable for how far the wheel travels per encoder pulse
        double distancePerPulse = (WHEEL_DIAMETER * Math.PI) / ENCODER_RESOLUTION;

        // Sets encoder distance per pulse
        leftEncoder.setDistancePerPulse(distancePerPulse);
        rightEncoder.setDistancePerPulse(distancePerPulse);

        // Inverts the left encoder
        leftEncoder.setInverted(true);

    }

    // Gets the current drive mode
    @Override
    public DriveMode getCurrentDriveMode(){
        return driveMode;
    }

    // Makes the robot drive a certain distance at a certain speed
    @Override
    public void driveDistance(double distanceInches, double yDirectionSpeed) {
        driveDistance(distanceInches, yDirectionSpeed, null);
    }

    // Unused method for rotating the robot a certain angle at a certain speed
    @Override
    public void rotateDegrees(double angle, double angularSpeed) {

    }

    // Logic for driving a distance. Sets the necessary variables for doing so
    @Override
    public void driveDistance(double distanceInches, double yDirectionSpeed, Runnable completionRoutine) {
        setCompletionRoutine(completionRoutine);
        this.yDirectionSpeed = yDirectionSpeed;
        driveMode = DriveMode.AUTODRIVINGTRAIGHT;
        this.distanceInches = distanceInches;
        leftEncoder.reset();
        rightEncoder.reset();
    }

    // Unused rotation method
    @Override
    public void rotateDegrees(double relativeAngle, double angularSpeed, Runnable completionRoutine) {
        
    }

    // Sets x and y direction speeds of the robot when under driver control
    @Override
    public void driveManual(double xDirectionSpeed, double yDirectionSpeed) {
        setCompletionRoutine(null);
        this.xDirectionSpeed = xDirectionSpeed;
        this.yDirectionSpeed = yDirectionSpeed;
        driveMode = DriveMode.DRIVERCONTROL;
    }

    // Stops the robot and makes it face forward
    public void stop() {
        driveManual(0.0, 0.0);
        lookAt(0.0, 0.0);
    }

    // Rotates the robot to a certain angle at a certain speed
    @Override
    public void lookAt(double angle, double speed) {
        driveMode = DriveMode.DRIVERCONTROL;
        setCompletionRoutine(null);
        desiredAngle = MathUtilities.normalizeAngle(angle);
        rotationSpeed = speed;
    }

    // Compensates for drift caused by off-center middle wheel
    @Override
    public void maintainHeading() {
        driveMode = DriveMode.DRIVERCONTROL;
        setCompletionRoutine(null);
        desiredAngle = gyroscope.getYaw();
        rotationSpeed = MAINTAINING_HEADING_SPEED;
    }

    // Makes the robot drive until it finds a line
    @Override
    public void driveToLine(double strafeSpeed,Runnable completionRoutine) {
        setCompletionRoutine(completionRoutine);
        xDirectionSpeed = strafeSpeed;
        driveMode = DriveMode.LINEALIGNMENT;
    }

    // Same as method above without completionRoutine parameter
    @Override
    public void driveToLine(double strafeSpeed) {
        driveToLine(strafeSpeed, null);
    }

    // Sets the completion routine if one is not already active
    private void setCompletionRoutine(Runnable completionRountime) {
        if (currentCompletionRoutine != null) {
            throw new IllegalStateException("Tried to perform an autonomous action while one was already in progress!");
        }

        currentCompletionRoutine = completionRountime;
    }

    // Handles completion routine ending
    private void handleActionEnd() {
        // Saves currentCompletionRoutine before calling stop() so nothing is cleared
        Runnable oldCompletionRoutine = currentCompletionRoutine;

        // Stops robot from moving
        stop();

        // Dispatch the completion routine if there is one configured
        if (oldCompletionRoutine != null) {
            currentCompletionRoutine = null;
            oldCompletionRoutine.run();
        }

    }

    // Runs periodically. Controls the motion of the robot
    private void manualControlPeriodic() {
        // Variables for the speed of each motor pair
        double leftSpeed;
        double rightSpeed;
        double middleSpeed;

        // Linear Motion

        // The angle the robot is moving at relative to the field
        double fieldAngle = Math.atan2(yDirectionSpeed, xDirectionSpeed) - (Math.PI / 2);
        // Adds the angle the robot is facing to to its movement angle
        double robotDriveAngle = fieldAngle + gyroscope.getYaw();

        // Pythagorean theorem to figure out speed magnitude
        double speedMagnitude = Math.sqrt(Math.pow(xDirectionSpeed, 2) + Math.pow(yDirectionSpeed, 2));
        // Sets left and right motor speeds proportional to x-component of the robot drive angle
        leftSpeed = Math.cos(robotDriveAngle) * speedMagnitude * .7;
        rightSpeed = leftSpeed;
        // Sets middle motor speed proportional to y-component of robot drive angle
        middleSpeed = Math.sin(robotDriveAngle) * speedMagnitude;

        // Reduce power draw at competition
        //leftSpeed *= .75;
        //rightSpeed *= .75;
        //middleSpeed *= .75;

        // Angular Motion
        if (true) {
            // Variable for the angle the robot needs to rotate
            double deltaAngle = gyroscope.getYaw() - desiredAngle;

            //Debug.logPeriodic(" desiredAngle: " + desiredAngle);
            //Debug.logPeriodic(" deltaAngle1: " + deltaAngle);

            // Moves the angle range from (0, 2pi) to (-pi, pi)
            if (Math.abs(deltaAngle) > Math.PI) {
                deltaAngle -= (Math.PI * 2) * Math.signum(deltaAngle);
            }

            // Debug printouts
            //Debug.logPeriodic(" deltaAngle2: " + deltaAngle);
            Debug.logPeriodic("Yaw: " + gyroscope.getYaw());

            // Sets the speed proportional to the square of the ratio of deltaAngle to pi
            double actualSpeed = rotationSpeed * Math.pow(Math.abs(deltaAngle) / Math.PI, DELTA_ANGLE_SPEED_POWER);
            leftSpeed *= 1 - actualSpeed;
            rightSpeed *= 1 - actualSpeed;

            // Adds or subtracts the actual speed depending on whether the robot is turning left or right
            if (deltaAngle < 0) {
                leftSpeed += actualSpeed;
                rightSpeed -= actualSpeed;
            }
            else {
                leftSpeed -= actualSpeed;
                rightSpeed += actualSpeed;
            }

            //Debug.logPeriodic(" actualSpeed: " + actualSpeed);
        }

        // Set Motor Speeds
        leftMotorControllerLead.set(ControlMode.PercentOutput, leftSpeed);
        rightMotorControllerLead.set(ControlMode.PercentOutput, rightSpeed);
        middleMotorControllerLead.set(ControlMode.PercentOutput, middleSpeed);
    }

    // Stops the robot and resets its yaw upon startup
    @Override
    public void init() {
        currentCompletionRoutine = null;
        stop();
        if(DriverStation.getInstance().isAutonomous()) {
            gyroscope.resetYaw();
        }
    }

    @Override
    public void periodic() {
        //Debug.logPeriodic("Left Encoder: " + leftEncoder.getDistance());
        //Debug.logPeriodic("Right Encoder: " + rightEncoder.getDistance());

        // If robot is autonomous, sets lead motors to respective speeds
        if (driveMode == DriveMode.DRIVERCONTROL) {
            manualControlPeriodic();
        } else if (driveMode == DriveMode.AUTODRIVINGTRAIGHT) {
            leftMotorControllerLead.set(ControlMode.PercentOutput, yDirectionSpeed);
            rightMotorControllerLead.set(ControlMode.PercentOutput, yDirectionSpeed);
            middleMotorControllerLead.set(ControlMode.PercentOutput, 0.0);

            // Check if we've completed our travel
            double averageDistanceTraveled = Math.abs((leftEncoder.getDistance() + rightEncoder.getDistance()) / 2);
            if (averageDistanceTraveled > distanceInches) {
                handleActionEnd();
            }
        } else if (driveMode == DriveMode.AUTOROTATING) {
            throw new IllegalArgumentException("Auto-driving rotation is not implemented!");
        } else if (driveMode == DriveMode.LINEALIGNMENT) {
            leftMotorControllerLead.set(ControlMode.PercentOutput, 0.0);
            rightMotorControllerLead.set(ControlMode.PercentOutput, 0.0);
            middleMotorControllerLead.set(ControlMode.PercentOutput, xDirectionSpeed);

            if (lineDetector.isOnLine()) {
                handleActionEnd();

            }

        } else {
            throw new IllegalArgumentException("The drive base controller is in an invalid drive mode.");
        }
    }
}

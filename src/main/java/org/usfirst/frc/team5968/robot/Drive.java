package org.usfirst.frc.team5968.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DriverStation;

public class Drive implements IDrive {

    private IGyroscopeSensor gyroscope;
    private ILineDetector lineDetector;
    private DriveMode driveMode;
    private IEncoder leftEncoder;
    private IEncoder rightEncoder;

    private TalonSRX leftMotorControllerLead;
    private TalonSRX leftMotorControllerFollow;
    private TalonSRX rightMotorControllerLead;
    private TalonSRX rightMotorControllerFollow;
    private TalonSRX middleMotorControllerLead;
    private TalonSRX middleMotorControllerFollow;

    private Runnable currentCompletionRoutine;

    private double xDirectionSpeed;
    private double yDirectionSpeed;
    private double desiredAngle;
    private double rotationSpeed;
    private double distanceInches;

    private static final double DELTA_ANGLE_SPEED_POWER = 1;
    private static final double MAINTAINING_HEADING_SPEED = 1.0;
    private static final double WHEEL_DIAMETER = 6.0; // inches
    private static final double ENCODER_RESOLUTION = 2048.0;

    public Drive(IGyroscopeSensor gyroscope, ILineDetector lineDetector){

        this.gyroscope = gyroscope;
        this.lineDetector = lineDetector;

        leftMotorControllerLead = new TalonSRX(PortMap.CAN.LEFT_MOTOR_CONTROLLER_LEAD);
        leftMotorControllerFollow = new TalonSRX(PortMap.CAN.LEFT_MOTOR_CONTROLLER_FOLLOW);
        rightMotorControllerLead = new TalonSRX(PortMap.CAN.RIGHT_MOTOR_CONTROLLER_LEAD);
        rightMotorControllerFollow = new TalonSRX(PortMap.CAN.RIGHT_MOTOR_CONTROLLER_FOLLOW);
        middleMotorControllerLead = new TalonSRX(PortMap.CAN.MIDDLE_MOTOR_CONTROLLER_LEAD);
        middleMotorControllerFollow = new TalonSRX(PortMap.CAN.MIDDLE_MOTOR_CONTROLLER_FOLLOW);

        leftMotorControllerLead.configFactoryDefault();
        leftMotorControllerFollow.configFactoryDefault();
        middleMotorControllerLead.configFactoryDefault();
        middleMotorControllerFollow.configFactoryDefault();
        rightMotorControllerLead.configFactoryDefault();
        rightMotorControllerFollow.configFactoryDefault();

        leftMotorControllerLead.setNeutralMode(NeutralMode.Brake);
        leftMotorControllerFollow.setNeutralMode(NeutralMode.Brake);
        middleMotorControllerLead.setNeutralMode(NeutralMode.Brake);
        middleMotorControllerFollow.setNeutralMode(NeutralMode.Brake);
        rightMotorControllerLead.setNeutralMode(NeutralMode.Brake);
        rightMotorControllerFollow.setNeutralMode(NeutralMode.Brake);

        leftMotorControllerFollow.setInverted(false);
        leftMotorControllerLead.setInverted(false);
        middleMotorControllerFollow.setInverted(true);
        middleMotorControllerLead.setInverted(true);
        rightMotorControllerLead.setInverted(true);
        rightMotorControllerFollow.setInverted(true);

        leftMotorControllerFollow.follow(leftMotorControllerLead);
        rightMotorControllerFollow.follow(rightMotorControllerLead);
        middleMotorControllerFollow.follow(middleMotorControllerLead);

        leftEncoder = new TalonEncoder(leftMotorControllerFollow);
        rightEncoder = new TalonEncoder(rightMotorControllerFollow);

        double distancePerPulse = (WHEEL_DIAMETER * Math.PI) / ENCODER_RESOLUTION;

        leftEncoder.setDistancePerPulse(distancePerPulse);
        rightEncoder.setDistancePerPulse(distancePerPulse);

        leftEncoder.setInverted(true);

    }

    @Override
    public DriveMode getCurrentDriveMode(){
        return driveMode;
    }

    @Override
    public void driveDistance(double distanceInches, double yDirectionSpeed) {
        driveDistance(distanceInches, yDirectionSpeed, null);
    }

    @Override
    public void rotateDegrees(double angle, double angularSpeed) {

    }

    @Override
    public void driveDistance(double distanceInches, double yDirectionSpeed, Runnable completionRoutine) {
        setCompletionRoutine(completionRoutine);
        this.yDirectionSpeed = yDirectionSpeed;
        driveMode = DriveMode.AUTODRIVINGTRAIGHT;
        this.distanceInches = distanceInches;
        leftEncoder.reset();
        rightEncoder.reset();
    }

    @Override
    public void rotateDegrees(double relativeAngle, double angularSpeed, Runnable completionRoutine) {
        
    }

    @Override
    public void driveManual(double xDirectionSpeed, double yDirectionSpeed) {
        setCompletionRoutine(null);
        this.xDirectionSpeed = xDirectionSpeed;
        this.yDirectionSpeed = yDirectionSpeed;
        driveMode = DriveMode.DRIVERCONTROL;
    }

    public void stop() {
        driveManual(0.0, 0.0);
        lookAt(0.0, 0.0);
    }

    @Override
    public void lookAt(double angle, double speed) {
        driveMode = DriveMode.DRIVERCONTROL;
        setCompletionRoutine(null);
        desiredAngle = MathUtilities.normalizeAngle(angle);
        rotationSpeed = speed;
    }

    @Override
    public void maintainHeading() {
        driveMode = DriveMode.DRIVERCONTROL;
        setCompletionRoutine(null);
        desiredAngle = gyroscope.getYaw();
        rotationSpeed = MAINTAINING_HEADING_SPEED;
    }

    @Override
    public void driveToLine(double strafeSpeed,Runnable completionRoutine) {
        setCompletionRoutine(completionRoutine);
        xDirectionSpeed = strafeSpeed;
        driveMode = DriveMode.LINEALIGNMENT;
    }

    @Override
    public void driveToLine(double strafeSpeed) {
        driveToLine(strafeSpeed, null);
    }

    private void setCompletionRoutine(Runnable completionRountime) {
        if (currentCompletionRoutine != null) {
            throw new IllegalStateException("Tried to perform an autonomous action while one was already in progress!");
        }

        currentCompletionRoutine = completionRountime;
    }

    private void handleActionEnd() {
        // Saves currentCompletionRoutine before calling stop() so nothing is cleared
        Runnable oldCompletionRoutine = currentCompletionRoutine;

        // Stops robot from moving
        stop();

        // Dispatch the completion routin if there is one configured
        if (oldCompletionRoutine != null) {
            currentCompletionRoutine = null;
            oldCompletionRoutine.run();
        }

    }

    private void manualControlPeriodic() {
        double leftSpeed;
        double rightSpeed;
        double middleSpeed;

        // Linear Motion
        double fieldAngle = Math.atan2(yDirectionSpeed, xDirectionSpeed) - (Math.PI / 2);
        double robotDriveAngle = fieldAngle + gyroscope.getYaw();

        double speedMagnitude = Math.sqrt(Math.pow(xDirectionSpeed, 2) + Math.pow(yDirectionSpeed, 2));
        leftSpeed = Math.cos(robotDriveAngle) * speedMagnitude * .7;
        rightSpeed = leftSpeed;
        middleSpeed = Math.sin(robotDriveAngle) * speedMagnitude;

        // Reduce power draw at competition
        //leftSpeed *= .75;
        //rightSpeed *= .75;
        //middleSpeed *= .75;

        // Angular Motion
        if (true) {
            double deltaAngle = gyroscope.getYaw() - desiredAngle;

            //Debug.logPeriodic(" desiredAngle: " + desiredAngle);
            //Debug.logPeriodic(" deltaAngle1: " + deltaAngle);

            if (Math.abs(deltaAngle) > Math.PI) {
                deltaAngle -= (Math.PI * 2) * Math.signum(deltaAngle);
            }

            //Debug.logPeriodic(" deltaAngle2: " + deltaAngle);
            Debug.logPeriodic("Yaw: " + gyroscope.getYaw());

            double actualSpeed = rotationSpeed * Math.pow(Math.abs(deltaAngle) / Math.PI, DELTA_ANGLE_SPEED_POWER);
            leftSpeed *= 1 - actualSpeed;
            rightSpeed *= 1 - actualSpeed;

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

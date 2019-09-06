package org.usfirst.frc.team5968.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;

public class MotorTest implements IRobotMode
    {
        /* This class is a robot mode for testing the motors.
           requires a joystick to be plugged in */
        
        private Boolean lastButton0 = false;
        private Boolean lastButton1 = false;
        private TalonSRX[] motors;
        private int currentMotor = 0;
        private Joystick joystick;

        private Boolean lastDoubleButton = false;
        private Boolean doubleMode = false;

        // Assigns each motor to their respective CAN ports
        public MotorTest() {
            int[] canPorts = {
                10, 3, 6, 4, 5, 7
            };

            motors = new TalonSRX[canPorts.length];

            for (int i = 0; i < canPorts.length; i++) {
                motors[i] = new TalonSRX(canPorts[i]);
            }

            joystick = new Joystick(0);
        }

        // Resets motor and button settings
        @Override
        public void init()
        {
            currentMotor = 0;
            lastButton0 = false;
            lastButton1 = false;
            doubleMode = false;
            lastDoubleButton = false;
        }

        @Override
        public void periodic()
        {
            // Booleans to store whether a button has been pressed
            Boolean button0 = joystick.getRawButton(11);
            Boolean button1 = joystick.getRawButton(12);
            Boolean doubleButton = joystick.getRawButton(7);

            // Changes which motor is being tested for each button press
            if (button0 && !lastButton0) {
                currentMotor--;

                if (currentMotor < 0) {
                    currentMotor = motors.length - 1;
                }
            } else if (button1 && !lastButton1) {
                currentMotor++;

                if (currentMotor >= motors.length) {
                    currentMotor = 0;
                }
            }

            if (doubleButton && !lastDoubleButton) {
                doubleMode = !doubleMode;
            }

            // Sets motor speed to joystick y-value
            lastButton0 = button0;
            lastButton1 = button1;
            lastDoubleButton = doubleButton;
            double speed = joystick.getY();
            motors[currentMotor].set(ControlMode.PercentOutput, speed);
            int nextMotorIndex = -1;

            if (doubleMode) {
                nextMotorIndex = currentMotor + 1;

                if (nextMotorIndex >= motors.length) {
                    nextMotorIndex = 0;
                }

                motors[nextMotorIndex].set(ControlMode.PercentOutput, speed);
            }

            for (int i = 0; i < motors.length; i++) {
                if (i == currentMotor || i == nextMotorIndex) {
                    continue;
                }

                motors[i].set(ControlMode.PercentOutput, 0.0);
            }
        }
    }

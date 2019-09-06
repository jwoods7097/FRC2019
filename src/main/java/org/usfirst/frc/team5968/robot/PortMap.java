package org.usfirst.frc.team5968.robot;

public class PortMap {
    
    /* This class is where the ports for peripherals, motors, etc. are assigned */

    // Ports on the computer
    public class USB {
        public static final int XBOXCONTROLLER = 0;
    }
    
    // CAN ports
    public class CAN {
        public static final int LEFT_MOTOR_CONTROLLER_LEAD = 10;
        public static final int LEFT_MOTOR_CONTROLLER_FOLLOW = 3;
        public static final int RIGHT_MOTOR_CONTROLLER_LEAD = 6;
        public static final int RIGHT_MOTOR_CONTROLLER_FOLLOW = 4;
        public static final int MIDDLE_MOTOR_CONTROLLER_LEAD = 5;
        public static final int MIDDLE_MOTOR_CONTROLLER_FOLLOW = 7;
        public static final int CONVEYER_MOTOR_CONTROLLER = 2;
        public static final int PCM = 0;
    }
    
}

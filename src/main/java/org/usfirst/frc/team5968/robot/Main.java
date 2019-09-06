package org.usfirst.frc.team5968.robot; 

import edu.wpi.first.wpilibj.RobotBase;

// Starts the robot
public final class Main {
  private Main() {
  }

  public static void main(String... args) {
    RobotBase.startRobot(Robot::new);
  }
}

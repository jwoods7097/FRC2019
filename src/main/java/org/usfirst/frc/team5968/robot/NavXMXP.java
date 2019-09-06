package org.usfirst.frc.team5968.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;

public class NavXMXP implements IGyroscopeSensor {

    /* This class provides various methods to get values from the NavX */

    private AHRS navX;
    
    // Resets the NavX
    public NavXMXP() {
        Debug.log("Start");
        navX = new AHRS(SerialPort.Port.kUSB);
        navX.enableBoardlevelYawReset(false);
        navX.reset();
    }
    
    // Returns pitch from the gyroscope
    @Override
    public double getPitch() {
        return Math.toRadians(navX.getPitch()); 
    }

    // Returns roll from the gyroscope
    @Override
    public double getRoll() {
        return Math.toRadians(navX.getRoll());
    }

    // Gets the yaw from the gyroscope
    @Override
    public double getYaw() {
        return Math.toRadians(navX.getYaw());
    }

    // Resets the yaw
    @Override
    public void resetYaw() {
        navX.reset();
    }

}

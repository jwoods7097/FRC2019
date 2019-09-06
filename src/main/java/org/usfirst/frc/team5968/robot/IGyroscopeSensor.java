package org.usfirst.frc.team5968.robot;

// Interface for Gyroscope
public interface IGyroscopeSensor {

    public double getPitch();

    public double getRoll();

    public double getYaw();

    public void resetYaw();

}

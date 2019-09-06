package org.usfirst.frc.team5968.robot; 

// Interface for Encoder
public interface IEncoder {

    public void setInverted(boolean inverted);

    public void setDistancePerPulse(double distance);

    public double getDistance();
    
    public void reset();

}

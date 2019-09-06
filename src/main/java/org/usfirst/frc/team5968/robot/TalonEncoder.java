package org.usfirst.frc.team5968.robot;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class TalonEncoder implements IEncoder {

    /* This class provides methods for using the Talon Encoders */

    private boolean isInverted = false;
    private TalonSRX talon;
    private double distancePerPulse = 1.0;
    
    // Sets talon field to real talon
    public TalonEncoder(TalonSRX talon) {
        this.talon = talon;
    }

    // Inverts the direction of the motor
    @Override
    public void setInverted(boolean inverted) {
        isInverted = inverted;
    }

    // Sets the distance per pulse of the encoder
    @Override
    public void setDistancePerPulse(double distance) {
        distancePerPulse = distance;
    }

    // Gets the distance from the raw encoder value
    @Override
    public double getDistance() {
        int rawEncoderValue =
        talon.getSensorCollection().getQuadraturePosition();
        if (isInverted) {
            rawEncoderValue = -rawEncoderValue;
        }
        // Convert the raw reading to distance and return it
        return ((double)rawEncoderValue) * distancePerPulse;
    }

    // Resets the encoder
    @Override
    public void reset() {
    ErrorCode error = talon.getSensorCollection().setQuadraturePosition(0, 500);
        if (error != ErrorCode.OK) {
            // Bleh, overly generic exception.
            // If we were feeling fancy, we'd determine an ideal exception
            // based on the error code.
            throw new RuntimeException("Failed to reset Talon encoder! Error code = " + error.toString());
        }
    }
}
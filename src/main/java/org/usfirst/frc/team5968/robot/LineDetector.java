package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.AnalogInput;

public class LineDetector implements ILineDetector {

    /* This class provides methods for using the line detector */

    // Field for the Line Detector
    private AnalogInput analogInput;

    // Threshold for sensor input to tell if its on a line
    private static final double LINE_THRESHOLD = 3.5;

    // Instantiates the line detector
    public LineDetector() {
        analogInput = new AnalogInput(0);
    }

    // Determines if robot is on line
    @Override
    public boolean isOnLine() {

        if (getRawValue() > LINE_THRESHOLD) {
            return true;
        } else {
            return false;
        }

    }

    // Gets the raw value of the sensor
    @Override
    public double getRawValue() {
        return analogInput.getVoltage();
    }

}

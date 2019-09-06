package org.usfirst.frc.team5968.robot;

public class MathUtilities {

    // Keeps angle within the range (-pi, pi)
    public static double normalizeAngle(double angle) {
        final double twoPI = Math.PI * 2.0;
        return angle - (twoPI * Math.floor((angle + Math.PI) / twoPI));
    }
}

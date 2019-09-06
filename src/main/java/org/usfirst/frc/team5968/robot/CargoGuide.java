package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class CargoGuide implements ICargoGuide {

    /* This class controls the behavior of the Cargo Guide */

    // Field for the Guide Piston
    private DoubleSolenoid guidePiston;

    // Sets the piston to the correct ports on the PCM. 1 is the forward port, 0 is the reverse port
    public CargoGuide() {
        guidePiston = new DoubleSolenoid(1, 0);
    }

    // Retracts the Guide upon startup.
    @Override
    public void init() {
        disengageGuide();
    }

    // Extends the Cargo Guide
    @Override
    public void engageGuide() {
        guidePiston.set(DoubleSolenoid.Value.kForward);
    }

    // Retracts the Cargo Guide
    @Override
    public void disengageGuide() {
        guidePiston.set(DoubleSolenoid.Value.kReverse);
    }

}

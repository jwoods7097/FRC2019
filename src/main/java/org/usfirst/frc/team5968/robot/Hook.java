package org.usfirst.frc.team5968.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;

public class Hook implements IHook {

    /* This class controlled the behavior of the hook */

    // Field for the hook piston
    private DoubleSolenoid piston;

    // Instantiates the field piston and deactivates hook
    public Hook (){
        piston = new DoubleSolenoid(3, 2);
        releasePanel();
    }

    // Activates piston to grab panel
    @Override
    public void grabPanel() {
        piston.set(DoubleSolenoid.Value.kForward);
    }

    // Deactivates piston to release panel
    @Override
    public void releasePanel() {
        piston.set(DoubleSolenoid.Value.kReverse);
    }

    // Deactivates piston upon startup
    @Override
    public void init() {
        releasePanel();
    }
}

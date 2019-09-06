package org.usfirst.frc.team5968.robot;

public class Compressor implements ICompressor {

    /* This class controls the Compressor */
    
    // Instantiates the Compressor
    private edu.wpi.first.wpilibj.Compressor compressor;
    
    // Sets the Compressor to the proper port on the PCM
    public Compressor() {
        compressor = new edu.wpi.first.wpilibj.Compressor(PortMap.CAN.PCM);
    }

    // Starts the Compressor
    public void enable() {
        compressor.start();
    }

    // Stops the Compressor
    public void disable() {
        compressor.stop();
    }

    // Starts the Compressor upon startup
    public void init() {
        enable();
    }
}
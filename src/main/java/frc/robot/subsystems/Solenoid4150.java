package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Solenoid4150 {

    private DoubleSolenoid solenoid;
    private boolean setOnce;
    private boolean currentState;

    public Solenoid4150(DoubleSolenoid solenoid) {
        this.solenoid = solenoid;
        this.setOnce = false;
    }

    public void set(boolean forward) {
        if (forward != currentState || !setOnce) {
            currentState = forward;
            Value setTo = forward ? Value.kForward : Value.kReverse;
            this.solenoid.set(setTo);
            this.setOnce = true;
        }
    }

}
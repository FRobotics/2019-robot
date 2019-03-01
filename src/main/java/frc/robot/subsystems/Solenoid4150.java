package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Solenoid4150 {

    private DoubleSolenoid solenoid;

    public Solenoid4150(DoubleSolenoid solenoid) {
        this.solenoid = solenoid;
    }

    public void set(boolean forward) {
        Value setTo = forward ? Value.kForward : Value.kReverse;
        if(this.solenoid.get() != setTo) {
            this.solenoid.set(setTo);
        }
    }

}
package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class HatchSystem {

    private DoubleSolenoid solenoid;

    public void init(DoubleSolenoid solenoid) {
        this.solenoid = solenoid;
    }

    public void comeTogether() {
        this.solenoid.set(Value.kForward);
    }

    public void pushOutward() {
        this.solenoid.set(Value.kReverse);
    }

    public void reset() {
        this.comeTogether();
    }

}
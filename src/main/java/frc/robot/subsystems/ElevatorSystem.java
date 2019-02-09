package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class ElevatorSystem {

    private SpeedController winch;
    private DoubleSolenoid brake;
    private DoubleSolenoid arms;
    // TODO: add sonar

    public void init(SpeedController winch, DoubleSolenoid brake, DoubleSolenoid arms) {
        this.winch = winch;
        this.brake = brake;
        this.arms = arms;
    }

    public void moveUp(double speed) {
        brake.set(Value.kReverse);
        winch.set(speed);
    }

    public void moveDown(double speed) {
        brake.set(Value.kReverse);
        winch.set(-speed);
    }

    public void stop() {
        winch.set(0);
        brake.set(Value.kForward);
    }

    public void lowerArms() {
        arms.set(Value.kForward);
    }

    public void raiseArms() {
        arms.set(Value.kReverse);
    }

    public void reset() {
        this.stop();
        this.raiseArms();
    }

}
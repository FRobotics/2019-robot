package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.robot.subsystems.base.Motor;

public class ElevatorSystem {

    private Motor winch;
    private DoubleSolenoid brake;
    private DoubleSolenoid arms;
    // TODO: add sonar

    public void init(Motor winch, DoubleSolenoid brake, DoubleSolenoid arms) {
        this.winch = winch;
        this.brake = brake;
        this.arms = arms;
    }

    public void moveUp(double speed) {
        brake.set(Value.kReverse);
        winch.setSpeed(speed);
    }

    public void moveDown(double speed) {
        brake.set(Value.kReverse);
        winch.setSpeed(-speed);
    }

    public void stop() {
        winch.setSpeed(0);
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
package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;

public class Roller {
    private SpeedController motor;
    private DoubleSolenoid solenoid;

    public void init(SpeedController motor, DoubleSolenoid solenoid) {
        this.motor = motor;
        this.solenoid = solenoid;
    }

    public void pickupBall() {
        motor.set(-1);
    }

    public void releaseBall() {
        motor.set(1);
    }

    public void stopBallMotor() {
        motor.set(0);
    }
}

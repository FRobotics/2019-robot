package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class BallSystem {
    private SpeedController motor;
    private DoubleSolenoid arms;
    private DoubleSolenoid puncher;


    public void init(SpeedController motor, DoubleSolenoid arms, DoubleSolenoid puncher) {
        this.motor = motor;
        this.arms = arms;
        this.puncher = puncher;
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

    public void armsDown() {
        arms.set(Value.kForward);
    }

    public void raiseArms() {
        arms.set(Value.kReverse);
    }

    public void punchBall() {
        puncher.set(Value.kForward);
    }

    public void retractPuncher() {
        puncher.set(Value.kReverse);
    }

    public void reset() {
        raiseArms();
        stopBallMotor();
        retractPuncher();
    }
}

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.robot.subsystems.base.Motor;

public class BallSystem {
    private Motor motor;
    private DigitalInput sensor;
    private DoubleSolenoid arms;
    private DoubleSolenoid puncher;


    public void init(Motor motor, DoubleSolenoid arms, DoubleSolenoid puncher, DigitalInput sensor) {
        this.motor = motor;
        this.arms = arms;
        this.puncher = puncher;
        this.sensor = sensor;
    }

    public void pickupBall() {
        motor.setPercentOutput(-0.6);
    }

    public void stopBallMotor() {
        motor.setPercentOutput(0);
    }

    public void lowerArms() {
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

    public boolean sensedBall() {
        return sensor.get();
    }

    public void reset() {
        lowerArms();
        stopBallMotor();
        retractPuncher();
    }
}

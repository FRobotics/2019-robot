package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.subsystems.base.Motor;

public class BallSystem {
    private Motor motor;
    private DigitalInput sensor;
    private Solenoid4150 arms;
    private Solenoid4150 puncher;


    public void init(Motor motor, Solenoid4150 arms, Solenoid4150 puncher, DigitalInput sensor) {
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
        arms.set(true);
    }

    public void raiseArms() {
        arms.set(false);
    }

    public void punchBall() {
        puncher.set(true);
    }

    public void retractPuncher() {
        puncher.set(false);
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

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.robot.subsystems.base.Motor;

public class ElevatorSystem {

    private Motor winch;
    private DoubleSolenoid brake;
    private DoubleSolenoid arms;
    private Ultrasonic sensor;

    public void init(Motor winch, DoubleSolenoid brake, DoubleSolenoid arms, Ultrasonic sensor) {
        this.winch = winch;
        this.brake = brake;
        this.arms = arms;
        this.sensor = sensor;
        this.sensor.setAutomaticMode(true);
    }

    public void move(double speed) {
        brake.set(Value.kReverse);
        winch.setSpeed(speed);
    }

    public void moveUp(double speed) {
        this.move(speed);
    }

    public void moveDown(double speed) {
        this.move(-speed);
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

    public double getHeight() {
        return sensor.getRangeInches();
    }

}
package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Counter;
import frc.robot.subsystems.base.Motor;

public class ElevatorSystem {

    private final static double heightMult = 1000000.0/10.0/2.54;

    private Motor winch;
    private Solenoid4150 brake;
    private Solenoid4150 arms;
    private Counter sensor;

    public void init(Motor winch, Solenoid4150 brake, Solenoid4150 arms, Counter sensor) {
        this.winch = winch;
        this.brake = brake;
        this.arms = arms;
        this.sensor = sensor;
        this.sensor.setSemiPeriodMode(true);
        //this.sensor.setDistancePerPulse(1000000/10/2.54);
    }

    public void move(double speed) {
        brake.set(false);
        winch.setPercentOutput(-speed);
    }

    public void moveUp(double speed) {
        this.move(speed);
    }

    public void moveDown(double speed) {
        this.move(-speed);
    }

    public void stop() {
        winch.setSpeed(0);
        brake.set(true);
    }

    public void lowerArms() {
        arms.set(true);
    }

    public void raiseArms() {
        arms.set(false);
    }

    public void reset() {
        this.stop();
        this.raiseArms();
    }

    public double getHeight() {
        return sensor.getPeriod() * heightMult;
    }

}
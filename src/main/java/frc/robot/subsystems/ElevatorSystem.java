package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.robot.subsystems.base.Motor;

public class ElevatorSystem {

    private final static double heightMult = 1000000.0/10.0/2.54;

    private Motor winch;
    private DoubleSolenoid brake;
    private DoubleSolenoid arms;
    private Counter sensor;

    public void init(Motor winch, DoubleSolenoid brake, DoubleSolenoid arms, Counter sensor) {
        this.winch = winch;
        this.brake = brake;
        this.arms = arms;
        this.sensor = sensor;
        this.sensor.setSemiPeriodMode(true);
        //this.sensor.setDistancePerPulse(1000000/10/2.54);
    }

    public void move(double speed) {
        if(speed > 0.3) {
            brake.set(Value.kReverse);
        } else {
            brake.set(Value.kForward);
        }
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
        return sensor.getPeriod() * heightMult;
    }

}
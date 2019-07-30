package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Counter;
import frc.robot.subsystems.base.Motor;

public class ElevatorSystem {

    private static final int MAX_HEIGHT = 91;
    private static final int MIN_HEIGHT = 38;
    private final static double heightMult = 1000000.0 / 10.0 / 2.54;

    private Motor winch;
    private Solenoid4150 brake;
    private Solenoid4150 arms;
    private Counter sensor;
    private double height;

    public void init(Motor winch, Solenoid4150 brake, Solenoid4150 arms, Counter sensor) {
        this.winch = winch;
        this.brake = brake;
        this.arms = arms;
        this.sensor = sensor;
        this.sensor.setSemiPeriodMode(true);
        // this.sensor.setDistancePerPulse(1000000/10/2.54);
        this.heightIssue = false;
    }

    public void move(double speed) {
        this.move(speed, true);
    }

    public void move(double speed, boolean enableBreak) {
        if ((enableBreak && speed == 0) || (this.getHeight() >= MAX_HEIGHT && speed > 0) || (this.getHeight() <= MIN_HEIGHT && speed < 0)) {
            this.stop();
        } else {
            brake.set(false);
            winch.setPercentOutput(speed);
        }
    }

    public void moveUp(double speed) {
        this.move(speed);
    }

    public void moveDown(double speed) {
        this.move(-speed);
    }

    public void stop() {
        this.winch.setPercentOutput(0);
        this.brake.set(true);
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

    private double lastHeight;
    private boolean heightIssue;

    public double getHeight() {
        return height;
    }

    private boolean firstHeight = true;
    private boolean firstIssue = true;

    public void updateHeight() {
        this.height = getRawHeight();
        /*if(firstHeight) {
            lastHeight = height;
            firstHeight = false;
        } else {
            if(heightIssue || height > MAX_HEIGHT + 3 || height < MIN_HEIGHT - 3 || Math.abs(height - lastHeight) > 20) {
                this.heightIssue = true;
                if(firstIssue) {
                    System.out.println(height + "/" + lastHeight);
                    firstIssue = false;
                }
            } else {
                lastHeight = height;
            }
        }*/
    }

    public void setNoHeightIssue() {
        this.heightIssue = false;
        this.lastHeight = getRawHeight();
    }

    public boolean heightIssue() {
        return this.heightIssue;
    }

    private double getRawHeight() {
        return sensor.getPeriod() * heightMult;
    }

}
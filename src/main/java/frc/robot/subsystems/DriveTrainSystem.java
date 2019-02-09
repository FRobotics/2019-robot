package frc.robot.subsystems;

import frc.robot.subsystems.base.Motor;
import frc.util.RateLimiter;

public class DriveTrainSystem {

    private Motor leftMotor;
    private Motor rightMotor;

    private RateLimiter rightRateLimiter;
    private RateLimiter leftRateLimiter;

    public DriveTrainSystem() {
        this(-1);
    }

    public DriveTrainSystem(double rateLimit) {
        this.rightRateLimiter = new RateLimiter(rateLimit);
        this.leftRateLimiter = new RateLimiter(rateLimit);
    }

    public void init(Motor leftMotor, Motor rightMotor) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.rightMotor.setInverted(true);
    }

    /**
     * Sets the left motor's speed
     * @param speed - the speed to set the left motor to; should be -1 to +1
     */
    public void setLeftMotorSpeed(double speed) {
        speed = leftRateLimiter.get(speed);
        this.leftMotor.setSpeed(speed);
    }

    /**
     * Sets the right motor's speed
     * @param speed - the speed to set the left motor to; should be -1 to +1
     */
    public void setRightMotorSpeed(double speed) {
        speed = rightRateLimiter.get(speed);
        this.rightMotor.setSpeed(speed);
    }

    /**
     * Sets the speed of both motors to the same thing.
     * (the right motor is automatically inversed so it will drive straight)
     * @param speed - the speed to set the left and right motor to; should be -1 to +1
     */
    public void setSpeed(double speed) {
        this.setLeftMotorSpeed(speed);
        this.setRightMotorSpeed(speed);
    }

    /**
     * Sets the speed of both motors to the same thing but inverts the right so it turns.
     * Also divides the speed by 2 for consistency.
     * (the right motor is inversed again so it will turn)
     * @param speed - the speed at which the robot should turn; should be -2 to +2
     */
    public void turn(double speed) {
        this.setLeftMotorSpeed(speed/2);
        this.setRightMotorSpeed(-speed/2);
    }

    /**
     * Sets the speed of both motors to 0
     */
    public void stop() {
        this.setSpeed(0);
    }

    /**
     * Resets both of the rate limiters
     */
    public void reset() {
        this.leftRateLimiter.reset();
        this.rightRateLimiter.reset();
    }

    public double getRateLimit() {
        return this.rightRateLimiter.getRate();
    }

    public void setRateLimit(double rateLimit) {
        this.rightRateLimiter = new RateLimiter(rateLimit);
    }
}
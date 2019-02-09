package frc.robot.subsystems;

import edu.wpi.first.wpilibj.SpeedController;
import frc.util.RateLimiter;

public class DriveTrainSystem {

    private SpeedController leftMotor;
    private SpeedController rightMotor;
    /**
     * Whether the motor outputs should be inverted or not
     */
    private boolean inverted;

    private RateLimiter rightRateLimiter;
    private RateLimiter leftRateLimiter;

    public DriveTrainSystem() {
        this(false, -1);
    }

    public DriveTrainSystem(boolean inverted) {
        this(inverted, -1);
    }

    public DriveTrainSystem(double rateLimit) {
        this(false, rateLimit);
    }

    public DriveTrainSystem(boolean inverted, double rateLimit) {
        this.inverted = inverted;
        this.rightRateLimiter = new RateLimiter(rateLimit);
        this.leftRateLimiter = new RateLimiter(rateLimit);
    }

    public void init(SpeedController leftMotor, SpeedController rightMotor) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
    }

    /**
     * Sets the left motor's speed
     * @param speed - the speed to set the left motor to; should be -1 to +1
     */
    public void setLeftMotorSpeed(double speed) {
        speed = leftRateLimiter.get(speed);
        this.leftMotor.set(inverted ? -speed : speed);
    }

    /**
     * Sets the right motor's speed and inverses it for simplicity
     * @param speed - the speed to set the right motor to; should be -1 to +1
     */
    public void setRightMotorSpeed(double speed) {
        speed = rightRateLimiter.get(speed);
        this.rightMotor.set(inverted ? speed : -speed);
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

    public boolean isInverted() {
        return this.inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public double getRateLimit() {
        return this.rightRateLimiter.getRate();
    }

    public void setRateLimit(double rateLimit) {
        this.rightRateLimiter = new RateLimiter(rateLimit);
    }
}
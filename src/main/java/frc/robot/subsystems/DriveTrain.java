package frc.robot.subsystems;

import edu.wpi.first.wpilibj.SpeedController;

public class DriveTrain {

    private SpeedController leftMotor;
    private SpeedController rightMotor;
    /**
     * Whether the motor outputs should be inverted or not
     */
    private boolean inverted;

    /**
     * The max change in speed per call.
     * 0 means no limit.
     */
    private double rateLimit;

    public DriveTrain() {
        this(false, 0);
    }

    public DriveTrain(boolean inverted) {
        this(inverted, 0);
    }

    public DriveTrain(double rateLimit) {
        this(false, rateLimit);
    }

    public DriveTrain(boolean inverted, double rateLimit) {
        this.inverted = inverted;
        this.rateLimit = rateLimit;
    }

    public void init(SpeedController leftMotor, SpeedController rightMotor) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
    }

    /**
     * Sets the left motor's speed
     */
    public void setLeftMotorSpeed(double speed) {
        //apply limiting once ready
        this.leftMotor.set(inverted ? -speed : speed);
    }

    /**
     * Sets the right motor's speed and inverses it for simplicity
     */
    public void setRightMotorSpeed(double speed) {
        //apply limiting once ready
        this.rightMotor.set(inverted ? speed : -speed);
    }

    /**
     * Sets the speed of both motors to the same thing.
     * (the right motor is automatically inversed so it will drive straight)
     */
    public void setSpeed(double speed) {
        this.setLeftMotorSpeed(speed);
        this.setRightMotorSpeed(speed);
    }

    /**
     * Sets the speed of both motors to the same thing but inverts the right so it turns.
     * Also divides the speed by 2 for consistency.
     * (the right motor is inversed again so it will turn)
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

    public boolean isInverted() {
        return this.inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public double getRateLimit() {
        return this.rateLimit;
    }

    public void setRateLimit(double rateLimit) {
        this.rateLimit = rateLimit;
    }
}
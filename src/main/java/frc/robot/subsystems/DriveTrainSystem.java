package frc.robot.subsystems;

import com.analog.adis16448.frc.ADIS16448_IMU;

import frc.robot.subsystems.base.EncoderMotor;
import frc.util.RateLimiter;

public class DriveTrainSystem {

    private EncoderMotor leftMotor;
    private EncoderMotor rightMotor;

    private ADIS16448_IMU gyro;

    private RateLimiter rightRateLimiter;
    private RateLimiter leftRateLimiter;

    // information

    private double leftMotorTarget;
    private double rightMotorTarget;
    private double leftMotorOutput;
    private double rightMotorOutput;

    public DriveTrainSystem() {
        this(-1);
    }

    public DriveTrainSystem(double rateLimit) {
        this.setRateLimit(rateLimit);
    }

    public void init(EncoderMotor leftMotor, EncoderMotor rightMotor, ADIS16448_IMU gyro) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.gyro = gyro;
        this.rightMotor.invert();
    }

    /**
     * Sets the left motor's speed
     * 
     * @param speed - the speed to set the left motor to; should be -1 to +1
     * @return the rate limited speed the motor was actually set to
     */
    public void setLeftMotorSpeed(double speed) {
        this.leftMotorTarget = speed;
        speed = leftRateLimiter.get(speed, leftMotor.getSpeed());
        this.leftMotor.setSpeed(speed);
        this.leftMotorOutput = speed;
    }

    /**
     * Sets the right motor's speed
     * 
     * @param speed - the speed to set the left motor to; should be -1 to +1
     * @return the rate limited speed the motor was actually set to
     */
    public void setRightMotorSpeed(double speed) {
        this.rightMotorTarget = speed;
        speed = rightRateLimiter.get(speed, rightMotor.getSpeed());
        this.rightMotor.setSpeed(speed);
        this.rightMotorOutput = speed;
    }

    /**
     * Sets the speed of both motors to the same thing. (the right motor is
     * automatically inversed so it will drive straight)
     * 
     * @param speed - the speed to set the left and right motor to; should be -1 to
     *              +1
     */
    public void setSpeed(double speed) {
        this.setLeftMotorSpeed(speed);
        this.setRightMotorSpeed(speed);
    }

    /**
     * Sets the speed of both motors to the same thing but inverts the right so it
     * turns. Also divides the speed by 2 for consistency. (the right motor is
     * inversed again so it will turn)
     * 
     * @param speed - the speed at which the robot should turn; should be -2 to +2
     */
    public void turn(double speed) {
        this.setLeftMotorSpeed(speed / 2);
        this.setRightMotorSpeed(-speed / 2);
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
        this.stop();
    }

    public double getRateLimit() {
        return this.rightRateLimiter.getRate();
    }

    public void setRateLimit(double rateLimit) {
        this.leftRateLimiter = new RateLimiter(rateLimit);
        this.rightRateLimiter = new RateLimiter(rateLimit);
    }

    public double getLeftMotorSpeed() {
        return this.leftMotor.getSpeed();
    }

    public double getRightMotorSpeed() {
        return this.rightMotor.getSpeed();
    }

    public double getLeftMotorOutputPercent() {
        return this.leftMotor.getOutputPercent();
    }

    public double getRightMotorOutputPercent() {
        return this.rightMotor.getOutputPercent();
    }

    public double getAngle() {
        return gyro.getAngleZ();
    }

    public void drive(double fb, double lr) {
        fb = smooth(fb, 0.2)*10;
        lr = smooth(lr, 0.2)*10;
        if (lr == 0) {
            this.setSpeed(fb);
        } else if (fb == 0) {
            this.turn(lr);
        } else {
            double hlr = lr * 0.5;

            double left = fb + hlr;
            double right = fb - hlr;
            this.setLeftMotorSpeed(left);
            this.setRightMotorSpeed(right);
        }
    }

    private double smooth(double value, double deadband) {
        int power = 2;
        if (value > deadband) {
            double newValue = (value - deadband) / (1 - deadband);
            return Math.pow(newValue, power);
        }
        if (value < -deadband) {
            double newValue = (value + deadband) / (1 - deadband);
            return -Math.abs(Math.pow(newValue, power));
        }
        return 0;
    }

    public double getLeftMotorTarget() {
        return this.leftMotorTarget;
    }

    public double getRightMotorTarget() {
        return this.rightMotorTarget;
    }

    public double getLeftMotorOutput() {
        return this.leftMotorOutput;
    }

    public double getRightMotorOutput() {
        return this.rightMotorOutput;
    }
}
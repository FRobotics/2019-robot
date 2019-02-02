package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.SpeedController;

public class DriveTrain {

    private SpeedController leftMotor;
    private SpeedController rightMotor;
    /**
     * Whether the motor outputs should be inverted or not
     */
    private boolean invert;

    /**
     * The max change in speed per call.
     * 0 means no limit.
     */
    private double rateLimit;

    public DriveTrain() {
        this.invert = false;
        this.rateLimit = 0;
    }

    public void init(int leftMotorPort, int rightMotorPort) {
        this.leftMotor = new PWMTalonSRX(leftMotorPort);
        this.rightMotor = new PWMTalonSRX(rightMotorPort);
    }

    /**
     * Sets the left motor's speed
     */
    public void setLeftMotorSpeed(double speed) {
        //apply limiting once ready
        this.leftMotor.set(invert ? -speed : speed);
    }

    /**
     * Sets the right motor's speed and inverses it for simplicity
     */
    public void setRightMotorSpeed(double speed) {
        //apply limiting once ready
        this.rightMotor.set(invert ? speed : -speed);
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
}